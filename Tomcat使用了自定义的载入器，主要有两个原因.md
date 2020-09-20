Tomcat使用了自定义的载入器，主要有两个原因

1. servlet容器不应该完全信任它正在运行的servlet类，servlet类应该只能访问WEB-INF目录下的类
2. 提供自动重载功能

同时在Tomcat的自定义载入器中，还是用了自定义的类载入器，原因有三

1. 在载入类中指定某些规则
2. 缓存已经加载的类
3. 实现类的预载入

接下来我们看Tomcat是如何实现这些功能的

一：Loader接口

在Loader接口的实现中，ClassLoader对应的是WebappClassLoader的一个实例，其中modified方法用于支持自动重载，默认禁用

``` java
// 类载入器
public interface Loader {

  // 获取Web载入器中ClassLoader类的实例
  public ClassLoader getClassLoader();

  // 关联的容器
  public Container getContainer();
  public void setContainer(Container container);

  // 默认的Context容器
  public DefaultContext getDefaultContext();
  public void setDefaultContext(DefaultContext defaultContext);


  // 支持自动重载
  public boolean getReloadable();
  public void setReloadable(boolean reloadable);

  public void addPropertyChangeListener(PropertyChangeListener listener);

  /**
   * WEB-INF/classes目录和WEB-INT/lib目录是作为仓库添加到载入器中的
   * 本方法就用于添加一个新的仓库
   */
  public void addRepository(String repository);

  // 返回所有已添加的仓库
  public String[] findRepositories();

  /**
   * 容器中的类被修改时，自动重新编译，并且重新载入，用来自动重载
   * （在具体实现中，载入器本身并不能自动重载，而是使用Context接口的reload方法来实现）
   */
  public boolean modified();

}
```



二：Reloader接口

实现自动重载的话必须实现Reloader接口

```java
// 类的自动重载
public interface Reloader {

  // 添加仓库目录
  public void addRepository(String repository);


  // 获取仓库目录列表
  public String[] findRepositories();


  // 是否有servlet相关的类被修改了
  public boolean modified();

}
```



三：WebappLoader类

```java
/**
 * Web应用程序中的载入器，负责载入web程序中使用的类
 * 实现Runnbale接口，这样可以指定一个线程不断地调用其载入器的modified方法，
 * 如果为true，那么调用关联的context容器的reload方法来重载
 */
public class WebappLoader
  implements Lifecycle, Loader, PropertyChangeListener, Runnable {

  private int checkInterval = 15;

  // 类载入器
  private WebappClassLoader classLoader = null;

  // 指定类加载器的名字
  private String loaderClass =
    "org.apache.catalina.loader.WebappClassLoader";

  // 父载入器
  private ClassLoader parentClassLoader = null;

  // 获取检查间隔
  public int getCheckInterval() {
    return (this.checkInterval);
  }

  // 设置检查间隔
  public void setCheckInterval(int checkInterval) {
    int oldCheckInterval = this.checkInterval;
    this.checkInterval = checkInterval;
    support.firePropertyChange("checkInterval",
                  new Integer(oldCheckInterval),
                  new Integer(this.checkInterval));
  }

  public ClassLoader getClassLoader() {}
  public Container getContainer() {}
  public void setContainer(Container container) {}
  public DefaultContext getDefaultContext() {}
  public void setDefaultContext(DefaultContext defaultContext) {}

  // 拿到类加载器
  public String getLoaderClass() {}

  // 设置类加载器（注意区别ClassLoader）
  public void setLoaderClass(String loaderClass) {}

  public boolean getReloadable() {}
  public void setReloadable(boolean reloadable) {}
  public void addPropertyChangeListener(PropertyChangeListener listener) {}
  public void addRepository(String repository) {}
  public String[] findRepositories() {}

  public boolean modified() {
    return (classLoader.modified());
  }


  /**
   * 1.创建一个类载入器
   * 2.设置仓库
   * 3.设置类路径
   * 4.设置访问权限
   */
  public void start() throws LifecycleException {
    // Validate and update our current component state
    if (started)
      throw new LifecycleException
        (sm.getString("webappLoader.alreadyStarted"));
    if (debug >= 1)
      log(sm.getString("webappLoader.starting"));
    lifecycle.fireLifecycleEvent(START_EVENT, null);
    started = true;
    if (container.getResources() == null)
      return;
    // Register a stream handler factory for the JNDI protocol
    URLStreamHandlerFactory streamHandlerFactory =
      new DirContextURLStreamHandlerFactory();
    try {
      URL.setURLStreamHandlerFactory(streamHandlerFactory);
    } catch (Throwable t) {
      // Ignore the error here.
    }



   // Construct a class loader based on our current repositories list
   try {
     classLoader = createClassLoader();
     classLoader.setResources(container.getResources());
     classLoader.setDebug(this.debug);
     classLoader.setDelegate(this.delegate);
     for (int i = 0; i < repositories.length; i++) {
       classLoader.addRepository(repositories[i]);
     }
     // Configure our repositories
     setRepositories();
     setClassPath();
     setPermissions();
     if (classLoader instanceof Lifecycle)
       ((Lifecycle) classLoader).start();
     // Binding the Webapp class loader to the directory context
     DirContextURLStreamHandler.bind
       ((ClassLoader) classLoader, this.container.getResources());
   } catch (Throwable t) {
     throw new LifecycleException("start: ", t);
   }
   // Validate that all required packages are actually available
   validatePackages();
   // Start our background thread if we are reloadable
   if (reloadable) {
     log(sm.getString("webappLoader.reloading"));
     try {
       threadStart();
     } catch (IllegalStateException e) {
       throw new LifecycleException(e);
     }
   }
  }



  /**
   * 创建默认的类加载器
   * 可以不使用WebappClassLoader实例，但是由于返回值，所有自定义类载入器必须继承WebappClassLoader
   */
  private WebappClassLoader createClassLoader()
    throws Exception {

    Class clazz = Class.forName(loaderClass);
    WebappClassLoader classLoader = null;

    if (parentClassLoader == null) {
      // Will cause a ClassCast is the class does not extend WCL, but
      // this is on purpose (the exception will be caught and rethrown)
      classLoader = (WebappClassLoader) clazz.newInstance();
    } else {
      Class[] argTypes = { ClassLoader.class };
      Object[] args = { parentClassLoader };
      Constructor constr = clazz.getConstructor(argTypes);
      classLoader = (WebappClassLoader) constr.newInstance(args);
    }
    return classLoader;
  }


  /**
   * 通知Context重新加载servlet
   * ------------------------
   * 创建一个内部类，传入一个新线程，这样重载类就不影响现有线程，在新线程中调用Context容器的reload方法
   */
  private void notifyContext() {
    WebappContextNotifier notifier = new WebappContextNotifier();
    (new Thread(notifier)).start();
  }



  /**
   * 周期性检查是否有类被修改了，默认15s
   */
  public void run() {
    while (!threadDone) {
      threadSleep();
      if (!started)
        break;
      try {
        if (!classLoader.modified())
          continue;
      } catch (Exception e) {
        log(sm.getString("webappLoader.failModifiedCheck"), e);
        continue;
      }
      // 如果类被修改了，调用
      notifyContext();
      break;
    }
  }


  protected class WebappContextNotifier implements Runnable {
    public void run() {
      ((Context) container).reload();
    }
  }

```



四：WebappclassLoader类

webappclassloader类是真正负责类载入的类

\* 载入器

\* 负责载入类，遵循如下规则

\* 1.载入类时先检查本地缓存

\* 2.本地缓存没有时，检查上一层缓存，即调用ClassLoader的findLoadedClass方法

\* 3.两个缓存都没有，调用系统的类载入器载入

\* 4.如果启用了SecurityManager，还需要检查是否允许载入

\* 5.如果打开标志位delegate或者载入的类属于包触发器的包名，则调用父载入器来载入，如果为null，那么使用系统的类载入器

\* 6.从当前仓库载入相关类

\* 7.如果当前仓库没有，且delegate关闭，则使用父载入器，如果为null，那么使用系统的类载入器

\* 8.如果还没有，调用ClassNotFoundException异常
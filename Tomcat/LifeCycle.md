Tomcat里面有各种各样的组件，每个组件各司其职，组件之间又相互协作共同完成web服务器这样的工程。在这些组件之上，Tomcat通过Lifecycle（生命周期机制）来完成对组件的生命周期管理，所以在学习各个组件之前，我们需要看看Lifecycle是什么以及能做什么？实现原理又是怎样的？

一：Lifecycle接口

Lifecycle接口中最重要的就是start和stop方法，组件必须实现这两个方法来供父组件调用，以实现启动/关闭，其他的三个方法都是有关事件监听器的，组件可以注册多个事件监听器来监听某个事件，当事件发送时，相应的事件监听器会收到通知。

```java
public interface Lifecycle {

  public static final String START_EVENT = "start";

  public static final String BEFORE_START_EVENT = "before_start";

  public static final String AFTER_START_EVENT = "after_start";

  public static final String STOP_EVENT = "stop";

  public static final String BEFORE_STOP_EVENT = "before_stop";

  public static final String AFTER_STOP_EVENT = "after_stop";

  // 事件监听器
  public void addLifecycleListener(LifecycleListener listener);
  public LifecycleListener[] findLifecycleListeners();
  public void removeLifecycleListener(LifecycleListener listener);

  /**
   * 负责组件的启动
   * 在使用组件任何公共方法前，都应该调用本方法，同时将START_EVENT类型的事件发送给任何注册的监听器
   */
  public void start() throws LifecycleException;

  /**
   * 负责组件的关闭
   * 该方法应该是在此组件的给定实例上调用的最后一个方法。它还应将STOP_EVENT类型的事件发送给任何已注册的侦听器。
   */
  public void stop() throws LifecycleException;

}
```



二：LifecycleEvent类

生命周期事件的包装类，由于表示一个事件

```java
// 生命周期事件
public final class LifecycleEvent
  extends EventObject {

  public LifecycleEvent(Lifecycle lifecycle, String type) {
   this(lifecycle, type, null);
  }

  public LifecycleEvent(Lifecycle lifecycle, String type, Object data) {
   super(lifecycle);
   this.lifecycle = lifecycle;
   this.type = type;
  }

  private Object data = null;

  private Lifecycle lifecycle = null;

  private String type = null;

  // 返回事件的数据
  public Object getData() {
    return (this.data);
  }

  // 返回事件关联的生命周期
  public Lifecycle getLifecycle() {
    return (this.lifecycle);
  }

  // 返回事件类型
  public String getType() {
    return (this.type);
  }
}
```



三：LifecycleListener接口

该接口中只有一个方法，当某个事件监听器听到相关事件发生时，会调用该方法

```java
// 事件监听器
public interface LifecycleListener {

  // 某个事件监听器听到相关事件发生，会调用本方法
  public void lifecycleEvent(LifecycleEvent event);

}
```



四：LifecycleSupport类

为了方便监听器的管理，tomcat提供了LifecycleSupport工具类，其中所有注册的事件都会被存储到一个数组中由于管理，其中fireLifecycleEvent方法用于触发相应的生命周期事件

```java
// 管理监听器
public final class LifecycleSupport {

  public LifecycleSupport(Lifecycle lifecycle) {
    super();
    this.lifecycle = lifecycle;
 }

 private Lifecycle lifecycle = null;

 private LifecycleListener listeners[] = new LifecycleListener[0];

 // 增加事件监听器
 public void addLifecycleListener(LifecycleListener listener) {
  synchronized (listeners) {
     LifecycleListener results[] =
      new LifecycleListener[listeners.length + 1];

     for (int i = 0; i < listeners.length; i++)
       results[i] = listeners[i];
    
     results[listeners.length] = listener;
     listeners = results;
  }
 }

 public LifecycleListener[] findLifecycleListeners() {
    return listeners;
 }

 // 移除事件监听器
 public void removeLifecycleListener(LifecycleListener listener) {
    synchronized (listeners) {
      int n = -1;

      for (int i = 0; i < listeners.length; i++) {
        if (listeners[i] == listener) {
          n = i;
          break;
        }
      }

      if (n < 0)
        return;
      
      LifecycleListener results[] =
          new LifecycleListener[listeners.length - 1];
      int j = 0;

      for (int i = 0; i < listeners.length; i++) {
        if (i != n)
          results[j++] = listeners[i];
      }
      listeners = results;
    }
  }


  /**
   * 负责触发生命周期事件
   * 首先复制一份注册的监听器数组，然后挨个调用对应的lifecycleEvent方法，并传入要触发的事件
   */
  public void fireLifecycleEvent(String type, Object data) {
    LifecycleEvent event = new LifecycleEvent(lifecycle, type, data);
    LifecycleListener interested[] = null;

    synchronized (listeners) {
      interested = (LifecycleListener[]) listeners.clone();
    }

    for (int i = 0; i < interested.length; i++)
      interested[i].lifecycleEvent(event);
  }
}
```

生命周期的整个设计过程采用了观察者模式，整体不是很难。
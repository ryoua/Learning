Tomcat的日志相关实现比较简单，只涉及到五个类，Logger接口，LoggerBase抽象类，SystemErrLogger、FileLogger、SystemOutLogger三个实现类。

以下是Logger接口，参数和方法意思比较简单，就不赘述了。

```java
// 日志记录器接口
public interface Logger {

  // 日志级别
  public static final int FATAL = Integer.MIN_VALUE;
  public static final int ERROR = 1;
  public static final int WARNING = 2;
  public static final int INFORMATION = 3;
  public static final int DEBUG = 4;

  // 关联的容器
  public Container getContainer();
  public void setContainer(Container container);

  public String getInfo();

  // 日志级别
  public int getVerbosity();
  public void setVerbosity(int verbosity);
  public void addPropertyChangeListener(PropertyChangeListener listener);

  // Log
  public void log(String message);
  public void log(Exception exception, String msg);
  public void log(String message, Throwable throwable);
  public void log(String message, int verbosity);
  public void log(String message, Throwable throwable, int verbosity);
  public void removePropertyChangeListener(PropertyChangeListener listener);
}
```



LoggerBase抽象类实现了Logger接口，同时三个实现类都继承了抽象类，LoggerBase完成了大部分的实现，只留了一个Log函数给子类实现

``` java
// 实现了Logger接口，所有日志实现类都要继承本类
public abstract class LoggerBase
  implements Logger {

  public void setVerbosityLevel(String verbosity) {
    if ("FATAL".equalsIgnoreCase(verbosity))
      this.verbosity = FATAL;
    else if ("ERROR".equalsIgnoreCase(verbosity))
      this.verbosity = ERROR;
    else if ("WARNING".equalsIgnoreCase(verbosity))
      this.verbosity = WARNING;
    else if ("INFORMATION".equalsIgnoreCase(verbosity))
      this.verbosity = INFORMATION;
    else if ("DEBUG".equalsIgnoreCase(verbosity))
      this.verbosity = DEBUG;
 }

 // 所有log都会最终调用这个方法，子类只需要实现这个方法即可
 public abstract void log(String msg);

 public void log(Exception exception, String msg) {
    log(msg, exception);
 }

 public void log(String msg, Throwable throwable) {
    CharArrayWriter buf = new CharArrayWriter();
    PrintWriter writer = new PrintWriter(buf);
    writer.println(msg);
    throwable.printStackTrace(writer);
    Throwable rootCause = null;
    if (throwable instanceof LifecycleException)
      rootCause = ((LifecycleException) throwable).getThrowable();
    else if (throwable instanceof ServletException)
      rootCause = ((ServletException) throwable).getRootCause();

    if (rootCause != null) {
      writer.println("----- Root Cause -----");
      rootCause.printStackTrace(writer);
    }
    log(buf.toString());
 }

 public void log(String message, int verbosity) {
    if (this.verbosity >= verbosity)
      log(message);
 }

 // 日志级别比设置的低才会记录
 public void log(String message, Throwable throwable, int verbosity) {
    if (this.verbosity >= verbosity)
      log(message, throwable);
 }

 public void removePropertyChangeListener(PropertyChangeListener listener) {
    support.removePropertyChangeListener(listener);
  }
}
```



SystemErrLogger的实现只是简单的通过System.err.println来打印信息

``` java
// 和SystemOutLogger基本一样，区别时通过System.err输出
public class SystemErrLogger
  extends LoggerBase {
  protected static final String info =
   "org.apache.catalina.logger.SystemErrLogger/1.0";

  public void log(String msg) {
    System.err.println(msg);
  }
}
```



SystemOutLogger的实现只是简单的通过System.out.println来打印信息

``` java
// 通过System.out输出的日志类
public class SystemOutLogger
  extends LoggerBase {
  protected static final String info =
    "org.apache.catalina.logger.SystemOutLogger/1.0";

	public void log(String msg) {
    System.out.println(msg);
	}
}
```



FileLogger是用来将日志输出到文件，log实现中通过检查时间是否是一样，比如天数不一样，就会新创建一个文件，同时将日期设置为新日期，之后调用writer将信息写入文件

```java
// 输出到文件
public class FileLogger
  extends LoggerBase
  implements Lifecycle {

  // 时间戳
  private String date = "";

  // 输出文件位置
  public String getDirectory() {
    return (directory);
  }

  public void log(String msg) {
    Timestamp ts = new Timestamp(System.currentTimeMillis());
    String tsString = ts.toString().substring(0, 19);
    String tsDate = tsString.substring(0, 10);
    if (!date.equals(tsDate)) {
      synchronized (this) {
        // 如果时间不对，那么新开一个文件来输出日志
        if (!date.equals(tsDate)) {
          close();
          date = tsDate;
          open();
        }
      }
    }

    if (writer != null) {
      if (timestamp) {
        writer.println(tsString + " " + msg);
      } else {
        writer.println(msg);
      }
    }
 }

 // 关闭打开的文件
 private void close() {
    if (writer == null)
      return;
    writer.flush();
    writer.close();
    writer = null;
    date = "";
 }

 // 打开一个新的日志文件
 private void open() {
    File dir = new File(directory);
    if (!dir.isAbsolute())
      dir = new File(System.getProperty("catalina.base"), directory);

   dir.mkdirs();
    try {
      String pathname = dir.getAbsolutePath() + File.separator +
        prefix + date + suffix;
      writer = new PrintWriter(new FileWriter(pathname, true), true);
    } catch (IOException e) {
      writer = null;
    }
  }
}
```




## Valve

Valve作为一个基础的阀门，扮演着业务实际执行者的角色。Valve这个接口只有一个invoke方法，用于执行阀，同时接受ValveContext作为其参数，用来迭代调用

```java
public interface Valve {
    public String getInfo();

    /**
     * 执行阀, 调用ValveContext的invoke逐个调用
     */
    public void invoke(Request request, Response response, ValveContext context)
        throws IOException, ServletException;
}
```



## Contained

Pipeline及其他相关组件都实现了Contained接口，我们看看这个接口有哪些方法。很简单，就是get/set容器操作。主要用于将组件和容器绑定

```java
/**
 * 组件与容器绑定
 */

public interface Contained {
    public Container getContainer();

    public void setContainer(Container container);
}
```



## Pipeline

Pipeline作为一个管道，我们可以简单认为是一个Valve的集合，如果我们实现，我们可以对这个集合进行遍历，调用每个元素的业务逻辑方法invoke()。但是在Tomcat中，采用了ValveContext内部类来进行遍历，直接看源码。

```java
/**
 * 管道
 */
public interface Pipeline {
    /**
     * 基础阀
     */
    public Valve getBasic();
    public void setBasic(Valve valve);

    /**
     * 增加阀
     */
    public void addValve(Valve valve);
    public Valve[] getValves();

    /**
     * 调用
     */
    public void invoke(Request request, Response response)
        throws IOException, ServletException;

    public void removeValve(Valve valve);
}
```



## StandardPipeline

StandardPipeline是Pipeline接口的标准实现类，通过源码可以看到，ValveContext通过调用Valve的invoke方法，并传入自身，以此来进行一个迭代遍历，并且在执行到最后的时候调用basicValve基础阀。

```java
public class StandardPipeline
    implements Pipeline, Contained, Lifecycle {
    // 获取基本阀门
  public Valve getBasic();

  // 设置基本阀门
  public void setBasic(Valve valve);

  // 添加阀门
  public void addValve(Valve valve);

  // 获取阀门数组
  public Valve[] getValves();

  // 删除阀门
  public void removeValve(Valve valve);

  // 调用内部类的invokeNext
  public void invoke(Request request, Response response)
    throws IOException, ServletException {
    (new StandardPipelineValveContext()).invokeNext(request, response);
 }

 protected class StandardPipelineValveContext
    implements ValveContext {
    protected int stage = 0;

    // 使用subscript和stage标明当前使用的阀，每次调用完增加，直到调用完开始调用基础阀
    public void invokeNext(Request request, Response response)
      throws IOException, ServletException {
      int subscript = stage;
      stage = stage + 1;

      if (subscript < valves.length) {
        valves[subscript].invoke(request, response, this);
      } else if ((subscript == valves.length) && (basic != null)) {
        basic.invoke(request, response, this);
      } else {
        throw new ServletException
          (sm.getString("standardPipeline.noValve"));
      }
    }
  }
}

```



总结

通过上面的代码分析，我们发现了几个关键的设计模式：

1. 模板方法模式，父类定义框架，子类实现
2. 责任链模式，就是这的管道/阀门的实现方式，每个阀门维护一个next属性指向下一个阀门


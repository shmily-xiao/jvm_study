# JFR 事件流
JFR事件中，我们需要去读取JFR文件，进行分析。但是文件是死的，人是活的，每次分析都需要先生成JFR文件简直是太复杂了。是个程序员都不能容忍。

在JFR事件流中，我们可以监听Event的变化，从而在程序中进行相应的处理。这样不需要生成JFR文件也可以监听事件变化。

```java
public static void main(String[] args) throws IOException, ParseException {
    //default or profile 两个默认的profiling configuration files
    Configuration config = Configuration.getConfiguration("default");
    try (var es = new RecordingStream(config)) {
      es.onEvent("jdk.GarbageCollection", System.out::println);
      es.onEvent("jdk.CPULoad", System.out::println);
      es.onEvent("jdk.JVMInformation", System.out::println);
      es.setMaxAge(Duration.ofSeconds(10));
      es.start();
    }
  }
```

看看上面的例子。我们通过Configuration.getConfiguration("default")获取到了默认的default配置。

然后通过构建了default的RecordingStream。通过onEvent
方法，我们对相应的Event进行处理。

来自：https://www.jb51.net/article/186337.htm
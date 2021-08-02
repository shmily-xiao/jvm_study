# JFR事件
JMC好用是好用，但是要一个一个的去监听JFR文件会很繁琐。接下来我们来介绍一下怎么采用写代码的方式来监听JFR事件。

我们的思路就是使用jdk.jfr.consumer.RecordingFile去读取生成的JFR文件，然后对文件中的数据进行解析。

相应代码如下：
```java
@Slf4j
public class JFREvent {
 
  private static Predicate<RecordedEvent> testMaker(String s) {
    return e -> e.getEventType().getName().startsWith(s);
  }
 
  private static final Map<Predicate<RecordedEvent>,
      Function<RecordedEvent, Map<String, String>>> mappers =
      Map.of(testMaker("jdk.ClassLoadingStatistics"),
          ev -> Map.of("start", ""+ ev.getStartTime(),
              "Loaded Class Count",""+ ev.getLong("loadedClassCount"),
              "Unloaded Class Count", ""+ ev.getLong("unloadedClassCount")
          ));
 
  @Test
  public void readJFRFile() throws IOException {
    RecordingFile recordingFile = new RecordingFile(Paths.get("/Users/flydean/flight_recording_1401comflydeaneventstreamThreadTest21710.jfr"));
    while (recordingFile.hasMoreEvents()) {
      var event = recordingFile.readEvent();
      if (event != null) {
        var details = convertEvent(event);
        if (details == null) {
          // details为空
        } else {
          // 打印目标
          log.info("{}",details);
        }
      }
    }
  }
 
  public Map<String, String> convertEvent(final RecordedEvent e) {
    for (var ent : mappers.entrySet()) {
      if (ent.getKey().test(e)) {
        return ent.getValue().apply(e);
      }
    }
    return null;
  }
}
```

注意，在convertEvent方法中，我们将从文件中读取的Event转换成了map对象。

在构建map时，我们先判断Event的名字是不是我们所需要的jdk.ClassLoadingStatistics，然后将Event中其他的字段进行转换。最后输出。

运行结果：
```text
{start=2020-04-29T02:18:41.770618136Z, Loaded Class Count=2861, Unloaded Class Count=0}
...
```

来自：https://www.jb51.net/article/186337.htm
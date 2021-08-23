package com.study.jfr;

import jdk.jfr.consumer.RecordedEvent;
import jdk.jfr.consumer.RecordingFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.logging.Logger;

public class JfrEventTest {

    private static Predicate<RecordedEvent> testMaker(String s) {
        return e -> e.getEventType().getName().startsWith(s);
    }

    private static final Map<Predicate<RecordedEvent>,
            Function<RecordedEvent, Map<String, String>>> mappers =
//            Map.of(testMaker("ClassLoadingStatistics"),
//                    ev -> Map.of("start", ""+ ev.getStartTime(),
//                            "Loaded Class Count",""+ ev.getValue("loadedClassCount"),
//                            "Unloaded Class Count", ""+ ev.getValue("unloadedClassCount"),
//                            "Java Monitor Blocked", ""+ev.getValue("Monitor Class")
//                    ));
      Map.of(testMaker("ClassLoadingStatistics"),
              ev -> Map.of("start", ""+ ev.getStartTime(),
                "Loaded Class Count",""+ ev.getValue("loadedClassCount"),
                "Unloaded Class Count", ""+ ev.getValue("unloadedClassCount"),
                "Java Monitor Blocked", ""+ev.getValue("Monitor Class")
                ));

    public void saveContent2Txt() throws IOException{
        RecordingFile recordingFile = new RecordingFile(Paths.get("/Users/wzj/workspace/alibaba/speed-server/myflightv2.jfr"));

        File f = new File("myflight_print.txt");
        FileOutputStream fop = new FileOutputStream(f);
        // 构建FileOutputStream对象,文件不存在会自动新建

        OutputStreamWriter writer = new OutputStreamWriter(fop, "UTF-8");
        // 构建OutputStreamWriter对象,参数可以指定编码,默认为操作系统默认编码,windows上是gbk

        while (recordingFile.hasMoreEvents()) {
            RecordedEvent event = recordingFile.readEvent();
            if (event != null) {
                writer.append(event.toString());
            }
        }

        writer.close();
        // 关闭写入流,同时会把缓冲区内容写入文件,所以上面的注释掉

        fop.close();
        // 关闭输出流,释放系统资源
    }

    public void readJFRFile() throws IOException {
        Set<String> keys = new HashSet<>();
        RecordingFile recordingFile = new RecordingFile(Paths.get("/Users/wzj/workspace/alibaba/speed-server/myflightv2.jfr"));
        while (recordingFile.hasMoreEvents()) {
            RecordedEvent event = recordingFile.readEvent();

            if (event != null) {
//                System.out.println(event);
//                System.out.println(event.getEventType().getName());
                keys.add(event.getEventType().getName());
                if ("jdk.JavaErrorThrow".equals(event.getEventType().getName())){
                    /**
                     *
                         jdk.JavaErrorThrow {
                         startTime = 14:52:04.412
                         message = null
                         thrownClass = org.apache.ibatis.ognl.OgnlParser$LookaheadSuccess (classLoader = com.taobao.pandora.boot.loader.ReLaunchURLClassLoader)
                         eventThread = "ITaskService-15" (javaThreadId = 668)
                         stackTrace = [
                         java.lang.Error.<init>() line: 59
                         org.apache.ibatis.ognl.OgnlParser$LookaheadSuccess.<init>() line: 3054
                         org.apache.ibatis.ognl.OgnlParser$LookaheadSuccess.<init>(OgnlParser$1) line: 3054
                         org.apache.ibatis.ognl.OgnlParser.<init>(Reader) line: 3055
                         org.apache.ibatis.ognl.Ognl.parseExpression(String) line: 109
                         ...
                         ]
                         }
                     * **/
                    System.out.println(event);

                }
                Map<String, String> details = convertEvent(event);
                if (details == null) {
                    // details为空
                } else {
                    // 打印目标
                    System.out.println(details);
                }
            }
        }
        System.out.println(keys);
    }

    public Map<String, String> convertEvent(final RecordedEvent e) {
        for (Map.Entry<Predicate<RecordedEvent>, Function<RecordedEvent, Map<String, String>>> ent : mappers.entrySet()) {
            if (ent.getKey().test(e)) {
                return ent.getValue().apply(e);
            }
        }
        return null;
    }

    public static void main(String[] args) throws IOException {
        JfrEventTest test = new JfrEventTest();
        test.readJFRFile();
    }
}

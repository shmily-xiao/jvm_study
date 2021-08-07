package com.study.jfr;

import jdk.jfr.consumer.RecordedEvent;
import jdk.jfr.consumer.RecordingFile;

import java.io.IOException;
import java.nio.file.Paths;
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
            Map.of(testMaker("ClassLoadingStatistics"),
                    ev -> Map.of("start", ""+ ev.getStartTime(),
                            "Loaded Class Count",""+ ev.getValue("loadedClassCount"),
                            "Unloaded Class Count", ""+ ev.getValue("unloadedClassCount")
                    ));

    public void readJFRFile() throws IOException {
        RecordingFile recordingFile = new RecordingFile(Paths.get("/Users/wzj/workspace/alibaba/speed-server/myflight.jfr"));
        while (recordingFile.hasMoreEvents()) {
            RecordedEvent event = recordingFile.readEvent();
            if (event != null) {
                Map<String, String> details = convertEvent(event);
                if (details == null) {
                    // details为空
                } else {
                    // 打印目标
                    System.out.println(details);
                }
            }
        }
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

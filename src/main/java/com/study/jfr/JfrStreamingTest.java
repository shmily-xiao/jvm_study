package com.study.jfr;

import jdk.jfr.Configuration;
import jdk.jfr.consumer.RecordingStream;

import java.io.IOException;
import java.text.ParseException;
import java.time.Duration;

public class JfrStreamingTest {
    public static void main(String[] args) throws IOException, ParseException {
        //default or profile 两个默认的profiling configuration files
        Configuration config = Configuration.getConfiguration("default");
        try (RecordingStream es = new RecordingStream(config)) {
            es.onEvent("jdk.GarbageCollection", System.out::println);
//            es.onEvent("jdk.CPULoad", System.out::println);
            es.onEvent("jdk.JVMInformation", System.out::println);
            es.onEvent("jdk.SocketRead", System.out::println);
            es.onEvent("jdk.JavaMonitorEnter", event -> {
                System.out.println(event.getClass("monitorClass"));
            });
            es.setMaxAge(Duration.ofSeconds(10));
            es.start();
        }
    }
}

package com.study.attach;

import com.study.attach.impl.JavaFlightRecordServiceImpl;
import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;
import jdk.jfr.Configuration;
import sun.tools.jconsole.LocalVirtualMachine;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author wzj
 * @date 2022/06/09
 */
public class JvmAttach {

    public static void testAttach(){
        Map<Integer, LocalVirtualMachine> allVirtualMachines = LocalVirtualMachine.getAllVirtualMachines();
        System.out.println("");

        Optional<LocalVirtualMachine> applicationOptional = allVirtualMachines.entrySet().stream()
                .filter(kv -> kv.getValue().displayName().contains("xxx.application"))
                .map(kv -> kv.getValue())
                .findAny();

        LocalVirtualMachine application = applicationOptional.get();
        System.out.println("");
        System.out.println(application.connectorAddress());
        try {
            JMXServiceURL serviceUrl = new JMXServiceURL(application.connectorAddress());

            JMXConnector jmxConnector = JMXConnectorFactory.connect(serviceUrl, null);
            try {
                MBeanServerConnection mbeanConn = jmxConnector.getMBeanServerConnection();
                // now query to get the beans or whatever
                Set<ObjectName> beanSet = mbeanConn.queryNames(null, null);
                // 过滤出我想要的jmx的服务
                ObjectName jfrObjectName = beanSet.stream().filter(kv -> kv.getDomain().contains("jdk.management.jfr")).findAny().get();


                // 调用服务里面的方法
                Boolean stopRecording = (Boolean)mbeanConn.invoke(jfrObjectName, "stopRecording", new Object[]{1L}, new String[]{long.class.getName()});
                System.out.println(stopRecording);

                Boolean stopRecordingV2 = (Boolean)mbeanConn.invoke(jfrObjectName, "stopRecording", new Object[]{2L}, new String[]{long.class.getName()});
                System.out.println(stopRecordingV2);

                System.out.println("");
            } catch(Exception e){
                e.printStackTrace();
            } finally {
                jmxConnector.close();
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }

    public static void star(IJavaFlightRecordService recordService) throws Exception{
        Configuration jfrConfig;
        try {
            Reader reader = new InputStreamReader(JvmAttach.class.getClassLoader()
                    .getResourceAsStream("default.jfc"));
            jfrConfig = Configuration.create(reader);
        } catch (Exception e) {
            throw new RuntimeException("An error occurred getting JFR configuration.", e);
        }
        Map<String, String> settings = jfrConfig.getSettings();

        List<IRecordingDescriptor> availableRecordings = recordService.getAvailableRecordings();

        IRecordingDescriptor iRecordingDescriptor = availableRecordings.get(0);
        System.out.println(iRecordingDescriptor.getId());

        Map<String, String> eventSettings = recordService.getEventSettings(iRecordingDescriptor);
        Map<String, String> recordingOptions = recordService.getRecordingOptions(iRecordingDescriptor);

        System.out.println(eventSettings);
        System.out.println(recordingOptions);

        recordService.updateEventOptions(iRecordingDescriptor, settings);

        recordService.start(iRecordingDescriptor, recordingOptions, eventSettings);

        Map<String, String> eventSettingsNew = recordService.getEventSettings(iRecordingDescriptor);
        System.out.println(eventSettingsNew);

        Map<String, String> settingsNew = new HashMap<>(1);
        settingsNew.put("jdk.ExecutionSample#period", "20 ms");

        Thread.sleep(2000);

        recordService.updateEventOptions(iRecordingDescriptor, settingsNew);

        Map<String, String> eventSettingsNewModify = recordService.getEventSettings(iRecordingDescriptor);
        System.out.println(eventSettingsNewModify);

        Thread.sleep(2000);

    }

    public static void main(String[] args) throws Exception{

        IJavaFlightRecordService recordService = new JavaFlightRecordServiceImpl();

        List<IRecordingDescriptor> availableRecordings = recordService.getAvailableRecordings();

        IRecordingDescriptor iRecordingDescriptor = availableRecordings.get(0);
        System.out.println(iRecordingDescriptor.getId());

        byte[] inputStream = recordService.openStream(iRecordingDescriptor, true);

//            GZIPInputStream gzipInputStream = new GZIPInputStream(streams);

        Path path = Paths.get("/Users/wzj/workspace/my_github/jvm_study/src/main/java/com/study/attach/temp.jfr");

        FileOutputStream fileOutputStream = new FileOutputStream(path.toFile());

        int len = inputStream.length;
        try {
            fileOutputStream.write(inputStream, 0, len);
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}

package com.study.attach.integration;

import sun.tools.jconsole.LocalVirtualMachine;

import javax.management.JMException;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * @author wzj
 * @date 2022/06/10
 */
public class FlightRecorderExecutor {
    private  MBeanServerConnection mbeanServer;
    private  ObjectName jfr2MBeanObjectName;
    private  JMXConnector jmxConnector;

    private final static String JFR2_MBEAN_OBJECT_NAME = "jdk.management.jfr:type=FlightRecorder";

    public static FlightRecorderExecutor INSTANCE = new FlightRecorderExecutor();

    private FlightRecorderExecutor() {
        // 暂时没有考虑业务容器重启的场景
        this.initMBeanServer();
    }
    public void closeJmxConnector(){
        // connect close
        try {
            this.jmxConnector.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public Object getJfrAttribute(String attribute) throws JMException, IOException {
        return this.mbeanServer.getAttribute(this.jfr2MBeanObjectName, attribute);
    }

    public Object invokeJFROperation(String operation, Object params[], String signature[] ){
        try {
            return this.mbeanServer.invoke(jfr2MBeanObjectName, operation, params, signature);
        }catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }


    private void initMBeanServer(){
        Map<Integer, LocalVirtualMachine> allVirtualMachines = LocalVirtualMachine.getAllVirtualMachines();
        // fixme 需要想办法找到业务自己的容器的名称
        Optional<LocalVirtualMachine> applicationOptional = allVirtualMachines.entrySet().stream()
                .filter(kv -> kv.getValue().displayName().contains("com.alibaba.awp.Application"))
                .map(kv -> kv.getValue())
                .findAny();
        try {
            LocalVirtualMachine application = applicationOptional.get();
            JMXServiceURL serviceUrl = new JMXServiceURL(application.connectorAddress());
            this.jmxConnector = JMXConnectorFactory.connect(serviceUrl, null);
            this.mbeanServer = this.jmxConnector.getMBeanServerConnection();
            this.jfr2MBeanObjectName = new ObjectName(JFR2_MBEAN_OBJECT_NAME);
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}

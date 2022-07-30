package com.study.practice.remoteExecutor;

import java.lang.reflect.Method;

/**
 * Javaclass 执行工具
 *
 * @author wzj
 * @date 2022/05/26
 */
public class JavaclassExecuter {

    /**
     * 执行外部传过来的代表一个Java 类的Byte数组
     * 将输入类的byte数组中代表 java.lang.System 的CONSTANT_Utf8_info 常量修改为劫持后的HackSystem类
     * 执行方法为该类的static main(String[] args)方法， 输出结果为该类向 System.out/err 输出信息
     *
     * @param classBytes 代表一个Java类的Byte数组
     * @return 执行结果
     */
    public static String execute(byte[] classBytes){
        HackSystem.clearBuffer();
        ClassModifier cm = new ClassModifier(classBytes);
        byte[] modiBytes = cm.modifyUTF8Constant("java/lang/System", "com/study/practice/remoteExecutor/HackSystem");
        HotSwapClassLoader hotSwapClassLoader = new HotSwapClassLoader();
        Class aClass = hotSwapClassLoader.loadByte(modiBytes);
        try{
            // 这个main方法是 传入的类的
            Method method = aClass.getMethod("main", new Class[]{String[].class});
            method.invoke(null, new String[]{null});
        }catch (Exception e){
            e.printStackTrace(HackSystem.out);
        }
        return HackSystem.getBufferString();
    }
}

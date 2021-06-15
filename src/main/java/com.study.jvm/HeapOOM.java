package com.study.jvm;

import java.util.ArrayList;
import java.util.List;

/**
 * vm args: -Xms20m -Xms20m -XX:+HeapDumpOnOutOfMemoryError
 * @author wzj
 * @date 2021/06/15
 */
public class HeapOOM {
    static class OOMObject{

    }

    public static void main(String[] args) {
        List<OOMObject> list = new ArrayList<>();
        while (true){
            list.add(new OOMObject());
        }
    }
}

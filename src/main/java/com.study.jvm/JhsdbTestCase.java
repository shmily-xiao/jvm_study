package com.study.jvm;

/**
 * @author wzj
 * @date 2021/06/23
 */
public class JhsdbTestCase {
    static class Test{
        static ObjectHolder staticObj = new ObjectHolder();
        ObjectHolder instanceObj = new ObjectHolder();

        public void foo(){
            ObjectHolder localObj =  new ObjectHolder();
            System.out.println("done");
        }
    }

    private static class ObjectHolder{}

    public static void main(String[] args) {
        Test test = new Test();
        test.foo();
    }
}

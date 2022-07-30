package com.study.practice.ast;

/**
 * @author wzj
 * @date 2022/06/06
 */
public class Test {

    public static void main(String[] args) {
        String string1 = "a" + "b" + "c";

        String a = "a";
        String b = "b";
        String c = "c";
        String string2 = a + b + c;

        String string3 = "abc";

        int i = 10 + 2+ 3;
        int ia= 10;
        int ib = 2;
        int ic = 3;
        int ii = ia + ib + ic;

        System.out.println(string1 == string2);
        System.out.println(string3 == string1);
        System.out.println(i == ii);
    }
}

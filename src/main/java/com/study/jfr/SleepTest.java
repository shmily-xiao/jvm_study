package com.study.jfr;

import java.util.ArrayList;
import java.util.List;

public class SleepTest {
    public static void main(String[] args) {
        List<Object> items = new ArrayList<>(1);
        try {
            while (true){
                items.add(new Object());
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    System.out.println(e.getMessage());
                }
            }
        } catch (OutOfMemoryError e){
            System.out.println(e.getMessage());
        }
        assert items.size() > 0;
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }
    }
}

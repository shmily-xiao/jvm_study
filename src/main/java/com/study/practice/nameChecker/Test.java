package com.study.practice.nameChecker;

import javax.tools.ToolProvider;

/**
 *
 *
 * javac -cp $JAVA_HOME/lib/tools.jar com/study/practice/nameChecker/WzjGetter.java com/study/practice/nameChecker/MyGetterProcessor.java
 * javac -processor com.study.practice.nameChecker.MyGetterProcessor com/study/practice/nameChecker/Test.java
 *
 *
 * @author wzj
 * @date 2022/06/01
 */
@WzjGetter
public class Test {

    private String wzj;

    public static void main(String[] args) {
        javax.tools.JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
//        int results = compiler.run(null, null, null, new String[] {
//                "-processor", "com.study.practice.nameChecker.NameCheckProcessor",
//                "/Users/wzj/workspace/my_github/jvm_study/src/main/java/com/study/practice/nameChecker/Test_Name_Checker.java"
//
//        });
//
        int results = compiler.run(null, null, null, new String[] {
                "-processor", "com.study.practice.nameChecker.MyGetterProcessor",
                "/Users/wzj/workspace/my_github/jvm_study/src/main/java/com/study/practice/ast/Test.java"
        });

        System.out.println(results);
    }
}

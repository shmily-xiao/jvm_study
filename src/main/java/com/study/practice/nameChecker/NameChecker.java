package com.study.practice.nameChecker;

import com.alibaba.fastjson.JSON;

import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.*;
import javax.lang.model.util.ElementScanner8;
import javax.tools.Diagnostic;
import java.util.EnumSet;

/**
 * @author wzj
 * @date 2022/05/31
 */
public class NameChecker {

    private final Messager messager;

    NameCheckScanner nameCheckScanner = new NameCheckScanner();


    NameChecker(ProcessingEnvironment environment) {
        this.messager = environment.getMessager();
    }

    /**
     * 名字命名规范：
     * 1。 类和接口：符合驼峰命名方式，首字母大写。
     * 2。 方法：符合驼峰命名方式，首字母小写。
     * 3。 字段：
     * 类、实例变量：符合驼峰命名方式，首字母小写
     * 常量：全部要求大写。
     *
     * @param element
     */
    public void checkNames(Element element) {
        // 把元素给到scanner去检查
        nameCheckScanner.scan(element);
    }

    /**
     * 名称检查实现类，继承了JDK 8 中新提供的ElementScanner8<br>
     * 将会以Visitor模式访问抽象语法树中的元素
     */
    private class NameCheckScanner extends ElementScanner8<Void, Void> {

        /**
         * 此方法用于检查JAVA类
         *
         * @param e
         * @param p
         * @return
         */
        @Override
        public Void visitType(TypeElement e, Void p) {
            ElementKind kind = e.getKind();// kind: --> CLASS、ENUM
            System.out.println("visitType: " + e.toString());

            super.scan(e.getTypeParameters(), p);
            this.checkCamelCase(e, true);
            super.visitType(e, p);
            return null;
        }

        /**
         * 检查方法命名是否合法
         *
         * @param e
         * @param p
         * @return
         */
        @Override
        public Void visitExecutable(ExecutableElement e, Void p) {
            System.out.println("visitExecutable: " + e.toString());
            if (e.getKind() == ElementKind.METHOD) {
                Name simpleName = e.getSimpleName();
                if (simpleName.contentEquals(e.getEnclosingElement().getSimpleName())) {
                    messager.printMessage(Diagnostic.Kind.WARNING, "普通方法 '" + simpleName + "' 不应当与类名重复，避免与构造方法产生混淆", e);
                }
                checkCamelCase(e, false);
            }
            super.visitExecutable(e, p);
            return null;
        }

        /**
         * 检查常量或者是枚举
         *
         * @param e
         * @param p
         * @return
         */
        @Override
        public Void visitVariable(VariableElement e, Void p) {
            System.out.println("visitVariable: " + e.toString());

            // 如果这个Variable是枚举或者是常量，则按照大写命名检查，否则按照驼峰式命名法则进行检查
            if (e.getKind() == ElementKind.ENUM_CONSTANT || e.getConstantValue() != null || this.heuristicallyConstant(e)) {
                this.checkAllCaps(e);
            } else {
                checkCamelCase(e, false);
            }
            return null;
        }

        /**
         * 判断一个变量是不是常量
         *
         * @param e
         * @return
         */
        private boolean heuristicallyConstant(VariableElement e) {
            // 获取这个变量上一层级的对象，如果上一层级的对象是接口，那么这个变量一定是一个常量
            if (e.getEnclosingElement().getKind() == ElementKind.INTERFACE) {
                return true;
                // 如果当前变量所在层级是属性域，那么当前变量里面必须有public、static、final的修饰符
            } else if (e.getKind() == ElementKind.FIELD && e.getModifiers().containsAll(EnumSet.of(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL))) {
                return true;
            } else {
                return false;
            }
        }

        /**
         * 检查传入的Element是否驼峰式命名，如果不符合，则输出警告信息
         *
         * @param e
         * @param b
         */
        private void checkCamelCase(Element e, boolean b) {
            String name = e.getSimpleName().toString();
            boolean previousUpper = false;
            boolean conventional = true; // 常规的，符合规定的
            int firstCodePoint = name.codePointAt(0);// 得到首字母的ASCII的值

            if (Character.isUpperCase(firstCodePoint)) {
                // 说明首字母是大写的
                previousUpper = true;
                if (!b) {
                    // 如果b 是true，说明首字母需要大写， 如果是 false，首字母不能是大写
                    // 说明不符合需求
                    messager.printMessage(Diagnostic.Kind.WARNING, "名称 '" + name + "' 应当以小写字母开头", e);
                    return;
                }
            } else if (Character.isLowerCase(firstCodePoint)) {
                if (b) {
                    // 说明需要大写了，但是首字母却没有大写
                    messager.printMessage(Diagnostic.Kind.WARNING, "名称 '" + name + "' 应当以大写字母开头", e);
                    return;
                }
            } else {
                // 说明既不是大写，也不是小写，说明是异常的
                conventional = false;
            }
            if (conventional) {
                int cp = firstCodePoint;
                // Character.charCount(cp) : 意思是判断cp是否是大于10000，如果是，就是2，否则就是 1
                // 正常的英文字母都是1，可以对照ASCII来比对
                // 目前可以认为是 1
                for (int i = Character.charCount(cp); i < name.length(); i += Character.charCount(cp)) {
                    // name.codePointAt(i) ：得到指定index的字母的ASCII码
                    cp = name.codePointAt(i);
                    // 通过ASCII来判断字符串是否是大写字母
                    if (Character.isUpperCase(cp)) {
                        if (previousUpper) {
                            // 如果cp是大写且上一个字母还是是大写的，就说明有问题
                            // 比如"ABc、cBD"这样的字符串认为是不合法的
                            conventional = false;
                            break;
                        }
                        previousUpper = true;
                    } else {
                        previousUpper = false;
                    }
                }
            }
            if (!conventional) {
                // 如果不合规定就爆出错误
                messager.printMessage(Diagnostic.Kind.WARNING, "名称 '" + name + "' 应当符合驼峰式命名法则", e);
            }
        }

        /**
         * 大写命名检查，要求第一个字母必须是大写的英文字母，其余部分可以是下划线或大写字母
         *
         * @param e
         */
        private void checkAllCaps(VariableElement e) {
            String name = e.getSimpleName().toString();

            boolean conventional = true;
            // 获取首字母
            int firstCodePoint = name.codePointAt(0);

            // 检查首字母是不是大写
            if (!Character.isUpperCase(firstCodePoint)) {
                // 如果不是就不符合规范
                conventional = false;
            } else {
                // 前一个字符串是不是下划线
                boolean previousUnderscore = false;
                int cp = firstCodePoint;

                for (int i = Character.charCount(cp); i < name.length(); i += Character.charCount(cp)) {
                    cp = name.codePointAt(i);
                    // 判断当前这个字符是不是下划线
                    if (cp == (int) '_') {
                        // 如果上一个字符也是下划线，说明不符合规范 例如："__xx"
                        if (previousUnderscore) {
                            conventional = false;
                            break;
                        }
                        // 如果不是那就是正常，需要判断后面的字符
                        previousUnderscore = true;
                    } else {
                        previousUnderscore = false;
                        // Character.isDigit(cp) 判断字符是不是数字
                        // 判断当前字符是不是大写和是不是数字，如果不是大写且不是数字的话就是不符合规范的
                        if (!Character.isUpperCase(cp) && !Character.isDigit(cp)) {
                            conventional = false;
                            break;
                        }
                    }
                }
            }
            if (!conventional) {
                messager.printMessage(Diagnostic.Kind.WARNING, "常量 '" + name + "' 应当全部以大写字母或者下划命名，并且以字母开头", e);
            }
        }

    }
}

package com.study.practice.nameChecker;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.util.Set;

/**
 * SupportedAnnotationTypes("*") 代表了这个注解处理器对哪些注解感兴趣，可以使用星号"*"作为通配符代表对所有的注解感兴趣
 * SupportedSourceVersion 表示这个注解处理器可以处理哪些版本的Java代码
 * @author wzj
 * @date 2022/05/31
 */
@SupportedAnnotationTypes("*")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class NameCheckProcessor extends AbstractProcessor {

    private NameChecker nameChecker;
    /**
     * 初始化名称检查插件
     * @param processingEnv
     */
    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        this.nameChecker = new NameChecker(processingEnv);
    }

    /**
     * 对输入的语法树的各个节点进行名称检查
     *
     * @param annotations
     * @param roundEnv
     * @return
     */
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        System.out.println("wzj name check is running");

        // 如果本轮生成的类型将不受后续一轮注释处理的影响，则返回true;否则返回false。
        if (!roundEnv.processingOver()){
            for (Element element: roundEnv.getRootElements()){
                nameChecker.checkNames(element);
            }
        }
        // 因为只是涉及到文件的命名，并不需要修改语法树的内容，所以方法目前都是返回false
        return false;
    }
}

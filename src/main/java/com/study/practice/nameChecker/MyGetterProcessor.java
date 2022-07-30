package com.study.practice.nameChecker;

import com.sun.source.tree.Tree;
import com.sun.tools.javac.api.JavacTrees;
import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.tree.TreeTranslator;
import com.sun.tools.javac.util.*;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.util.Set;

/**
 * JCStatement：声明语法树节点，常见的子类如下
 *      JCBlock：语句块语法树节点
 *      JCReturn：return语句语法树节点
 *      JCClassDecl：类定义语法树节点
 *      JCVariableDecl：字段/变量定义语法树节点
 * JCMethodDecl：方法定义语法树节点
 * JCModifiers：访问标志语法树节点
 * JCExpression：表达式语法树节点，常见的子类如下
 *      JCAssign：赋值语句语法树节点
 *      JCIdent：标识符语法树节点，可以是变量，类型，关键字等等
 *
 * @author wzj
 * @date 2022/06/01
 */
@SupportedAnnotationTypes("com.study.practice.nameChecker.WzjGetter")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class MyGetterProcessor extends AbstractProcessor {

    // 主要是输出信息
    private Messager messager;

    private JavacTrees javacTrees;

    private TreeMaker treeMaker;

    private Names names;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        this.messager = processingEnv.getMessager();
        this.javacTrees = JavacTrees.instance(processingEnv);
        Context context = ((JavacProcessingEnvironment)processingEnv).getContext();
        this.treeMaker = TreeMaker.instance(context);
        this.names = Names.instance(context);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        System.out.println("process 1");
        Set<? extends Element> elementsAnnotatedWith = roundEnv.getElementsAnnotatedWith(WzjGetter.class);
        elementsAnnotatedWith.forEach(element -> {
            System.out.println("process 2");
            JCTree tree = javacTrees.getTree(element);
            tree.accept(new TreeTranslator(){
                @Override
                public void visitClassDef(JCTree.JCClassDecl jcClassDecl) {
                    List<JCTree.JCVariableDecl> jcVariableDeclList = List.nil();

                    // 在抽象树中找出所有的变量
                    for(JCTree jcTree: jcClassDecl.defs){
                        if (jcTree.getKind().equals(Tree.Kind.VARIABLE)){
                            JCTree.JCVariableDecl jcVariableDecl = (JCTree.JCVariableDecl)jcTree;
                            jcVariableDeclList = jcVariableDeclList.append(jcVariableDecl);
                        }
                    }

                    // 对于变量进行生成方法的操作
                    for (JCTree.JCVariableDecl jcVariableDecl : jcVariableDeclList) {
                        messager.printMessage(Diagnostic.Kind.NOTE, jcVariableDecl.getName() + " has been processed");
                        jcClassDecl.defs = jcClassDecl.defs.prepend(makeSetterMethodDecl(jcVariableDecl));
                        jcClassDecl.defs = jcClassDecl.defs.prepend(makeGetterMethodDecl(jcVariableDecl));
                    }

                    super.visitClassDef(jcClassDecl);
                }

            });

        });

        return true;
    }

    // 添加一个setter方法
    private JCTree.JCMethodDecl makeSetterMethodDecl(JCTree.JCVariableDecl jcVariableDecl) {
        System.out.println("makeSetterMethodDecl 1");
        ListBuffer<JCTree.JCStatement> statements = new ListBuffer<>();
        // 生成表达式 例如 this.a = a;
        JCTree.JCExpressionStatement aThis = makeAssignment(treeMaker.Select(treeMaker.Ident(names.fromString("this")), jcVariableDecl.getName()), treeMaker.Ident(jcVariableDecl.getName()));
        statements.append(aThis);
        JCTree.JCBlock block = treeMaker.Block(0, statements.toList());

        // 生成入参
        JCTree.JCVariableDecl param = treeMaker.VarDef(treeMaker.Modifiers(Flags.PARAMETER), jcVariableDecl.getName(), jcVariableDecl.vartype, null);
        List<JCTree.JCVariableDecl> parameters = List.of(param);

        // 生成返回对象
        JCTree.JCExpression methodType = treeMaker.Type(new Type.JCVoidType());

        return treeMaker.MethodDef(treeMaker.Modifiers(Flags.PUBLIC), getNewSetterMethodName(jcVariableDecl.getName()), methodType, List.nil(), parameters, List.nil(), block, null);
    }

    private JCTree.JCMethodDecl makeGetterMethodDecl(JCTree.JCVariableDecl jcVariableDecl){
        System.out.println("makeGetterMethodDecl 1");
        ListBuffer<JCTree.JCStatement> statements = new ListBuffer<>();
        // 生成表达式
        JCTree.JCReturn aReturn = treeMaker.Return(treeMaker.Ident(jcVariableDecl.getName()));
        statements.append(aReturn);
        JCTree.JCBlock block = treeMaker.Block(0, statements.toList());

        // 无入参

        // 生成返回对象
        JCTree.JCExpression returnType = treeMaker.Type(jcVariableDecl.getType().type);

        return treeMaker.MethodDef(treeMaker.Modifiers(Flags.PUBLIC), getNewGetterMethodName(jcVariableDecl.getName()), returnType, List.nil(), List.nil(), List.nil(), block, null);
    }

    // 拼装Setter方法名称字符串
    private Name getNewSetterMethodName(Name name) {
        System.out.println("getNewSetterMethodName 1");
        String s = name.toString();
        return names.fromString("set" + s.substring(0,1).toUpperCase() + s.substring(1, name.length()));
    }

    //拼装 Getter 方法名称的字符串
    private Name getNewGetterMethodName(Name name) {
        System.out.println("getNewGetterMethodName 1");
        String s = name.toString();
        return names.fromString("get" + s.substring(0,1).toUpperCase() + s.substring(1, name.length()));
    }

    // 生成表达式
    private JCTree.JCExpressionStatement makeAssignment(JCTree.JCExpression lhs, JCTree.JCExpression rhs) {
        return treeMaker.Exec(
                treeMaker.Assign(lhs, rhs)
        );
    }


}

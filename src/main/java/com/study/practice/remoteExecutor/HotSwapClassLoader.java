package com.study.practice.remoteExecutor;

/**
 * 为了多次载入执行类而加入的加载器
 * 把defineClass方法开放出来，只有外部都显示调用的时候才会使用到loadByte方法
 * 由虚拟机调用时，仍然按照原有的双亲委派规则使用loadclass方法进行类加载
 *
 *
 * @author wzj
 * @date 2022/05/26
 */
public class HotSwapClassLoader extends ClassLoader{

    public HotSwapClassLoader(){
        super(HotSwapClassLoader.class.getClassLoader());
    }

    /**
     * 我们可以远程导入class文件，然后让classloader加载
     *
     * @param classByte
     * @return
     */
    public Class loadByte(byte[] classByte){
        return defineClass(null, classByte, 0, classByte.length);
    }

}

package com.study.practice.remoteExecutor;

/**
 * 修改Class文件，暂时只提供修改常量池常量的功能
 * @author wzj
 * @date 2022/05/26
 */
public class ClassModifier {
    /**
     * Class 文件中常量池的起始偏移
     */
    private static final int CONSTANT_POOL_COUNT_INDEX = 8;

    /**
     * CONSTANT_Utf8_info常量的tag标志
     */
    private static final int CONSTANT_Utf8_info = 1;

    /**
     * 常量池中11种常量所占的长度，CONSTANT_Utf8_info型常量除外，因为让不是定长的
     *
     *  0   1   2  3  4  5  6  7  8  9  10  11  12      <--    脚标是常量类型的TAG值
     * -1, -1, -1, 5, 5, 9, 9, 3, 3, 5, 5,  5,  5       <--    常量的长度
     *
     * 因为常量的值是从1 开始 且没有2 ，所以前三个都是 -1 ，表示不定长
     */
    private static final int[] CONSTANT_ITEM_LENGTH = {-1, -1, -1, 5, 5, 9, 9, 3, 3, 5, 5, 5, 5 };

    private static final int u1 = 1;

    private static final int u2 = 2;

    private byte[] classByte;

    public ClassModifier(byte[] classByte){
        this.classByte = classByte;
    }

    /**
     * 修改常量池中 CONSTANT_Itf8_info 常量的内容
     * @param oldStr 修改前的字符
     * @param newStr 修改后的字符
     * @return 修改结果
     */
    public byte[] modifyUTF8Constant(String oldStr, String newStr){
        int cpc = getConstantPoolCount();
        int offset = CONSTANT_POOL_COUNT_INDEX + u2;
        for (int i = 0; i < cpc; i++) {
            // 获取常量的类型
            int tag = ByteUtils.bytes2Int(classByte, offset, u1);
            // 解析 CONSTANT_Itf8_info 的数据结构
            if (tag == CONSTANT_Utf8_info) {
                int len = ByteUtils.bytes2Int(classByte, offset + u1, u2);
                offset += (u1 + u2);
                String str = ByteUtils.bytes2String(classByte, offset, len);
                if (str.equalsIgnoreCase(oldStr)){
                    byte[] strBytes = ByteUtils.string2Bytes(newStr);
                    byte[] strLen = ByteUtils.int2Bytes(newStr.length(), u2);
                    // 覆盖长度
                    classByte = ByteUtils.bytesReplace(classByte, offset - u2, u2, strLen);
                    // 覆盖内容
                    classByte = ByteUtils.bytesReplace(classByte, offset, len, strBytes);
                    return classByte;
                }else {
                    offset += len;
                }
            } else {
                offset += CONSTANT_ITEM_LENGTH[tag];
            }
        }
        return classByte;
    }

    /**
     * 获取常量池中常量的数量
     *
     * 偏移8个，然后后面2个数组成的 0x0016 就是常量池的长度信息
     *
     * @return 常量池数量
     */
    public int getConstantPoolCount(){
        return ByteUtils.bytes2Int(classByte, CONSTANT_POOL_COUNT_INDEX, u2);
    }
}

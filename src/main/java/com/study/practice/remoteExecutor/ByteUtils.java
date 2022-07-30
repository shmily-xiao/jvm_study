package com.study.practice.remoteExecutor;

/**
 * Bytes 数组处理工具
 *
 * @author wzj
 * @date 2022/05/26
 */
public class ByteUtils {

    /**
     *
     * @param b      字节数组
     * @param start  开始
     * @param len    长度
     * @return       选取的字节的int值
     */
    public static int bytes2Int(byte[] b, int start, int len){
        int sum = 0;
        int end = start + len;
        for (int i = start; i < end; i++){
            // 0xff   :   1111 1111
            // 与操作  保留 1 的位
            int n = ((int) b[i]) & 0xff;
            // 移位
            // len = len - 1   -1是因为它本身就占了8位了
            // n = n << (len*8)
            n <<= (--len) * 8;
            sum = n + sum;
        }
        return sum;
    }

    public static String bytes2String(byte[] b, int start, int len){
        return new String(b, start, len);
    }

    public static byte[] string2Bytes(String newStr) {
        return newStr.getBytes();
    }

    public static byte[] int2Bytes(int value, int length) {
        byte[] b = new byte[length];
        for (int i = 0; i < length; i++) {
            b[i] = (byte)((value >> ((length-i-1)*8)) & 0xff);
            // b[len -i -1] = (byte)((value >> 8*i) & 0xff);
        }
        return b;
    }

    public static byte[] bytesReplace(byte[] originalBytes, int offset, int len, byte[] replaceBytes) {
        byte[] newBytes = new byte[originalBytes.length + (replaceBytes.length - len)];
        // 复制原数据到新的byte数组的前半部分
        System.arraycopy(originalBytes, 0, newBytes, 0, offset);
        // 复制replace的数据到中段部分
        System.arraycopy(replaceBytes, 0, newBytes, offset, replaceBytes.length);
        // 复制原始数据的后面部分到newBytes后半部分
        System.arraycopy(originalBytes, offset+len, newBytes, offset + replaceBytes.length, originalBytes.length - len - offset);
        return newBytes;
    }
}

package util;



import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class BaseUtil {

    private BaseUtil(){}


    /**
     * 16进制的字符串表示转成字节数组
     *
     * @param hexString 16进制格式的字符串
     * @return 转换后的字节数组
     **/
    public static byte[] toByteArray(String hexString) {
        if (hexString==null||"".equals(hexString))
            throw new IllegalArgumentException("this hexString must not be empty");
        hexString = hexString.toLowerCase();
        final byte[] byteArray = new byte[hexString.length() / 2];
        int k = 0;
        for (int i = 0; i < byteArray.length; i++) {//因为是16进制，最多只会占用4位，转换成字节需要两个16进制的字符，高位在先
            byte high = (byte) (Character.digit(hexString.charAt(k), 16) & 0xff);
            byte low = (byte) (Character.digit(hexString.charAt(k + 1), 16) & 0xff);
            byteArray[i] = (byte) (high << 4 | low);
            k += 2;
        }
        return byteArray;
    }


    public static String encodeHexStr(int b) {
        return Integer.toHexString(0xFF&b);
    }

    /**
     * 将一个数字转换为二进制的数组表现形式 如：8------00001000
     * @param n
     * @return
     */
    public static byte[] byte2CharArr(int n){
        char[] cs = Integer.toBinaryString(n).toCharArray();
        byte[] bs=new byte[8];
        //1000
        for(int i=cs.length-1,j=0;i>=0;i--,j++){
            bs[j]= (byte) ((cs[i]=='1')?1:0);
        }
        for(int i=0,j=7;i<j;i++,j--){
            byte b;
            b=bs[i];
            bs[i]=bs[j];
            bs[j]=b;
        }
        return bs;
    }


    public static int getInt(byte[] bytes) {
        return (bytes[0]  << 8)           | //还原int值最高8位
                ((bytes[1] & 0xff) ); //还原int值接下来的8位
                /*((bytes[2] & 0xff) << 8 ) |//还原int值再接下来的8位
                (bytes[3] & 0xff); */        //还原int值的最低8位
    }

    public static void main(String[] args) {
        byte[] bs={4,13};
        System.out.println(getInt(bs));
    }
}



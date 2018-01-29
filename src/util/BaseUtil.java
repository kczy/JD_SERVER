package util;



import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
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

        String pck=b>15?Integer.toString(b,16):"0"+Integer.toString(b,16);
        return pck;
    }

    public static String encodeHexStr2(int b){
        b=b&0xff;
        String pck=b>15?Integer.toString(b,16):"0"+Integer.toString(b,16);
        return pck;
    }
    public static String byteToHexString(byte b){
        StringBuilder stringBuilder = new StringBuilder("");
        int v = b & 0xFF;
        String hv = Integer.toHexString(v);
        if (hv.length() < 2) {
            stringBuilder.append(0);
        }
        stringBuilder.append(hv);
        return stringBuilder.toString();
    }
    public static void main(String[] args) {
        byte[] bts={(byte) 278};
        System.out.println(bts[0]);

        System.out.println(byteToHexString(bts[0]));
        System.out.println(encodeHexStr2(bts[0]));
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





    public static byte[] doListTransByte(List<Byte> list) {
        byte[] bts=new byte[list.size()];
        for(int i=0;i<bts.length;i++){
            bts[i]=list.get(i);
        }
        return bts;
    }

    public static List<Byte> doByteTransList(byte[] bts){
        List<Byte> list=new ArrayList<Byte>();
        for(byte b:bts){
            list.add(b);
        }
        return list;
    }



    public static String intTranHexStr(int i){

        String  num=Integer.toHexString(i);
        return num;
    }
}



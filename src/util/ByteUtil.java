package util;

import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.Date;

public class ByteUtil {
    private static final String CHARSET_NAME = "UTF-8";

    /**
     * byte转16进制字符串
     * @param bArray
     * @return
     */
    public static String bytesToHexString(byte[] bArray) {

        StringBuffer sb = new StringBuffer(bArray.length);
        String sTemp;
        for (int i = 0; i < bArray.length; i++) {
            sTemp = Integer.toHexString(bArray[i] & 0xFF);
            if (sTemp.length() < 2) {
                sb.append(0);
            }
            sb.append(sTemp.toUpperCase());
        }
        return sb.toString();
    }

    /**
     * 16进制字符串转byte
     * @param hexString
     * @return
     */
    public static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.toUpperCase();
        hexString = hexString.replaceAll("0X", "");
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }

    /**
     * 获取 char对应的byte
     * @param c
     * @return
     */
    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    /**
     * int 转byte数组
     * @param num               int 型数字
     * @param byteLength       生成数组长度
     * @return                  生成的byte数组，低位在前，高位在后
     */
    public static byte[] intToByteArray(int num, int byteLength) {

        byte[] bLocalArr = new byte[byteLength];
        for (int i = 0; i < byteLength; i++) {
            bLocalArr[i] = (byte) (num >> 8 * i & 0xFF);
        }
        return bLocalArr;
    }

    public static int byteArrayToInt(byte[] byteArr) {
        int value= 0;
        for (int i = 0; i < byteArr.length; i++) {
            int shift= i * 8;
            value += (byteArr[i] & 0x000000FF) << shift;
        }
        return value;
    }
    public static String bytesToHexs(byte[] buffer) {
        StringBuffer sb = new StringBuffer(buffer.length * 2);
        for (int i = 0; i < buffer.length; i++) {
            sb.append(" 0x"+Character.forDigit((buffer[i] & 240) >> 4, 16));
            sb.append(Character.forDigit(buffer[i] & 15, 16));
        }
        return sb.toString();
    }
    public static String byteToUtf8String(byte[] byteArr) {
        String str = null;
        try {
            str = new String(byteArr, CHARSET_NAME);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return str;
    }

    public static byte[] utf8StringToByte(String str) {

        if (str == null) {
            return null;
        }

        try {
            return str.getBytes(CHARSET_NAME);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static byte[] timeToByte(Date date) {

        if (date == null) {
            return null;
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        byte[] yearArr = ByteUtil.intToByteArray(year, 2);
        byte[] monthArr = ByteUtil.intToByteArray(month, 1);
        byte[] dayArr = ByteUtil.intToByteArray(day, 1);

        byte[] resultArr = new byte[4];

        System.arraycopy(dayArr, 0, resultArr, 0, dayArr.length);
        System.arraycopy(monthArr, 0, resultArr, 1, monthArr.length);
        System.arraycopy(yearArr, 0, resultArr, 2, yearArr.length);

        return resultArr;
    }

    public static void main(String[] args) {
        byte[] bs = timeToByte(new Date());
        String str = ByteUtil.bytesToHexString(bs);
        System.out.println(str);
        for (byte b : bs) {
            System.out.println((b));
        }
    }
}

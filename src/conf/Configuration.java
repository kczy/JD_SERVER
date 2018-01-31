package conf;

public class Configuration {
    public static final int PORT=10051;

    public static final int BUFFER_SIZE=2048;


    /**
     * 本地上报
     */
    public static  String DEVICE_LIST_LOCAL="http://127.0.0.1:9080/device/elder/security/security-device-jiade-impl";

    /**
     * 上报阿里云
     */
    public static  String DEVICE_LIST_ALIYUN="http://120.77.215.202:10055/device/elder/security/security-device-jiade-impl";

    /**
     * 上报李宾本地
     */
    public static  String DEVICE_LIST_LIBIN="http://lib18610386362.oicp.net/icare-device-api/device/elder/security/security-device-jiade-impl";

    /**
     * 通用
     */
    public static String DEVICE_LIST=DEVICE_LIST_LIBIN;


}

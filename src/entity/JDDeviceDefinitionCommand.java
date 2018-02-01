package entity;

/**
 * 使用byte数组，
 * 避免使用字符串作为命令，
 * 节约字符串转化byte数组的计算开销
 * 节约静态区字符串堆积过多
 */
public class JDDeviceDefinitionCommand {

    /**
     * 用于心跳通信，维持链路,FF FF 00 05 07 00 0000 00
     */
    public static byte[] HEARTBEAT={
            (byte)0xFF, (byte)0xFF,//包头
            (byte)0x00,(byte)0x05,//包长度
            (byte)0x07,//命令
            (byte)0x00,//包序号
            (byte)0x00,(byte)0x00,//flags
            (byte)0x00//校验和
    };

    /**
     *下发查询设备网关序列号
     *sendGataway(session, "FFFF 0005 01 00 0000 06");
     */
     public static byte[] GET_GATEWAY_ID={
            (byte)0xFF, (byte)0xFF,         //包头
            (byte)0x00,(byte)0x05,          //包长度
            (byte)0x01,                     //命令
            (byte)0x00,                     //包序号
            (byte)0x00,(byte)0x00,          //flags
            (byte)0x06                      //校验和
    };

    /**
     *扫描设备列表
     *sendGataway(session, "FFFF 0008 03 24 0000 050301 38");
     */
    public static byte[] SCAN_DEVICES_LIST={
            (byte)0xFF, (byte)0xFF, //包头
            (byte)0x00,(byte)0x08,  //包长度
            (byte)0x03,                   //命令
            (byte)0x24,
            (byte)0x00,(byte)0x00,
            (byte)0x05,(byte)0x03,(byte)0x01,(byte)0x38
    };

    /**
     * zigbee设备开网-添加子设备
     * "FFFF
     * 0010
     * 03
     * 22
     * 0000
     * 01 00
     * 0202
     * 00 00 00 00 00 00 00
     * 3A"
     */
    public static byte[] INSERT_DEVICE_INTO_ZIGBEE={
            (byte)0xFF, (byte)0xFF, //包头
            (byte)0x00,(byte)0x10,  //包长度
            (byte)0x03,
            (byte)0x22,
            (byte)0x00,(byte)0x00,
            (byte)0x01,(byte)0x00,
            (byte)0x02,(byte)0x02,
            (byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,
            (byte)0x3A
    };


    /**
     * APP后端需要验证登录和添加设备等操作
     * 此处自定义回复APP，FFFF0003010000
     */
    public static byte[] ANSWER_APPSERVER_OK={
            (byte)0xFF, (byte)0xFF, //包头
            (byte)0x00,(byte)0x03,  //包长度
            0x01,                   //命令
            0x00,
            0x00,
    };
    /**
     * APP后端需要验证登录和添加设备等操作
     * 此处自定义回复APP，FFFF0003000000
     */
    public static byte[] ANSWER_APPSERVER_FAILED={
            (byte)0xFF, (byte)0xFF, //包头
            (byte)0x00,(byte)0x03,  //包长度
            (byte)0x00,             //命令
            (byte)0x00,
            (byte)0x00,
    };

    /*
    * 回复网关收到了
    * FFFF000506" + BaseUtil.encodeHexStr(bts[5]) + "00000C
    * */
    public static byte[] ANSWER_GATEWAY_ACK={
            (byte)0xFF, (byte)0xFF, //包头
            (byte)0x00,(byte)0x05,  //包长度
            (byte)0x06,             //命令
            (byte)0x00,                   //回复包序号，动态添加
            (byte)0x00,(byte)0x00,
            (byte)0x0C                    //校验和
    };


    /**
     * 打开凤鸣
     */
    public static byte[] CONTROL_GATEWAY_HOWL={
            (byte)0xFF,(byte)0xFF,//包头
            (byte)0x00,(byte)0x10,      //包长度
            (byte)0x03,                 //命令
            (byte)0x00,                 //包序号
            (byte)0x00,(byte)0x00,      //flags
            (byte)0x01,                 //action
            (byte)0x00,                 //attr_flags
            (byte)0x041,                 //attr_flags
            (byte)0x01,                 //attr_vals  byte0 00000000
            (byte)0x00,                 //attr_vals  byte1
            (byte)0x00,                 //attr_vals  byte2 00000000
            (byte)0x10,                 //attr_vals  byte3 00000000
            (byte)0x00,                 //attr_vals  byte4
            (byte)0x00,                 //attr_vals  byte5
            (byte)0x00,                 //attr_vals  byte6
            (byte)0x00,                 //attr_vals  byte7
            (byte)0x54                  //校验和
            //FFFF 0010 03 22 0000 01 00 02 02000000000000003A 开网指令，异曲同工
            /////////////////////////////////
    };
    /**
     * 关闭凤鸣
     */
    public static byte[] CONTROL_GATEWAY_NOTHOWL={
            (byte)0xFF,(byte)0xFF,      //包头            0 1
            (byte)0x00,(byte)0x10,      //包长度           2 3
            (byte)0x03,                 //命令            4
            (byte)0x00,                 //包序号           5
            (byte)0x00,(byte)0x00,      //flags             6 7
            (byte)0x01,                 //action            8
            (byte)0x00,                 //attr_flags        9
            (byte)0x41,                 //attr_flags        10
            (byte)0x00,                 //attr_vals         11
            (byte)0x00,                 //attr_vals         12
            (byte)0x00,                 //attr_vals         13
            (byte)0x00,                 //attr_vals         14
            (byte)0x00,                 //attr_vals         15
            (byte)0x00,                 //attr_vals         16
            (byte)0x00,                 //attr_vals         17
            (byte)0x00,                 //attr_vals         18
            (byte)0x54                  //校验和
    };
    /**
     * 蜂鸣器时长
     */
    public static byte[] CONTROL_GATEWAY_HOWL_LONG_TIME={
            (byte)0xFF,(byte)0xFF,//包头
            (byte)0x00,(byte)0x10,      //包长度
            (byte)0x03,                 //命令
            (byte)0x00,                 //包序号
            (byte)0x00,(byte)0x00,      //flags
            (byte)0x01,                 //action
            (byte)0x00,                 //attr_flags
            (byte)0x40,                 //attr_flags
            (byte)0x00,                 //attr_vals  byte0 00000000
            (byte)0x00,                 //attr_vals  byte1
            (byte)0x00,                 //attr_vals  byte2 00000000
            (byte)0x35,                 //attr_vals  byte3 00000000
            (byte)0x00,                 //attr_vals  byte4
            (byte)0x00,                 //attr_vals  byte5
            (byte)0x00,                 //attr_vals  byte6
            (byte)0x00,                 //attr_vals  byte7
            (byte)0x54                  //校验和
    };

    /*
    *以下定义的是嘉德方定义的命令，即网关上报消息的命令 和 我们自定义的消息即APP后台发送给mina或者mina发送给app的指令约定
    * 按道理APP后端只需要封装一个Mina客户端工具类即可和mina服务端传输对象。
    * 但是APP后台和mina的包有冲突,加上mina服务端性能十分吃紧，因此后台采用的是原生socket
    * 约定byte指令，避免双方都要通过对象的解析器来方便自己增加服务器负担
    * */
    public static final int JD_MSG_CMD_INDEX = 4;
    public static final int JD_MSG_CMD_UP_GW_SN = 2;
    public static final int JD_MSG_CMD_UP_GW_SN_ACK = 4;
    public static final int JD_MSG_CMD_UP_DEV_STATUS = 5;
    public static final int JD_MSG_CMD_UP_TIME = 0x17;
    /*TODO:相关指令存在误触发（JD协议里面有CMD=该字段）*/
    public static final int KC_MSG_CMD_ADD_DEV = 81;          //添加网关
    public static final int KC_MSG_CMD_CHECK_GW_EXSIT = 82;   //检查网关是否存在或在线
    public static final int KC_MSG_CMD_DEK_DEV = 83;
    public static final int KC_MSG_CMD_SELECT_LIST=84;        //查询子设备列表
    public static final int KC_MSG_CMD_CONTROL_HOWL=85;       //控制设备鸣叫

    public static final int JD_MSG_CMD_ACTION_INDEX = 8;//网关回复心跳
    public static final int JD_MSG_CMD_ACTION_TF = 6;/*透传指令*/
    public static final int JD_MSG_CMD_ACTION_NOT_TF = 4;/*非透传指令*/


    public static final int KC_MSG_DBG_CMD = (byte) 0xF9;
    public static final int KC_MSG_DBG_CMD_GET_LIST = (byte) 0x01;
    public static final int KC_MSG_DBG_CMD_GET_GW_STATUS = (byte) 0x02;
    public static final int KC_MSG_DBG_CMD_SET_OPEN_BEEP = (byte) 0x03;
    public static final int KC_MSG_DBG_CMD_SET_CLOSE_BEEP = (byte) 0x04;
    public static final int KC_MSG_DBG_CMD_SET_BEEP_TIME = (byte) 0x05;
    public static final int KC_MSG_DBG_CMD_GET_GW_ATTR = (byte) 0x06;
}

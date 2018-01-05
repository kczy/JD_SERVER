import com.google.gson.Gson;
import entity.*;
import factory.SessionFactory;
import filter.JDDecoder;
import hansen.Outer;
import javassist.bytecode.ByteArray;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import util.*;

import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MessageHandler extends IoHandlerAdapter {

    static Logger log = Logger.getLogger(MinaServer.class.getName());

    private final int JD_MSG_CMD_INDEX = 4;
    private final int JD_MSG_CMD_UP_GW_SN = 2;
    private final int JD_MSG_CMD_UP_GW_SN_ACK = 4;
    private final int JD_MSG_CMD_UP_DEV_STATUS = 5;
    private final int JD_MSG_CMD_UP_TIME = 0x17;
    /*TODO:相关指令存在误触发（JD协议里面有CMD=该字段）*/
    private final int KC_MSG_CMD_ADD_DEV = 81;          //添加网关
    private final int KC_MSG_CMD_CHECK_GW_EXSIT = 82;   //检查网关是否存在或在线
    private final int KC_MSG_CMD_DEK_DEV = 83;
    private final int KC_MSG_CMD_SELECT_LIST=84;        //查询子设备列表

    private final int JD_MSG_CMD_ACTION_INDEX = 8;//网关回复心跳
    private final int JD_MSG_CMD_ACTION_TF = 6;/*透传指令*/
    private final int JD_MSG_CMD_ACTION_NOT_TF = 4;/*非透传指令*/


    private final int KC_MSG_DBG_CMD = (byte) 0xF9;
    private final int KC_MSG_DBG_CMD_GET_LIST = (byte) 0x01;
    private final int KC_MSG_DBG_CMD_GET_GW_STATUS = (byte) 0x02;


    public Gson gson = new Gson();

    static int count = 0;

    public Outer outer = null;

    //public static byte pcgNumber = 0;


    /**
     * 会话创建
     *
     * @param session
     * @throws Exception
     */
    @Override
    public void sessionCreated(IoSession session) throws Exception {
        log.log(Level.INFO, "【连接成功，会话创建：   " + new Date().toString() + "】");
        //给每一个会话设置一个包序号
        session.setAttribute("pcgNumber", (byte)0);
    }


    /**
     * 会话关闭
     *
     * @param session
     * @throws Exception
     */
    @Override
    public void sessionClosed(IoSession session) throws Exception {
        log.log(Level.SEVERE, "【连接关闭 " + new Date().toString() + "】");
        /*释放相关资源*/
        JDDecoder.Context ctx = MinaServer.thisSrvFty.getDecoder().getContext(session, false);
        if (ctx != null) {
            ctx = null;
        }
        //连接关闭后网关为离线，讲连接对象从map中移除
        SessionFactory.getSessionMap().remove(session.getAttribute(Constant.SESSION_ATTR_KEY_GW_SN));
        session.getService().getManagedSessions().remove(session.getId());
    }


    @Override
    public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
        log.log(Level.SEVERE, "session  IDLE.......");
        Long receiveTime= (Long) session.getAttribute("receiveTime");
        if(receiveTime==null){
            return;
        }
        receiveTime=new Date().getTime()-receiveTime;
        if(receiveTime>180000){
            //如果超时3翻钟，则关闭当前会话，并从集合中移除
            session.closeNow();
            return;
        }
        /*用于心跳通信，维持链路*/
        sendGataway(session, "FFFF00050700000000");
    }


    /**
     * 会话异常
     *
     * @param session
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
        log.log(Level.WARNING, "【异常：   " + new Date().toString() + " \n" + cause.toString() + "】");
        super.exceptionCaught(session, cause);
    }


    /**
     * 会话打开
     *
     * @param session
     * @throws Exception
     */
    @Override
    public void sessionOpened(IoSession session) throws Exception {
        log.info("【连接打开，服务器发送获取网关指令：" + "FF FF 00 05 01 00 00 00 06" + "   " + new Date().toString() + "】");
         /*下发查询设备网关序列号*/
        sendGataway(session, "FFFF00050100000006");


    }


    /**
     * 消息接收
     *
     * @param session
     * @param message
     * @throws Exception
     */
    @Override
    public void messageReceived(IoSession session, Object message) throws Exception {
        //记录最后一次接收消息的时间
        if(session.getAttribute("receiveTime")!=null){
            session.removeAttribute("receiveTime");
        }
        session.setAttribute("receiveTime",new Date().getTime());

        byte[] bts = (byte[]) message;
        System.out.println(session.getRemoteAddress().toString() + "\nRcv Msg" + ByteUtil.bytesToHexs(bts));

        switch (bts[JD_MSG_CMD_INDEX]) {
            //如果网关回复心跳时间超过三分钟就移除当前session
            case JD_MSG_CMD_ACTION_INDEX:
                log.info("收到心跳回复");
                break;
            case JD_MSG_CMD_UP_TIME:/*获取时间*/
                log.info("Dev adjust time   request\r\n");
                Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);

                byte[] timeMsg = {(byte) 0xFF, (byte) 0xFF, (byte) 0x00, (byte) 0x11, (byte) 0x18, (byte) 0x04, (byte) 0x00,
                        (byte) 0x00, (byte) 0x07, (byte) 0xE1, (byte) 0x09, (byte) 0x1C, (byte) 0x10, (byte) 0x36, (byte) 0x1D, (byte) 0x59, (byte) 0xCC,
                        (byte) 0xB8, (byte) 0xC5, (byte) 0x3E, (byte) 0x00};
                timeMsg[8] = (byte) ((year & 0xFF00) >> 8);
                timeMsg[9] = (byte) ((year & 0xff));
                year = c.get(Calendar.MONTH);
                timeMsg[10] = (byte) ((year & 0xff));
                year = c.get(Calendar.DAY_OF_MONTH);
                timeMsg[11] = (byte) ((year & 0xff));
                year = c.get(Calendar.HOUR);
                timeMsg[12] = (byte) ((year & 0xff));
                year = c.get(Calendar.MINUTE);
                timeMsg[13] = (byte) ((year & 0xff));
                year = c.get(Calendar.SECOND);
                timeMsg[14] = (byte) ((year & 0xff));
                /*ntp time*/
                year = c.get(Calendar.MILLISECOND) / 1000;
                timeMsg[14] = (byte) ((year & 0xff000000) >> 24);
                timeMsg[14] = (byte) ((year & 0xff0000) >> 16);
                timeMsg[14] = (byte) ((year & 0xff00) >> 8);
                timeMsg[14] = (byte) ((year & 0xff));
                sendGatawayThroughByts(session, timeMsg);

                break;
            case JD_MSG_CMD_UP_GW_SN://上报网关信息：序列号
                String ID = ParseUtil.getID(bts);//截取序列号
                log.info("\n收到网关序列号:" + ID);

                SessionFactory.getSessionMap().put(ID, session);
                session.setAttribute(Constant.SESSION_ATTR_KEY_GW_SN, ID);/*保存ID*/
                session.setAttribute(Constant.SESSION_ATTR_KEY_GW_TYPE, ParseUtil.GetGwType(bts));/*保存类型*/

                 /*TODO：需要更新网关在线状态*/
                /*查询子设备列表信息*/
                sendGataway(session, "FFFF00080324000005030138");
                break;
            case JD_MSG_CMD_UP_GW_SN_ACK://设备回复收到了，不需要解析
                log.info("【设备收到服务器的请求" + new Date().toString() + "】");
                System.out.println();
                break;
            case JD_MSG_CMD_UP_DEV_STATUS://设备主动上报状态
                log.info("Get Device msg 05");
                log.info("Send  Ack to Device");
                sendGatawayNoAddSN(session, "FFFF000506" + BaseUtil.encodeHexStr(bts[5]) + "00000C");
                if (bts[JD_MSG_CMD_ACTION_INDEX] == JD_MSG_CMD_ACTION_NOT_TF) {//非透传解析
                    Gateway gateway = ParseUtil.getGateway(bts);
                    if (gateway.isSrnOn()) {
                        System.out.println("设备报警了.....");
                        //设备一旦报警就下发请求列表
                        sendGataway(session, "FFFF00080324000005030138");
                    }
                } else if (bts[JD_MSG_CMD_ACTION_INDEX] == JD_MSG_CMD_ACTION_TF) {//透传解析
                    log.info("【设备上报透传    " + new Date().toString() + "】");
                    if (bts[9] == 1) {
                        log.info("单个子设备上报");
                    } else if (bts[9] == 4) {
                        System.out.println("\n子设备列表上报:");
                        List<SecurityDeviceResponseVO> devices = ParseUtil.getDevices(bts, session);
                        send(0, devices);
                        //解析包含报警的设备放入temp
                        List<SecurityDeviceResponseVO> temp = new ArrayList<>();
                        //判断devices里面的报警对象
                        for (SecurityDeviceResponseVO device : devices) {
                            if (device.getState() == 7) {
                                temp.add(device);
                            }
                        }
                        if (temp != null && temp.size() > 0) {
                            send(1, devices);
                        }
                    }
                }
                break;
            case KC_MSG_CMD_ADD_DEV:
                log.info("Srv_add_device_cmd:");
                StringBuilder sbadd = new StringBuilder();
                for (int i = 5; i < 5+Constant.JD_DEVICE_SN_LENGTH; i++) {
                    sbadd.append(BaseUtil.encodeHexStr(bts[i]));
                }
                String snoadd = sbadd.toString();
                final IoSession sessionadd = (IoSession) SessionFactory.getSessionMap().get(snoadd);
                if (sessionadd != null) {
                    switch ((int) sessionadd.getAttribute(Constant.SESSION_ATTR_KEY_GW_TYPE)) {
                        case Constant.SESSION_ATTR_VAL_GW_TYPE_ZIGBEE:
                            sendGataway(sessionadd, "FFFF00100322000001000202000000000000003A");
                            /*返回服务器ACK*/
                            sendGatawayNoAddSN(session, "FFFF0003010000");
                            final Timer timer = new Timer();
                            timer.schedule(new TimerTask() {
                                        public void run() {
                                            //网关请求列表
                                            sendGataway(sessionadd, "FFFF00080324000005030138");
                                            timer.cancel();
                                        }
                            }, 60000, 10000);
                            break;
                        case Constant.SESSION_ATTR_VAL_GW_TYPE_RF:
                                //紧急按钮
                                sendGataway(sessionadd,"FFFF000A1F"+sessionadd.getAttribute(Constant.SESSION_ATTR_KEY_GW_SN)+"2C0009");

 //////////////////////////////////////////////////////////////////////////////////////////////////////////
                            if(bts.length<13) { return;}
                            byte[]  addRfDeviceMsg={(byte)0xFF,(byte)0xFF,(byte)0x00,(byte)0x11,(byte)0x03,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x05,
                                    (byte)0x0D,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0x00,
                                    (byte)0x00,(byte)0x00};
                            addRfDeviceMsg[18]=bts[11];
                            addRfDeviceMsg[19]=bts[12];
                            sendGatawayThroughByts(sessionadd, addRfDeviceMsg);
//////////////////////////////////////////////////////////////////////////////////////////////////////////
                            break;
                        default:
                            break;
                    }

                }
                break;
            case KC_MSG_CMD_CHECK_GW_EXSIT:
                /*
                * 检查网关是否存在。写出对应的指令
                * */
                log.info("Srv_find_gw_is_exsit");
                StringBuilder sbSno = new StringBuilder();
                for (int i = 5; i < bts.length - 1; i++) {
                    sbSno.append(BaseUtil.encodeHexStr(bts[i]));
                }
                String snoStr = sbSno.toString();
                IoSession sessionLogin = (IoSession) SessionFactory.getSessionMap().get(snoStr);
                String isLogin = "";
                if (sessionLogin != null) {
                    isLogin = "FFFF0003010000";
                    //如果有此网关就主动去操一次列表
                    sendGataway(sessionLogin,"FFFF00080324000005030138");//发送获取设备列表命令
                } else {
                    isLogin = "FFFF0003000000";
                }
                sendGataway(session,isLogin);//发送网关在线结果
                break;
            case KC_MSG_CMD_DEK_DEV:
                //-1 -1 0 16 33 101 49 52 99 99 48 -74 -15 42 16 0 75 18 0 0
                delDevice(bts);
                break;
            case KC_MSG_CMD_SELECT_LIST://查询设备列表
                sendGataway(session, "FFFF00080324000005030138");//查询设备列表
                break;
            case KC_MSG_DBG_CMD:
                DbgIfForJd(session, bts);
                break;
            default:
                log.info("No deal Msg:" + ByteUtil.bytesToHexs(bts));
                break;
        }
    }

    /*
    * 调试接口
    * FF  FF  LEN_MSB  LEN_LSB   CMD   SN0 SN1 SN2 SN3 SN4 SN5 SN6 SUB_CMD CRC
    * */
    private void DbgIfForJd(IoSession session, byte[] bts) {

        StringBuilder sb = new StringBuilder();
        String sno;
        //嘉德的是40>=i<72，因为太长了 这里只用最后12个
        for (int i = (5); i < (11); i++) {
            sb.append(BaseUtil.encodeHexStr(bts[i]));
        }
        sno = sb.toString();
        IoSession sessionLogin = (IoSession) SessionFactory.getSessionMap().get(sno);
        if (sessionLogin == null) {
            sendGatawayNoAddSN(session, "FFFF0003000000");
            return;
        } else {
            sendGatawayNoAddSN(session, "FFFF0003010000");
        }
        switch (bts[11]) {
            case KC_MSG_DBG_CMD_GET_GW_STATUS:
                 /*下发查询设备网关序列号*/
                sendGataway(sessionLogin, "FFFF00050100000006");
                break;
            case KC_MSG_DBG_CMD_GET_LIST:
                /*查询子设备信息*/
                sendGataway(sessionLogin, "FFFF00080324000005030138");
                break;
            default:
                break;
        }

    }

    /**
     * 消息正确发送
     *
     * @param session
     * @param message
     */
    @Override
    public void messageSent(IoSession session, Object message) {
        log.log(Level.INFO, "[Send success  " + new Date().toString() + "]");
        IoBuffer buffer = buffer = (IoBuffer) message;
//        log.info("消息发送完毕buffer.remaining()：" + buffer.remaining());
//        log.info("消息发送完毕buffer.limit()：" + buffer.limit());
//        log.info("消息发送完毕buffer.position()：" + buffer.position());
        System.out.println("\nsend data: "+buffer.toString());
        log.info(session.getRemoteAddress().toString() + "Send MSG" + String.valueOf(message));
    }


    /**
     * 客户端断开连接
     *
     * @param session
     * @throws Exception
     */
    @Override
    public void inputClosed(IoSession session) throws Exception {
        log.log(Level.WARNING, "[Client disconected" + new Date().toString() + "]");
        super.inputClosed(session);
    }

    /**
     * 发送数据方法
     *
     * @param session
     * @param HexData
     */
    public void sendGataway(IoSession session, String HexData) {
        byte[] data = BaseUtil.toByteArray(HexData);

        byte pcgNumber = (byte) session.getAttribute("pcgNumber");
        pcgNumber = (++pcgNumber) > 255 ? 0 : pcgNumber;
        data[5] = pcgNumber;
        session.setAttribute("pcgNumber", pcgNumber);

        int checkSum = 0;
        for (int i = 2; i < data.length - 1; i++) {
            checkSum += data[i];
        }
        data[data.length - 1] = (byte) (checkSum % 256);
        IoBuffer buffer = IoBuffer.allocate(data.length);
        buffer.setAutoExpand(false);
        buffer.put(data);
        buffer.flip();
        session.write(buffer);
    }

    public void sendGatawayThroughByts(IoSession session, byte[] HexData) {
        byte pcgNumber = (byte) session.getAttribute("pcgNumber");
        pcgNumber = (++pcgNumber) > 255 ? 0 : pcgNumber;
        HexData[5] = pcgNumber;
        session.setAttribute("pcgNumber", pcgNumber);
        int checkSum = 0;
        for (int i = 2; i < HexData.length - 1; i++) {
            checkSum += HexData[i];
        }
        HexData[HexData.length - 1] = (byte) (checkSum % 256);
        IoBuffer buffer = IoBuffer.allocate(HexData.length);
        buffer.setAutoExpand(false);
        buffer.put(HexData);
        buffer.flip();
        session.write(buffer);
    }

    /**
     * 发送消息，不需要包序
     * @param session
     * @param HexData
     */
    public void sendGatawayNoAddSN(IoSession session, String HexData) {
        byte[] data = BaseUtil.toByteArray(HexData);
        int checkSum = 0;
        for (int i = 2; i < data.length - 1; i++) {
            checkSum += data[i];
        }
        data[data.length - 1] = (byte) (checkSum % 256);
        IoBuffer buffer = IoBuffer.allocate(HexData.length());
        buffer.setAutoExpand(false);
        buffer.put(data);
        buffer.flip();
        session.write(buffer);
    }

    public void send(int type, List list) {
        JiadeParams params = new JiadeParams(type, list);
        String jsonString = gson.toJson(params);
        log.info("设备列表json：" + jsonString);
        try {
            HttpUtil.doPostStr("http://lib18610386362.oicp.net/icare-device-api/device/elder/security/security-device-jiade-impl", jsonString);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void delDevice(byte[] bts) {
        //-1 -1 0 16 33 101 49 52 99 99 48 -74 -15 42 16 0 75 18 0 0
        //信息: 设备列表json：{"type":1,"securityDeviceResponseVOList":[{"sno":"653134636330","address":"/192.168.1.175:63843","endpoint":1,"profile":0,"device":264,"name":"门磁感应器","state":0,"ieee":"0be01e0b004b1200","clusterId":0,"electric":0},{"sno":"653134636330","address":"/192.168.1.175:63843","endpoint":1,"profile":0,"device":263,"name":"红外感应器","state":7,"ieee":"b6f12a10004b1200","clusterId":7,"electric":0}]}
        log.info("Srv_del_device_cmd");
        byte[] sno = new byte[6];
        for (int i = 5, j = 0; i < 11; i++, j++) {
            sno[j] = bts[i];
        }
        String snoStr = ByteUtil.bytesToHexString(sno);
        IoSession session = (IoSession) SessionFactory.getSessionMap().get(snoStr);
        byte[] mac = new byte[8];
        for (int i = 11, j = 0; i < 19; i++, j++) {
            mac[j] = bts[i];
        }
        String macStr = ByteUtil.bytesToHexString(mac);
        if (session != null) {
            sendGataway(
                    session,
                    "FFFF0000036700000502" + macStr + "F1"
            );
        }
    }

}

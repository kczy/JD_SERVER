import com.google.gson.Gson;
import conf.Configuration;
import entity.*;
import factory.SessionFactory;
import filter.JDDecoder;
import hansen.Outer;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import util.*;
import util.LoggerUtils;

import java.io.IOException;

import java.util.*;


import java.util.logging.*;

public class MessageHandler extends IoHandlerAdapter {



    static Logger log = Logger.getLogger(MinaServer.class.getName());
    static{
        try {
            LoggerUtils.setLogingProperties(log);       //将日志打印到指定的文件位置！！！
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Gson gson = new Gson();
    public static int count = 0;
    public Outer outer = null;

    public StringBuilder sb=new StringBuilder();


    public MessageHandler() throws IOException {
    }

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
        log.log(Level.SEVERE, "【连接关闭 " + new Date().toString() + "】"+session.getRemoteAddress().toString()+"--"+session.getLocalAddress().toString());
        /*释放相关资源*/
        JDDecoder.Context ctx = MinaServer.thisSrvFty.getDecoder().getContext(session, false);
        if (ctx != null) {
            ctx = null;
        }


        //连接关闭后向APPServer推送状态
        SecurityDeviceResponseVO vo=new SecurityDeviceResponseVO();
        vo.setSno((String) session.getAttribute(Constant.SESSION_ATTR_KEY_GW_SN));
        vo.setState(6);//0  在线触发  7 报警  6 离线
        List<SecurityDeviceResponseVO> list=new ArrayList<SecurityDeviceResponseVO>();
        list.add(vo);
        sendToAPPServer(2,list);//type=2更新网关状态

        //连接关闭后网关为离线，讲连接对象从map中移除
        SessionFactory.getSessionMap().remove(session.getAttribute(Constant.SESSION_ATTR_KEY_GW_SN));
        //session.getService().getManagedSessions().remove(session.getId());
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
        //sendGataway(session, "FFFF00050700000000");
        sendGatawayThroughByts(session,JDDeviceDefinitionCommand.HEARTBEAT);
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
        log.log(Level.WARNING, "【服务端异常】");
        cause.printStackTrace();
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
        //sendGataway(session, "FFFF00050100000006");
        sendGatawayThroughByts(session,JDDeviceDefinitionCommand.GET_GATEWAY_ID);
        ///////////记录接收网关发送过来的包的顺序////////////
        session.setAttribute("receive_package_serial",0);
        ///////////////////////////////////
        log.info("连接建立-------关闭蜂鸣");
        sendGatawayThroughByts(session,  JDDeviceDefinitionCommand.CONTROL_GATEWAY_NOTHOWL);

    }


    /**
     * 消息接收
     *
     * @param session
     * @param message
     * @throws Exception
     */
    @Override
    public void messageReceived(final IoSession session, Object message) throws Exception {

        //记录最后一次接收消息的时间
        if(session.getAttribute("receiveTime")!=null){
            session.removeAttribute("receiveTime");
        }
        session.setAttribute("receiveTime",new Date().getTime());

        byte[] bts = (byte[]) message;
        System.out.println(session.getRemoteAddress().toString() + "\nRcv Msg" + ByteUtil.bytesToHexs(bts));
        ///////////记录网关的包序////////////
        Integer rps= (Integer) session.getAttribute("receive_package_serial");
        System.out.println("\n------->接收的包个数："+(++rps)+"----->网关上传的包序号："+Integer.parseInt(BaseUtil.encodeHexStr2(bts[5]),16));
        session.setAttribute("receive_package_serial",rps);
        ///////////////////////////////////
        switch (bts[JDDeviceDefinitionCommand.JD_MSG_CMD_INDEX]) {
            //如果网关回复心跳时间超过三分钟就移除当前session
            case JDDeviceDefinitionCommand.JD_MSG_CMD_ACTION_INDEX:
                log.info("收到心跳回复");
                break;
            case JDDeviceDefinitionCommand.JD_MSG_CMD_UP_TIME:/*获取时间*/
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
            case JDDeviceDefinitionCommand.JD_MSG_CMD_UP_GW_SN://上报网关信息：序列号

                String ID = ParseUtil.getID(bts);//截取序列号
                log.info("\n收到网关序列号:" + ID);

                SessionFactory.getSessionMap().put(ID, session);
                session.setAttribute(Constant.SESSION_ATTR_KEY_GW_SN, ID);/*保存ID*/
                session.setAttribute(Constant.SESSION_ATTR_KEY_GW_TYPE, ParseUtil.GetGwType(bts));/*保存类型*/



                 /*TODO：需要更新网关在线状态*/
                SecurityDeviceResponseVO vo=new SecurityDeviceResponseVO();
                vo.setSno(ID);
                vo.setState(0);//0  在线触发  7 报警  6 离线
                List<SecurityDeviceResponseVO> list=new ArrayList<SecurityDeviceResponseVO>();
                list.add(vo);
                sendToAPPServer(2,list);

                /*查询子设备列表信息*/
                //sendGataway(session, "FFFF00080324000005030138");
                sendGatawayThroughByts(session,JDDeviceDefinitionCommand.SCAN_DEVICES_LIST);
                break;
            case JDDeviceDefinitionCommand.JD_MSG_CMD_UP_GW_SN_ACK://设备回复收到了，不需要解析
                log.info("【网关收到服务器的指令" + new Date().toString() + "】");
                break;
            case JDDeviceDefinitionCommand.JD_MSG_CMD_UP_DEV_STATUS://设备主动上报状态
                log.info("网关主动上报当前状态");
                /*回复设备收到*/
                JDDeviceDefinitionCommand.ANSWER_GATEWAY_ACK[5]=(byte)bts[5];
                sendGatawayThroughBytsNotComputedPackageNumber(session,JDDeviceDefinitionCommand.ANSWER_GATEWAY_ACK);//回复设备收到了

                if (bts[JDDeviceDefinitionCommand.JD_MSG_CMD_ACTION_INDEX] == JDDeviceDefinitionCommand.JD_MSG_CMD_ACTION_NOT_TF) {//非透传解析
                    Gateway gateway = ParseUtil.getGateway(bts);
                    DeviceState deviceState=ParseUtil.parseStatus(bts);
                    System.out.println("\n网关主动上报当前状态：\n"+deviceState.toString());

                } else if (bts[JDDeviceDefinitionCommand.JD_MSG_CMD_ACTION_INDEX] == JDDeviceDefinitionCommand.JD_MSG_CMD_ACTION_TF) {//透传解析
                    log.info("【设备上报透传    " + new Date().toString() + "】");
                    if (bts[9] == 1) {
                        log.info("单个子设备上报");
                    } else if (bts[9] == 4) {
                        System.out.println("\n子设备列表上报:");

                        //TODO
                        //目前只处理第一组包，后面如果嘉德有32个子设备就需要合并第二组包到集合
                        if(bts[10]==2){
                            System.out.println("第二组包不解..........");
                            return;
                        }

                        List<SecurityDeviceResponseVO> devices = ParseUtil.getDevices(bts, session);

                        /*********************************发送列表给安装APP******************************/
                        SendListToAndoid(session,devices);


                        /********************************发送列表给安装APP********************************/


                        sendToAPPServer(0, devices);
                        //解析包含报警的设备放入temp
                        List<SecurityDeviceResponseVO> temp = new ArrayList<>();

                        //是否报警
                        boolean srnOn=false;
                        //判断devices里面的报警对象
                        for (SecurityDeviceResponseVO device : devices) {
                            if (device.getState() == 7) {
                                log.info(device.getName()+"报警了");
                                temp.add(device);
                                if(device.getDevice()!=263&&device.getDevice()!=264){//只要列表中存在门磁或者红外之外的设备
                                    log.info("设置蜂鸣时长");
                                    sendGatawayThroughByts(session,JDDeviceDefinitionCommand.CONTROL_GATEWAY_HOWL_LONG_TIME);
                                    log.info("开启蜂鸣");
                                    sendGatawayThroughByts(session,JDDeviceDefinitionCommand.CONTROL_GATEWAY_HOWL);
                                    //打开后需要在指定时间内关闭
                                    Timer timer=new Timer();
                                    JDTimerTask task=new JDTimerTask(session,timer);
                                    timer.schedule(task,10000);
                                }
                            }
                        }


                        if (temp != null && temp.size() > 0) {
                            try {
                                sendToAPPServer(1, temp);
                                for(SecurityDeviceResponseVO device:temp){
                                    byte[] ieee=ByteUtil.hexStringToBytes(device.getIeee());
                                    byte[] cmd=JDDeviceDefinitionCommand.CLOSE_DEVICES_ARM_STATE;
                                    for(int i=0,j=10;i<ieee.length;i++,j++){
                                        cmd[j]=ieee[i];
                                    }
                                    //cmd[60]= (byte) 0x80;
                                    sendGatawayThroughByts(session,cmd);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }


                    }
                }
                break;
            case JDDeviceDefinitionCommand.KC_MSG_CMD_ADD_DEV:
                log.info("Srv_add_device_cmd:");
                StringBuilder sbadd = getStringBuilder();
                for (int i = 5; i < 5+Constant.JD_DEVICE_SN_LENGTH; i++) {
                    sbadd.append(BaseUtil.encodeHexStr(bts[i]));
                }
                String snoadd = sbadd.toString();
                final IoSession sessionadd = (IoSession) SessionFactory.getSessionMap().get(snoadd);
                if (sessionadd != null) {
                    //回复APPServer
                    sendGatawayThroughByts(session,JDDeviceDefinitionCommand.ANSWER_APPSERVER_OK);
                    switch ((int) sessionadd.getAttribute(Constant.SESSION_ATTR_KEY_GW_TYPE)) {
                        case Constant.SESSION_ATTR_VAL_GW_TYPE_ZIGBEE:  //zigbee操作
                            sendGatawayThroughByts(sessionadd,JDDeviceDefinitionCommand.INSERT_DEVICE_INTO_ZIGBEE);
                            final Timer timer = new Timer();
                            timer.schedule(new TimerTask() {
                                public void run() {
                                    //网关请求列表
                                    //sendGataway(sessionadd, "FFFF00080324000005030138");
                                    sendGatawayThroughByts(sessionadd,JDDeviceDefinitionCommand.SCAN_DEVICES_LIST);
                                    timer.cancel();
                                }
                            }, 60000, 10000);
                            break;
                        case Constant.SESSION_ATTR_VAL_GW_TYPE_RF:  //433操作
                            ///////////////////////////////////////////////////之前的代码 是对的 替换为下面注释中的代码/////////////////////////////////////////////////////////////////////
                            if(bts.length<13) { return;}
                            byte[]  addRfDeviceMsg={(byte)0xFF,(byte)0xFF,(byte)0x00,(byte)0x11,(byte)0x03,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x05,
                                            (byte)0x0D,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0x00,
                                            (byte)0x00,(byte)0x00};
                            addRfDeviceMsg[18]=bts[11];
                            addRfDeviceMsg[19]=bts[12];
                            sendGatawayThroughByts(sessionadd, addRfDeviceMsg);

                            //ScannerDeviceList(sessionadd);
                            final Timer timer433 = new Timer();
                            timer433.schedule(new TimerTask() {
                                public void run() {
                                    //网关请求列表
                                    //sendGataway(sessionadd, "FFFF00080324000005030138");
                                    sendGatawayThroughByts(sessionadd,JDDeviceDefinitionCommand.SCAN_DEVICES_LIST);
                                    timer433.cancel();
                                }
                            }, 60000, 10000);
                            break;
                        default:
                            break;
                    }
                    if(sessionadd.getAttribute("Android")!=null){
                        sendGatawayThroughByts((IoSession) sessionadd.getAttribute("Android"),JDDeviceDefinitionCommand.ANSWER_APPSERVER_OK);
                    }
                }else{
                    sendGatawayThroughByts(session,JDDeviceDefinitionCommand.ANSWER_APPSERVER_FAILED);//回复APPServer
                    if(sessionadd.getAttribute("Android")!=null){
                        sendGatawayThroughByts((IoSession) sessionadd.getAttribute("Android"),JDDeviceDefinitionCommand.ANSWER_APPSERVER_FAILED);
                    }
                }
                break;
            case JDDeviceDefinitionCommand.KC_MSG_CMD_CHECK_GW_EXSIT:
                /*
                * 检查网关是否存在。写出对应的指令
                *
                * */
                log.info("Srv_find_gw_is_exsit");
                StringBuilder sbSno = getStringBuilder();
                for (int i = 5; i < bts.length - 1; i++) {
                    sbSno.append(BaseUtil.encodeHexStr(bts[i]));
                }
                String snoStr = sbSno.toString();
                IoSession sessionLogin = (IoSession) SessionFactory.getSessionMap().get(snoStr);

                if (sessionLogin != null) {
                    sendGatawayThroughByts(sessionLogin,JDDeviceDefinitionCommand.SCAN_DEVICES_LIST);//扫描网关下的列表
                    sendGatawayThroughByts(session,JDDeviceDefinitionCommand.ANSWER_APPSERVER_OK);//回复APP
                    log.info("回复APPServer网关在线："+JDDeviceDefinitionCommand.ANSWER_APPSERVER_OK.toString());
                } else {
                    sendGatawayThroughByts(session,JDDeviceDefinitionCommand.ANSWER_APPSERVER_FAILED);
                    System.out.println("\n回复appServer网关离线："+JDDeviceDefinitionCommand.ANSWER_APPSERVER_FAILED.toString()+"\n");//回复app
                }
                break;
            case JDDeviceDefinitionCommand.KC_MSG_CMD_DEK_DEV:
                //-1 -1 0 16 33 101 49 52 99 99 48 -74 -15 42 16 0 75 18 0 0
                delDevice(bts);
                break;
            case JDDeviceDefinitionCommand.KC_MSG_CMD_SELECT_LIST://查询设备列表
                /*
                /*
                * 检查网关是否存在。写出对应的指令
                * */
                StringBuilder snoStrSB =getStringBuilder();
                for (int i = 5; i < 11; i++) {
                    snoStrSB.append(BaseUtil.encodeHexStr(bts[i]));
                }
                String snoString = snoStrSB.toString();
                IoSession session_SlectList = (IoSession) SessionFactory.getSessionMap().get(snoString);
                //sendGataway(session_SlectList, "FFFF00080324000005030138");//查询设备列表
                sendGatawayThroughByts(session_SlectList,JDDeviceDefinitionCommand.SCAN_DEVICES_LIST);//扫描网关下的列表
                break;
            case JDDeviceDefinitionCommand.KC_MSG_CMD_CONTROL_HOWL://控制网关娇喘嘘嘘
                StringBuilder snoStrSBHowl = getStringBuilder();
                for (int i = 5; i < 11; i++) {
                    snoStrSBHowl.append(BaseUtil.encodeHexStr(bts[i]));
                }
                String snoStringHowl = snoStrSBHowl.toString();
                IoSession sessionHowl = (IoSession) SessionFactory.getSessionMap().get(snoStringHowl);
                if(sessionHowl!=null){
                    switch (bts[11]) {
                        case JDDeviceDefinitionCommand.KC_MSG_DBG_CMD_SET_OPEN_BEEP:
                            /*打开蜂鸣器*/
                            sendGatawayThroughByts(sessionHowl,  JDDeviceDefinitionCommand.CONTROL_GATEWAY_HOWL);
                            break;
                        case JDDeviceDefinitionCommand.KC_MSG_DBG_CMD_SET_CLOSE_BEEP:
                            /*关闭蜂鸣器*/
                            sendGatawayThroughByts(sessionHowl,  JDDeviceDefinitionCommand.CONTROL_GATEWAY_NOTHOWL);
                            break;
                        case JDDeviceDefinitionCommand.KC_MSG_DBG_CMD_SET_BEEP_TIME:
                            /*蜂鸣器时长*/
                            sendGatawayThroughByts(sessionHowl,  JDDeviceDefinitionCommand.CONTROL_GATEWAY_HOWL_LONG_TIME);
                            break;
                    }
                }else{
                    throw new Exception("空指针异常：session为null!!!!!!");
                }
                break;
            case JDDeviceDefinitionCommand.KC_MSG_CMD_APP:
                if(bts[8]==JDDeviceDefinitionCommand.KC_MSG_CMD_APP_REGISTER){

                    StringBuilder sb=getStringBuilder();
                    for(int i=9;i<15;i++){
                        sb.append(BaseUtil.encodeHexStr(bts[i]));
                    }
                    IoSession sessionDevice = (IoSession) SessionFactory.getSessionMap().get(sb.toString());
                    if(sessionDevice==null){
                        sendGatawayThroughByts(session,JDDeviceDefinitionCommand.ANSWER_APPSERVER_FAILED);
                    }else{
                        sendGatawayThroughByts(session,JDDeviceDefinitionCommand.ANSWER_APPSERVER_OK);
                        sessionDevice.setAttribute("Android",session);

                    }
                }
            default:
                log.info("No deal Msg:" + ByteUtil.bytesToHexs(bts));
                break;
        }
    }

    /**
     * 发送消息列表给Android
     * @param session
     * @param devices
     */
    private void SendListToAndoid(IoSession session, List<SecurityDeviceResponseVO> devices) {
        byte jinji=00000001;
        byte yanwu=00000001;
        byte qigan=00000001;
        byte hongwai=00000001;
        byte menci=00000001;
        for(SecurityDeviceResponseVO device:devices){
            switch(device.getDevice()){
                case 0x0404:jinji= (byte) (jinji<<7);break;
                case 0x0402:yanwu= (byte) (yanwu<<6);break;
                case 0x010A:qigan= (byte) (qigan<<5);break;
                case 0x0107:hongwai= (byte) (hongwai<<4);break;
                case 0x0108:menci= (byte) (menci<<3);break;
                default:;
            }
        }
        byte total= (byte) (jinji|yanwu|qigan|hongwai|menci);
        if(session.getAttribute("Android")!=null){
            byte[] cmd={(byte)0xFF, (byte)0xFF, //包头
                    (byte)0x00,(byte)0x07,  //包长度
                    (byte)0x05A,                   //命令
                    (byte)0x00,                   //包序
                    (byte)0x00,(byte)0x00,  //flags
                    (byte)0x00,             //action
                    (byte)0x00,             //列表
                    0x00
            };
            cmd[9]=total;
            sendGatawayThroughByts((IoSession) session.getAttribute("Android"),cmd);
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
        System.out.println("\n           send data:   回复的包序："+session.getAttribute("pcgNumber")+"  <------------------------\n");
        System.out.println();
        for (int i = 0; i <buffer.array().length ; i++) {
            System.out.print(buffer.array()[i]+" ");
        }
        System.out.println();
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
     * 发送数据方法,不推荐使用
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



    public static void sendGatawayThroughByts(IoSession session, byte[] HexData) {
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

    public void sendGatawayThroughBytsNotComputedPackageNumber(IoSession session, byte[] HexData) {

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

    /**
     * 发送消息给服务器
     * @param type  发送类型
     * @param list  发送列表
     */
    public void sendToAPPServer(int type, List list) {
        JiadeParams params = new JiadeParams(type, list);
        String jsonString = gson.toJson(params);
        log.info("设备列表json：" + jsonString);
        try {
            //fileLogger.logp(Level.INFO, MessageHandler.class.getName(), "sendToAPPServer", "推送json到APPServer");
            String 无用 = gson.toJson(params);
            HttpUtil.doPostStr(Configuration.DEVICE_LIST, jsonString);
        } catch (Exception e) {
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
        final IoSession session = (IoSession) SessionFactory.getSessionMap().get(snoStr);
        byte[] mac = new byte[8];
        for (int i = 13, j = 0; i < 20; i++, j++) {
            mac[j] = bts[i];
        }
        String macStr = ByteUtil.bytesToHexString(mac);

        byte[] zontype=new byte[2];
        zontype[0]=bts[28];
        zontype[1]=bts[29];
        String zontypeStr = ByteUtil.bytesToHexString(zontype);
        if (session != null) {
            sendGataway(
                    session,
                    "FFFF00180305000005"+
                            "02" + //透传命令
                            macStr + //透传IEEEE
                            "0000" + //透传填充短地址
                            "00" + //透传填充endpoeint
                            "0000" + //透传填充ProfileID
                            "0000" + //透传填充DeviceID
                            zontypeStr + //透传填充ZoneType
                            "F1"
            );
            //ScannerDeviceList(session);
            final Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                public void run() {
                    //网关请求列表
                    //sendGataway(session, "FFFF00080324000005030138");
                    sendGatawayThroughByts(session,JDDeviceDefinitionCommand.SCAN_DEVICES_LIST);
                    timer.cancel();
                }
            }, 60000, 10000);
        }

    }






    public static class JDTimerTask extends TimerTask{
        public IoSession session;
        public Timer timer;
        public JDTimerTask(IoSession session,Timer timer){
            this.session=session;
            this.timer=timer;
        }
        public JDTimerTask(){}
        public IoSession getSession() {
            return session;
        }

        public void setSession(IoSession session) {
            this.session = session;
        }

        @Override
        public void run() {
            log.info("关闭蜂鸣");
            sendGatawayThroughByts(session,JDDeviceDefinitionCommand.CONTROL_GATEWAY_NOTHOWL);
            this.timer.cancel();
        }
    }

    public StringBuilder getStringBuilder() {
        this.sb.delete(0,this.sb.length());
        return sb;
    }

    public void setStringBuilder(StringBuilder sb) {
        this.sb = sb;
    }
}

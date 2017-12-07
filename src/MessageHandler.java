import com.google.gson.Gson;
import entity.*;
import factory.SessionFactory;
import hansen.Outer;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import util.BaseUtil;
import util.HttpUtil;
import util.ParseUtil;
import util.ScannerGatawayTask;

import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MessageHandler extends IoHandlerAdapter {

    static Logger log = Logger.getLogger(MinaServer.class.getName());

    public Gson gson = new Gson();

    static int count = 0;

    public Outer outer = null;




    /**
     * 会话创建
     * @param session
     * @throws Exception
     */
    @Override
    public void sessionCreated(IoSession session) throws Exception {
        log.log(Level.INFO, "【连接成功，会话创建：   " + new Date().toString() + "】");
    }


    /**
     * 会话关闭
     * @param session
     * @throws Exception
     */
    @Override
    public void sessionClosed(IoSession session) throws Exception {
        log.log(Level.SEVERE, "【连接关闭 " + new Date().toString() + "】");
        super.sessionClosed(session);
    }



    @Override
    public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
        super.sessionIdle(session, status);
    }


    /**
     * 会话异常
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
     * @param session
     * @throws Exception
     */
    @Override
    public void sessionOpened(IoSession session) throws Exception {
        log.info("【连接打开，服务器发送获取网关指令：" + "FF FF 00 05 01 00 00 00 06" + "   " + new Date().toString() + "】");
        String str = "FFFF00050100000006";
        byte[] bts = BaseUtil.toByteArray(str);
        IoBuffer buf = IoBuffer.allocate(100);
        buf.setAutoExpand(true);
        buf.put(bts);
        buf.flip();
        session.write(buf);
    }


    /**
     * 消息接收
     * @param session
     * @param message
     * @throws Exception
     */
    @Override
    public void messageReceived(IoSession session, Object message) throws Exception {
        byte[] bts = (byte[]) message;
        System.out.println("\n收到数据：");
        for (int i = 0; i < bts.length; i++) {
            System.out.print(bts[i] + " ");
        }
        switch (bts[4]) {
            case 2://上报网关信息：序列号
                String ID = ParseUtil.getID(bts);
                System.out.println("\n收到网关序列号:" + ID);
                SessionFactory.getSessionMap().put(ID, session);
                session.setAttribute("sno", ID);
                break;
            case 4://设备回复收到了，不需要解析
                log.info("【设备收到服务器的请求" + new Date().toString() + "】");
                System.out.println();
                break;
            case 5://设备主动上报状态
                log.info("设备上报信息，命令05");
                String str = "FFFF000506" + BaseUtil.encodeHexStr(bts[5]) + "00000C";
                byte[] msg = BaseUtil.toByteArray(str);
                IoBuffer buf = IoBuffer.allocate(100);
                buf.setAutoExpand(true);
                buf.put(msg);
                buf.flip();
                session.write(buf);

                if (bts[8] == 4) {//非透传解析
                    Gateway gateway = ParseUtil.getGateway(bts);
                    if (gateway.isSrnOn()) {
                        System.out.println("设备报警了.....");
                        //设备一旦报警就下发请求列表
                        String requestListStr = "FFFF00080324000005030138";//发送获取设备列表命令
                        byte[] requestListByte = BaseUtil.toByteArray(requestListStr);
                        IoBuffer requestListBuf = IoBuffer.allocate(100);
                        buf.setAutoExpand(true);
                        buf.put(bts);
                        buf.flip();
                        session.write(requestListBuf);
                    }
                } else if (bts[8] == 6) {//透传解析
                    log.info("【设备上报透传    " + new Date().toString() + "】");
                    if (bts[9] == 1) {
                        //上报子设备
                        System.out.println("单个子设备上报:");
                        Mcu mcu = ParseUtil.getMuc(bts);
                        System.out.println(mcu);

                        String msg2 = "FFFF00080324000005030138";
                        byte[] bts2 = BaseUtil.toByteArray(msg2);
                        IoBuffer buf3 = IoBuffer.allocate(100);
                        buf.setAutoExpand(true);
                        buf.put(bts2);
                        buf.flip();
                        session.write(buf);
                    } else if (bts[9] == 4) {
                        System.out.println("\n子设备列表上报(保存至session):");
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
            //获得序列号拿到session然后发送请求设备列表
            case 30:
                System.out.println("//获得序列号拿到session然后发送请求设备列表");
                System.out.println("当前session应该是socket的session：" + session.getLocalAddress() + ":" + session.getRemoteAddress());
                StringBuilder sb = new StringBuilder();
                for (int i = 5; i < bts.length - 1; i++) {
                    sb.append(BaseUtil.encodeHexStr(bts[i]));
                }
                String sno = sb.toString();
                IoSession sessionObj = (IoSession) SessionFactory.getSessionMap().get(sno);
                if (sessionObj != null) {
                    List<Byte> mculist = (List<Byte>) sessionObj.getAttribute("mculist");
                    if (mculist == null) {
                        mculist = new ArrayList<Byte>();
                    }
                    byte[] mucs = BaseUtil.doListTransByte(mculist);
                    System.out.println("拿到会话session:" + sessionObj);
                    IoBuffer devices = IoBuffer.allocate(100);
                    devices.setAutoExpand(true);
                    devices.put(mucs);
                    devices.flip();
                    session.write(devices);
                }
                break;
            case 31:
                log.info("服务器下发添加设备");
                StringBuilder sbadd = new StringBuilder();
                for (int i = 5; i < bts.length - 1; i++) {
                    sbadd.append(BaseUtil.encodeHexStr(bts[i]));
                }
                String snoadd = sbadd.toString();
                IoSession sessionadd = (IoSession) SessionFactory.getSessionMap().get(snoadd);
                if (sessionadd != null) {
                    try {
                        //如果网关存在但是是离线的
                        if(sessionadd.isClosing()){
                            String isSend = "FFFF0003020000";
                            byte[] isSendBt=BaseUtil.toByteArray(isSend);
                            IoBuffer isSendBuf = IoBuffer.allocate(100);
                            isSendBuf.setAutoExpand(true);
                            isSendBuf.put(isSendBt);
                            isSendBuf.flip();
                            session.write(isSendBuf);
                        }
                        String msgadd = "FFFF00100322000001000202000000000000003A";
                        byte[] btsadd = BaseUtil.toByteArray(msgadd);
                        IoBuffer bufadd = IoBuffer.allocate(100);
                        bufadd.setAutoExpand(true);
                        bufadd.put(btsadd);
                        bufadd.flip();
                        sessionadd.write(bufadd);
                        String isSend = "FFFF0003010000";
                        byte[] isSendBt=BaseUtil.toByteArray(isSend);
                        IoBuffer isSendBuf = IoBuffer.allocate(100);
                        isSendBuf.setAutoExpand(true);
                        isSendBuf.put(isSendBt);
                        isSendBuf.flip();
                        session.write(isSendBuf);
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        String isSend = "FFFF0003000000";
                        byte[] isSendBt=BaseUtil.toByteArray(isSend);
                        IoBuffer isSendBuf = IoBuffer.allocate(100);
                        isSendBuf.setAutoExpand(true);
                        isSendBuf.put(isSendBt);
                        isSendBuf.flip();
                        session.write(isSendBuf);
                    }
                }
                break;
            case 32:
                log.info("服务器查询是否有该网关");
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
                    String requestListStr = "FFFF00080324000005030138";//发送获取设备列表命令
                    byte[] requestListByte = BaseUtil.toByteArray(requestListStr);
                    IoBuffer requestListBuf = IoBuffer.allocate(100);
                    requestListBuf.setAutoExpand(true);
                    requestListBuf.put(bts);
                    requestListBuf.flip();
                    session.write(requestListBuf);
                } else {
                    isLogin = "FFFF0003000000";
                }
                IoBuffer bufisLoggin = IoBuffer.allocate(100);
                bufisLoggin.setAutoExpand(true);
                byte[] btsisLogin = BaseUtil.toByteArray(isLogin);
                bufisLoggin.put(btsisLogin);
                bufisLoggin.flip();
                session.write(bufisLoggin);
                break;
            case 33:
                log.info("下发删除指定设备");

                break;
            default:
                System.out.println("默认什么也不做..............");
                break;
        }
    }


    /**
     * 消息正确发送
     * @param session
     * @param message
     */
    @Override
    public void messageSent(IoSession session, Object message) {
        log.log(Level.INFO, "【消息发送成功   " + new Date().toString() + "】");
        IoBuffer buffer = null;
        StringBuilder sb = new StringBuilder();

        buffer = (IoBuffer) message;
        while (buffer.hasRemaining()) {
            sb.append(buffer.get() + " ");
        }
        log.info("消息发送完毕buffer.remaining()：" + buffer.remaining());
        log.info("消息发送完毕buffer.limit()：" + buffer.limit());
        log.info("消息发送完毕buffer.position()：" + buffer.position());
        log.info("发送的消息："+sb.toString());
    }


    /**
     * 客户端断开连接
     * @param session
     * @throws Exception
     */
    @Override
    public void inputClosed(IoSession session) throws Exception {
        log.log(Level.WARNING, "【客户端断开连接 " + new Date().toString() + "】");
        super.inputClosed(session);
    }

    public void send(int type, List list) {
        JiadeParams params = new JiadeParams(1, list);
        String jsonString = gson.toJson(params);
        log.info("设备列表json：" + jsonString);
        try {
            HttpUtil.doPostStr("http://10.39.100.72:8080/device/elder/security/security-device-jiade-impl", jsonString);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}

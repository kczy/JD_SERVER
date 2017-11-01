import com.google.gson.Gson;
import entity.DeviceAlarm;
import entity.Gateway;
import entity.Mcu;
import factory.SessionFactory;
import hansen.EventNotify;
import mqtt.MtClient;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import util.BaseUtil;
import util.ParseUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MessageHandler extends IoHandlerAdapter {
    static Logger log = Logger.getLogger(MinaServer.class.getName());
    static int count=0;
    MtClient client;
    @Override
    public void sessionCreated(IoSession session) throws Exception {
        log.log(Level.INFO,"【连接成功，会话创建：   "+new Date().toString()+"】");


        List<EventNotify> list=new ArrayList<EventNotify>();
        session.setAttribute("notifyList",list);



        ////会话创建的时候创建百度云客户端
        client=new MtClient(session);
        client.start();
        session.setAttribute("MTClient",client);
    }



    @Override
    public void sessionClosed(IoSession session) throws Exception {
        log.log(Level.SEVERE,"【连接关闭 "+new Date().toString()+"】");
        super.sessionClosed(session);
    }

    @Override
    public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
        super.sessionIdle(session, status);
    }

    @Override
    public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
        log.log(Level.WARNING,"【异常：   "+new Date().toString()+" \n"+cause.toString()+"】");
        super.exceptionCaught(session, cause);
    }

    @Override
    public void sessionOpened(IoSession session) throws Exception {
        log.info("【连接打开，服务器发送获取网关指令："+"FF FF 00 05 01 00 00 00 06"+"   "+new Date().toString()+"】");
        super.sessionOpened(session);
        String str="FFFF00050100000006";
        byte[] bts= BaseUtil.toByteArray(str);
        IoBuffer buf = IoBuffer.allocate(100);
        buf.setAutoExpand(true);
        buf.put(bts);
        buf.flip();
        session.write(buf);
    }
    @Override
    public void messageReceived(IoSession session, Object message) throws Exception {

        byte[] bts= (byte[]) message;

        StringBuilder sb=new StringBuilder();

        for(int i=0;i<bts.length;i++){
            sb.append(bts[i]+" ");
        }
        System.out.println();
        log.info("【"+(++count)+"接收消息成功:"+new Date().toString()+"】");
        System.out.println("收到数据："+sb.toString());

        switch (bts[4]){

            case 2://上报网关信息：序列号

                log.info("【上报网关信息："+sb.toString()+"  "+new Date().toString()+"】");

                String ID=ParseUtil.getID(bts);

                SessionFactory.getSessionMap().put(ID,session);

                client= (MtClient) session.getAttribute("MTClient");
                //百度天工发布序列号消息
                client.publish("HA_IAS/OUT/DEVICE_LIST",ID);

                break;

            case 4://设备回复收到了，不需要解析
                log.info("【设备收到服务器的请求"+new Date().toString()+"】");
                System.out.println();
                break;
            case 5://设备主动上报状态
                log.info("设备上报信息，命令05");
                String str="FFFF000506"+BaseUtil.encodeHexStr(bts[5])+"00000C";
                byte[] msg=BaseUtil.toByteArray(str);
                IoBuffer buf = IoBuffer.allocate(100);
                buf.setAutoExpand(true);
                buf.put(msg);
                buf.flip();
                session.write(buf);

                if(bts[8]==4){//非透传解析
                    Gateway gateway=ParseUtil.getGateway(bts);
                    System.out.println("网关上报");
                    if(gateway.isSrnOn()){
                        System.out.println("设备报警了.....");

                        DeviceAlarm deviceAlarm= (DeviceAlarm) session.getAttribute("deviceAlarm");
                        if(deviceAlarm==null){
                            session.setAttribute("deviceAlarm",new DeviceAlarm());
                            deviceAlarm= (DeviceAlarm) session.getAttribute("deviceAlarm");
                        }

                        deviceAlarm.setTime(new Date().getTime());
                    }

                }else if(bts[8]==6){//透传解析
                    log.info("【设备上报透传    "+new Date().toString()+"】");
                    if(bts[9]==1){
                        //上报子设备
                        System.out.println("子设备上报");
                        Mcu mcu=ParseUtil.getMuc(bts);
                        System.out.println(mcu);

                        DeviceAlarm deviceAlarm= (DeviceAlarm) session.getAttribute("deviceAlarm");
                        if(deviceAlarm!=null){
                            //发布设备类型到百度天工
                            deviceAlarm.setName0(mcu.getZoneType2());
                            client= (MtClient) session.getAttribute("MTClient");
                            Gson gson=new Gson();
                            client.publish("HA_IAS/OUT/DEVICE_ALS_ALARM",gson.toJson(deviceAlarm));
                            deviceAlarm=null;
                            session.removeAttribute("deviceAlarm");
                        }

                    }else if(bts[9]==4){
                        //上报子设备列表
                        System.out.println("子设备列表上报");
                        List<Mcu> mcus=ParseUtil.getMcus(bts);
                        System.out.println(mcus);
                    }
                }

                break;
            default:
                System.out.println("默认包不做..............");
                break;
        }
    }

    @Override
    public void messageSent(IoSession session, Object message) throws Exception {
        log.log(Level.FINEST,"【消息发送成功   "+new Date().toString()+"】");
        super.messageSent(session, message);
    }

    @Override
    public void inputClosed(IoSession session) throws Exception {
        log.log(Level.WARNING,"【客户端断开连接 "+new Date().toString()+"】");
        super.inputClosed(session);

    }
}

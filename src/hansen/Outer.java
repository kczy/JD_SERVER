package hansen;

import entity.Event;
import factory.SessionFactory;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import util.BaseUtil;
import util.ByteUtil;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class Outer {

    private IoSession session;
    private Event event=new Event();
    private Map<Long,IoSession> map= SessionFactory.getSessionMap();
    private List<EventNotify> list;

    private Outer(){}
    public Outer(String number) throws Exception {
        //1拿到序列好到会话中去查找，前提：会话中保存了网关序列号

        session=map.get(number);
        if(session==null){
            throw new Exception("会话为空！请检查序列号正确与否");
        }
    }
    public Outer(String number, EventNotify e) throws Exception {
        //1拿到序列好到会话中去查找，前提：会话中保存了网关序列号

        session=map.get(number);
        if(session==null){
            throw new Exception("会话为空！请检查序列号正确与否");
        }
        addListener(e);
    }

    private void addListener(EventNotify e) throws Exception {
        if(e==null){
            throw new Exception("空对象异常");
        }
        List<EventNotify> list= (List<EventNotify>) session.getAttribute("notifyList");
        list.add(e);
    }

    private void setEvent(IoSession session){
        event.setActive(session.isActive());
        event.setClosing(session.isClosing());
        event.setConnected(session.isConnected());
        event.setSecured(session.isSecured());
        event.setReaderIdle(session.isReaderIdle());
        event.setReadSuspended(session.isReadSuspended());
        event.setWriterIdle(session.isWriterIdle());
        event.setWriterSuspended(session.isWriteSuspended());
    }

    /**
     * 删除设备
     * @param IEEE
     */
    public void deleteMCU(String IEEE){

        setEvent(session);//更新事件的状态
        //将重要的错误触发调用者对象做相关处理，并且返回空集合
        if(event.isClosing()){
            for(EventNotify e:list){
                e.connectBreak();//触发调用对象执行连接失效处理
            }
            return;
        }
        //如果没有链接就触发调用者对象做相关处理，并且返回空集合
        if(!event.isConnected()){
            for(EventNotify e:list){
                e.connectBreak();//触发调用对象执行连接失效处理
            }
            return;
        }
        System.out.println("请求网关删除设备的指令：");
        //FF FF 00 0F 03 67 00 00 05 02 0B E0 1E 0B 00 4B 12 00 F1
        String msg3="FFFF000F036700000502"+IEEE+"F1";
        byte[] bts2= BaseUtil.toByteArray(msg3);
        IoBuffer buf = IoBuffer.allocate(100);
        buf.setAutoExpand(true);
        buf.put(bts2);
        buf.flip();
        session.write(buf);
    }


    public void addMCU(){

        setEvent(session);//更新事件的状态
        //将重要的错误触发调用者对象做相关处理，并且返回空集合
        if(event.isClosing()){
            for(EventNotify e:list){
                e.connectBreak();//触发调用对象执行连接失效处理
            }
            return;
        }
        //如果没有链接就触发调用者对象做相关处理，并且返回空集合
        if(!event.isConnected()){
            for(EventNotify e:list){
                e.connectBreak();//触发调用对象执行连接失效处理
            }
            return;
        }

        String msg2="FFFF00100322000001000202000000000000003A";
        byte[] bts2= BaseUtil.toByteArray(msg2);
        IoBuffer buf = IoBuffer.allocate(100);
        buf.setAutoExpand(true);
        buf.put(bts2);
        buf.flip();
        session.write(buf);
    }

    /**
     * 获取子设备
     * @param  //网关序列号
     */
    public void getMCUList(){
        setEvent(session);//更新事件的状态
        //将重要的错误触发调用者对象做相关处理，并且返回空集合
        if(event.isClosing()){

            for(EventNotify e:list){
                e.connectBreak();//触发调用对象执行连接失效处理
            }
            return;
        }
        //如果没有链接就触发调用者对象做相关处理，并且返回空集合
        if(!event.isConnected()){
            for(EventNotify e:list){
                e.connectBreak();//触发调用对象执行连接失效处理
            }
            return;
        }
        //发送获取设备列表命令
        String msg="FFFF00080324000005030138";
        byte[] bts2=BaseUtil.toByteArray(msg);
        IoBuffer buf = IoBuffer.allocate(100);
        buf.setAutoExpand(true);
        buf.put(bts2);
        buf.flip();
        session.write(buf);
    }

    public void sendTime(IoSession session,byte pkgNum){
        Calendar calendar=Calendar.getInstance();
        String msg="FF FF 00 00 18 "+pkgNum+" 00 00 "
                +Integer.toHexString(calendar.get(Calendar.YEAR))
                +Integer.toHexString(calendar.get(Calendar.MONTH))
                +Integer.toHexString(calendar.get(Calendar.DAY_OF_MONTH))
                +Integer.toHexString(calendar.get(Calendar.HOUR_OF_DAY))
                +Integer.toHexString(calendar.get(Calendar.MINUTE))
                +Integer.toHexString(calendar.get(Calendar.SECOND))
                +"00000000"
                +"00";
        byte[] bts=BaseUtil.toByteArray(msg);
        IoBuffer buf = IoBuffer.allocate(100);
        buf.setAutoExpand(true);
        buf.put(bts);
        buf.flip();
        session.write(buf);
    }

    public static void main(String[] args) throws IOException {
        Socket socket=new Socket("171.208.222.97",10051);
        BufferedOutputStream out=new BufferedOutputStream(socket.getOutputStream());
        byte[] snNoByteArr = ByteUtil.hexStringToBytes("303030303031");
        byte[] sendArr=new byte[5+1+snNoByteArr.length];
        sendArr[0]=(byte)0xFF;//包头
        sendArr[1]=(byte)0xFF;
        sendArr[2]=(byte)0x00;//包序号
        sendArr[3]=(byte)0x00;
        sendArr[4]=(byte)31;//命令
        for(int i=5,j=0;j<snNoByteArr.length;i++,j++){
            sendArr[i]=snNoByteArr[j];
        }
        sendArr[sendArr.length-1]=(byte)0x00;//最后一位，校验和(胡乱设置，不需要解析)
        sendArr[3]=(byte)(sendArr.length-4);

        socket.setSoTimeout(40);
        out.write(sendArr);
        out.flush();
    }
}

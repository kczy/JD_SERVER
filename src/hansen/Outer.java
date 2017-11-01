package hansen;

import entity.Event;
import factory.SessionFactory;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import util.BaseUtil;

import java.util.List;
import java.util.Map;

public class Outer {

    private IoSession session;
    private Event event=new Event();
    private Map<Long,IoSession> map= SessionFactory.getSessionMap();
    private List<EventNotify> list;

    private Outer(){}

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
}

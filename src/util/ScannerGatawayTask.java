package util;

import factory.SessionFactory;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ScannerGatawayTask {

    static Timer timer = new Timer(true);
    static Map<Long, IoSession> sessionMap;

    static TimerTask task = new TimerTask() {

        @Override
        public void run() {
            sessionMap = getSessionMap();
            if (sessionMap.size() > 0 && sessionMap != null) {
                for (Map.Entry<Long, IoSession> entry : sessionMap.entrySet()) {

                    IoSession session = entry.getValue();
                    String msg = "FFFF00080324000005030138";//发送获取设备列表命令
                    byte[] bts = BaseUtil.toByteArray(msg);
                    IoBuffer buf = IoBuffer.allocate(100);
                    buf.setAutoExpand(true);
                    buf.put(bts);
                    buf.flip();
                    session.write(buf);
                    System.out.println("................session"+session.getId()+":扫描设备,session总数："+getSessionMap().size()+"...................");
                }
            }
        }
    };

    public static void execute() {
        //timer.schedule(task,60000);
        timer.schedule(task, 5000, 60000);
    }

    public static Map<Long, IoSession> getSessionMap() {
        return sessionMap = SessionFactory.getSessionMap();
    }

    public static void main(String[] args) {
        HashMap<String,Session> sessionMap=new HashMap<String,Session>();
        sessionMap.put("111",new Session("小强"));
        sessionMap.put("111",new Session("旺财"));
        System.out.println(sessionMap.size());
        System.out.println(sessionMap.get("111"));
    }
}
class Session{
    String name;
    public Session(String name){
        this.name=name;
    }

    @Override
    public String toString() {
        return "Session{" +
                "name='" + name + '\'' +
                '}';
    }
}

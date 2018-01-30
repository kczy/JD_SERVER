package entity;

import org.apache.mina.core.session.IoSession;

import java.util.TimerTask;

public class JDTimerTask extends TimerTask {
    public IoSession session;
    public JDTimerTask(IoSession session){
        session=session;
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
        //网关请求列表
        //sendGataway(session, "FFFF00080324000005030138");
        //sendGatawayThroughByts(session,JDDeviceDefinitionCommand.SCAN_DEVICES_LIST);
        //timer.cancel();
    }
}

package factory;

import org.apache.mina.core.session.IoSession;

import java.util.HashMap;
import java.util.Map;


/**
 * mina框架提供了session的map
 * 但是他是session的ID对应session
 *
 * 我们需要以序列号对应一个session
 * 因此自定义数据结构
 */
public class SessionFactory {

    private SessionFactory(){}

    private static Map<Long,IoSession> map=new HashMap<Long,IoSession>();
    public static Map getSessionMap(){
        return map;
    }

}

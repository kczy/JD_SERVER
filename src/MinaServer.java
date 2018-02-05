import conf.Configuration;
import factory.JDDecoderFactory;
import factory.SessionFactory;
import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.core.session.IoSessionConfig;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MinaServer {

    static Logger log = Logger.getLogger(MinaServer.class.getName());
    public static JDDecoderFactory  thisSrvFty=null;

    public static void main(String[] args) throws IOException {

        //IoAcceptor acceptor=null;
        //创建一个非阻塞的socket
        NioSocketAcceptor acceptor = new NioSocketAcceptor(Runtime.getRuntime().availableProcessors() + 1);// 端口重用
        acceptor.setReuseAddress(true);


        /*acceptor.getFilterChain().addLast("remove55decodee",new ProtocolCodecFilter(new Remove55DecoderFactory()));
        acceptor.getFilterChain().addLast("cutdecoder",new ProtocolCodecFilter(new ByteDecodeFactory()));*/
        thisSrvFty=new JDDecoderFactory();
        acceptor.getFilterChain().addLast("jddecoder",new ProtocolCodecFilter(thisSrvFty));

        // 获得IoSessionConfig对象
        IoSessionConfig cfg = acceptor.getSessionConfig();

        // 读写通道30秒内无操作进入空闲状态,进入空闲后立马发送嘉德指定的心跳包维持连接
        cfg.setIdleTime(IdleStatus.BOTH_IDLE, 30);

        //设置读取缓冲区大小
        cfg.setReadBufferSize(512);//越小越看出问题
        //cfg.setMinReadBufferSize(200);
        //cfg.setMaxReadBufferSize(200);

        // 绑定逻辑处理器
        acceptor.setHandler(new MessageHandler());

        InetSocketAddress socket=null;
        for(String s :args)
        {
            System.out.println(s+"\r\n");
        }
        //绑定端口
        int port=Configuration.PORT;
        try {
            if(args.length>0) {
                if (args[0] == null) {
                    port = Configuration.PORT;
                } else {
                    port = Integer.valueOf(args[0]);
                    if ((port < 0) | (port > 65535)) {
                        port = Configuration.PORT;
                    }
                }
            }
            socket=new InetSocketAddress(port);
            acceptor.bind(socket);
            log.log(Level.INFO,"【服务启动成功，端口号为:"+port+"】");
        } catch (IOException e) {
            log.log(Level.INFO,"【端口绑定错误"+e.toString()+"】");
            acceptor.unbind(socket);
        }finally {
            acceptor.unbind(new InetSocketAddress(port));
        }
    }
}

import conf.Configuration;
import factory.JDDecoderFactory;
import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSessionConfig;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MinaServer {

    static Logger log = Logger.getLogger(MinaServer.class.getName());
    public static void main(String[] args) {

        IoAcceptor acceptor=null;

        //创建一个非阻塞的socket
        acceptor=new NioSocketAcceptor();


        /*acceptor.getFilterChain().addLast("remove55decodee",new ProtocolCodecFilter(new Remove55DecoderFactory()));
        acceptor.getFilterChain().addLast("cutdecoder",new ProtocolCodecFilter(new ByteDecodeFactory()));*/
        acceptor.getFilterChain().addLast("jddecoder",new ProtocolCodecFilter(new JDDecoderFactory()));

        // 获得IoSessionConfig对象
        IoSessionConfig cfg = acceptor.getSessionConfig();

        // 读写通道30秒内无操作进入空闲状态
        cfg.setIdleTime(IdleStatus.BOTH_IDLE, 50);

        //设置读取缓冲区大小
        cfg.setReadBufferSize(2048);//越小越看出问题
        cfg.setMinReadBufferSize(2048);
        cfg.setMaxReadBufferSize(2048);

        // 绑定逻辑处理器
        acceptor.setHandler(new MessageHandler());


        //绑定端口
        try {
            InetSocketAddress socket=new InetSocketAddress(Configuration.PORT);
            acceptor.bind(socket);
            log.log(Level.INFO,"【服务启动成功，端口号为:"+Configuration.PORT+"】");

        } catch (IOException e) {
            log.log(Level.SEVERE,"【端口绑定错误"+e.toString()+"】");
        }finally {
            acceptor.unbind(new InetSocketAddress(Configuration.PORT));
        }
    }
}

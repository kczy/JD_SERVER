package factory;

import filter.JDDEncoder;
import filter.JDDecoder;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;

public class JDDecoderFactory implements ProtocolCodecFactory {

    private JDDecoder decoder;
    private JDDEncoder encoder;

    public JDDecoderFactory(){
        decoder=new JDDecoder();
    }

    public JDDecoder getDecoder() {
        return decoder;
    }

    public void setDecoder(JDDecoder decoder) {
        this.decoder = decoder;
    }

    @Override
    public ProtocolEncoder getEncoder(IoSession ioSession) throws Exception {
        return encoder;
    }

    @Override
    public ProtocolDecoder getDecoder(IoSession ioSession) throws Exception {
        return decoder;
    }
}

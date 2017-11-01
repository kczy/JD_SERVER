package factory;

import filter.Remove55Decoder;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;

public class Remove55DecoderFactory implements ProtocolCodecFactory {
    private Remove55Decoder decoder;

    public Remove55DecoderFactory(){
        decoder=new Remove55Decoder();
    }
    @Override
    public ProtocolEncoder getEncoder(IoSession ioSession) throws Exception {
        return null;
    }

    @Override
    public ProtocolDecoder getDecoder(IoSession ioSession) throws Exception {
        return decoder;
    }
}

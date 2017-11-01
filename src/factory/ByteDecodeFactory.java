package factory;

import filter.CutDecoder;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;

public class ByteDecodeFactory implements ProtocolCodecFactory {

    private CutDecoder decoder;

    public ByteDecodeFactory() {
        decoder = new CutDecoder();
    }

    @Override
    public ProtocolEncoder getEncoder(IoSession ioSession) throws Exception {
        return null;
    }

    @Override
    public ProtocolDecoder getDecoder(IoSession session) throws Exception {
        return decoder;
    }


}

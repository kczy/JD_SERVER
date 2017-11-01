package filter;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderAdapter;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

public class JDDEncoder extends ProtocolEncoderAdapter {
    @Override
    public void encode(IoSession ioSession, Object message, ProtocolEncoderOutput protocolEncoderOutput) throws Exception {

        byte[] bytes = (byte[])message;

        IoBuffer buffer=IoBuffer.allocate(1024);

        buffer.put(bytes);

        buffer.flip();

        protocolEncoderOutput.write(buffer);

        protocolEncoderOutput.flush();

        buffer.free();

    }
}

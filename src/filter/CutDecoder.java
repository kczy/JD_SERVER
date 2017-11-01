package filter;

import conf.Configuration;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.filterchain.IoFilter;
import org.apache.mina.core.filterchain.IoFilterAdapter;
import org.apache.mina.core.filterchain.IoFilterChain;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.core.write.WriteRequest;
import org.apache.mina.filter.codec.*;
import sun.util.logging.PlatformLogger;
import util.BaseUtil;


import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CutDecoder extends CumulativeProtocolDecoder {

    static Logger log = Logger.getLogger(CutDecoder.class.getName());
    static int i=0;




    @Override
    public void decode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) throws Exception {
        super.decode(session, in, out);
    }

    @Override
    protected boolean doDecode(IoSession ioSession, IoBuffer ioBuffer, ProtocolDecoderOutput protocolDecoderOutput)  {
        //log.info(ioBuffer.getHexDump());


        if(ioBuffer.remaining()>0){//有数据时，读取前4字节判断消息长度
            try {
                byte [] sizeBytes = new byte[4];//我的是4字节
                ioBuffer.mark();////标记当前位置，以便reset

                ioBuffer.get(sizeBytes,0,sizeBytes.length);

                byte packagelength= (byte) (sizeBytes[2]*((byte)16)+sizeBytes[3]);//包长度
                ioBuffer.reset();
                if(packagelength>ioBuffer.remaining()){//如果消息内容不够，则重置，相当于不读取size
                    return false;//父类接收新数据，以拼凑成完整数据
                }else{
                    byte[] bytes = new byte[packagelength+4];
                    ioBuffer.get(bytes,0,bytes.length);
                    protocolDecoderOutput.write(bytes);
                    if(ioBuffer.remaining()>0){//如果读取内容后还粘了包，就让父类再重读  一次，进行下一次解析
                        return true;
                    }
                }
            } catch (Exception e) {
                log.log(Level.SEVERE,"异常："+e.toString());
                e.printStackTrace();
            }
        }
        return false;//处理成功，让父类进行接收下个包
    }

    @Override
    public void dispose(IoSession session) throws Exception {
        super.dispose(session);
    }


}

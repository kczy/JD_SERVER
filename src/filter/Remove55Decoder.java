package filter;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

import java.util.ArrayList;
import java.util.List;


/**
 * 信息过来删除ff后面的55
 */
public class Remove55Decoder implements ProtocolDecoder{

    @Override
    public void decode(IoSession ioSession, IoBuffer ioBuffer, ProtocolDecoderOutput protocolDecoderOutput) throws Exception {

        Context ctx=getContext(ioSession);
        ctx.append(ioBuffer);
        IoBuffer buffer=ctx.getBuf();
        List<Byte> list=ctx.getList();

        buffer.flip();

        buffer.mark();

        byte flag=0;
        while(buffer.hasRemaining()){
            byte b=buffer.get();

            if(b==85&&flag==-1){
                continue;
            }
            list.add(b);

            flag=b;
        }
        flag=0;

        buffer.reset();
        //buffer.flip();
        buffer.clear();
        byte[] bts=new byte[list.size()];
        for (int i=0;i<list.size();i++){

            bts[i]=list.get(i);
        }

        list.clear();
        protocolDecoderOutput.write(bts);

    }

    @Override
    public void finishDecode(IoSession ioSession, ProtocolDecoderOutput protocolDecoderOutput) throws Exception {

    }

    @Override
    public void dispose(IoSession ioSession) throws Exception {

    }

    private Context getContext(IoSession session){
        Context ctx= (Context) session.getAttribute("context");
        if(ctx==null){
            ctx=new Context();
            session.setAttribute("context",ctx);
        }
        return ctx;
    }

    private class Context{

        private IoBuffer buf;
        private List<Byte> list=new ArrayList<Byte>();
        private int flag=0;


        private Context(){
            buf = IoBuffer.allocate(100).setAutoExpand(true);
        }



        public void reset(){
            this.buf.clear();
        }
        public void append(IoBuffer buffer){
            getBuf().put(buffer);
        }


        public List<Byte> getList() {
            return list;
        }

        public void setList(List<Byte> list) {
            this.list = list;
        }

        public IoBuffer getBuf() {
            return buf;
        }

        public void setBuf(IoBuffer buf) {
            this.buf = buf;
        }

        public int getFlag() {
            return flag;
        }

        public void setFlag(int flag) {
            this.flag = flag;
        }

    }
}

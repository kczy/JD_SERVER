package filter;
/*
*   http://blog.csdn.net/sunzhenhua0608/article/details/31778519   Iobuffer的详细介绍
* */
import factory.ByteDecodeFactory;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


public class JDDecoder extends CumulativeProtocolDecoder {

    static Logger log = Logger.getLogger(JDDecoder.class.getName());


    private byte[]  JD_POTO_HRAD={(byte)0xFF,(byte)0xFF};
    private byte[]  JD_POTO_FF={(byte)0xFF,(byte)0x55};
    private enum DEC_FSM_STATUS {
        DEC_FIND_HEAD, /*寻找包头*/
        DEC_GET_MSG_LEN_MSB,/*获取内容长度_msb*/
        DEC_GET_MSG_LEN_LSB,/*获取内容长度_lsb*/
        DEC_RCV_CTX/*接收内容*/
    }

    /*检测包头*/

    private   boolean JDDecoderIsHead(byte  data,List<Byte> ba )
    {

        if(ba.size()>0)
            ba.add(1,ba.get(0));

        ba.add(0,data);

        if(ba.size()>1)
        return (ba.get(1)==JD_POTO_HRAD[0])&(ba.get(0)==JD_POTO_HRAD[1])?true:false;
        else
            return false;
    }
    /*
    * 检测转义字符0xFF
    * */
    private   boolean JDDecoderCheckFF(byte  data,List<Byte> ba)
    {
        if(ba.size()>0)
        return ((ba.get(ba.size()-1)==JD_POTO_FF[0])&(data==JD_POTO_FF[1]))?true:false;
        else
            return false;
    }
    /*
    * 消息校验和检测
    * */
    private   boolean JDDecoderCheckCRC(List<Byte>  data)
    {
        int checkSum=0;
        /*TODO：进行校验检测*/
        for (Byte datum : data) {
            checkSum+=datum;
        }
        System.out.println("校验和："+((   (checkSum-data.get(data.size()-1))%256   )&0xFF)+"==="+((data.get(data.size()-1))&0xFF));
        return ((   (checkSum-data.get(data.size()-1))%256   )&0xFF)==((data.get(data.size()-1))&0xFF);
    }

    public static void main(String[] args) {
        //System.out.println(-110&0xFF);//146
        //System.out.println(-110&0xFF);
    }


    @Override
    protected boolean doDecode(IoSession ioSession, IoBuffer ioBuffer, ProtocolDecoderOutput protocolDecoderOutput) throws Exception {
        //IoBuffer.reset  reset方法 是把当前位置position设置为当前mark，而不是0

        Context ctx=getContext(ioSession,true);
        List<Byte> lsBuffer=ctx.getListBuffer();//搜集正确的包
        byte b=(byte)0x00;
        //System.out.println("打印打印打印打印打印打印打印");
        //如果有数据就要拼包
        while (ioBuffer.hasRemaining()){


            b=ioBuffer.get();//获取一个,position加1

//            System.out.printf(" %2X",b);
//            System.out.println();

             /*断包重置*/
            if((ctx.lastByte==JD_POTO_HRAD[0])&&(b==JD_POTO_HRAD[1]))
            {
                if( ctx.decFsm!=DEC_FSM_STATUS.DEC_FIND_HEAD) {
                    ctx.decFsm = DEC_FSM_STATUS.DEC_GET_MSG_LEN_MSB;
                    lsBuffer.clear();
                    ctx.msgLen = 0;
                    continue;
                }
            }
            ctx.lastByte=b;

            /*是否是转义0xFF*/
            if(JDDecoderCheckFF(b,lsBuffer)){//如果55的前面是ff（ff 55）那么忽略本次55
                continue;
            }



            /*状态机，接收信息，保证包信息的完整性，然后调用解析*/
            switch(ctx.decFsm)
            {
                case DEC_FIND_HEAD:/*包头*/
                    if( JDDecoderIsHead(b,lsBuffer)) {
                        ctx.decFsm = DEC_FSM_STATUS.DEC_GET_MSG_LEN_MSB;
                    }
                    else
                    {
                        ctx.msgLen=0;
                    }
                    break;
                case DEC_GET_MSG_LEN_MSB:/*包长*/
                    lsBuffer.clear();
                    lsBuffer.add(0,b);

                    ctx.decFsm=DEC_FSM_STATUS.DEC_GET_MSG_LEN_LSB;
                    break;
                case DEC_GET_MSG_LEN_LSB:
                    lsBuffer.add(b);
                    ctx.decFsm=DEC_FSM_STATUS.DEC_RCV_CTX;
                    ctx.msgLen=((lsBuffer.get(0)<<8)&0xFF00)|(lsBuffer.get(1)&0xFF);
                    System.out.println("ctx.msgLen="+ctx.msgLen);
                    break;
                case DEC_RCV_CTX:/*内容*/
                    ctx.msgLen--;
                    lsBuffer.add(b);
                    if(ctx.msgLen==0) {

                        if (JDDecoderCheckCRC(lsBuffer)) {
                            /*为了保证后继解析index位置*/
                            lsBuffer.add(0,(byte)0xFF);
                            lsBuffer.add(1,(byte)0xFF);
                             /*调用Handler*/
                            protocolDecoderOutput.write(ctx.getByteBuffer(lsBuffer));
                            ctx.decFsm = DEC_FSM_STATUS.DEC_FIND_HEAD;
                            lsBuffer.clear();
                        } else {
                            ctx.decFsm = DEC_FSM_STATUS.DEC_FIND_HEAD;
                            lsBuffer.clear();
                        }
                    }
                    break;
                default:
                    ctx.decFsm=DEC_FSM_STATUS.DEC_FIND_HEAD;
                    lsBuffer.clear();
                    break;
            }

        }

        if(ioBuffer.hasRemaining()){//如果读取一个完整包内容后还有数据，让父类再调用一次
            return true;
        }
        else {//处父类进行接收下个包
            return false;
        }
    }

    /**
     * 从session中获得上下文对象
     * 上下文对象是自己弄得，目的是为了使用它里面的缓冲对象iobuffer
     * @param session  creat==没有是否要创建
     * @return
     */
    public Context getContext(IoSession session,boolean  creat){
        Context ctx= (Context) session.getAttribute("context");
        if((ctx==null)&(creat)){
            ctx=new Context();
            session.setAttribute("context",ctx);
        }
        return ctx;
    }


    public class Context{
        private List<Byte>  listBuffer=new ArrayList<>();
        public DEC_FSM_STATUS decFsm=DEC_FSM_STATUS.DEC_FIND_HEAD;
        public int   msgLen=0;/*数据长度记录*/
        public byte  lastByte=0x00;/*上一个字节*/
        public byte[] getByteBuffer(List<Byte> list){
            byte[] bs=new byte[list.size()];
            for(int i=0;i<bs.length;i++){
                bs[i]=list.get(i);
            }
            return bs;
        }

        public List<Byte> getListBuffer()
        {
            return listBuffer;
        }

        private Context(){

        }

    }

}

package filter;

import factory.ByteDecodeFactory;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


public class JDDecoder extends CumulativeProtocolDecoder {

    static Logger log = Logger.getLogger(JDDecoder.class.getName());


    @Override
    protected boolean doDecode(IoSession ioSession, IoBuffer ioBuffer, ProtocolDecoderOutput protocolDecoderOutput) throws Exception {

        Context ctx=getContext(ioSession);
        List<Byte> lsBuffer=ctx.getListBuffer();//搜集正确的包
        ioBuffer.mark();//标记当前position    0
        byte b=0;
        byte flag=0;
        //如果有数据就要拼包
        while (ioBuffer.hasRemaining()){
            b=ioBuffer.get();//获取一个

            if(lsBuffer.size()>2&&!ctx.getState()){
                if(b==-1&&flag==-1){
                    ctx.resetState();
                    continue;
                }

            }

            if(b==85&&flag==-1){//如果55的前面是ff（ff 55）那么忽略本次55
                flag=b;/////////////////
                continue;
            }
            flag=b;
            if(lsBuffer.size()==2&&lsBuffer.get(0)==-1&&lsBuffer.get(1)==-1){
                //如果当中有了ff ff头那么此包具备了头
                ctx.setIshead(true);//头完成
            }
            if(!ctx.ishead){    //如果头状态不满足就必须要让他满足
                if(b!=-1){
                    continue;
                }
            }
            lsBuffer.add(b);
            if(lsBuffer.size()==4){//包长度达到了就设置状态 并把包长度保存
                byte packagelength= (byte) (lsBuffer.get(2)*((byte)16)+lsBuffer.get(3));
                ctx.setIslength(true);
                ctx.setPackageLength(packagelength);
            }
            if(lsBuffer.size()==4+ctx.getPackageLength()) {//报文头 FF FF 00 05=4    4+5=整个数据长度
                ctx.setIscontent(true); //数据长度完成 设置状态
                ctx.setIschecksum(true);//默认校验和成功
                if(ctx.getState()){//如果状态都达到了一个包就好了
                    //1将数据写出去
                    protocolDecoderOutput.write(ctx.getByteBuffer(lsBuffer));
                    ctx.resetState();
                    break;
                }
            }
        }

        if(ioBuffer.hasRemaining()){//如果还有数据  请继续接收  调用本方法
            return true;
        }
        //没数据了就重新接收了,
        return false;
    }

    /**
     * 从session中获得上下文对象
     * 上下文对象是自己弄得，目的是为了使用它里面的缓冲对象iobuffer
     * @param session
     * @return
     */
    private Context getContext(IoSession session){
        Context ctx= (Context) session.getAttribute("context");
        if(ctx==null){
            ctx=new Context();
            session.setAttribute("context",ctx);
        }
        return ctx;
    }

    private class Context{

        private IoBuffer buffer;        //自定义缓冲区，用来拼接正确的包

        private List<Byte> listBuffer=new ArrayList<Byte>();

        private boolean ishead;         //头判断 判断一个完整包的标准

        private boolean islength;       //长度判断 判断一个完整包的标准

        private boolean iscontent;      //内容判断  判断一个完整包的标准

        private boolean ischecksum;     //校验和   判断一个完整包的标准

        private byte packageLength;     //包长度记录

        private byte checksum;          //校验和
        /**
         * 获取状态
         * @return
         */
        public boolean getState(){

            return this.ishead && this.islength && this.iscontent && this.ischecksum;
        }
        /**
         * 重置状态
         */
        public void resetState(){
            this.ishead=false;
            this.islength=false;
            this.iscontent=false;
            this.ischecksum=false;
            this.packageLength=0;
            this.checksum=0;
            this.listBuffer.clear();
        }

        public byte[] getByteBuffer(List<Byte> list){
            byte[] bs=new byte[list.size()];
            for(int i=0;i<bs.length;i++){
                bs[i]=list.get(i);
            }
            return bs;
        }


        private Context(){
            buffer = IoBuffer.allocate(100).setAutoExpand(true);
            resetState();
        }


        /////////////////////getter  setter/////////////////////////////
        public void setChecksum(byte checksum) {
            this.checksum = checksum;
        }

        public byte getChecksum() {
            return checksum;
        }

        public byte getPackageLength() {
            return packageLength;
        }

        public void setPackageLength(byte packageLength) {
            this.packageLength = packageLength;
        }

        public IoBuffer getBuffer() {
            return buffer;
        }

        public void setBuffer(IoBuffer buffer) {
            this.buffer = buffer;
        }

        public boolean isIshead() {
            return ishead;
        }

        public boolean isIslength() {
            return islength;
        }

        public boolean isIscontent() {
            return iscontent;
        }

        public boolean isIschecksum() {
            return ischecksum;
        }

        public void setIshead(boolean ishead) {
            this.ishead = ishead;
        }

        public void setIslength(boolean islength) {
            this.islength = islength;
        }

        public void setIscontent(boolean iscontent) {
            this.iscontent = iscontent;
        }

        public void setIschecksum(boolean ischecksum) {
            this.ischecksum = ischecksum;
        }

        public List<Byte> getListBuffer() {
            return listBuffer;
        }

        public void setListBuffer(List<Byte> listBuffer) {
            this.listBuffer = listBuffer;
        }
    }



}

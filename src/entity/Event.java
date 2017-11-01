package entity;

public class Event {

    private boolean connected;
    private boolean secured;
    private boolean active;
    private boolean closing;
    private boolean readerIdle;
    private boolean writerIdle;
    private boolean readSuspended;
    private boolean writerSuspended;




    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    public void setSecured(boolean secured) {
        this.secured = secured;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setClosing(boolean closing) {
        this.closing = closing;
    }

    public void setReaderIdle(boolean readerIdle) {
        this.readerIdle = readerIdle;
    }

    public void setWriterIdle(boolean writerIdle) {
        this.writerIdle = writerIdle;
    }

    public void setReadSuspended(boolean readSuspended) {
        this.readSuspended = readSuspended;
    }

    public void setWriterSuspended(boolean writerSuspended) {
        this.writerSuspended = writerSuspended;
    }

    public boolean isConnected() {
        return connected;
    }

    public boolean isSecured() {
        return secured;
    }

    public boolean isActive() {
        return active;
    }

    public boolean isClosing() {
        return closing;
    }

    public boolean isReaderIdle() {
        return readerIdle;
    }

    public boolean isWriterIdle() {
        return writerIdle;
    }

    public boolean isReadSuspended() {
        return readSuspended;
    }

    public boolean isWriterSuspended() {
        return writerSuspended;
    }


    public static void main(String[] args) throws InterruptedException {
        System.out.println("编译成功：");
        Thread.sleep(1000*10);
        System.out.println("开始执行：");
        long s=System.currentTimeMillis();
        for(int i=0;i<100000;i++){
            int num=1*2*3*4*5;
            int num2=num;
            int num3=num+num2;
            if(num3==(num+num2)){

            }
            System.out.println(i);
        }
        long e=System.currentTimeMillis();

        System.out.println("耗时:"+(e-s));
    }
}

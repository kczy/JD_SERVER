package entity;

public class Gateway {

    private int armState;    //布撤防状态
    private boolean srnOn;       //警号是否在名叫
    private boolean enrolling;   //允许子设备入网，只能开启等待自动结束不能手动结束
    private boolean reset;      //重置出厂设置
    private boolean smsControl; //不知道什么东东
    private String armDlyg;     //布防延时标记
    private String almdlyg;     //报警延时标记
    private int timeSrnOn;   //报警时长
    private int armDly;      //报警延时
    private int armDly2;      //报警延时
    private int deviceType;     //设备类型
    private int pushID;
    private int tmSrnEnd;    //报警声音剩余时长
    private int tmArmDlyEnd; //布防延时剩余时长
    private int tmAlmDlyEnd; //报警/进入延时剩余时长
    private int alms;        //为撤防报警数量
    private String RSSI;        //信号强度


    private String pushValue;//根据pushID来确定值

    public void setArmDlyg(String armDlyg) {
        this.armDlyg = armDlyg;
    }

    public void setAlmdlyg(String almdlyg) {
        this.almdlyg = almdlyg;
    }

    public void setRSSI(String RSSI) {
        this.RSSI = RSSI;
    }

    public void setPushValue(String pushValue) {
        this.pushValue = pushValue;
    }

    public String getArmDlyg() {
        return armDlyg;
    }

    public String getAlmdlyg() {
        return almdlyg;
    }

    public String getRSSI() {
        return RSSI;
    }

    public String getPushValue() {
        if(pushID==1){
            pushValue="有子设备防拆报警";
        }else if(pushID==2){
            pushValue="子设备报警";
        }else if(pushID==3){
            pushValue="SOS紧急报警";
        }else if(pushID==4){
            pushValue="子设备电池电压低";
        }else if(pushID==5){
            pushValue="外出不放时有门窗未关好";
        }
        return pushValue;
    }

    public int getArmDly2() {
        return armDly2;
    }

    public void setArmDly2(int armDly2) {
        this.armDly2 = armDly2;
    }

    public void setTmAlmDlyEnd(int tmAlmDlyEnd) {
        this.tmAlmDlyEnd = tmAlmDlyEnd;
    }

    public int getTmAlmDlyEnd() {

        return tmAlmDlyEnd;
    }

    public void setTmArmDlyEnd(int tmArmDlyEnd) {
        this.tmArmDlyEnd = tmArmDlyEnd;
    }

    public int getTmArmDlyEnd() {

        return tmArmDlyEnd;
    }

    public void setTmSrnEnd(int tmSrnEnd) {
        this.tmSrnEnd = tmSrnEnd;
    }

    public int getTmSrnEnd() {

        return tmSrnEnd;
    }

    public void setPushID(int pushID) {
        this.pushID = pushID;
    }

    public int getPushID() {

        return pushID;
    }

    public int getAlms() {
        return alms;
    }

    public void setAlms(int alms) {
        this.alms = alms;
    }

    public void setDeviceType(int deviceType) {
        this.deviceType = deviceType;
    }

    public int getDeviceType() {

        return deviceType;
    }

    public void setArmDly(int armDly) {
        this.armDly = armDly;
    }

    public int getArmDly() {

        return armDly;
    }

    public void setTimeSrnOn(int timeSrnOn) {
        this.timeSrnOn = timeSrnOn;
    }

    public int getTimeSrnOn() {

        return timeSrnOn;
    }

    public boolean isSrnOn() {
        return srnOn;
    }

    public void setSrnOn(boolean srnOn) {
        this.srnOn = srnOn;
    }

    public boolean isEnrolling() {
        return enrolling;
    }

    public void setEnrolling(boolean enrolling) {
        this.enrolling = enrolling;
    }

    public void setReset(boolean reset) {
        this.reset = reset;
    }

    public boolean isReset() {
        return reset;
    }

    public boolean isSmsControl() {
        return smsControl;
    }

    public void setSmsControl(boolean smsControl) {
        this.smsControl = smsControl;
    }

    public int getArmState() {
        return armState;
    }

    public void setArmState(int armState) {
        this.armState = armState;
    }

    @Override
    public String toString() {
        return "Gateway{" +
                "armState=" + armState +
                ", srnOn=" + srnOn +
                ", enrolling=" + enrolling +
                ", reset=" + reset +
                ", smsControl=" + smsControl +
                ", armDlyg='" + armDlyg + '\'' +
                ", almdlyg='" + almdlyg + '\'' +
                ", timeSrnOn=" + timeSrnOn +
                ", armDly=" + armDly +
                ", armDly2=" + armDly2 +
                ", deviceType=" + deviceType +
                ", pushID=" + pushID +
                ", tmSrnEnd=" + tmSrnEnd +
                ", tmArmDlyEnd=" + tmArmDlyEnd +
                ", tmAlmDlyEnd=" + tmAlmDlyEnd +
                ", alms=" + alms +
                ", RSSI='" + RSSI + '\'' +
                '}';
    }
}

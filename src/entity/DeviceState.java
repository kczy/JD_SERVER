package entity;

public class DeviceState {
    private Integer srnOn;           //警号是否在鸣叫
    private Integer erolling;        //iii.允许子设备入网，enrolling；只能开启，等待自动结束，不能手动停止；
    private Integer factoryReset;
    private Integer smsControl;
    private Integer armState;        //i.布撤防状态，armState；（0.撤防 1.在家布防 2.外出布防）
    private Integer systemLanguage;
    private Integer setTimeSrnOn;    //vi.报警时长设置，setTimeSrnOn；
    private Integer armDly;          //vii.布防延时设置，armDly；
    private Integer almDly;          //viii.报警延时，almDly；
    private Integer softap;
    private Integer armDlyg;             //iv.布防延时标记：armDlyg
    private Integer almDlyg;             //v.报警延时标记：almdlyg，
    private Integer gsmSimCheck;
    private Integer gsmSearchNetwork;
    private Integer pushAlarm;
    private Integer rssi;
    private Integer alms;
    private Integer gsmCsq;
    private Integer deviceType;
    private Integer pushID;
    private Integer tmSrnEnd;           //ix.报警声音剩余时长，tmSrnEnd；
    private Integer tmArmDlyEnd;         //x.布防延时剩余时长，tmArmDlyEnd
    private Integer tmAlmDlyEnd;         //xi.报警/进入延时剩余时长，tmAlmDlyEnd
    private Integer subDeviceModify;
    private Integer otaUpgrading;


    public Integer getSrnOn() {
        return srnOn;
    }

    public void setSrnOn(Integer srnOn) {
        this.srnOn = srnOn;
    }

    public Integer getErolling() {
        return erolling;
    }

    public void setErolling(Integer erolling) {
        this.erolling = erolling;
    }

    public Integer getFactoryReset() {
        return factoryReset;
    }

    public void setFactoryReset(Integer factoryReset) {
        this.factoryReset = factoryReset;
    }

    public Integer getSmsControl() {
        return smsControl;
    }

    public void setSmsControl(Integer smsControl) {
        this.smsControl = smsControl;
    }

    public Integer getArmState() {
        return armState;
    }

    public void setArmState(Integer armState) {
        this.armState = armState;
    }

    public Integer getSystemLanguage() {
        return systemLanguage;
    }

    public void setSystemLanguage(Integer systemLanguage) {
        this.systemLanguage = systemLanguage;
    }

    public Integer getSetTimeSrnOn() {
        return setTimeSrnOn;
    }

    public void setSetTimeSrnOn(Integer setTimeSrnOn) {
        this.setTimeSrnOn = setTimeSrnOn;
    }

    public Integer getArmDly() {
        return armDly;
    }

    public void setArmDly(Integer armDly) {
        this.armDly = armDly;
    }

    public Integer getAlmDly() {
        return almDly;
    }

    public void setAlmDly(Integer almDly) {
        this.almDly = almDly;
    }

    public Integer getSoftap() {
        return softap;
    }

    public void setSoftap(Integer softap) {
        this.softap = softap;
    }

    public Integer getArmDlyg() {
        return armDlyg;
    }

    public void setArmDlyg(Integer armDlyg) {
        this.armDlyg = armDlyg;
    }

    public Integer getAlmDlyg() {
        return almDlyg;
    }

    public void setAlmDlyg(Integer almDlyg) {
        this.almDlyg = almDlyg;
    }

    public Integer getGsmSimCheck() {
        return gsmSimCheck;
    }

    public void setGsmSimCheck(Integer gsmSimCheck) {
        this.gsmSimCheck = gsmSimCheck;
    }

    public Integer getGsmSearchNetwork() {
        return gsmSearchNetwork;
    }

    public void setGsmSearchNetwork(Integer gsmSearchNetwork) {
        this.gsmSearchNetwork = gsmSearchNetwork;
    }

    public Integer getPushAlarm() {
        return pushAlarm;
    }

    public void setPushAlarm(Integer pushAlarm) {
        this.pushAlarm = pushAlarm;
    }

    public Integer getRssi() {
        return rssi;
    }

    public void setRssi(Integer rssi) {
        this.rssi = rssi;
    }

    public Integer getAlms() {
        return alms;
    }

    public void setAlms(Integer alms) {
        this.alms = alms;
    }

    public Integer getGsmCsq() {
        return gsmCsq;
    }

    public void setGsmCsq(Integer gsmCsq) {
        this.gsmCsq = gsmCsq;
    }

    public Integer getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(Integer deviceType) {
        this.deviceType = deviceType;
    }

    public Integer getPushID() {
        return pushID;
    }

    public void setPushID(Integer pushID) {
        this.pushID = pushID;
    }

    public Integer getTmSrnEnd() {
        return tmSrnEnd;
    }

    public void setTmSrnEnd(Integer tmSrnEnd) {
        this.tmSrnEnd = tmSrnEnd;
    }

    public Integer getTmArmDlyEnd() {
        return tmArmDlyEnd;
    }

    public void setTmArmDlyEnd(Integer tmArmDlyEnd) {
        this.tmArmDlyEnd = tmArmDlyEnd;
    }

    public Integer getTmAlmDlyEnd() {
        return tmAlmDlyEnd;
    }

    public void setTmAlmDlyEnd(Integer tmAlmDlyEnd) {
        this.tmAlmDlyEnd = tmAlmDlyEnd;
    }

    public Integer getSubDeviceModify() {
        return subDeviceModify;
    }

    public void setSubDeviceModify(Integer subDeviceModify) {
        this.subDeviceModify = subDeviceModify;
    }

    public Integer getOtaUpgrading() {
        return otaUpgrading;
    }

    public void setOtaUpgrading(Integer otaUpgrading) {
        this.otaUpgrading = otaUpgrading;
    }

    @Override
    public String toString() {
        return "DeviceState{" +
                "srnOn=" + srnOn +
                ", erolling=" + erolling +
                ", factoryReset=" + factoryReset +
                ", smsControl=" + smsControl +
                ", armState=" + armState +
                ", systemLanguage=" + systemLanguage +
                ", setTimeSrnOn=" + setTimeSrnOn +
                ", armDly=" + armDly +
                ", almDly=" + almDly +
                ", softap=" + softap +
                ", armDlyg=" + armDlyg +
                ", almDlyg=" + almDlyg +
                ", gsmSimCheck=" + gsmSimCheck +
                ", gsmSearchNetwork=" + gsmSearchNetwork +
                ", pushAlarm=" + pushAlarm +
                ", rssi=" + rssi +
                ", alms=" + alms +
                ", gsmCsq=" + gsmCsq +
                ", deviceType=" + deviceType +
                ", pushID=" + pushID +
                ", tmSrnEnd=" + tmSrnEnd +
                ", tmArmDlyEnd=" + tmArmDlyEnd +
                ", tmAlmDlyEnd=" + tmAlmDlyEnd +
                ", subDeviceModify=" + subDeviceModify +
                '}';
    }
}

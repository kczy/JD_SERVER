package entity;

import java.util.Arrays;
import java.util.List;

public class Mcu {

    private boolean isAlarm=false;

    private byte[] IEEE;            //IEEE地址
    private byte[] shortAdress;     //短地址
    private byte endPoint;        //结束点
    private byte[] profileID;       //ProfileID
    private byte[] DeviceID;        //设备ID
    private byte[] zoneType;        //类型
    private String name;            //设备名字
    private byte[] currStatus;      //当前状态
    private byte[] currStatusStr;   //当前状态
    private byte[] set;             //设置
    private byte[] signal;          //信号强度

    private int IEEE2;            //IEEE地址
    private int shortAdress2;     //短地址
    private int endPoint2;        //结束点
    private int profileID2;       //ProfileID
    private int DeviceID2;        //设备ID
    private int zoneType2;        //类型
    private String name2;            //设备名字
    private int currStatus2;      //当前状态
    private int currStatusStr2;   //当前状态
    private int set2;             //设置
    private int signal2;          //信号强度


    public boolean isAlarm() {
        return isAlarm;
    }

    public void setAlarm(boolean alarm) {
        isAlarm = alarm;
    }

    public void setIEEE2(int IEEE2) {
        this.IEEE2 = IEEE2;
    }

    public void setShortAdress2(int shortAdress2) {
        this.shortAdress2 = shortAdress2;
    }

    public void setEndPoint2(int endPoint2) {
        this.endPoint2 = endPoint2;
    }

    public void setProfileID2(int profileID2) {
        this.profileID2 = profileID2;
    }

    public void setDeviceID2(int deviceID2) {
        DeviceID2 = deviceID2;
    }

    public void setZoneType2(int zoneType2) {

        this.zoneType2 = zoneType2;
        switch (zoneType2){
            case 0x0015://门磁
                this.name="门磁";
                break;
            case 0x000D://红外
                this.name="红外";
                break;

            case 0x0028://烟感
                this.name="烟感";
                break;
            case 0x002A://水感
                this.name="水感";
                break;
            case 0x002B://气感
                this.name="气感";
                break;
            case 0x0225://警号
                this.name="警号";
                break;
            case 0x8AAA://门铃
                this.name="门铃";
                break;
            case 0x8BBB://PGM
                this.name="PGM";
                break;
            case 0x002C://紧急按钮
                this.name="紧急按钮";
                break;
            case 0x002D://震动
                this.name="震动";
                break;
            default://遥控
                this.name="遥控";
        }
    }


    public String getName() {
        return name;
    }

    public String getName2() {
        return name2;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setName2(String name2) {
        this.name2 = name2;
    }

    public void setCurrStatus2(int currStatus2) {
        this.currStatus2 = currStatus2;
    }

    public void setCurrStatusStr2(int currStatusStr2) {
        this.currStatusStr2 = currStatusStr2;
    }

    public void setSet2(int set2) {
        this.set2 = set2;
    }

    public void setSignal2(int signal2) {
        this.signal2 = signal2;
    }

    public int getIEEE2() {
        return IEEE2;
    }

    public int getShortAdress2() {
        return shortAdress2;
    }

    public int getEndPoint2() {
        return endPoint2;
    }

    public int getProfileID2() {
        return profileID2;
    }

    public int getDeviceID2() {
        return DeviceID2;
    }

    public int getZoneType2() {
        return zoneType2;
    }

    public int getCurrStatus2() {
        return currStatus2;
    }

    public int getCurrStatusStr2() {
        return currStatusStr2;
    }

    public int getSet2() {
        return set2;
    }

    public int getSignal2() {
        return signal2;
    }

    public byte getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(byte endPoint) {
        this.endPoint = endPoint;
    }

    public void setIEEE(byte[] IEEE) {
        this.IEEE = IEEE;
    }

    public void setShortAdress(byte[] shortAdress) {
        this.shortAdress = shortAdress;
    }

    public void setProfileID(byte[] profileID) {
        this.profileID = profileID;
    }

    public void setDeviceID(byte[] deviceID) {
        DeviceID = deviceID;
    }

    public void setZoneType(byte[] zoneType) {
        this.zoneType = zoneType;
    }

    public void setCurrStatus(byte[] currStatus) {
        this.currStatus = currStatus;
    }

    public void setCurrStatusStr(byte[] currStatusStr) {
        this.currStatusStr = currStatusStr;
    }

    public void setSet(byte[] set) {
        this.set = set;
    }

    public void setSignal(byte[] signal) {
        this.signal = signal;
    }

    public byte[] getIEEE() {

        return IEEE;
    }

    public byte[] getShortAdress() {
        return shortAdress;
    }


    public byte[] getProfileID() {
        return profileID;
    }

    public byte[] getDeviceID() {
        return DeviceID;
    }

    public byte[] getZoneType() {
        return zoneType;
    }



    public byte[] getCurrStatus() {
        return currStatus;
    }

    public byte[] getCurrStatusStr() {
        return currStatusStr;
    }

    public byte[] getSet() {
        return set;
    }

    public byte[] getSignal() {
        return signal;
    }

    @Override
    public String toString() {
        return "Mcu{" +
                "zoneType=" + Arrays.toString(zoneType) +
                ", name='" + name + '\'' +
                '}';
    }
}

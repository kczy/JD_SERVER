package entity;

import com.google.gson.Gson;

import mqtt.MtClient;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.io.Serializable;

/**
 * Created by 78354 on 2017-10-26.
 */

public class DeviceAlarm implements Serializable{


    /**
     * name0 : 1
     * stat0 : 16
     * ieee0 : 5149013224124422
     * name1 : 1
     * stat1 : 16
     * ieee1 : 5149013224124422
     * name2 : 1
     * stat2 : 16
     * ieee2 : 5149013224124422
     * name3 : 1
     * stat3 : 16
     * ieee3 : 5149013224124422
     * name4 : 1
     * stat4 : 16
     * ieee4 : 5149013224124422
     * time : 1508984429
     */

    private int name0;
    private int stat0;
    private long ieee0;
    private int name1;
    private int stat1;
    private long ieee1;
    private int name2;
    private int stat2;
    private long ieee2;
    private int name3;
    private int stat3;
    private long ieee3;
    private int name4;
    private int stat4;
    private long ieee4;
    private long time;

    public int getName0() {
        return name0;
    }

    public void setName0(int name0) {
        this.name0 = name0;
    }

    public int getStat0() {
        return stat0;
    }

    public void setStat0(int stat0) {
        this.stat0 = stat0;
    }

    public long getIeee0() {
        return ieee0;
    }

    public void setIeee0(long ieee0) {
        this.ieee0 = ieee0;
    }

    public int getName1() {
        return name1;
    }

    public void setName1(int name1) {
        this.name1 = name1;
    }

    public int getStat1() {
        return stat1;
    }

    public void setStat1(int stat1) {
        this.stat1 = stat1;
    }

    public long getIeee1() {
        return ieee1;
    }

    public void setIeee1(long ieee1) {
        this.ieee1 = ieee1;
    }

    public int getName2() {
        return name2;
    }

    public void setName2(int name2) {
        this.name2 = name2;
    }

    public int getStat2() {
        return stat2;
    }

    public void setStat2(int stat2) {
        this.stat2 = stat2;
    }

    public long getIeee2() {
        return ieee2;
    }

    public void setIeee2(long ieee2) {
        this.ieee2 = ieee2;
    }

    public int getName3() {
        return name3;
    }

    public void setName3(int name3) {
        this.name3 = name3;
    }

    public int getStat3() {
        return stat3;
    }

    public void setStat3(int stat3) {
        this.stat3 = stat3;
    }

    public long getIeee3() {
        return ieee3;
    }

    public void setIeee3(long ieee3) {
        this.ieee3 = ieee3;
    }

    public int getName4() {
        return name4;
    }

    public void setName4(int name4) {
        this.name4 = name4;
    }

    public int getStat4() {
        return stat4;
    }

    public void setStat4(int stat4) {
        this.stat4 = stat4;
    }

    public long getIeee4() {
        return ieee4;
    }

    public void setIeee4(long ieee4) {
        this.ieee4 = ieee4;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public static void main(String[] args) throws MqttException {

            MtClient client=new MtClient();
            client.start();

            DeviceAlarm a=new DeviceAlarm();
            a.setName0(3);
            a.setTime(1508984429);
            Gson gson=new Gson();
            client.publish("HA_IAS/OUT/DEVICE_ALS_ALARM",gson.toJson(a));
        }

}

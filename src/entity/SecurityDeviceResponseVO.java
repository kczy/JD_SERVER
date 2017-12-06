package entity;

import java.util.Arrays;

public class SecurityDeviceResponseVO {
    //网关序列号
    private String sno;
    //返回结果标记，不使用
    private Integer tag;
    //返回结果长度，不使用
    private Integer length;
    //安全设备ip地址，使用
    private String address;
    //终端，使用
    private Integer endpoint;
    //profile参数，不使用
    private byte profile;
    //设备类型，与DeviceEnum相同，使用
    private Integer device;
    //设备名，使用
    private String name;
    //设备状态，不使用
    private Integer state;
    //ieee相当于mac地址，使用
    private String ieee;
    //sn数组，不使用
    private byte[] sn;
    //sn字符串，使用
    private String snStr;
    //是否在线，1=是，2=否，使用
    private Integer deviceOpenInd;
    //clusterId，不使用
    private Integer clusterId;
    //attrID，不使用
    private Integer attrID;
    //电量，使用
    private Integer electric;

    public void setElectric(Integer electric) {
        this.electric = electric;
    }

    public void setTag(Integer tag) {
        this.tag = tag;
    }

    public void setLength(Integer length) {
        this.length = length;
    }

    public void setDevice(Integer device) {
        this.device = device;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Integer getAttrID() {
        return attrID;
    }

    public void setAttrID(Integer attrID) {
        this.attrID = attrID;
    }

    public Integer getClusterId() {
        return clusterId;
    }

    public void setClusterId(Integer clusterId) {
        this.clusterId = clusterId;
    }

    public Integer getDeviceOpenInd() {
        //默认当做未开启
        this.deviceOpenInd = this.deviceOpenInd == null ? 2 : this.deviceOpenInd;
        return this.deviceOpenInd;
    }

    public void setDeviceOpenInd(Integer deviceOpenInd) {
        this.deviceOpenInd = deviceOpenInd;
    }

    public int getTag() {
        return tag;
    }

    public void setTag(int tag) {
        this.tag = tag;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(Integer endpoint) {
        this.endpoint = endpoint;
    }

    public byte getProfile() {
        return profile;
    }

    public void setProfile(byte profile) {
        this.profile = profile;
    }

    public Integer getDevice() {
        return device;
    }

    /*public String getName() {
        if (name == null) {
            Integer deviceCode = this.getDevice();
            if (deviceCode != null) {
                DeviceEnum deviceEnum = DeviceEnum.getDeviceByCode(deviceCode);
                if (deviceEnum != null) {
                    name = deviceEnum.getName();
                }
            }
        }
        return name;
    }*/
    public String getName() {
        if (name == null) {
            Integer deviceCode = this.getDevice();
            if (deviceCode != null) {
                DeviceEnum deviceEnum = DeviceEnum.getDeviceByCode(deviceCode);
                if (deviceEnum != null) {
                    name = deviceEnum.getName();
                }
            }
            this.setDevice(DmDeviceEnum.mapping(deviceCode));
        }
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getIeee() {
        return ieee;
    }

    public void setIeee(String ieee) {
        this.ieee = ieee;
    }

    public byte[] getSn() {
        return sn;
    }

    public void setSn(byte[] sn) {
        this.sn = sn;
    }

    public String getSnStr() {
        return snStr;
    }

    public void setSnStr(String snStr) {
        this.snStr = snStr;
    }

    public String getSno() {
        return sno;
    }

    public void setSno(String sno) {
        this.sno = sno;
    }

    public Integer getElectric() {
        return electric;
    }

    @Override
    public String toString() {
        return "SecurityDeviceResponseVO{" +
                "tag=" + tag +
                ", length=" + length +
                ", address='" + address + '\'' +
                ", endpoint=" + endpoint +
                ", profile=" + profile +
                ", device=" + device +
                ", name='" + name + '\'' +
                ", state=" + state +
                ", ieee='" + ieee + '\'' +
                ", sn=" + Arrays.toString(sn) +
                ", snStr='" + snStr + '\'' +
                ", deviceOpenInd=" + deviceOpenInd +
                ", clusterId=" + clusterId +
                ", attrID=" + attrID +
                ", electric=" + electric +
                '}';
    }
}

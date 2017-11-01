package hansen;


import entity.Mcu;

import java.util.List;

public interface EventNotify {

    /**
     * 链接失效
     */
    public void connectBreak();

    /**
     * 链接超时
     */
    public void connectTimeOut();

    /**
     * 接收设备列表成功
     * @param  -设备列表
     */
    public void NotifyMcuList(List<Mcu> list);

    /**
     * 上报成功后放回MCU对象
     * @param mcu
     */
    public void NotifyMcuInfo(Mcu mcu);

    /**
     * 告警
     */
    public void alarm();


}

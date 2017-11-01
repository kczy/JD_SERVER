package mqtt;

import entity.Mcu;
import hansen.EventNotify;
import hansen.Outer;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.List;
import java.util.logging.Logger;

public class PushCallback implements MqttCallback, EventNotify {
    static Logger log = Logger.getLogger(PushCallback.class.getName());

    MtClient client;
    public PushCallback(MtClient client){
        this.client=client;
    }
    private Outer outer;

    @Override
    public void connectionLost(Throwable throwable) {
        // 连接丢失后，一般在这里面进行重连
        log.warning("【mqtt连接断开】");
    }

    @Override
    public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
        // subscribe后得到的消息会执行到这里面
        System.out.println("接收消息主题 : " + topic);
        System.out.println("接收消息Qos : " + mqttMessage.getQos());
        System.out.println("接收消息内容 : " + new String(mqttMessage.getPayload()));
        //1注册网关
        if("HA_IAS/OUT/DEVICE_LIST".equals(topic)){
            //接收到网关就注册Outer
            outer=new Outer(new String(mqttMessage.getPayload()),this);
            if (outer!=null){
                log.info("【接收到序列号：["+mqttMessage.getPayload()+"]outer注册成功！】");
                client.publish("HA_IAS/IN/ADD_DEVICE","HA_IAS/IN/ADD_DEVICE");
            }
        }

        //2 添加设备
        if("HA_IAS/IN/ADD_DEVICE".equals(topic)){
            System.out.println("添加子设备");
            outer.addMCU();
        }
        //3请求子设备列表
        if("HA_IAS/PRODUCT_ID/OUT/DEVICE_LIST".equals(topic)){
            System.out.println("子设备上报，准备查询子设备列表");
            outer.getMCUList();
        }
        //4删除设备
        if("HA_IAS/PRODUCT_ID/OUT/DELETE_DEVICE".equals(topic)){
            outer.deleteMCU(new String(mqttMessage.getPayload()));
        }

    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
        System.out.println("deliveryComplete---------" + iMqttDeliveryToken.isComplete());
    }

    @Override
    public void connectBreak() {

    }

    @Override
    public void connectTimeOut() {

    }

    @Override
    public void NotifyMcuList(List<Mcu> list) {

    }

    @Override
    public void NotifyMcuInfo(Mcu mcu) {

    }

    @Override
    public void alarm() {

    }
}

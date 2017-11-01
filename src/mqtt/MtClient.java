package mqtt;

import org.apache.mina.core.session.IoSession;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.concurrent.ScheduledExecutorService;
import java.util.logging.Logger;

public class MtClient {

    static Logger log = Logger.getLogger(MtClient.class.getName());

    private IoSession session;

    /**
     * 基本服务器连接信息
     */
    public static final String HOST = "tcp://kczy.mqtt.iot.gz.baidubce.com:1883";
    private static final String clientid = "CD001ZM0099";
    private String userName = "kczy/cd001";
    private String passWord = "xezbiXaLrXMtHcPcFBfCEySy1yoXbmJ9UoFVJR7E/FI=";


    public static final String TOPIC1 = "HA_IAS/OUT/DEVICE_LIST";
    public static final String TOPIC2 = "HA_IAS/IN/ADD_DEVICE";

    public MtClient(){

    }
    public MtClient(IoSession session){
        this.session=session;
    }

    private MqttClient client;

    private MqttConnectOptions options;

    private ScheduledExecutorService scheduler;


    public void start(){

        try {
            // host为主机名，clientid即连接MQTT的客户端ID，一般以唯一标识符表示，MemoryPersistence设置clientid的保存形式，默认为以内存保存
            client = new MqttClient(HOST, clientid, new MemoryPersistence());
            // MQTT的连接设置
            options = new MqttConnectOptions();
            // 设置是否清空session,这里如果设置为false表示服务器会保留客户端的连接记录，这里设置为true表示每次连接到服务器都以新的身份连接
            options.setCleanSession(true);
            // 设置连接的用户名  设置连接的密码
            options.setUserName(userName);
            options.setPassword(passWord.toCharArray());
            // 设置超时时间 单位为秒
            options.setConnectionTimeout(20);
            // 设置会话心跳时间 单位为秒 服务器会每隔1.5*20秒的时间向客户端发送个消息判断客户端是否在线，但这个方法并没有重连的机制
            options.setKeepAliveInterval(30);
            // 设置回调
            client.setCallback(new PushCallback(this));

//            MqttTopic topic = client.getTopic(TOPIC);
//            //setWill方法，如果项目中需要知道客户端是否掉线可以调用该方法。设置最终端口的通知消息
//            options.setWill(topic, "close".getBytes(), 2, true);
            client.connect(options);
            if(client.isConnected()){

                //订阅此主题，就可以接受其发布的信息
                client.subscribe("HA_IAS/IN/ADD_DEVICE");
                client.subscribe("HA_IAS/OUT/DEVICE_LIST");
                client.subscribe("HA_IAS/OUT/DEVICE_ALS_ALARM");//HA_IAS/OUT/DEVICE_ALS_ALARM

            }
          /*if(client.isConnected()){
                MqttMessage message = new MqttMessage("HA_IAS/IN/ADD_DEVICE".getBytes());
                client.publish("HA_IAS/IN/ADD_DEVICE",message);
           }*/
        } catch (MqttException e) {
            e.printStackTrace();
        }

    }
    public void publish(String topic,String content){
        if(client.isConnected()){
            try {
                MqttMessage message = new MqttMessage(content.getBytes());
                client.publish(topic,message);
            } catch (MqttException e) {
                e.printStackTrace();
            }

        }
    }
}

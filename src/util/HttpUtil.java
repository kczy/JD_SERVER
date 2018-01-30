package util;

import org.apache.http.*;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;


import java.io.IOException;
import java.io.InputStream;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class HttpUtil {
     static final  String CHARSET="UTF-8";


    public static void main(String[] args) {
        try {
            //doPostStr("http://127.0.0.1:8080/device/elder/security/security-device-jiade-impl","{{},{}}");

           //HttpUtil.doPostStr("http://120.77.215.202:10055/device/elder/security/security-device-jiade-impl", "{\"type\":0,\"securityDeviceResponseVOList\":[{\"sno\":\"303030303631\",\"address\":\"/117.136.63.142:17153\",\"endpoint\":1,\"profile\":0,\"device\":1028,\"name\":\"遥控2\",\"state\":0,\"ieee\":\"d5e82a10004b1200\",\"clusterId\":0,\"electric\":0}]}");
           // HttpUtil.doPostStr("http://171.208.222.97:9080/device/elder/security/security-device-jiade-impl", "{\"type\":0,\"securityDeviceResponseVOList\":[{\"sno\":\"303030303539\",\"address\":\"/223.104.254.208:23458\",\"endpoint\":0,\"profile\":0,\"device\":1028,\"name\":\"无线紧急按键\",\"state\":0,\"ieee\":\"d33a000000000000\",\"clusterId\":0,\"electric\":0},{\"sno\":\"303030303539\",\"address\":\"/223.104.254.208:23458\",\"endpoint\":0,\"profile\":0,\"device\":1026,\"name\":\"烟雾报警器\",\"state\":0,\"ieee\":\"5000c00000000000\",\"clusterId\":0,\"electric\":0},{\"sno\":\"303030303539\",\"address\":\"/223.104.254.208:23458\",\"endpoint\":0,\"profile\":0,\"device\":263,\"name\":\"红外感应器\",\"state\":0,\"ieee\":\"10b8e00000000000\",\"clusterId\":0,\"electric\":0}]}");
            //HttpUtil.doPostStr("http://lib18610386362.oicp.net/icare-device-api/device/elder/security/security-device-jiade-impl", "{\"type\":1,\"securityDeviceResponseVOList\":[{\"sno\":\"303030303539\",\"address\":\"/223.104.255.137:4410\",\"endpoint\":0,\"profile\":0,\"device\":1026,\"name\":\"烟雾报警器\",\"state\":7,\"ieee\":\"5000c00000000000\",\"clusterId\":7,\"electric\":0}]}");
            HttpUtil.doPostStr("http://lib18610386362.oicp.net/icare-device-api/device/elder/security/security-device-jiade-impl", "{\"type\":1,\"securityDeviceResponseVOList\":\n" +
                    "[{\"sno\":\"303030303539\",\"address\":\"/223.104.254.126:57343\",\"endpoint\":0,\"profile\":0,\"device\":1028,\"name\":\"无线紧急按键\",\"state\":7,\"ieee\":\"d3aa000000000000\",\"clusterId\":0,\"electric\":0}{\"sno\":\"303030303539\",\"address\":\"/223.104.254.126:57343\",\"endpoint\":0,\"profile\":0,\"device\":1028,\"name\":\"无线紧急按键\",\"state\":0,\"ieee\":\"d3aa000000000000\",\"clusterId\":0,\"electric\":0},\n" +
                    "" +
                    "]}");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void doPostStr(String url,String dataJson) throws IOException {
        CloseableHttpClient hc= HttpClients.createDefault();
        HttpPost httpPost =  new HttpPost(url);

//        httpGet.setHeader("Accept", "Accept text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
//        httpGet.setHeader("Accept-Charset", "GB2312,utf-8;q=0.7,*;q=0.7");
//        httpGet.setHeader("Accept-Encoding", "gzip, deflate");
//        httpGet.setHeader("Accept-Language", "zh-cn,zh;q=0.5");
//        httpGet.setHeader("Connection", "keep-alive");

        httpPost.setHeader("app-code","OM");
        httpPost.setHeader("app-version","1");
        httpPost.setHeader("device-type","Android");
        httpPost.setHeader("device-sn","552b9f5e559ad5c9e7450a93");
        httpPost.setHeader("device-os-version","4.2.3");
        httpPost.setHeader("x","104.064485");
        httpPost.setHeader("y","104.064485");
        httpPost.setHeader("ip","104.064485");
        httpPost.setHeader("address","四川省成都市");
        httpPost.setHeader("Connection","keep-alive");

        //设置参数
        List<NameValuePair> params = new ArrayList<NameValuePair>();

        params.add(new BasicNameValuePair("params",dataJson));
        params.add(new BasicNameValuePair("tm",System.currentTimeMillis()+""));
        params.add(new BasicNameValuePair("au","lisihang"));
        params.add(new BasicNameValuePair("tkn","lisihang"));

        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params, Consts.UTF_8);
        httpPost.setEntity(entity);

        HttpResponse response = hc.execute(httpPost);
        if(response != null){
            HttpEntity resEntity = response.getEntity();
            if(resEntity != null){
                InputStream in=resEntity.getContent();
                byte[] temp=new byte[1024];
                int len=0;
                while((len=in.read(temp))!=-1){
                    System.out.println(new String(temp));
                }
            }
        }

       // return result;
    }
}

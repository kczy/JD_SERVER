package util;

import conf.Configuration;
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
import java.util.logging.Level;

public class HttpUtil {

    static final  String CHARSET="UTF-8";
    public static void main(String[] args) {
        try {


         HttpUtil.doPostStr(
                 Configuration.DEVICE_LIST,
                 "{\"type\":2,\"securityDeviceResponseVOList\":[{\"sno\":\"303030303539\",\"profile\":0,\"state\":0}]}"
         );



        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void doPostStr(String url,String dataJson) throws IOException {

        //fileLogger.logp(Level.INFO, "ewrwerwer", "werwerw", "记录log?");

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

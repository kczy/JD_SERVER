package util;

import entity.Gateway;
import entity.Mcu;
import entity.SecurityDeviceResponseVO;
import org.apache.mina.core.session.IoSession;

import java.util.ArrayList;
import java.util.List;

public class ParseUtil {

    public static String getID(byte[] bts){
        StringBuilder sb=new StringBuilder();
        //嘉德的是40>=i<72，因为太长了 这里只用最后12个
        for(int i=66;i<72;i++){
            sb.append(BaseUtil.encodeHexStr(bts[i]));
        }
        return sb.toString();
    }
    public static String getID2(byte[] bts){
        StringBuilder sb=new StringBuilder();
        int num=0;
        for(int i=40;i<72;i++){
            num+=bts[i]&0xFF;
        }
        return num+"";
    }

    public static Gateway getGateway(byte[] bts) {
        Gateway gateway=new Gateway();
        byte[] bs=BaseUtil.byte2CharArr(bts[9]);////0b  0 0 1 1 1 1 1 1

        gateway.setSrnOn(bs[8-1]==1?true:false);
        gateway.setEnrolling(bs[8-2]==1?true:false);
        gateway.setReset(bs[8-3]==1?true:false);
        gateway.setSmsControl(bs[8-4]==1?true:false);
        gateway.setArmState(Integer.parseInt(bs[8-5]+""+bs[8-6],2));
        //将一个字符串转换为二进制的字符数组表现形式



        Integer timeSrnOn=bts[11]+bts[12];
        gateway.setTimeSrnOn(timeSrnOn);

        Integer armDly=bts[13]+bts[14];
        gateway.setArmDly(armDly);

        Integer armDly2=bts[15]+bts[16];
        gateway.setArmDly2(armDly2);

        int pushId=bts[22];
        gateway.setPushID(pushId);


        return gateway;
    }

    public static Mcu getMuc(byte[] bts) {

        Mcu mcu=new Mcu();

        mcu.setIEEE(new byte[8]);
        for(int i=9,j=0;i<17;i++,j++){
            mcu.getIEEE()[j]=bts[i];
        }

        mcu.setShortAdress(new byte[2]);
        mcu.getShortAdress()[0]=bts[17];
        mcu.getShortAdress()[1]=bts[18];

        mcu.setEndPoint(bts[19]);

        mcu.setProfileID(new byte[2]);
        mcu.getProfileID()[0]=bts[20];
        mcu.getProfileID()[1]=bts[21];

        mcu.setDeviceID(new byte[2]);
        mcu.getDeviceID()[0]=bts[22];
        mcu.getDeviceID()[1]=bts[23];

        mcu.setZoneType(new byte[2]);
        mcu.getZoneType()[0]=bts[24];
        mcu.getZoneType()[1]=bts[25];

        mcu.setZoneType2(BaseUtil.getInt(mcu.getZoneType()));
        return mcu;
    }


    public static List<SecurityDeviceResponseVO> getDevices(byte[] bts,IoSession session) {
        List<SecurityDeviceResponseVO> responseVOList = new ArrayList<>();
        //resultArr获得列表数据包，开始解析
        for(int i=0;i<bts[11];i++){//设备个数，循环(有多少设备就循环多少次)
            SecurityDeviceResponseVO stateVO=new SecurityDeviceResponseVO();
            //设置网关地址
            stateVO.setSno((String) session.getAttribute("sno"));
            //安全设备的IP地址
            stateVO.setAddress(session.getRemoteAddress().toString());
            //设置ieee地址
            StringBuilder sb=new StringBuilder();
            for(int j=0;j<8;j++){
                if((bts[12+j+i*52]&0xFF)<16){
                    sb.append("0");
                }
                sb.append(Integer.toHexString(bts[12+j+i*52]&0xFF));
            }
            stateVO.setIeee(sb.toString());
            //设备类型zone_type
            Integer type=bts[28+i*52]<<8|bts[27+i*52]&0xff;
            stateVO.setDevice(type);
            //设置设备名
            stateVO.setName(stateVO.getName());
            //设置endpoint
            stateVO.setEndpoint(bts[22+i*52]&0xff);
            /*
                是否在线，1=是，2=否，使用
                private Integer deviceOpenInd;
            */
            byte[] bArr=byte2CharArr(bts[61+i*52]&0xff);
            int isOn=bArr[7-6]==1?6:0;
            isOn=bArr[7-7]==1?7:isOn;
            stateVO.setState(isOn);
            stateVO.setClusterId(isOn);

            int isElectric=bArr[7-2]==1?1:0;
            stateVO.setElectric(isElectric);
            //加入
            responseVOList.add(stateVO);
        }

        //System.out.println(responseVOList);

        return responseVOList;
    }

    public static byte[] byte2CharArr(int n){
        char[] cs = Integer.toBinaryString(n).toCharArray();
        byte[] bs=new byte[8];
        //1000
        for(int i=cs.length-1,j=0;i>=0;i--,j++){
            bs[j]= (byte) ((cs[i]=='1')?1:0);
        }
        for(int i=0,j=7;i<j;i++,j--){
            byte b;
            b=bs[i];
            bs[i]=bs[j];
            bs[j]=b;
        }
        return bs;
    }

}

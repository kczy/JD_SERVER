package util;

import entity.DeviceState;
import entity.Gateway;
import entity.Mcu;
import entity.SecurityDeviceResponseVO;
import org.apache.mina.core.session.IoSession;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ParseUtil {

    public static int GetGwType(byte[] bts){
        return bts[113]==0x31?Constant.SESSION_ATTR_VAL_GW_TYPE_ZIGBEE:Constant.SESSION_ATTR_VAL_GW_TYPE_RF;
    }

    public static String getID(byte[] bts){
        StringBuilder sb=new StringBuilder();
        //嘉德的是40>=i<72，因为太长了 这里只用最后12个
        for(int i=(66);i<(72);i++){
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
            stateVO.setSno((String) session.getAttribute(Constant.SESSION_ATTR_KEY_GW_SN));
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

            int isElectric=bArr[7-2]==1?2:0;
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

    public static DeviceState parseStatus(byte[] bts) {

        DeviceState deviceState=  new DeviceState();

        bts= Arrays.copyOfRange(bts,9,33);

        byte[] bs=byte2CharArr(bts[0]&0xff);
        deviceState.setSrnOn(bs[8-1]==1?1:0);//是否告警
        deviceState.setErolling(bs[8-2]==1?1:0);//是否开网
        deviceState.setFactoryReset(bs[8-3]==1?1:0);//是否重置
        deviceState.setSmsControl(bs[8-4]==1?1:0);
        deviceState.setArmState(Integer.parseInt(bs[8-6]+""+bs[8-5],2));

        deviceState.setSystemLanguage(bts[1]&0xff);//设置系统语言
        deviceState.setSetTimeSrnOn(Integer.parseInt((bts[2]&0xff)+""+(bts[3]&0xff),10));
        deviceState.setArmDly(Integer.parseInt((bts[4]&0xff)+""+(bts[5]&0xff),10));
        deviceState.setAlmDly(Integer.parseInt((bts[6]&0xff)+""+(bts[7]&0xff),10));

        byte[] bs8=byte2CharArr(bts[8]&0xff);
        byte[] bs9=byte2CharArr(bts[9]&0xff);
        bs=new byte[16];
        for (int i = 0; i < bs8.length; i++) {
            bs[i]=bs8[i];
        }
        for (int i = 8,j=0; j < bs9.length; i++,j++) {
            bs[i]=bs9[j];
        }
        deviceState.setSoftap(bs[16-1]==1?1:0);
        deviceState.setArmDlyg(bs[16-2]==1?1:0);
        deviceState.setAlmDlyg(bs[16-3]==1?1:0);
        deviceState.setOtaUpgrading(bs[16-4]==1?1:0);
        deviceState.setGsmSimCheck(bs[16-5]==1?1:0);
        deviceState.setGsmSearchNetwork(bs[16-6]==1?1:0);
        deviceState.setPushAlarm(bs[16-7]==1?1:0);
        //deviceState.setArmState(Integer.parseInt(bs[8-6]+""+bs[8-5],2));
        String rssiStr=bs[16-10]+""+bs[16-9]+""+bs[16-8];
        Integer rssiInt=Integer.parseInt(rssiStr,2);
        deviceState.setRssi(rssiInt);
        //deviceState.setAlms(Integer.parseInt(""+(bts[10]&0xff),16));
        deviceState.setAlms(bts[10]&0xff);
        deviceState.setGsmCsq(bts[11]&0xff);
        deviceState.setDeviceType(bts[12]&0xff);
        deviceState.setPushID(bts[13]&0xff);
//        String tmSrnEndStr = (bts[14]&0xff)+""+(bts[15]&0xff);
//        Integer tmSrnEndInt= Integer.parseInt(tmSrnEndStr,16);
        deviceState.setTmSrnEnd(Integer.parseInt((bts[14]&0xff)+""+(bts[15]&0xff),10));
        deviceState.setTmArmDlyEnd(Integer.parseInt((bts[16]&0xff)+""+(bts[17]&0xff),10));
        deviceState.setTmAlmDlyEnd(Integer.parseInt((bts[18]&0xff)+""+(bts[19]&0xff),10));

        return deviceState;
    }
}

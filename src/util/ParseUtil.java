package util;

import entity.Gateway;
import entity.Mcu;

import java.util.ArrayList;
import java.util.List;

public class ParseUtil {

    public static String getID(byte[] bts){
        StringBuilder sb=new StringBuilder();
        for(int i=40;i<72;i++){
            sb.append(BaseUtil.encodeHexStr(bts[i]));
        }
        return sb.toString();
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

    public static List<Mcu> getMcus(byte[] bts) {

        List<Mcu> mcus=new ArrayList<Mcu>();
        for(int i=0;i<bts[10];i++){//第十一字节是设备个数，因此循环
            System.out.println("子设备列表第"+(i+1)+"个设备信息:"+mcus.get(i));
            Mcu mcu=new Mcu();
            mcu.setIEEE(new byte[8]);
            for(int j=0;j<8;j++){
                mcu.getIEEE()[j]=bts[11+j+i*52];
            }

            mcu.setZoneType(new byte[2]);
            mcu.getZoneType()[0]=bts[26];
            mcu.getZoneType()[1]=bts[27];
            mcu.setZoneType2(BaseUtil.getInt(mcu.getZoneType()));

            mcus.add(mcu);
        }
        return mcus;
    }
}

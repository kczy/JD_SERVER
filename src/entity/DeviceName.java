package entity;

public class DeviceName {
    public static final int NULL_SENSOR = 0;/*unknow*/
    public static final int IR_SENSOR=0x000D;//红外
    public static final int GAS_SENSOR=0x002B;/*气体*/
    public static final int SOS_SENSOR=0x002C;/*紧急按钮*/
    public static final int SMF_SENSOR=0x0028;/*烟雾*/
    public static final int DOS_SENSOR=0x0015;/*门磁*/
    public static final int WTS_SENSOR=0x002A;/*水浸*/
    public static final int ZRC_SENSOR=0;/*控制器，同SOS*/
}

    /**
     case 0x0015://门磁
     case 0x000D://红外
     case 0x0028://烟感
     case 0x002A://水感
     case 0x002B://气感
     case 0x0225://警号
     case 0x8AAA://门铃
     case 0x8BBB://PGM
     case 0x002C://紧急按钮
     case 0x002D://震动
     */


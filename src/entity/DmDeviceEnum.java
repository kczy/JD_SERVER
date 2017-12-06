package entity;

public enum DmDeviceEnum {

    //微服
    DM_DEVICE_ENUM_0104(0x0104, 24, "xx设备"),
    DM_DEVICE_ENUM_0107(0x0107, 25, "红外感应器"),
    DM_DEVICE_ENUM_0108(0x0108, 26, "门磁感应器"),
    DM_DEVICE_ENUM_010A(0x010A, 27, "燃气报警器"),
    DM_DEVICE_ENUM_0109(0x0109, 28, "漏水感应器"),
    DM_DEVICE_ENUM_0102(0x0402, 29, "烟雾报警器"),
    DM_DEVICE_ENUM_0404(0x0404, 30, "无线紧急按键"),
    DM_DEVICE_ENUM_0403(0x0403, 31, "声光报警器"),
    //嘉德
    DM_DEVICE_ENUM_010F(0x010F,34,"遥控1"),
    DM_DEVICE_ENUM_0D00(0x000D,35,"红外感应器"),
    DM_DEVICE_ENUM_1501(0x0115,36,"门磁感应器"),
    DM_DEVICE_ENUM_002B(0x002B,37,"燃气报警器"),
    DM_DEVICE_ENUM_002A(0x002A,38,"漏水感应器"),
    DM_DEVICE_ENUM_0028(0x0028,39,"烟雾报警器"),
    DM_DEVICE_ENUM_0225(0x0225,40,"无线紧急按键"),
    DM_DEVICE_ENUM_8AAA(0x8AAA,41,"门铃感应器"),
//    DM_DEVICE_ENUM_0115(0x0115,"遥控2"),
//    DM_DEVICE_ENUM_021D(0x021D,"遥控3"),


    ;




    private Integer code;       //获取到的设备16进制 转成十进制
    private Integer id;         //设备型号ID
    private String name;        //设备型号名称

    DmDeviceEnum(Integer code, Integer id, String name) {
        this.code = code;
        this.id = id;
        this.name = name;
    }

    /**
     *  根据code 获取
     * @param code
     * @return
     */
    public static DmDeviceEnum getDmDeviceEnum(Integer code){
        DmDeviceEnum[] dmDeviceEnums = DmDeviceEnum.values();
        for(DmDeviceEnum dmDeviceEnum : dmDeviceEnums) {
            if(dmDeviceEnum.getCode().equals(code)){
                return dmDeviceEnum;
            }
        }
        return null;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static Integer mapping(Integer code){

        if(code == 0x010F) {
            return 0x0104;
        }
        if(code == 0x000D) {
            return 0x0107;
        }

        if(code == 0x0115) {
            return 0x0108;
        }
        if(code == 0x002B) {
            return 0x010A;
        }
        if(code == 0x002A) {
            return 0x0109;
        }
        if(code == 0x0028) {
            return 0x0402;
        }
        if(code == 0x0225) {
            return 0x0404;
        }

        if(code == 0x8AAA) {
            return 0x0403;
        }
        return 0;
    }
}

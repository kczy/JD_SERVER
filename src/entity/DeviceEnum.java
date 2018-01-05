package entity;

public enum DeviceEnum {
    DEVICE_ENUM_0104(0x0104, "xx设备"),
    DEVICE_ENUM_0107(0x0107, "红外感应器"),
    DEVICE_ENUM_0108(0x0108, "门磁感应器"),
    DEVICE_ENUM_010A(0x010A, "燃气报警器"),
    DEVICE_ENUM_0109(0x0109, "漏水感应器"),
    DEVICE_ENUM_0102(0x0402, "烟雾报警器"),
    DEVICE_ENUM_0404(0x0404, "无线紧急按键"),

    DEVICE_ENUM_1501(0x0015,"门磁感应器"),
    DEVICE_ENUM_0D00(0x000D,"红外感应器"),
    DEVICE_ENUM_0028(0x0028,"烟雾报警器"),
    DEVICE_ENUM_002A(0x002A,"漏水感应器"),
    DEVICE_ENUM_002B(0x002B,"燃气报警器"),
    DEVICE_ENUM_0225(0x002C,"无线紧急按键"),
    DEVICE_ENUM_8AAA(0x8AAA,"门铃感应器"),
    DEVICE_ENUM_010F(0x010F,"遥控1"),
    DEVICE_ENUM_0115(0x0115,"遥控2"),
    DEVICE_ENUM_021D(0x021D,"遥控3"),
    ;
    private int code;
    private String name;

    private DeviceEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public static DeviceEnum getDeviceByCode(int code) {
        DeviceEnum[] deviceEnumArr = DeviceEnum.values();
        for (DeviceEnum deviceEnum : deviceEnumArr) {
            if (deviceEnum.getCode() == code) {
                return deviceEnum;
            }
        }

        return null;
    }

    public static DeviceEnum getDeviceByName(String name) {
        DeviceEnum[] deviceEnumArr = DeviceEnum.values();
        for (DeviceEnum deviceEnum : deviceEnumArr) {
            if (deviceEnum.getName().equals(name)) {
                return deviceEnum;
            }
        }
        return null;
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public static void main(String[] args) {
        int c = 5397;
        String name = "烟雾报警器";

        DeviceEnum deviceEnum = DeviceEnum.getDeviceByCode(c);
        deviceEnum = DeviceEnum.getDeviceByName(name);

        System.out.println(deviceEnum.getCode() + "-" + deviceEnum.getName());
    }



}

package entity;

import java.util.List;

public class JiadeParams {
    private  int type;
    private List<SecurityDeviceResponseVO> securityDeviceResponseVOList;


    public JiadeParams(int type, List<SecurityDeviceResponseVO> securityDeviceResponseVOList) {
        this.type = type;
        this.securityDeviceResponseVOList = securityDeviceResponseVOList;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public List<SecurityDeviceResponseVO> getSecurityDeviceResponseVOList() {
        return securityDeviceResponseVOList;
    }

    public void setSecurityDeviceResponseVOList(List<SecurityDeviceResponseVO> securityDeviceResponseVOList) {
        this.securityDeviceResponseVOList = securityDeviceResponseVOList;
    }
}

package entity;

public class Package {

    private String head;
    private String contentLength;
    private String cmd;
    private String packageNumber;
    private String flag;
    private String action;
    private String value;
    private String checkSum;


    public void setHead(String head) {
        this.head = head;
    }

    public String getHead() {

        return head;
    }

    public String getContentLength() {
        return contentLength;
    }

    public String getCmd() {
        return cmd;
    }

    public String getPackageNumber() {
        return packageNumber;
    }

    public String getFlag() {
        return flag;
    }

    public String getAction() {
        return action;
    }

    public String getValue() {
        return value;
    }

    public String getCheckSum() {
        return checkSum;
    }
}

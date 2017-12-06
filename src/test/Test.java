package test;

import util.BaseUtil;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.logging.Logger;

public class Test {
    static Logger logger=Logger.getLogger(Test.class.getName());

    static Socket socket=null;
    static OutputStream out;
    static InputStream in;


    public static void main(String[] args) throws IOException {
        Scanner scan=new Scanner(System.in);
        boolean flag=false;
        while(!flag){
            System.out.println("\n请输入指令:");
            int cmd=scan.nextInt();
            switch (cmd){
                case 0:
                    init();
                    break;
                case 1:
                    addDevice();
                    break;
                case 2:
                    delDevice(socket,"110");
                    break;
                case 3:
                    deviceList();
                    break;
                case 4:

                    break;
                case 5:break;
                default:
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    flag=true;
                    break;
            }
        }

    }
    public static void init(){
        try {
            socket=new Socket(InetAddress.getLocalHost(),10051);
            out=new BufferedOutputStream(socket.getOutputStream());
            in=new BufferedInputStream(socket.getInputStream());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void deviceList(){
        logger.info("设备列表");
        byte[] cmd={-1,-1,0,5,51,0,0,0,0};
        try {
            out.write(cmd);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void delDevice(Socket socket,String no){
        logger.info("删除设备");
        byte[] cmd={-1,-1,0,5,52,0,0,0,0};
        try {
            out.write(cmd);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void addDevice(){
        logger.info("添加设备");
        byte[] cmd={-1,-1,0,5,50,0,0,0,0};
        try {
            out.write(cmd);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

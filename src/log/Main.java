package log;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {

    private static Logger fileLogger;

    static {
        fileLogger = Logger.getLogger(Main.class.getName());
        fileLogger.setLevel(Level.INFO);
        Handler[] hs = fileLogger.getHandlers();
        for (Handler h : hs) {
            h.close();
            fileLogger.removeHandler(h);
        }
        try {
            //文件 日志文件名为mylog 日志最大写入为4000个字节 保存5天内的日志文件 如果文件没有达到规定大小则将日志文件添加到已有文件
            CustomFileStreamHandler fh = new CustomFileStreamHandler("E://mylog", 0, 1000, true);
            fh.setEncoding("UTF-8");
            fh.setFormatter(new CustomFormatter());
            fileLogger.setUseParentHandlers(false);
            fileLogger.addHandler(fh);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 返回一个文件记录实例
     */
    public static synchronized Logger getFileLogger() {
        return fileLogger;
    }

    public static void writeFileLogger(String info){
        getFileLogger().log(Level.INFO, info);
        //getFileLogger().logp(Level.INFO, sourceClass, sourceMethod, info);

    }
    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            fileLogger.logp(Level.INFO, "ewrwerwer", "werwerw", "记录log?");
        }
            /*String name = "insert into doc_huanxin_chatting_message (content,messageTimestamp,sender,addressee,timeStr,fromPhone,toPhone,questionId)";
            String sqlTemp = name.substring(0,50);
            System.out.println(sqlTemp);*/
    }
}

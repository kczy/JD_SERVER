package log;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.security.AccessController;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class CustomFormatter extends Formatter {

    //当前是第几条记录
    private int logCount;
    //时间
    private Date dat = new Date();
    //参数
    private Object[] args = new Object[1];
    //消息格式化器
    private MessageFormat formatter;
    //时间参数
    private String format = "日期 {0,date} 时间{0,time}";
    //行分格符
    private String lineSeparator = AccessController.doPrivileged(new sun.security.action.GetPropertyAction("line.separator"));

    /**
     * @param
     * @return 返回格式化好的日志内容
     */
    @Override
    public String format(LogRecord record) {
        StringBuffer sb = new StringBuffer();
        dat.setTime(record.getMillis());
        args[0] = dat;
        StringBuffer text = new StringBuffer();
        if (formatter == null) {
            formatter = new MessageFormat(format);
        }
        formatter.format(args, text, null);
        //sb.append("【第 " + (logCount++) + " 条记录】" + lineSeparator);
        //sb.append(text);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String trace = sdf.format(new Date().getTime());
        sb.append("[--|--][DEBUG] [" +trace).append("]");
        sb.append(" ");
        /*if (record.getSourceClassName() != null) {
            sb.append("源文件名 " + record.getSourceClassName());
        } else {
            sb.append("日志名 " + record.getLoggerName());
        }
        if (record.getSourceMethodName() != null) {
            sb.append(" ");
            sb.append("方法名 " + record.getSourceMethodName());
        }*/
        sb.append(" ");
        String message = formatMessage(record);
        sb.append(record.getLevel().getLocalizedName());
        sb.append(": ");
        sb.append(message);
        sb.append(lineSeparator);
        if (record.getThrown() != null) {
            try {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                record.getThrown().printStackTrace(pw);
                pw.close();
                sb.append(sw.toString());
            } catch (Exception ex) {
            }
        }
        return sb.toString();
    }

}

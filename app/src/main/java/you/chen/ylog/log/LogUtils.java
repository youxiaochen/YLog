package you.chen.ylog.log;

import androidx.annotation.IntDef;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * author: you : 2018/12/27
 */
public final class LogUtils {

    @IntDef({VERBOSE, DEBUG, INFO, WARN, ERROR})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Level {}

    public static final int VERBOSE = android.util.Log.VERBOSE;

    public static final int DEBUG = android.util.Log.DEBUG;

    public static final int INFO = android.util.Log.INFO;

    public static final int WARN = android.util.Log.WARN;

    public static final int ERROR = android.util.Log.ERROR;

    private LogUtils () {}

    private static Logger logger;

    public static void init(Logger logger) {
        LogUtils.logger = logger;
    }

    public static void flush() {
        if (logger != null) {
            logger.flush();
        }
    }

    public static char leveChar(@Level int level) {
        switch (level) {
            case VERBOSE:
                return 'V';
            case DEBUG:
                return 'D';
            case INFO:
                return 'I';
            case WARN:
                return 'W';
            case ERROR:
                return 'E';
            default:
                return ' ';
        }
    }

    //一般不使用
    public static void release() {
        if (logger != null) {
            logger.release();
        }
        logger = null;
    }

    public static void v(String tag, String msg) {
        if (logger != null) {
            logger.println(VERBOSE, tag, msg);
        }
    }

    public static void v(String tag, String format, Object ...args) {
        v(tag, String.format(format, args));
    }

    public static void d(String tag, String msg) {
        if (logger != null) {
            logger.println(DEBUG, tag, msg);
        }
    }

    public static void d(String tag, String format, Object ...args) {
        d(tag, String.format(format, args));
    }

    public static void i(String tag, String msg) {
        if (logger != null) {
            logger.println(INFO, tag, msg);
        }
    }

    public static void i(String tag, String format, Object ...args) {
        i(tag, String.format(format, args));
    }

    public static void w(String tag, String msg) {
        if (logger != null) {
            logger.println(WARN, tag, msg);
        }
    }

    public static void w(String tag, String format, Object ...args) {
        w(tag, String.format(format, args));
    }

    public static void w(String tag, Throwable t) {
        w(tag, throwable2str(t));
    }

    public static void e(String tag, String msg) {
        if (logger != null) {
            logger.println(ERROR, tag, msg);
        }
    }

    public static void e(String tag, String format, Object ...args) {
        e(tag, String.format(format, args));
    }

    public static void e(String tag, Throwable t) {
        e(tag, throwable2str(t));
    }

    private static String throwable2str(Throwable throwable) {
        if (throwable == null) {
            return "";
        }
        Writer info = new StringWriter();
        PrintWriter printWriter = new PrintWriter(info);
        throwable.printStackTrace(printWriter);
        printWriter.flush();
        printWriter.close();
        return info.toString();
    }
}

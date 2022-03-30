package you.chen.ylog.log;

import java.io.File;

/**
 * author: you : 2018/12/14
 * 注意此类不能混淆
 */
public final class LogAppender {

    static {
        System.loadLibrary("YlogCore");
    }

    //默认自动刷新日志最大时间单位 秒
    public static final int MAX_FLUSH_DELAY = 60 * 60;
    //最小刷新间隔, DEBUG状态时可小一点
    public static final int MIN_FLUSH_DELAY = 5 * 60;
    //最大与最小BufferSize 4K整数最佳
    public static final int MAX_BUFFERSIZE = 1024 * 1024;
    public static final int MIN_BUFFERSIZE = 40 * 1024;
    //最大日志文件大小, 0时只生成在一个文件中
    public static final int MAX_LOG_FILESIZE = 128 * 1024 * 1024;
    public static final int MIN_LOG_FILESIZE = 400 * 1024;
    //文件保留时间
    public static final int MAX_LOG_ALIVE_TIME = 10 * 24 * 3600;
    public static final int MIN_LOG_ALIVE_TIME = 1 * 24 * 3600;

    private long mNativeContext;

    public LogAppender(String logfileDir, long bufferSize, long flushDelay, long maxLogSize, long logAliveTime, boolean isDebug) {
        bufferSize = fixSize(bufferSize, MAX_BUFFERSIZE, MIN_BUFFERSIZE);
        flushDelay = fixSize(flushDelay, MAX_FLUSH_DELAY, MIN_FLUSH_DELAY);
        if (maxLogSize != 0) {
            maxLogSize = fixSize(maxLogSize, MAX_LOG_FILESIZE, MIN_LOG_FILESIZE);
            if (maxLogSize < bufferSize) { //限制最大文件不能小于缓冲大小
                maxLogSize = bufferSize;
            }
        }
        logAliveTime = fixSize(logAliveTime, MAX_LOG_ALIVE_TIME, MIN_LOG_ALIVE_TIME);
        if (!logfileDir.endsWith(File.separator)) {
            logfileDir = logfileDir + File.separator;
        }
        mNativeContext = initNative(logfileDir, bufferSize, flushDelay, maxLogSize, logAliveTime, isDebug);
    }

    public static long fixSize(long v, long max, long min) {
        if(v > max) {
            return max;
        } else if (v < min) {
            return min;
        }
        return v;
    }

    public void openBuffer(String bufferPath) {
        if (mNativeContext != 0) {
            openBuffer(mNativeContext, bufferPath);
        }
    }

    public void flush() {
        if (mNativeContext != 0) {
            flush(mNativeContext);
        }
    }

    public void appender(String logData) {
        if (mNativeContext != 0) {
            appender(mNativeContext, logData);
        }
    }

    public void closeBuffer() {
        if (mNativeContext != 0) {
            closeBuffer(mNativeContext);
        }
    }

    @Override
    protected void finalize() throws Throwable {
        if (mNativeContext != 0) {
            release(mNativeContext);
        }
    }

    //初始化日志相关参数
    private static native long initNative(String logfileDir, long bufferSize, long flushDelay, long maxLogSize, long maxLogAliveTime, boolean isDebug);

    //打开日志文件,mmap映射或者native内层
    private native void openBuffer(long logAppender, String bufferPath);

    //添加日志
    private native void appender(long logAppender, String logData);

    //flush日志,异步处理
    private native void flush(long logAppender);

    //关闭mmap
    private native void closeBuffer(long logAppender);

    //释放
    private native void release(long logAppender);
}

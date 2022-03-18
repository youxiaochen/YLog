package you.chen.ylog.log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Formatter;
import java.util.List;

/**
 * author: you : 2018/12/27
 */
public final class AppenderLogger implements Logger {

    //最好4K 倍数
    public static final int DEF_BUFFERSIZE = 180 * 1024;
    //单次最大写入的日志长度
    public static final int DEF_APPENDER_LEN = DEF_BUFFERSIZE / 10;
    //默认flush时长, 如Debug需要打印时可以小一点, 单位秒
    public static final int DEF_FLUSH_DELAY = 15 * 60;
    //日志保留时间
    public static final long DEF_LOG_ALIVE_TIME = 4 * 24 * 3600;

    //日志拦截
    private final List<Interceptor> interceptorList = new ArrayList<>();
    //单次日志添加时日志最大添加长度,超过时分割多段后写入
    private int maxAppendLength;
    //核心写入
    private LogAppender logAppender;
    //格式化
    private LogFormatter formatter;

    private AppenderLogger(Builder builder) {
        logAppender = new LogAppender(builder.logfileDir, builder.bufferSize,
                builder.flushDelay, builder.maxLogSize, builder.logAliveTime, builder.isDebug);
        logAppender.openBuffer(builder.bufferPath);
        this.maxAppendLength = builder.maxAppendLength;
        this.formatter = builder.formatter;
    }

    public final void addInterceptor(Interceptor interceptor) {
        if (interceptor != null) {
            interceptorList.add(interceptor);
        }
    }

    public final void removeInterceptor(Interceptor interceptor) {
        if (interceptor != null) {
            interceptorList.remove(interceptor);
        }
    }

    @Override
    public void println(int level, String tag, String msg) {
        boolean intercepted = false;
        for (Interceptor interceptor : interceptorList) {
            if (interceptor.intercept(level, tag, msg)) {
                intercepted = true;
                break;
            }
        }
        if (!intercepted && maxAppendLength > 0) {
            if (msg.length() <= maxAppendLength) {
                logAppender.appender(formatter.format(level, tag, msg));
                return;
            }
            int msgLength = msg.length();
            int start = 0;
            int end = maxAppendLength;
            while (start < msgLength) {
                logAppender.appender(formatter.format(level, tag, msg.substring(start, end)));
                start = end;
                end = Math.min(start + maxAppendLength, msgLength);
            }
        }
    }

    @Override
    public void flush() {
        logAppender.flush();
    }

    @Override
    public void release() {
        logAppender.closeBuffer();
    }

    /**
     * 格式化日志格式
     */
    public interface LogFormatter {

        String format(@LogUtils.Level int level, String tag, String msg);
    }

    /**
     * 时间格式化
     */
    public static final class DateLogFormatter implements LogFormatter {

        private static final String DEF_FORMAT_TIME = "%d-%02d-%02d %02d:%02d:%02d";
        //在大量format时间格式时,此种方式处理速度更快
        final StringBuilder currTimeStr;
        final Formatter formatter;
        //format格式
        final String format;
        final StringBuilder logStringBuilder;
        final Calendar calendar = Calendar.getInstance();

        final Object mSync = new Object();

        public DateLogFormatter() {
            this(DEF_FORMAT_TIME);
        }

        public DateLogFormatter(String format) {
            this.format = format;
            currTimeStr = new StringBuilder();
            formatter = new Formatter(currTimeStr);
            logStringBuilder = new StringBuilder();
        }

        @Override
        public String format(int level, String tag, String msg) {
            synchronized (mSync) {
                calendar.setTimeInMillis(System.currentTimeMillis());
                currTimeStr.setLength(0);
                formatter.format(format, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1,
                        calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.HOUR),
                        calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND));
                logStringBuilder.setLength(0);
                return logStringBuilder.append(currTimeStr)
                        .append(' ').append(LogUtils.leveChar(level))
                        .append(' ').append(tag)
                        .append(' ').append(msg)
                        .append('\n').toString();
            }
        }
    }

    public final static class Builder {

        private final String logfileDir;

        private final String bufferPath;

        private boolean isDebug = false;

        private long bufferSize;

        private int flushDelay;

        private long maxLogSize;

        private long logAliveTime;

        private int maxAppendLength;

        private LogFormatter formatter;

        public Builder(String logfileDir, String bufferPath) {
            this.logfileDir = logfileDir;
            this.bufferPath = bufferPath;
        }

        public Builder setDebug(boolean debug) {
            isDebug = debug;
            return this;
        }

        public Builder setBufferSize(long bufferSize) {
            this.bufferSize = bufferSize;
            return this;
        }

        public Builder setFlushDelay(int flushDelay) {
            this.flushDelay = flushDelay;
            return this;
        }

        public Builder setMaxLogSize(long maxLogSize) {
            this.maxLogSize = maxLogSize;
            return this;
        }

        public Builder setLogAliveTime(int logAliveTime) {
            this.logAliveTime = logAliveTime;
            return this;
        }

        public Builder setMaxAppendLength(int maxAppendLength) {
            this.maxAppendLength = maxAppendLength;
            return this;
        }

        public Builder setFormatter(LogFormatter formatter) {
            this.formatter = formatter;
            return this;
        }

        public final AppenderLogger build() {
            if (formatter == null) {
                formatter = new DateLogFormatter();
            }
            if (bufferSize <= 0) {
                bufferSize = DEF_BUFFERSIZE;
            }
            if (flushDelay <= 0) {
                flushDelay = DEF_FLUSH_DELAY;
            }
            if (logAliveTime <= 0) {
                logAliveTime = DEF_LOG_ALIVE_TIME;
            }
            if (maxAppendLength <= 0) {
                maxAppendLength = (int) (bufferSize / 3);
            }
            return new AppenderLogger(this);
        }
    }
}

package you.chen.ylog.log;

import java.util.ArrayList;
import java.util.List;

/**
 * author: you : 2018/12/27
 */
public final class MultiLogger implements Logger {

    private final List<Logger> loggerList = new ArrayList<>();

    public final void addLogger(Logger logger) {
        loggerList.add(logger);
    }

    public final void removeLogger(Logger logger) {
        loggerList.remove(logger);
    }

    @Override
    public void println(int level, String tag, String msg) {
        for (Logger logger : loggerList) {
            logger.println(level, tag, msg);
        }
    }

    @Override
    public void flush() {
        for (Logger logger : loggerList) {
            logger.flush();
        }
    }

    @Override
    public void release() {
        for (Logger logger : loggerList) {
            logger.release();
        }
    }
}

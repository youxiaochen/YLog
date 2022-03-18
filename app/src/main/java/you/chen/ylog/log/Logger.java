package you.chen.ylog.log;

/**
 * author: you : 2018/12/27
 */
public interface Logger {

    void println(int level, String tag, String msg);

    void flush();

    void release();
}

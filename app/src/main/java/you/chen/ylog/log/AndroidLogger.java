package you.chen.ylog.log;

/**
 * author: you : 2018/12/27
 */
public final class AndroidLogger implements Logger {

    private AndroidLogger() {
    }

    public static AndroidLogger getInstance() {
        return AndroidLoggerHolder.INSTANCE;
    }

    private interface AndroidLoggerHolder {
        AndroidLogger INSTANCE = new AndroidLogger();
    }

    @Override
    public void println(int level, String tag, String msg) {
        android.util.Log.println(level, tag, msg);
    }

    @Override
    public void flush() {
    }

    @Override
    public void release() {
    }

}

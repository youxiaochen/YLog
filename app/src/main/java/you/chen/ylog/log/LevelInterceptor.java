package you.chen.ylog.log;

/**
 * author: you : 2018/12/27
 */
public final class LevelInterceptor implements Interceptor {

    private final int level;

    public LevelInterceptor(int level) {
        this.level = level;
    }

    @Override
    public boolean intercept(int level, String tag, String msg) {
        return tag == null || msg == null || level < this.level;
    }
}

package you.chen.ylog.log;

/**
 * author: you : 2018/12/27
 */
public interface Interceptor {

    boolean intercept(@LogUtils.Level int level, String tag, String msg);
}

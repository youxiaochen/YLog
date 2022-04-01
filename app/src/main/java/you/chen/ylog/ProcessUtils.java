package you.chen.ylog;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;

import java.lang.reflect.Method;
import java.util.List;

/**
 * author: you : 2022/4/1
 */
public final class ProcessUtils {

    private ProcessUtils() {}

    public static String getCurrentProcessName(Context context) {
        String processName = getCurProcessNameByActivityThread();
        if (TextUtils.isEmpty(processName)) {
            processName = getCurProcessNameByPid(context);
        }
        return processName;
    }

    /**
     * >= 4.3版本ActivityThread有提供方法直接获取
     * @return
     */
    private static String getCurProcessNameByActivityThread() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            return Application.getProcessName();
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            //4.3或者以上有提供方法
            try {
                Method currentProcessMethod = Class.forName("android.app.ActivityThread").getMethod("currentProcessName");
                return (String) currentProcessMethod.invoke(null);
            } catch (Exception e) {
            }
        }
        return null;
    }

    /**
     * 获得当前进程的名字通过pid
     *
     * @return
     */
    private static String getCurProcessNameByPid(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningApps = am.getRunningAppProcesses();
        if (runningApps == null || runningApps.isEmpty()) {
            return null;
        }
        int pid = android.os.Process.myPid();
        for (ActivityManager.RunningAppProcessInfo procInfo : runningApps) {
            if (procInfo.pid == pid) {
                return procInfo.processName;
            }
        }
        return null;
    }

}

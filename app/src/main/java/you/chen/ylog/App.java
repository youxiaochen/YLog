package you.chen.ylog;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.util.List;

import you.chen.ylog.log.AndroidLogger;
import you.chen.ylog.log.AppenderLogger;
import you.chen.ylog.log.LevelInterceptor;
import you.chen.ylog.log.LogUtils;
import you.chen.ylog.log.MultiLogger;

/**
 * author: you : 2021/12/28
 */
public final class App extends Application {

    private static boolean logInited;
    //这里根据实际开发时的gradle配置
    private static boolean isDebug = true;

    @Override
    public void onCreate() {
        super.onCreate();
        if (!logInited) {
            logInited = true;
            initLog();
        }
    }

    //多进程时mmap映射文件分开
    private String subProcessName() {
        try {
            String procceName = ProcessUtils.getCurrentProcessName(this);
            int lastIndexProcessC = procceName.lastIndexOf(':');
            if (lastIndexProcessC >= 0) {
                return procceName.substring(lastIndexProcessC + 1);
            }
        } catch (Exception e) {
        }
        return null;
    }

    private void initLog() {
        //建议放在data/data/包...下的  ,确保mkdirs()
        String subProcessName = subProcessName();
        File dirPath = FileUtils.getCacheDirPath(this);
        if (!TextUtils.isEmpty(subProcessName)) {
            dirPath = new File(dirPath, subProcessName);
            if (!dirPath.exists()) dirPath.mkdirs();
        }

        //mmap路径
        String bufferPath = new File(dirPath, "testLog.mmapf").getAbsolutePath();
        //日志生成路径
        String logfileDir = dirPath.getAbsolutePath();
        //文件记录日志
        AppenderLogger appenderLogger = new AppenderLogger.Builder(logfileDir, bufferPath)
                .setDebug(isDebug) //debug模式时不加密不压缩
                .setBufferSize(180 * 1024)//设置映射大小
                .setFlushDelay(10 * 60)//测试时可以将参数设置小一些, 注意LogAppender那里的最大小值限制
                .setLogAliveTime(5 * 24 * 3600)//设置日志文件存放的时间
                .setMaxLogSize(10 * 1024 * 1024)//设置最大日志文件大小,0时不分多个文件生成
                .setMaxAppendLength(8 * 1024)
//                .setFormatter(new AppenderLogger.LogFormatter() {
//                    @Override
//                    public String format(int level, String tag, String msg) {
//                        return gson...; 也可以在这里添加Gson方式, 默认是时间格式化日志
//                    }
//                })
                .build();
        //添加日志拦截只记录INFO级别的日志
        appenderLogger.addInterceptor(new LevelInterceptor(LogUtils.INFO));
        //测试亦可用此方式, 建议线上只使用AppenderLogger的方式记录日志
        MultiLogger multiLogger = new MultiLogger();
        multiLogger.addLogger(appenderLogger);
        if (isDebug) {
            //控制台打印的日志
            AndroidLogger androidLogger = AndroidLogger.getInstance();
            multiLogger.addLogger(androidLogger);
        }
        LogUtils.init(multiLogger);
    }

    /**
     * 获得当前进程的名字
     *
     * @return
     */
    private String getCurProcessName() {
        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
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

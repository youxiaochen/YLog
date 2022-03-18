package you.chen.ylog;

import android.app.Application;

import java.io.File;

import you.chen.ylog.log.AndroidLogger;
import you.chen.ylog.log.AppenderLogger;
import you.chen.ylog.log.LevelInterceptor;
import you.chen.ylog.log.LogUtils;
import you.chen.ylog.log.MultiLogger;
import you.chen.ylog.ui.FileUtils;

/**
 * author: you : 2021/12/28
 */
public final class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        initLog();
    }


    private static boolean logInited;

    private void initLog() {
        if(logInited) return;
        logInited = true;


        String bufferPath = new File(FileUtils.getCacheDirPath(this), "testLog.mmapf").getAbsolutePath();
        String logfileDir = FileUtils.getCacheDirPath(this).getAbsolutePath();

        AppenderLogger appenderLogger = new AppenderLogger.Builder(logfileDir, bufferPath)
                .setDebug(false)
                .setBufferSize(180 * 1024)
                .setFlushDelay(10 * 60)//测试时可以将参数设置小一些, 注意LogAppender那里的最大小值限制
                .setLogAliveTime(5 * 24 * 3600)
                .setMaxLogSize(10 * 1024 * 1024)
//                .setFormatter(new AppenderLogger.LogFormatter() {
//                    @Override
//                    public String format(int level, String tag, String msg) {
//                        return gson...; 也可以在这里添加Gson方式, 默认是时间格式化日志
//                    }
//                })
                .build();
        appenderLogger.addInterceptor(new LevelInterceptor(LogUtils.INFO));

        AndroidLogger androidLogger = AndroidLogger.getInstance();

//        正式线上建议用此方式
//        boolean isDebug = true;
//        LogUtils.init(isDebug ? androidLogger : appenderLogger);


//        测试亦可用此方式
        MultiLogger multiLogger = new MultiLogger();
        multiLogger.addLogger(appenderLogger);
//        multiLogger.addLogger(androidLogger);
        LogUtils.init(multiLogger);
    }

}

# YLog
### 基于mmap的高性能Log记录, ECC生成TEA对称加密的KEY，在Java2Ylog.cpp代码中配置公钥,DEBUG时不加密不压缩


```
private void initLog() {
	//建议放在data/data/包...下的  ,确保mkdirs()
	//mmap路径
	String bufferPath = new File(FileUtils.getCacheDirPath(this), "testLog.mmapf").getAbsolutePath();
	//日志生成路径
	String logfileDir = FileUtils.getCacheDirPath(this).getAbsolutePath();
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
    
```

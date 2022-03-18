//
// Created by you on 2018/11/24.
//

#ifndef YLOG_LOGAPPENDER_H
#define YLOG_LOGAPPENDER_H

#include <pthread.h>
#include <mutex>

#include "LogBuffer.h"

class LogAppender {

private:
    //日志后缀方式, debug时的
    static const char *const _log_suffix;
    static const char *const _debug_suffix;
    //日志文件地址
    const std::string logfile_dir;
    //缓冲大写, 最佳4K的整数倍
    const size_t buffer_size;
    //自动刷新的延时时间 单位秒
    const size_t flush_delay;
    //日志最大写入
    const size_t max_log_size;
    //日志最大保留时间, 秒
    const size_t max_logalive_time;
    //isDebug, debug模式时不压缩加密
    const bool isDebug = false;

    //日志缓冲处理
    LogBuffer *log_buffer = NULL;
    //是否关闭了flush线程
    volatile bool isLogFlushClose = true;
    //是否mmap方式
    bool isMmapBuffer = false;
    //内存缓冲区 mmap 或者 char array
    char *buffer_ptr = NULL;

    //开启线程用于flush写入文件
    pthread_t flush_thread = 0;
    //线程锁
    std::condition_variable async_condition;
    std::mutex async_mutex;

private:
    //子线程run
    friend void *_flush_thread(void *args);

    //删除过时日志文件
    void _rm_unalive_file();

    //检测flush buffer, while true
    void _asyn_flush();

    //打开日志目录下的文件写日志
    FILE *_open_logfile(const char *_suffix, size_t write_len);

    //耗时的写入文件, 由appender_flush异步触发
    void _log2file(const void *_data_ptr, size_t _len);

public:
    LogAppender(const char *_logfile_dir, size_t _buffer_size, size_t _flush_delay,
                const size_t _max_log_size, size_t _max_logalive_time, bool _is_debug);

    ~LogAppender();

    //打开日志缓冲与写入
    void appender_open(const char *_buffer_path, const char *_pub_key);

    //异步写入日志信息
    void appender_async(const char *_log, size_t _len);

    //关闭日志缓冲与写入
    void appender_close();

    //主动刷新将日志写入文件中
    void appender_flush();
};

#endif //YLOG_LOGAPPENDER_H

//
// Created by you on 2018/11/24.
//
#include <dirent.h>
#include <sys/stat.h>

#include "LogAppender.h"
#include "MmapUtils.h"
#include "CryptLogBuffer.h"
#include "NoCryptLogBuffer.h"

const char* const LogAppender::_log_suffix = ".log";
const char* const LogAppender::_debug_suffix = "_debug.log";

LogAppender::LogAppender(const char *_logfile_dir, size_t _buffer_size, size_t _flush_delay,
                         const size_t _max_log_size, size_t _max_logalive_time, bool _is_debug)
        : logfile_dir(_logfile_dir), buffer_size(_buffer_size), flush_delay(_flush_delay),
          max_log_size(_max_log_size), max_logalive_time(_max_logalive_time), isDebug(_is_debug) {
}

LogAppender::~LogAppender() {
//    LOGD("~LogAppender  close...");
    appender_close();
}

void *_flush_thread(void *args) {
    LogAppender *log_appender = static_cast<LogAppender *>(args);
    log_appender->_asyn_flush();
//    LOGD("flush thread exit...");
    pthread_exit(NULL);
}

void LogAppender::appender_open(const char *_buffer_path, const char *_pub_key) {
    if (!isLogFlushClose) return;
//    LOGD("appender_open ... %s  - dirpath %s", _buffer_path, logfile_dir.c_str());
    isLogFlushClose = false;
    buffer_ptr = static_cast<char *>(openMmapBuffer(_buffer_path, buffer_size));
    isMmapBuffer = buffer_ptr != NULL;
    if (!isMmapBuffer) {
        buffer_ptr = new char[buffer_size];
    }
    if (isDebug) {
        log_buffer = new NoCryptLogBuffer(buffer_ptr, buffer_size);
    } else {
        log_buffer = new CryptLogBuffer(buffer_ptr, buffer_size, _pub_key);
    }

    pthread_create(&flush_thread, NULL, _flush_thread, this);
}

void LogAppender::_rm_unalive_file() {
    DIR *dir;
    dir = opendir(logfile_dir.c_str());
    if (NULL == dir) {
        //java层去创建,判断逻辑
        //mkdir(logfile_dir.c_str(), ACCESSPERMS);
        return;
    }
    char file_path[128] = {0};
    memcpy(file_path, logfile_dir.c_str(), logfile_dir.length());
    timeval tv;
    gettimeofday(&tv, NULL);//获取当前时间来计算日志文件是否过时

    dirent *dire;
    struct stat fstat;
    //遍历文件夹
    while ((dire = readdir(dir)) != NULL) {
        if (dire->d_type == DT_REG) { //常规文件时
            const char *_suffix = isDebug ? _debug_suffix : _log_suffix;
            size_t _start = strlen(dire->d_name) - strlen(_suffix);
            //判断是否为当前所记录的日志文件
            if (_start < 0 || strcmp(dire->d_name + _start, _suffix) != 0) {
                continue;
            }
            strcat(file_path, dire->d_name);
            //判断当前文件是否超出日志文件的保存时效
            if (stat(file_path, &fstat) == 0 && (tv.tv_sec - fstat.st_mtim.tv_sec > max_logalive_time)) {
                remove(file_path);
            }
            memset(file_path + logfile_dir.length(), 0, sizeof(file_path) - logfile_dir.length());
        }
    }
    closedir(dir);
}

void LogAppender::_asyn_flush() {
    //初次打开时检测过时的日志文件,考虑日志文件一般保留时间为几天,因此不放在每次写入文件时检测
    _rm_unalive_file();
    while (true) {
        std::unique_lock<std::mutex> _lock(async_mutex, std::defer_lock);
        _lock.lock();
        if (log_buffer == NULL) break;
        AutoBuffer tmp;
        log_buffer->flush(tmp);
        _lock.unlock();

//        LOGD("flush log size %d", tmp.length());
        if (NULL != tmp.ptr() && tmp.length() > 0) _log2file(tmp.ptr(), tmp.length());

        _lock.lock();
        if (isLogFlushClose) break;
        //长时间写入大小没达到时, 定时唤醒
        async_condition.wait_for(_lock, std::chrono::seconds(flush_delay));
    }
}

void LogAppender::appender_async(const char *_log, size_t _len) {
    std::lock_guard<std::mutex> _lock(async_mutex);
    if (NULL == log_buffer) return;
    if (!log_buffer->write(_log, _len)) return;
    if (log_buffer->getBuffer().length() >= buffer_size / 3) {
//        LOGD("log length >= writeLength / 3");
        async_condition.notify_all();
    }
}

void LogAppender::appender_close() {
    if (isLogFlushClose) return;
    isLogFlushClose = true;
    std::unique_lock<std::mutex> _lock(async_mutex, std::defer_lock);
    _lock.lock();
    async_condition.notify_all();
    _lock.unlock();

    pthread_join(flush_thread, NULL);

    _lock.lock();
    if (isMmapBuffer) {
        closeMmapBuffer(buffer_ptr, buffer_size);
    } else {
        delete[] buffer_ptr;
    }
    buffer_ptr = NULL;
    delete log_buffer;
    log_buffer = NULL;
    _lock.unlock();
    //...
}

void LogAppender::appender_flush() {
    std::lock_guard<std::mutex> _lock(async_mutex);
    async_condition.notify_all();
}

FILE *LogAppender::_open_logfile(const char *_suffix, size_t write_len) {
    timeval tv;
    gettimeofday(&tv, NULL);
    time_t cur = tv.tv_sec;
    tm *curt = localtime(&cur);
    char temp[16] = {0};
    //按时间格式来写入日志
    snprintf(temp, 16, "_%d%02d%02d", 1900 + curt->tm_year, curt->tm_mon + 1, curt->tm_mday);

    FILE *log_file = NULL;
    char logfile_path[128] = {0};
    memcpy(logfile_path, logfile_dir.c_str(), logfile_dir.length());

    char *snprint_ptr = logfile_path + logfile_dir.length();
    size_t snprint_size = sizeof(logfile_path) - logfile_dir.length();

    if (max_log_size == 0) {
        //最大日志文件为0时只写在一个日志文件中
        snprintf(snprint_ptr, snprint_size, "%s%s", temp, _suffix);
        log_file = fopen(logfile_path, "ab+");
        if (NULL != log_file) {
            fseek(log_file, 0, SEEK_END);
        }
    } else if (max_log_size >= write_len) {//写入的最大值必须大于当前写入的大小
        uint32_t curr_c = 0;
        //检测文件是否能写下日志,写不下时分文件
        struct stat fstat;
        while (true) {
            //判断当前文件是否能继续写入,_20181110(1).log多个文件时按(n)来区分
            if (curr_c > 0) {
                snprintf(snprint_ptr, snprint_size, "%s(%d)%s", temp, curr_c, _suffix);
            } else {
                snprintf(snprint_ptr, snprint_size, "%s%s", temp, _suffix);
            }
            //先获取文件相关信息,再打开
            if (stat(logfile_path, &fstat) == 0) {
                if (fstat.st_size + write_len < max_log_size) {//能写下日志
                    log_file = fopen(logfile_path, "ab+");
                    //打开文件如果失败,可能权限被改动或者文件夹被删除等意外无法写入
                    if (NULL != log_file) {
                        fseek(log_file, 0, SEEK_END);
                    }
                    break;
                }
            } else { //获取失败时用打开新文件
                log_file = fopen(logfile_path, "ab+");
                if (NULL == log_file) {
                    break;
                }
                fseek(log_file, 0, SEEK_END);
                if (ftell(log_file) + write_len < max_log_size) {
                    break;
                }
                fclose(log_file);
            }
            curr_c++;
            memset(snprint_ptr, 0, snprint_size);
        }
    }
    return log_file;
}

void LogAppender::_log2file(const void *_data_ptr, size_t _len) {
//    LOGD("_log2file..size.%d", _len);
    FILE *log_file = _open_logfile(isDebug ? _debug_suffix : _log_suffix, _len);
    if (log_file != NULL) {
        fwrite(_data_ptr, _len, 1, log_file);
        fclose(log_file);
    }
}
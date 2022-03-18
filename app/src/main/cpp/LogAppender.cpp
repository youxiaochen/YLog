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
//    LOGD("appender_open ... %s", _buffer_path);
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
    if (NULL == dir) return;
    char file_path[128] = {0};
    memcpy(file_path, logfile_dir.c_str(), logfile_dir.length());
    timeval tv;
    gettimeofday(&tv, NULL);

    dirent *dire;
    struct stat fstat;
    while ((dire = readdir(dir)) != NULL) {
        if (dire->d_type == DT_REG) {
            const char *_suffix = isDebug ? _debug_suffix : _log_suffix;
            size_t _start = strlen(dire->d_name) - strlen(_suffix);
            if (_start < 0 || strcmp(dire->d_name + _start, _suffix) != 0) {
                continue;
            }
            strcat(file_path, dire->d_name);

            if (stat(file_path, &fstat) == 0 && (tv.tv_sec - fstat.st_mtim.tv_sec > max_logalive_time)) {
                remove(file_path);
            }
            memset(file_path + logfile_dir.length(), 0, sizeof(file_path) - logfile_dir.length());
        }
    }
    closedir(dir);
}

void LogAppender::_asyn_flush() {
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
        async_condition.wait_for(_lock, std::chrono::seconds(flush_delay));
    }
}

void LogAppender::appender_async(const char *_log, size_t _len) {
    std::unique_lock<std::mutex> _lock(async_mutex);
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
    std::unique_lock<std::mutex> _lock(async_mutex);
    async_condition.notify_all();
}

FILE *LogAppender::_open_logfile(const char *_suffix, size_t write_len) {
    timeval tv;
    gettimeofday(&tv, NULL);
    time_t cur = tv.tv_sec;
    tm *curt = localtime(&cur);
    char temp[16] = {0};
    snprintf(temp, 16, "_%d%02d%02d", 1900 + curt->tm_year, curt->tm_mon + 1, curt->tm_mday);

    FILE *log_file = NULL;
    char logfile_path[128] = {0};
    memcpy(logfile_path, logfile_dir.c_str(), logfile_dir.length());

    char *snprint_ptr = logfile_path + logfile_dir.length();
    size_t snprint_size = sizeof(logfile_path) - logfile_dir.length();

    if (max_log_size == 0) {
        snprintf(snprint_ptr, snprint_size, "%s%s", temp, _suffix);
        log_file = fopen(logfile_path, "ab+");
        if (NULL != log_file) {
            fseek(log_file, 0, SEEK_END);
        }
    } else if (max_log_size >= write_len) {//
        uint32_t curr_c = 0;
        while (true) {
            if (curr_c > 0) {
                snprintf(snprint_ptr, snprint_size, "%s(%d)%s", temp, curr_c, _suffix);
            } else {
                snprintf(snprint_ptr, snprint_size, "%s%s", temp, _suffix);
            }
            log_file = fopen(logfile_path, "ab+");
            if (NULL == log_file) {
                break;
            }
            fseek(log_file, 0, SEEK_END);
            if (ftell(log_file) + write_len < max_log_size) {
                break;
            }
            fclose(log_file);
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
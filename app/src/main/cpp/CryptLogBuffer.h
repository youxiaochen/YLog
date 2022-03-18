//
// Created by you on 2018/11/25.
//

#ifndef YLOG_CRYPTLOGBUFFER_H
#define YLOG_CRYPTLOGBUFFER_H

#include <zlib.h>
#include "LogBuffer.h"
#include "LogCrypt.h"


class CryptLogBuffer : public LogBuffer {

private:
    //z压缩
    z_stream cstream_;
    //日志加密
    LogCrypt *log_crypt = NULL;
    //每次加密块剩余未加密的长度
    size_t remain_uncrypt_len = 0;

private:
    //重置,设置头部信息
    bool _reset();
    //将指针数据全部清0
    void _clear();
    //将所有缓冲写入到文件前的处理结束符
    void _flush();

public:
    CryptLogBuffer(char *data_ptr, size_t buffer_size, const char* _pub_key);

    ~CryptLogBuffer();

    void flush(AutoBuffer &_buffer);

    bool write(const char *_log, size_t _len);
};


#endif //YLOG_CRYPTLOGBUFFER_H

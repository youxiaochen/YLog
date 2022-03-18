//
// Created by you on 2018/11/25.
//

#ifndef YLOG_NOCRYPTLOGBUFFER_H
#define YLOG_NOCRYPTLOGBUFFER_H

#include "LogBuffer.h"

class NoCryptLogBuffer : public LogBuffer {

private:
    //没有加密开始标记
    static const char kMagicStart = '\x05';

private:
    static uint32_t getHeaderLen();
    static uint32_t getLogLen(const char* const _data, size_t _len);
    static void updateLogLen(char* _data, uint32_t _add_len);

    //设置头部信息, char(magicStart), len(uint32_t), publickey(64)
    void setHeaderInfo(char* _data);
    //重置,设置头部信息
    bool _reset();
    //将指针数据全部清0
    void _clear();

public:

    NoCryptLogBuffer(char *data_ptr, size_t buffer_size);

    ~NoCryptLogBuffer();

    void flush(AutoBuffer &_buffer);

    bool write(const char *_log, size_t _len);
};

#endif //YLOG_NOCRYPTLOGBUFFER_H

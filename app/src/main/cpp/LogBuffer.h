//
// Created by you on 2018/11/25.
//

#ifndef YLOG_LOGBUFFER_H
#define YLOG_LOGBUFFER_H

#include "PtrBuffer.h"
#include "AutoBuffer.h"

class LogBuffer {

protected:
    PtrBuffer ptr_buffer;

public:

    LogBuffer(char *data_ptr, size_t buffer_size) {
        ptr_buffer.attach(data_ptr, buffer_size);
    }

    virtual ~LogBuffer(){}

    PtrBuffer& getBuffer() {
        return ptr_buffer;
    };

    virtual void flush(AutoBuffer &_buffer) = 0;

    virtual bool write(const char *_log, size_t _len) = 0;
};

#endif //YLOG_LOGBUFFER_H

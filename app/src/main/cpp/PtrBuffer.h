//
// Created by you on 2018/11/14.
//

#include <string.h>

#ifndef YLOG_PTRBUFFER_H
#define YLOG_PTRBUFFER_H

class PtrBuffer {

private:
    char* parray_ = NULL;
    off_t pos_;
    size_t length_;
    size_t max_length_;

public:
    PtrBuffer();
    ~PtrBuffer();
    void attach(void* _buffer, size_t _len);
    void attach(void* _buffer, size_t _len, size_t _maxlen);

    void write(const void* _buffer, size_t _len);
    void write(const void* _buffer, size_t _len, off_t _pos);
    void setLength(off_t _pos, size_t _length);
    void reset();

    void* ptr();
    const void* posPtr() const;

    size_t length() const;
    size_t maxLength() const;
};


#endif //YLOG_PTRBUFFER_H

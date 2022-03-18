//
// Created by you on 2018/11/23.
//

#ifndef CTEST_AUTOBUFFER2_H
#define CTEST_AUTOBUFFER2_H

#include <string>

class AutoBuffer {

private:
    char *parray_ = NULL;
    //指针位置
    off_t pos_ = 0;
    //当前已写入的长度
    size_t length_ = 0;
    //当前Buffer容量
    size_t capacity_ = 0;
    //申请Buffer增长单元大小
    size_t malloc_unitsize_;

private:
    void _fitSize(size_t _len);

public:
    AutoBuffer(size_t _size = 128);
    ~AutoBuffer();

    void alloc_write(size_t _readytowrite, bool _changelength = true);

    void write(const void* _buffer, size_t _len);
    void write(const void* _buffer, size_t _len, off_t _pos);

    void reset();

    void* ptr(off_t _offset = 0);

    off_t pos() const;
    size_t length() const;
};


#endif //CTEST_AUTOBUFFER2_H

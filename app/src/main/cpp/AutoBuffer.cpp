//
// Created by you on 2018/11/23.
//
#include "AutoBuffer.h"

#ifndef max
#define max(a, b) (((a) > (b)) ? (a) : (b))
#endif
#ifndef min
#define min(a, b) (((a) < (b)) ? (a) : (b))
#endif

AutoBuffer::AutoBuffer(size_t _size) : malloc_unitsize_(_size) {
}

AutoBuffer::~AutoBuffer() {
    reset();
}

void AutoBuffer::alloc_write(size_t _readytowrite, bool _changelength) {
    size_t len = pos_ + _readytowrite;
    _fitSize(len);

    if (_changelength) length_ = max(len, length_);
}

void AutoBuffer::write(const void *_buffer, size_t _len) {
    write(_buffer, _len, pos_);
    pos_ += _len;
}

void AutoBuffer::write(const void *_buffer, size_t _len, off_t _pos) {
    size_t writeLen = _pos + _len;
    _fitSize(writeLen);
    length_ = max(writeLen, length_);
    memcpy(parray_ + _pos, _buffer, _len);
}

void AutoBuffer::reset() {
    if (NULL != parray_) {
        free(parray_);
        parray_ = NULL;
    }
    pos_ = 0;
    length_ = 0;
    capacity_ = 0;
}

void *AutoBuffer::ptr(off_t _offset) {
    return parray_ + _offset;
}

off_t AutoBuffer::pos() const {
    return pos_;
}

size_t AutoBuffer::length() const {
    return length_;
}

void AutoBuffer::_fitSize(size_t _len) {
    if (_len > capacity_) {
        size_t mallocsize = ((_len + malloc_unitsize_ - 1) / malloc_unitsize_) * malloc_unitsize_;
        void *p = realloc(parray_, mallocsize);
        if (NULL == p) {
            free(parray_);
            parray_ = NULL;
            capacity_ = 0;
            return;
        }
        parray_ = static_cast<char *>(p);
        memset(parray_ + capacity_, 0, mallocsize - capacity_);
        capacity_ = mallocsize;
    }
}
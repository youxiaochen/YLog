//
// Created by you on 2018/11/14.
//

#include "PtrBuffer.h"

#ifndef max
#define max(a, b) (((a) > (b)) ? (a) : (b))
#endif
#ifndef min
#define min(a, b) (((a) < (b)) ? (a) : (b))
#endif

PtrBuffer::PtrBuffer() {
    reset();
}

PtrBuffer::~PtrBuffer() {
}

void PtrBuffer::attach(void *_buffer, size_t _len) {
    attach(_buffer, _len, _len);
}

void PtrBuffer::attach(void *_buffer, size_t _len, size_t _maxlen) {
    reset();
    parray_ = static_cast<char *>(_buffer);
    length_ = _len;
    max_length_ = _maxlen;
}

void PtrBuffer::write(const void *_buffer, size_t _len) {
    write(_buffer, _len, pos_);
    pos_ += _len;
}

void PtrBuffer::write(const void *_buffer, size_t _len, off_t _pos) {
    size_t copylen = min(_len, max_length_ - _pos);
    length_ = max(length_, copylen + _pos);
    memcpy(parray_ + _pos, _buffer, copylen);
}

void PtrBuffer::setLength(off_t _pos, size_t _length) {
    length_ = max_length_ < _length ? max_length_ : _length;
    pos_ = _pos;
}

void PtrBuffer::reset() {
    parray_ = NULL;
    pos_ = 0;
    length_ = 0;
    max_length_ = 0;
}

void* PtrBuffer::ptr() {
    return parray_;
}

const void* PtrBuffer::posPtr() const {
    return parray_ + pos_;
}

size_t PtrBuffer::length() const {
    return length_;
}

size_t PtrBuffer::maxLength() const {
    return max_length_;
}
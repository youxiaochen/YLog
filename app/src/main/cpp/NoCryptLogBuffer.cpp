//
// Created by you on 2018/11/25.
//
#include "NoCryptLogBuffer.h"

//magicHeader + len
uint32_t NoCryptLogBuffer::getHeaderLen() {
    return sizeof(char) + sizeof(uint32_t);
}

uint32_t NoCryptLogBuffer::getLogLen(const char *const _data, size_t _len) {
    if (_len < getHeaderLen() || NULL == _data || kMagicStart != _data[0]) return 0;
    uint32_t len = 0;
    memcpy(&len, _data + sizeof(char), sizeof(len));
    return len;
}

void NoCryptLogBuffer::updateLogLen(char *_data, uint32_t _add_len) {
    uint32_t curlen = getLogLen(_data, getHeaderLen()) + _add_len;
    memcpy(_data + sizeof(char), &curlen, sizeof(curlen));
}

NoCryptLogBuffer::NoCryptLogBuffer(char *data_ptr, size_t buffer_size) : LogBuffer(data_ptr, buffer_size) {
    uint32_t raw_len = getLogLen(data_ptr, buffer_size);
    if (raw_len == 0) {
        ptr_buffer.setLength(0, 0);
    } else {
        ptr_buffer.setLength(raw_len + getHeaderLen(),raw_len + getHeaderLen());
    }
}

NoCryptLogBuffer::~NoCryptLogBuffer() {
}

void NoCryptLogBuffer::setHeaderInfo(char *_data) {
    if (NULL == _data) return;
    char _magic_start = kMagicStart;
    memcpy(_data, &_magic_start, sizeof(_magic_start));
    int32_t len = 0;
    memcpy(_data + sizeof(_magic_start), &len, sizeof(len));
}

void NoCryptLogBuffer::flush(AutoBuffer &_buffer) {
    const char *buffer_data = (char*) ptr_buffer.ptr();
    if (getLogLen(buffer_data, ptr_buffer.length()) == 0 || ptr_buffer.length() <= getHeaderLen()) {
        _clear();
        return;
    }

    //非加密压缩的直接将header后面的数据写入文件当中
    _buffer.write(buffer_data + getHeaderLen(), ptr_buffer.length() - getHeaderLen());
    _clear();
}

bool NoCryptLogBuffer::write(const char *_log, size_t _len) {
    if (NULL == _log || _len == 0)
        return false;
    if (ptr_buffer.length() == 0 && !_reset())//当前日志没有时, 重置header
        return false;

    size_t before_len = ptr_buffer.length();
    ptr_buffer.write(_log, _len);
    updateLogLen((char *) ptr_buffer.ptr(), (uint32_t) (ptr_buffer.length() - before_len));
    return true;
}

bool NoCryptLogBuffer::_reset() {
    _clear();
    setHeaderInfo((char*) ptr_buffer.ptr());
    uint32_t headerLen = getHeaderLen();
    ptr_buffer.setLength(headerLen, headerLen);
    return true;
}

void NoCryptLogBuffer::_clear() {
    memset(ptr_buffer.ptr(), 0, ptr_buffer.length());
    ptr_buffer.setLength(0, 0);
}


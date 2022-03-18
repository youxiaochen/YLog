//
// Created by you on 2018/11/25.
//

#include "CryptLogBuffer.h"

CryptLogBuffer::CryptLogBuffer(char *data_ptr, size_t buffer_size, const char *_pub_key)
        : LogBuffer(data_ptr, buffer_size), remain_uncrypt_len(0) {
    log_crypt = new LogCrypt(_pub_key);

    uint32_t log_len = LogCrypt::getLogLen(data_ptr, buffer_size);
    if (log_len == 0) {
        ptr_buffer.setLength(0, 0);
    } else {
        log_len += LogCrypt::getHeaderLen();
        ptr_buffer.setLength(log_len, log_len);
    }

    memset(&cstream_, 0, sizeof(cstream_));
}

CryptLogBuffer::~CryptLogBuffer() {
    if (Z_NULL != cstream_.state) {
        deflateEnd(&cstream_);
    }
    delete log_crypt;
}

void CryptLogBuffer::flush(AutoBuffer &_buffer) {
    if (Z_NULL != cstream_.state) {
        deflateEnd(&cstream_);
    }

    if (log_crypt->getLogLen((char *) ptr_buffer.ptr(), ptr_buffer.length()) == 0) {
        _clear();
        return;
    }

    _flush();
    _buffer.write(ptr_buffer.ptr(), ptr_buffer.length());
    _clear();
}

bool CryptLogBuffer::write(const char *_log, size_t _len) {
    if (NULL == _log || _len == 0)
        return false;
    if (ptr_buffer.length() == 0 && !_reset())//当前日志没有时, 重置header
        return false;

    size_t before_len = ptr_buffer.length();
    cstream_.avail_in = (uInt) _len;
    cstream_.next_in = (Bytef *) _log;

    uInt avail_out = (uInt) (ptr_buffer.maxLength() - ptr_buffer.length());
    cstream_.next_out = (Bytef *) ptr_buffer.posPtr();
    cstream_.avail_out = avail_out;
    if (Z_OK != deflate(&cstream_, Z_SYNC_FLUSH)) {
        return false;
    }

    size_t write_len = avail_out - cstream_.avail_out;
    before_len -= remain_uncrypt_len;

    //将压缩后的日志加密
    AutoBuffer out_buffer;
    size_t last_remain_len = remain_uncrypt_len;
    log_crypt->encryptLog((char *) ptr_buffer.ptr() + before_len, write_len + remain_uncrypt_len,
                          out_buffer, remain_uncrypt_len);
    ptr_buffer.write(out_buffer.ptr(), out_buffer.length(), before_len);
    before_len += out_buffer.length();
    ptr_buffer.setLength(before_len, before_len);

    //更新当前日志长度
    log_crypt->updateLogLen((char *) ptr_buffer.ptr(),
                            (uint32_t) (out_buffer.length() - last_remain_len));
    return true;
}

bool CryptLogBuffer::_reset() {
    _clear();

    cstream_.zalloc = Z_NULL;
    cstream_.zfree = Z_NULL;
    cstream_.opaque = Z_NULL;
    if (Z_OK != deflateInit2(&cstream_, Z_BEST_COMPRESSION, Z_DEFLATED, -MAX_WBITS,
                             MAX_MEM_LEVEL, Z_DEFAULT_STRATEGY)) {
        return false;
    }

    log_crypt->setHeaderInfo((char *) ptr_buffer.ptr());
    uint32_t headerLen = LogCrypt::getHeaderLen();
    ptr_buffer.setLength(headerLen, headerLen);
    return true;
}

void CryptLogBuffer::_clear() {
    memset(ptr_buffer.ptr(), 0, ptr_buffer.length());
    ptr_buffer.setLength(0, 0);
    remain_uncrypt_len = 0;
}

void CryptLogBuffer::_flush() {
    log_crypt->setFooterInfo((char *) ptr_buffer.ptr() + ptr_buffer.length());
    size_t cur_length = ptr_buffer.length() + LogCrypt::getFooterLen();
    ptr_buffer.setLength(cur_length, cur_length);
}
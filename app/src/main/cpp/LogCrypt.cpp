//
// Created by you on 2018/11/13.
//

#include "LogCrypt.h"
#include "Tea_crypt.h"
#include "uECC.h"
#include "Log.h"

//magicHeader + len + seckey
uint32_t LogCrypt::getHeaderLen() {
    return sizeof(char) + sizeof(uint32_t) + sizeof(char) * 64;
}

uint32_t LogCrypt::getFooterLen() {
    return sizeof(char);
}

uint32_t LogCrypt::getLogLen(const char *const _data, size_t _len) {
    if (_len < getHeaderLen()) return 0;
    if (kMagicCryptStart != _data[0] && kMagicStart != _data[0]) return 0;
    uint32_t len = 0;
    memcpy(&len, _data + sizeof(char), sizeof(len));
    return len;
}

void LogCrypt::updateLogLen(char *_data, uint32_t _add_len) {
    uint32_t curlen = getLogLen(_data, getHeaderLen()) + _add_len;
    memcpy(_data + sizeof(char), &curlen, sizeof(curlen));
}

LogCrypt::LogCrypt(const char *public_key_) {
    if (NULL == public_key_ || 128 != strnlen(public_key_, 256)) return;
    unsigned char svr_pubkey[64] = {0};
    if (!hex2Buffer(public_key_, 128, svr_pubkey)) return;

    unsigned char private_key[32] = {0};
    if (uECC_make_key(public_key, private_key, uECC_secp256k1()) == 0) return ;
    uint8_t ecdh_key[32] = {0};
    if (uECC_shared_secret(svr_pubkey, private_key, ecdh_key, uECC_secp256k1()) == 0) return;
    memcpy(tea_key, ecdh_key, sizeof(tea_key));
    LOGD("isCrypte true");
    is_crypt = true;
}

LogCrypt::~LogCrypt() {
}

void LogCrypt::setHeaderInfo(char *_data) {
    if (NULL == _data) return;
    char _magic_start = is_crypt ? kMagicCryptStart : kMagicStart;
    memcpy(_data, &_magic_start, sizeof(_magic_start));
    int32_t len = 0;
    memcpy(_data + sizeof(_magic_start), &len, sizeof(len));
    memcpy(_data + sizeof(_magic_start) + sizeof(len), public_key, sizeof(public_key));
}

void LogCrypt::setFooterInfo(char *_data) {
    if (NULL == _data) return;
    char magic_end = kMagicEnd;
    memcpy(_data, &magic_end, sizeof(magic_end));
}

void LogCrypt::encryptLog(const char *_log, size_t _len, AutoBuffer& _out_buff, size_t& _remain_uncrypt_len) {
    _out_buff.alloc_write(_len);

    if (!is_crypt) {
        memcpy(_out_buff.ptr(), _log, _len);
        _remain_uncrypt_len = 0;
        return;
    }

    uint32_t tmp[2] = {0};
    size_t cnt = _len / TEA_BLOCK_LEN;
    _remain_uncrypt_len = _len % TEA_BLOCK_LEN;

    for (size_t i = 0; i < cnt; ++i) {
        memcpy(tmp, _log + i * TEA_BLOCK_LEN, TEA_BLOCK_LEN);
        tea_encrypt(tmp, tea_key);
        memcpy((char*)_out_buff.ptr() + i * TEA_BLOCK_LEN, tmp, TEA_BLOCK_LEN);
    }

    memcpy((char*) _out_buff.ptr() + _len - _remain_uncrypt_len, _log + _len - _remain_uncrypt_len, _remain_uncrypt_len);
}

//bool LogCrypt::fix(char *_data, size_t _data_len, uint32_t &_raw_len) {
//    if (_data_len < getHeaderLen()) return false;
//    if (kMagicCryptStart != _data[0] && kMagicStart != _data[0]) return false;
//    _raw_len = getLogLen(_data, _data_len);
//    return true;
//}
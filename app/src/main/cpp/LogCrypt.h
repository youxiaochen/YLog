//
// Created by you on 2018/11/9.
//

#include "AutoBuffer.h"

#ifndef CTEST_LOGCRYPT_H
#define CTEST_LOGCRYPT_H


class LogCrypt {

private:
    //没有加密开始标记
    static const char kMagicStart = '\x06';
    //加密开始标记
    static const char kMagicCryptStart ='\x08';
    //结束标记
    static const char kMagicEnd  = '\0';
    //TEA加密区长度
    static const int TEA_BLOCK_LEN = 8;
public:
    static uint32_t getHeaderLen();
    static uint32_t getFooterLen();

    static uint32_t getLogLen(const char* const _data, size_t _len);
    static void updateLogLen(char* _data, uint32_t _add_len);

private:
    //是否加密
    bool is_crypt = false;
    //ecc public key
    uint8_t public_key[64]{0};
    //tea加密key
    uint32_t tea_key[4];
public:
    //uECC_secp256k1 模式下十六进制字符串公钥长度128, 私64, 详见uEcc使用
    LogCrypt(const char *public_key_);

    ~LogCrypt();

    //设置头部信息, char(magicStart), len(uint32_t), publickey(64)
    void setHeaderInfo(char* _data);
    //footer
    void setFooterInfo(char* _data);
    //加密日志
    void encryptLog(const char *_log, size_t _len, AutoBuffer& _out_buff, size_t& _remain_uncrypt_len);
};

#endif //CTEST_LOGCRYPT_H

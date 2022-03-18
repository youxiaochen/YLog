//
// Created by you on 2018/11/12.
//

#include <cstdlib>

#ifndef JETPACK_TEA_CRYPT_H
#define JETPACK_TEA_CRYPT_H

//16进制字符串转buffer
bool hex2Buffer(const char *str, size_t len, unsigned char *buffer);

//buffer转16进制字符串, str长度应当+1用于结束符
bool buffer2Hex(const unsigned char *buffer, size_t len, char *str);

//tea解密
void tea_decrypt(uint32_t *v, uint32_t *key);

#endif //JETPACK_TEA_CRYPT_H

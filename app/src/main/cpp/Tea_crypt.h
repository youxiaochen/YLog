//
// Created by you on 2018/11/12.
//

#include <cstdlib>

#ifndef JETPACK_TEA_CRYPT_H
#define JETPACK_TEA_CRYPT_H

//16进制字符串转buffer
bool hex2Buffer(const char *str, size_t len, unsigned char *buffer);

//tea加密
void tea_encrypt(uint32_t *v, uint32_t *key);

#endif //JETPACK_TEA_CRYPT_H

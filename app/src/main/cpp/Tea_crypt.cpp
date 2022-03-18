//
// Created by you on 2018/11/12.
//

#include "Tea_crypt.h"

bool hex2Buffer(const char *str, size_t len, unsigned char *buffer) {
    if (NULL == str || len == 0 || (len & 1)) {
        return true;
    }
    char tmp[3] = {0};
    size_t i, j;
    for (i = 0; i < len - 1; i += 2) {
        for (j = 0; j < 2; ++j) {
            tmp[j] = str[i + j];
            if (!(('0' <= tmp[j] && tmp[j] <= '9') ||
                  ('a' <= tmp[j] && tmp[j] <= 'f') ||
                  ('A' <= tmp[j] && tmp[j] <= 'F'))) {
                return false;
            }
        }
        buffer[i / 2] = (unsigned char) strtol(tmp, NULL, 16);
    }
    return true;
}

void tea_encrypt(uint32_t *v, uint32_t *key) {
    uint32_t v0 = v[0], v1 = v[1], sum, i;
    const static uint32_t delta = 0x9e3779b9;
    const static uint32_t totalSum = 0x9e3779b9 << 4;
    sum = totalSum;

    for (i = 0; i < 16; i++) { //左移4即2^4
        v1 -= ((v0 << 4) + key[2]) ^ (v0 + sum) ^ ((v0 >> 5) + key[3]);
        v0 -= ((v1 << 4) + key[0]) ^ (v1 + sum) ^ ((v1 >> 5) + key[1]);
        sum -= delta;
    }
    v[0] = v0;
    v[1] = v1;
}
//
// Created by you on 2018/11/14.
//

#include <cstdlib>

#ifndef YLOG_YLOGUTILS_H
#define YLOG_YLOGUTILS_H

void *openMmapBuffer(const char *buffer_path, size_t buffer_size);

void closeMmapBuffer(void *buffer, size_t buffer_size);

#endif //YLOG_YLOGUTILS_H

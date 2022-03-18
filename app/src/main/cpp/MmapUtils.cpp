//
// Created by you on 2018/11/14.
//
#include <fcntl.h>
#include <sys/mman.h>
#include <zconf.h>
#include "MmapUtils.h"

void *openMmapBuffer(const char *buffer_path, size_t buffer_size) {
    int buffer_fd = open(buffer_path, O_RDWR|O_CREAT, S_IRUSR|S_IWUSR|S_IRGRP|S_IROTH);
    if (buffer_fd == -1) return NULL;//open error
    if (ftruncate(buffer_fd, buffer_size) == -1) {
        close(buffer_fd);
        return NULL;
    }
    lseek(buffer_fd, 0, SEEK_SET);
    char *mmap_ptr = (char*) mmap(0, buffer_size, PROT_WRITE | PROT_READ, MAP_SHARED, buffer_fd, 0);
    close(buffer_fd);
    if (MAP_FAILED != mmap_ptr && mmap_ptr) {
        return mmap_ptr;
    }
    return NULL;
}

void closeMmapBuffer(void *buffer, size_t buffer_size) {
    if (NULL != buffer) {
        munmap(buffer, buffer_size);
    }
}
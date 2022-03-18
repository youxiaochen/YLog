//
// Created by you on 2018/11/23.
//
#include "AutoBuffer.h"

AutoBuffer::AutoBuffer(size_t _size) : malloc_unitsize_(_size) {
}

AutoBuffer::~AutoBuffer() {
    if (NULL != parray_) {
        free(parray_);
        parray_ = NULL;
    }
}

void AutoBuffer::reset() {
    if (NULL != parray_) {
        memset(parray_, 0, capacity_);
    }
}

void *AutoBuffer::ptr() {
    return parray_;
}


void AutoBuffer::fitSize(size_t _len) {
    if (_len > capacity_) {
        size_t mallocsize = ((_len + malloc_unitsize_ - 1) / malloc_unitsize_) * malloc_unitsize_;
        void *p = realloc(parray_, mallocsize);
        if (NULL == p) {
            free(parray_);
            parray_ = NULL;
            capacity_ = 0;
            return;
        }
        parray_ = static_cast<char *>(p);
        memset(parray_ + capacity_, 0, mallocsize - capacity_);
        capacity_ = mallocsize;
    }
}
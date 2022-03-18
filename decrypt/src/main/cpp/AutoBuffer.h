//
// Created by you on 2018/11/23.
//

#ifndef CTEST_AUTOBUFFER2_H
#define CTEST_AUTOBUFFER2_H

#include <string>

class AutoBuffer {

private:
    char *parray_ = NULL;
    //当前Buffer容量
    size_t capacity_ = 0;
    //申请Buffer增长单元大小
    size_t malloc_unitsize_;

public:
    AutoBuffer(size_t _size = 128);

    ~AutoBuffer();

    void fitSize(size_t _len);

    void reset();

    void* ptr();
};


#endif //CTEST_AUTOBUFFER2_H

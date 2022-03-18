
cmake_minimum_required(VERSION 3.4.1)

set(CMAKE_C_FLAGS_RELEASE "${CMAKE_C_FLAGS_RELEASE} -Os -Wall -s")
set(CMAKE_CXX_FLAGS_RELEASE "${CMAKE_CXX_FLAGS_RELEASE} -Os -Wall -s")



#include头文件目录
include_directories(${PROJECT_SOURCE_DIR}/include)

file(GLOB SRC_CPP_LIST ${PROJECT_SOURCE_DIR}/*.cpp)

file(GLOB SRC_ECC_LIST ${PROJECT_SOURCE_DIR}/ecc/*.inc ${PROJECT_SOURCE_DIR}/ecc/*.c)

add_library(Decryptlog

        SHARED

        ${SRC_CPP_LIST} ${SRC_ECC_LIST} AutoBuffer.cpp)

target_link_libraries(Decryptlog
#        z_stream压缩
        z
        log)
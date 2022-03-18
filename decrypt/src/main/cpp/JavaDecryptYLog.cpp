#include <jni.h>
#include <string>
#include <zlib.h>
#include <sys/stat.h>
#include <dirent.h>

#include "uECC.h"

#include "Log.h"
#include "Tea_crypt.h"
#include "AutoBuffer.h"


static const char *privateKey_ = "145aa7717bf9745b91e9569b80bbf1eedaa6cc6cd0e26317d810e35710f44cf8";

//---------------------------  以下这些static const的要与加密的定义一致  -------------------------------

//加密开始标记
static const char kMagicCryptStart = '\x08';
//结束标记
static const char kMagicEnd = '\0';
//没有加密开始标记
static const char kMagicStart = '\x06';
//TEA加密区长度
static const int TEA_BLOCK_LEN = 8;

uint32_t getHeaderLen() {
    return sizeof(char) + sizeof(uint32_t) + sizeof(char) * 64;
}

uint32_t getLogLen(const char *const _data) {
    if (kMagicCryptStart != _data[0]) return 0;
    uint32_t len = 0;
    memcpy(&len, _data + sizeof(char), sizeof(len));
    return len;
}

//----------------------------  以上这些static const的要与加密的定义一致 -------------------------------

void descrypt(char *data, size_t length, const uint8_t *public_key, const uint8_t *privateKey_) {
    unsigned char ecdhKey[32] = {0};
    if (0 == uECC_shared_secret(public_key, privateKey_, ecdhKey, uECC_secp256k1())) {
        LOGD("teaKey error");
        return;
    }
    uint32_t teaKey[4];
    memcpy(teaKey, ecdhKey, sizeof(teaKey));
    uint32_t tmp[2] = {0};
    size_t cnt = length / TEA_BLOCK_LEN;

    size_t i;
    for (i = 0; i < cnt; i++) {
        memcpy(tmp, data + i * TEA_BLOCK_LEN, TEA_BLOCK_LEN);
        tea_decrypt(tmp, teaKey);
        memcpy(data + i * TEA_BLOCK_LEN, tmp, TEA_BLOCK_LEN);
    }
}

bool zlibDecompress(const char *compressedBytes, size_t compressedBytesSize, char **outBuffer, size_t *outBufferSize) {
    *outBuffer = NULL;
    *outBufferSize = 0;
    if (compressedBytesSize == 0) {
        return true;
    }

    unsigned fullLength = compressedBytesSize;
    unsigned halfLength = compressedBytesSize / 2;

    unsigned uncompLength = fullLength;
    char *uncomp = (char *) calloc(sizeof(char), uncompLength);

    z_stream strm;
    strm.next_in = (Bytef *) compressedBytes;
    strm.avail_in = compressedBytesSize;
    strm.total_out = 0;
    strm.zalloc = Z_NULL;
    strm.zfree = Z_NULL;

    bool done = false;

    if (inflateInit2(&strm, (-MAX_WBITS)) != Z_OK) {
        free(uncomp);
        return false;
    }

    while (!done) {
        // If our output buffer is too small
        if (strm.total_out >= uncompLength) {
            // Increase size of output buffer
            char *uncomp2 = (char *) calloc(sizeof(char), uncompLength + halfLength);
            memcpy(uncomp2, uncomp, uncompLength);
            uncompLength += halfLength;
            free(uncomp);
            uncomp = uncomp2;
        }

        strm.next_out = (Bytef *) (uncomp + strm.total_out);
        strm.avail_out = uncompLength - strm.total_out;

        // Inflate another chunk.
        int err = inflate(&strm, Z_SYNC_FLUSH);
        if (err == Z_STREAM_END) {
            done = true;
        } else if (err != Z_OK) {
            break;
        }
    }

    if (inflateEnd(&strm) != Z_OK) {
        free(uncomp);
        return false;
    }

    *outBuffer = uncomp;
    *outBufferSize = strm.total_out;
    return true;
}



extern "C" JNIEXPORT void JNICALL
Java_you_chen_ylog_LogAppender_test(JNIEnv *env, jclass clazz, jstring logfile_path_, jstring out_path_) {
    const char *logfile_path = env->GetStringUTFChars(logfile_path_, JNI_FALSE);
    const char *out_path = env->GetStringUTFChars(out_path_, JNI_FALSE);
    LOGD("test deEcnry %s", logfile_path);

    unsigned char svr_prikey[32] = {0};
    if (!hex2Buffer(privateKey_, 64, svr_prikey)) {
        LOGD("privatekey hex2Buffer error");
        return;
    }

    FILE *file = fopen(logfile_path, "rb");
    if (file == NULL) {
        LOGD(" file open error... %s", logfile_path);
        return;
    }
    FILE *outFile = fopen(out_path, "wb");
    if (outFile == NULL) {
        LOGD(" outfile open error... %s", logfile_path);
        return;
    }


    uint32_t header_len = getHeaderLen();
    char buffer[header_len];
    fseek(file, 0, SEEK_END);
    size_t fileSize = ftell(file);
    LOGD("init file size %d", fileSize);
    if (fileSize <= header_len) {
        LOGD(" file size 0 ...");
        fclose(file);
        return;
    }
    rewind(file);

    size_t readHeaderResult, readDataResult;
    uint32_t dataLen;

    while (true) {
        memset(buffer, 0, header_len);
        readHeaderResult = fread(buffer, 1, header_len, file);
        if (readHeaderResult != header_len) {
            LOGD("read header error... ");
            break;
        }
        dataLen = getLogLen(buffer) + 1;//+1 kMagicEnd标识
        LOGD("dataLen %d", dataLen);
        if (dataLen <= 0) break;

        char *data = new char[dataLen];
        readDataResult = fread(data, 1, dataLen, file);
        if (readDataResult != dataLen || data[dataLen - 1] != kMagicEnd) {
            LOGD("read data error... %d, %d", readDataResult, data[dataLen]);
            break;
        }
//        LOGD("data :%s, end:%d", data, data[dataLen - 1]);

        fileSize -= header_len + dataLen;

//        LOGD("left filseSize %d" , fileSize);
        uint8_t *clientPublicKey = reinterpret_cast<uint8_t *>(buffer + sizeof(char) +
                                                               sizeof(uint32_t));

        descrypt(data, dataLen - 1, clientPublicKey, svr_prikey);


        char *constStr;
        size_t outlen;
        zlibDecompress(data, dataLen - 1, &constStr, &outlen);
//        LOGD("constr %s", constStr);
        fwrite(constStr, outlen, 1, outFile);

        if (constStr != NULL) {
            delete[] constStr;
        }


        delete[] data;
        if (fileSize <= header_len) break;
    }


    fclose(file);
    fclose(outFile);
    env->ReleaseStringUTFChars(logfile_path_, logfile_path);
    env->ReleaseStringUTFChars(out_path_, out_path);
}

extern "C" JNIEXPORT void JNICALL
Java_you_chen_decrypt_LogDecrypt_decrypt(JNIEnv *env, jclass clazz, jstring log_path_, jstring decrypt_path_) {
    const char *log_path = env->GetStringUTFChars(log_path_, JNI_FALSE);
    const char *decrypt_path = env->GetStringUTFChars(decrypt_path_, JNI_FALSE);

    unsigned char svr_prikey[32] = {0};
    if (!hex2Buffer(privateKey_, 64, svr_prikey)) {
        LOGD("privateKey_ 您的密钥长度错了");
        return;
    }
    FILE *log_file = fopen(log_path, "rb");
    if (NULL == log_file) {
        LOGD(" file open error... %s", log_path);
        return;
    }
    FILE *decrypt_file = fopen(decrypt_path, "wb");
    if (NULL == decrypt_file) {
        LOGD(" outfile open error... %s", decrypt_path);
        fclose(log_file);
        return;
    }

    uint32_t header_len = getHeaderLen();
    fseek(log_file, 0, SEEK_END);
    size_t fileSize = ftell(log_file);
    LOGD("log file size %d", fileSize);
    if (fileSize <= header_len) {
        LOGD(" file size 0 ...");
        fclose(log_file);
        return;
    }
    rewind(log_file);

    char buffer[header_len];
    size_t readHeaderResult, readDataResult;
    uint32_t dataLen;
    AutoBuffer autoBuffer;

    while (true) {
        memset(buffer, 0, header_len);
        readHeaderResult = fread(buffer, 1, header_len, log_file);
        if (readHeaderResult != header_len) {
            LOGD("read header error... ");//格式错误
            break;
        }
        dataLen = getLogLen(buffer) + 1;//+1 kMagicEnd标识
        LOGD("dataLen %d", dataLen);
        if (dataLen <= 0) {
            LOGD("read data error... ");//无数据
            break;
        }

        autoBuffer.fitSize(dataLen);
        readDataResult = fread(autoBuffer.ptr(), 1, dataLen, log_file);
        if (readDataResult != dataLen || ((char*) autoBuffer.ptr())[dataLen -1] != kMagicEnd) {
            LOGD("read data error... %d, %d", readDataResult, ((char*) autoBuffer.ptr())[dataLen -1]);//读取长度与head实际长度不符,或者结束符不合
            break;
        }
        fileSize -= header_len + dataLen;

        uint8_t *clientPublicKey = reinterpret_cast<uint8_t *>(buffer + sizeof(char) + sizeof(uint32_t));
        descrypt((char*) autoBuffer.ptr(), dataLen - 1, clientPublicKey, svr_prikey);
        char *constStr;
        size_t outlen;
        zlibDecompress((char*) autoBuffer.ptr(), dataLen - 1, &constStr, &outlen);
        fwrite(constStr, outlen, 1, decrypt_file);

        if (constStr != NULL) {
            delete[] constStr;
        }
        autoBuffer.reset();
        if (fileSize <= header_len) break;
    }
    fclose(log_file);
    fclose(decrypt_file);

    env->ReleaseStringUTFChars(log_path_, log_path);
    env->ReleaseStringUTFChars(decrypt_path_, decrypt_path);
}

extern "C"
JNIEXPORT jstring JNICALL
Java_you_chen_decrypt_LogDecrypt_createEccKey(JNIEnv *env, jclass clazz) {
    uint8_t clientPublicKey[64] = {0};
    uint8_t clientPrivateKey[32] = {0};
    int res = uECC_make_key(clientPublicKey, clientPrivateKey, uECC_secp256k1());
    if (res == 0) {
        LOGD("make key error");
        return NULL;
    }

    char public_key_hex[64 * 2 + 1] = {0};
    char private_key_hex[32 * 2 + 1] = {0};
    buffer2Hex(clientPublicKey, 64, public_key_hex);
    buffer2Hex(clientPrivateKey, 32, private_key_hex);

    std::string str = "你的公钥用于加密日志的,请放到日志打印Java2YLog.cpp的publicKey_中,值为:\n";
    str.append(public_key_hex);
    str.append("\n你的私私的钥用于解密日志的,请放到JavaDecryptYlog.cpp的privateKey_中,值为:\n");
    str.append(private_key_hex);
    return env->NewStringUTF(str.c_str());
}
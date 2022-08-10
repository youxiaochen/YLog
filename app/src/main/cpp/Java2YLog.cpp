//
// Created by you on 2018/11/23.
//
#include <jni.h>

#include "LogAppender.h"

static const char* const javaLogClassName = "you/chen/ylog/log/LogAppender";

static const char* const publicKey_ = "572d1e2710ae5fbca54c76a382fdd44050b3a675cb2bf39feebe85ef63d947aff0fa4943f1112e8b6af34bebebbaefa1a0aae055d9259b89a1858f7cc9af9df1";

static jlong init_native(JNIEnv *env, jclass clazz, jstring logfile_dir_,
                  jlong buffer_size, jlong flush_delay,
                  jlong max_log_size, jlong max_logalive_time,
                  jboolean isDebug) {
    const char *logfile_dir = env->GetStringUTFChars(logfile_dir_, JNI_FALSE);
//    LOGD("init native logPath %s", logfile_dir);
    LogAppender *logAppender = new LogAppender(logfile_dir, buffer_size, flush_delay, max_log_size,
                                               max_logalive_time, isDebug);
    env->ReleaseStringUTFChars(logfile_dir_, logfile_dir);
    return reinterpret_cast<jlong>(logAppender);
}

static void open_buffer(JNIEnv *env, jobject thiz, jlong log_appender, jstring buffer_path_) {
    LogAppender *logAppender = reinterpret_cast<LogAppender *>(log_appender);
    const char *buffer_path = env->GetStringUTFChars(buffer_path_, JNI_FALSE);
//    LOGD("open native logbuffer %s", buffer_path);
    logAppender->appender_open(buffer_path, publicKey_);
    env->ReleaseStringUTFChars(buffer_path_, buffer_path);
}

static void appender_log(JNIEnv *env, jobject thiz, jlong log_appender, jstring log_data_) {
    LogAppender *logAppender = reinterpret_cast<LogAppender *>(log_appender);
    const char *log_data = env->GetStringUTFChars(log_data_, JNI_FALSE);
//    LOGD("appender : %s", log_data);
    size_t log_data_len = env->GetStringUTFLength(log_data_);
    logAppender->appender_async(log_data, log_data_len);
    env->ReleaseStringUTFChars(log_data_, log_data);
}

static void flush_buffer(JNIEnv *env, jobject thiz, jlong log_appender) {
//    LOGD("logAppender flush");
    LogAppender *logAppender = reinterpret_cast<LogAppender *>(log_appender);
    logAppender->appender_flush();
}

static void close_buffer(JNIEnv *env, jobject thiz, jlong log_appender) {
//    LOGD("logAppender close");
    LogAppender *logAppender = reinterpret_cast<LogAppender *>(log_appender);
    logAppender->appender_close();
}

static void release(JNIEnv *env, jobject thiz, jlong log_appender) {
//    LOGD("logAppender release");
    LogAppender *logAppender = reinterpret_cast<LogAppender *>(log_appender);
    if (logAppender != NULL) {
        delete logAppender;
    }
}

JNINativeMethod gMethods[] = {
        {"initNative",  "(Ljava/lang/String;JJJJZ)J", (void *) init_native},
        {"openBuffer",  "(JLjava/lang/String;)V",     (void *) open_buffer},
        {"appender",    "(JLjava/lang/String;)V",     (void *) appender_log},
        {"flush",       "(J)V",                       (void *) flush_buffer},
        {"closeBuffer", "(J)V",                       (void *) close_buffer},
        {"release",     "(J)V",                       (void *) release}
};

extern "C" JNIEXPORT jint JNICALL
JNI_OnLoad(JavaVM *vm, void *reserved) {
    JNIEnv *env = NULL;
    if ((vm)->GetEnv((void **) &env, JNI_VERSION_1_4) != JNI_OK) {
        return JNI_ERR;
    }
    jclass javaLogClass = env->FindClass(javaLogClassName);
    assert(javaLogClass != NULL);
    if (env->RegisterNatives(javaLogClass, gMethods, sizeof(gMethods) / sizeof(gMethods[0])) < 0) {
        return JNI_ERR;
    }
    env->DeleteLocalRef(javaLogClass);
    return JNI_VERSION_1_4;
}


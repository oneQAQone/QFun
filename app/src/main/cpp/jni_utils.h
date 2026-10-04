#pragma once

#include <jni.h>

#include <android/log.h>

namespace qfun {

inline bool ClearException(JNIEnv* env, const char* where, const char* tag) {
    if (!env || !env->ExceptionCheck()) return false;
    __android_log_print(ANDROID_LOG_ERROR, tag, "JNI exception: %s", where);
    env->ExceptionDescribe();
    env->ExceptionClear();
    return true;
}

} // namespace qfun

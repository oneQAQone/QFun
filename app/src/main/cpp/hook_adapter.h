#pragma once

#include <jni.h>

namespace qfun {

bool InitializeHookEngine(JNIEnv* env);
bool RegisterBridgeNatives(JNIEnv* env, jobject loader, jmethodID load_class);
jobject HookMethod(JNIEnv* env, jclass clazz, jobject target_method, jobject hooker_object, jobject callback_method);
jboolean Deoptimize(JNIEnv* env, jclass clazz, jobject target_method);

} // namespace qfun

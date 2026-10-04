#include "zygisk_loader.h"

#include <android/log.h>

#include "hook_adapter.h"
#include "jni_utils.h"

#define TAG "QFunZygisk"
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, TAG, __VA_ARGS__)

namespace qfun {
namespace {

constexpr char kEntryClass[] = "me.yxp.qfun.loader.zygisk.ZygiskHookEntry";

bool CreatePathClassLoader(
    JNIEnv* env,
    const RuntimeAssets& assets,
    jclass& path_class_loader,
    jobject& loader,
    jmethodID& load_class) {
    path_class_loader = env->FindClass("dalvik/system/PathClassLoader");
    const bool lookup_exception = ClearException(env, "PathClassLoader lookup", TAG);
    if (!path_class_loader || lookup_exception) {
        LOGE("PathClassLoader unavailable");
        return false;
    }

    const jmethodID constructor = env->GetMethodID(
        path_class_loader,
        "<init>",
        "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/ClassLoader;)V");
    load_class = env->GetMethodID(
        path_class_loader,
        "loadClass",
        "(Ljava/lang/String;)Ljava/lang/Class;");
    const bool method_exception = ClearException(env, "PathClassLoader methods", TAG);
    if (!constructor || !load_class || method_exception) {
        LOGE("PathClassLoader methods unavailable");
        env->DeleteLocalRef(path_class_loader);
        path_class_loader = nullptr;
        return false;
    }

    jstring apk_path = env->NewStringUTF(assets.apk_path.c_str());
    jstring library_path = env->NewStringUTF(assets.library_path.c_str());
    loader = (apk_path && library_path)
        ? env->NewObject(path_class_loader, constructor, apk_path, library_path, static_cast<jobject>(nullptr))
        : nullptr;
    const bool loader_exception = ClearException(env, "create PathClassLoader", TAG);

    if (!loader || loader_exception) {
        LOGE("create PathClassLoader failed");
        if (library_path) env->DeleteLocalRef(library_path);
        if (apk_path) env->DeleteLocalRef(apk_path);
        env->DeleteLocalRef(path_class_loader);
        path_class_loader = nullptr;
        return false;
    }

    if (library_path) env->DeleteLocalRef(library_path);
    if (apk_path) env->DeleteLocalRef(apk_path);
    return true;
}

bool LoadEntryClass(JNIEnv* env, jobject loader, jmethodID load_class, jclass& entry) {
    jstring entry_name = env->NewStringUTF(kEntryClass);
    entry = entry_name
        ? static_cast<jclass>(env->CallObjectMethod(loader, load_class, entry_name))
        : nullptr;
    if (entry_name) env->DeleteLocalRef(entry_name);

    const bool load_exception = ClearException(env, "load ZygiskHookEntry", TAG);
    if (!entry || load_exception) {
        LOGE("load ZygiskHookEntry failed");
        return false;
    }
    return true;
}

bool InvokeEntryInit(JNIEnv* env, jclass entry, const char* process_name, const RuntimeAssets& assets) {
    const jmethodID init = env->GetStaticMethodID(
        entry,
        "init",
        "(Ljava/lang/String;Ljava/lang/String;)V");
    const bool lookup_exception = ClearException(env, "ZygiskHookEntry.init lookup", TAG);
    if (!init || lookup_exception) return false;

    jstring process = env->NewStringUTF(process_name);
    jstring apk = env->NewStringUTF(assets.apk_path.c_str());
    if (!process || !apk) {
        if (process) env->DeleteLocalRef(process);
        if (apk) env->DeleteLocalRef(apk);
        return false;
    }

    env->CallStaticVoidMethod(entry, init, process, apk);
    const bool success = !ClearException(env, "ZygiskHookEntry.init", TAG);

    env->DeleteLocalRef(process);
    env->DeleteLocalRef(apk);
    return success;
}

} // namespace

bool InitializeZygiskRuntime(JNIEnv* env, const char* process_name, const RuntimeAssets& assets) {
    if (!env || !process_name) return false;

    jclass path_class_loader = nullptr;
    jobject loader = nullptr;
    jmethodID load_class = nullptr;
    if (!CreatePathClassLoader(env, assets, path_class_loader, loader, load_class)) {
        return false;
    }

    const bool natives_registered = RegisterBridgeNatives(env, loader, load_class);
    const bool hook_engine_initialized = natives_registered && InitializeHookEngine(env);
    if (!natives_registered || !hook_engine_initialized) {
        LOGE("hook engine initialization failed");
        env->DeleteLocalRef(loader);
        env->DeleteLocalRef(path_class_loader);
        return false;
    }

    jclass entry = nullptr;
    const bool entry_loaded = LoadEntryClass(env, loader, load_class, entry);
    const bool initialized = entry_loaded && InvokeEntryInit(env, entry, process_name, assets);

    if (entry) env->DeleteLocalRef(entry);
    env->DeleteLocalRef(loader);
    env->DeleteLocalRef(path_class_loader);
    return initialized;
}

} // namespace qfun

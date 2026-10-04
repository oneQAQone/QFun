#include "hook_adapter.h"

#include "jni_utils.h"

#include <android/log.h>
#include <dobby.h>
#include <string>
#include <string_view>

#include <xdl.h>
#include "lsplant.hpp"

#define TAG "QFunHook"
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, TAG, __VA_ARGS__)

namespace {

constexpr char kBridgeClass[] = "me.yxp.qfun.loader.zygisk.ZygiskBridge";

void* g_art_handle = nullptr;
bool g_initialized = false;

void* ResolveArtSymbol(std::string_view name) {
    if (!g_art_handle || name.empty()) return nullptr;

    const std::string symbol(name);
    if (void* address = xdl_sym(g_art_handle, symbol.c_str(), nullptr)) return address;
    return xdl_dsym(g_art_handle, symbol.c_str(), nullptr);
}

void* ResolveArtPrefix(std::string_view prefix) {
    return ResolveArtSymbol(prefix);
}

void* HookWithDobby(void* target, void* replacement) {
    if (!target || !replacement) return nullptr;

    void* original = nullptr;
    if (DobbyHook(target, replacement, &original) == RS_SUCCESS) return original;

    LOGE("DobbyHook failed: %p", target);
    return nullptr;
}

bool UnhookWithDobby(void* target) {
    return target && DobbyDestroy(target) == RT_SUCCESS;
}

} // namespace

namespace qfun {

bool InitializeHookEngine(JNIEnv* env) {
    if (g_initialized) return true;

    g_art_handle = xdl_open("libart.so", XDL_DEFAULT);
    if (!g_art_handle) {
        LOGE("xdl_open(libart.so) failed");
        return false;
    }

    lsplant::InitInfo info{
        .inline_hooker = HookWithDobby,
        .inline_unhooker = UnhookWithDobby,
        .art_symbol_resolver = ResolveArtSymbol,
        .art_symbol_prefix_resolver = ResolveArtPrefix,
        .generated_class_name = "me/yxp/qfun/generated/HookStub",
        .generated_source_name = "QFunHook",
        .generated_field_name = "hooker",
        .generated_method_name = "{target}",
    };

    g_initialized = lsplant::Init(env, info);
    if (env->ExceptionCheck()) {
        ClearException(env, "LSPlant::Init", TAG);
        g_initialized = false;
    }
    return g_initialized;
}

bool RegisterBridgeNatives(JNIEnv* env, jobject loader, jmethodID load_class) {
    if (!env || !loader || !load_class) return false;

    jstring name = env->NewStringUTF(kBridgeClass);
    if (!name) {
        ClearException(env, "create bridge class name", TAG);
        return false;
    }

    auto bridge = static_cast<jclass>(env->CallObjectMethod(loader, load_class, name));
    env->DeleteLocalRef(name);
    if (!bridge || env->ExceptionCheck()) {
        ClearException(env, "load ZygiskBridge", TAG);
        if (bridge) env->DeleteLocalRef(bridge);
        return false;
    }

    static JNINativeMethod methods[] = {
        {const_cast<char*>("hookMethod"), const_cast<char*>("(Ljava/lang/reflect/Member;Ljava/lang/Object;Ljava/lang/reflect/Method;)Ljava/lang/reflect/Method;"), reinterpret_cast<void*>(HookMethod)},
        {const_cast<char*>("deoptimize"), const_cast<char*>("(Ljava/lang/reflect/Member;)Z"), reinterpret_cast<void*>(Deoptimize)},
    };

    const jint result = env->RegisterNatives(bridge, methods, 2);
    const bool success = result == JNI_OK && !env->ExceptionCheck();
    if (!success) ClearException(env, "RegisterNatives(ZygiskBridge)", TAG);

    env->DeleteLocalRef(bridge);
    return success;
}

jobject HookMethod(JNIEnv* env, jclass, jobject target_method, jobject hooker_object, jobject callback_method) {
    if (!g_initialized || !target_method || !hooker_object || !callback_method) return nullptr;
    return lsplant::Hook(env, target_method, hooker_object, callback_method);
}

jboolean Deoptimize(JNIEnv* env, jclass, jobject target_method) {
    if (!g_initialized || !target_method) return JNI_FALSE;
    return lsplant::Deoptimize(env, target_method) ? JNI_TRUE : JNI_FALSE;
}

} // namespace qfun

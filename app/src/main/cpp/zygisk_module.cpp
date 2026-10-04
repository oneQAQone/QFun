#include <jni.h>
#include <unistd.h>
#include <fcntl.h>
#include <android/log.h>

#include <cerrno>
#include <cstring>
#include <string>
#include <cstdlib>

#include "zygisk.hpp"
#include "runtime_assets.h"
#include "target_config.h"
#include "zygisk_loader.h"

#define TAG "QFunZygisk"
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, TAG, __VA_ARGS__)

namespace {

constexpr char kSelfPackageName[] = "me.yxp.qfun";
constexpr char kZygiskStateEnv[] = "QFUN_ZYGISK_STATE";
constexpr char kZygiskStateValue[] = "Activated";

int g_module_apk_fd = -1;

bool OpenModuleApk(zygisk::Api* api) {
    const int module_dir = api->getModuleDir();
    if (module_dir < 0) {
        LOGE("getModuleDir failed");
        return false;
    }

    const int fd = openat(module_dir, "module.apk", O_RDONLY | O_CLOEXEC);
    close(module_dir);
    if (fd < 0) {
        LOGE("open module.apk failed: %s", strerror(errno));
        return false;
    }

    api->exemptFd(fd);
    g_module_apk_fd = fd;
    return true;
}

void ReleaseModuleApkFd() {
    if (g_module_apk_fd < 0) return;
    close(g_module_apk_fd);
    g_module_apk_fd = -1;
}

} // namespace

class QFunModule final : public zygisk::ModuleBase {
public:
    void onLoad(zygisk::Api* api, JNIEnv* env) override {
        this->api = api;
        this->env = env;
    }

    void preAppSpecialize(zygisk::AppSpecializeArgs* args) override {
        should_hook = false;
        is_self = false;
        ReleaseModuleApkFd();

        if (!args || !args->nice_name) {
            api->setOption(zygisk::Option::DLCLOSE_MODULE_LIBRARY);
            return;
        }

        const char* nice_name = env->GetStringUTFChars(args->nice_name, nullptr);
        if (!nice_name) {
            api->setOption(zygisk::Option::DLCLOSE_MODULE_LIBRARY);
            return;
        }

        const std::string package = qfun::ProcessPackage(nice_name);
        if (package == kSelfPackageName) {
            is_self = true;
            env->ReleaseStringUTFChars(args->nice_name, nice_name);
            api->setOption(zygisk::Option::DLCLOSE_MODULE_LIBRARY);
            return;
        }

        const int config_dir = api->getModuleDir();
        if (config_dir >= 0) {
            should_hook = qfun::ShouldHookApp(nice_name, args->uid, config_dir);
            close(config_dir);
        } else {
            LOGE("getModuleDir failed while reading config");
        }

        if (should_hook && !OpenModuleApk(api)) should_hook = false;
        if (!should_hook) api->setOption(zygisk::Option::DLCLOSE_MODULE_LIBRARY);

        env->ReleaseStringUTFChars(args->nice_name, nice_name);
    }

    void postAppSpecialize(const zygisk::AppSpecializeArgs* args) override {
        if (is_self) {
            setenv(kZygiskStateEnv, kZygiskStateValue, 1);
            return;
        }

        if (!should_hook || !args || !args->nice_name || !args->app_data_dir) return;

        const char* data_dir = env->GetStringUTFChars(args->app_data_dir, nullptr);
        if (!data_dir) return;

        qfun::RuntimeAssets assets;
        const bool deployed = qfun::DeployRuntimeAssets(env, g_module_apk_fd, data_dir, assets);
        env->ReleaseStringUTFChars(args->app_data_dir, data_dir);
        if (!deployed) {
            LOGE("runtime assets unavailable");
            return;
        }

        const char* process_name = env->GetStringUTFChars(args->nice_name, nullptr);
        if (!process_name) return;

        const bool initialized = qfun::InitializeZygiskRuntime(env, process_name, assets);
        env->ReleaseStringUTFChars(args->nice_name, process_name);
        if (!initialized) {
            LOGE("hook engine initialization failed");
        }
    }

private:
    zygisk::Api* api = nullptr;
    JNIEnv* env = nullptr;
    bool should_hook = false;
    bool is_self = false;
};

REGISTER_ZYGISK_MODULE(QFunModule)

#pragma once

#include <jni.h>

#include <string>

namespace qfun {

struct RuntimeAssets {
    std::string apk_path;
    std::string library_path;
};

bool DeployRuntimeAssets(JNIEnv* env, int& module_apk_fd, const char* data_dir, RuntimeAssets& assets);

} // namespace qfun

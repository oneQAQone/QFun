#pragma once

#include <jni.h>

#include "runtime_assets.h"

namespace qfun {

bool InitializeZygiskRuntime(
    JNIEnv* env,
    const char* process_name,
    const RuntimeAssets& assets);

} // namespace qfun

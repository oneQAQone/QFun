#include "target_config.h"

#include <android/log.h>
#include <fcntl.h>
#include <unistd.h>

#include <cerrno>
#include <cstdlib>

#define TAG "QFunZygisk"
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, TAG, __VA_ARGS__)

namespace qfun {

std::string ProcessPackage(std::string_view process) {
    const size_t colon = process.find(':');
    return std::string(process.substr(0, colon));
}

namespace {

size_t SkipWhitespace(std::string_view value, size_t position) {
    while (position < value.size()) {
        const char c = value[position];
        if (c != ' ' && c != '\t' && c != '\r' && c != '\n') break;
        ++position;
    }
    return position;
}

bool JsonBoolEnabled(std::string_view object) {
    const size_t position = object.find("\"enabled\"");
    if (position == std::string_view::npos) return false;

    const size_t colon = object.find(':', position + 9);
    if (colon == std::string_view::npos) return false;

    const size_t value = SkipWhitespace(object, colon + 1);
    return object.substr(value, 4) == "true";
}

bool JsonUserIdMatches(std::string_view object, int user_id) {
    const size_t position = object.find("\"userId\"");
    if (position == std::string_view::npos) return false;

    const size_t colon = object.find(':', position + 8);
    if (colon == std::string_view::npos) return false;

    const size_t value = SkipWhitespace(object, colon + 1);
    char* end = nullptr;
    const long parsed = strtol(object.data() + value, &end, 10);
    return end != object.data() + value && parsed == user_id;
}

} // namespace

bool ReadTargetConfig(int module_dir_fd, std::string& config) {
    if (module_dir_fd < 0) return false;

    const int fd = openat(module_dir_fd, "target_config.json", O_RDONLY | O_CLOEXEC);
    if (fd < 0) return false;

    char buffer[4096];
    while (true) {
        const ssize_t count = read(fd, buffer, sizeof(buffer));
        if (count == 0) break;
        if (count < 0) {
            close(fd);
            return false;
        }
        config.append(buffer, static_cast<size_t>(count));
    }

    close(fd);
    return true;
}

bool ConfigEnablesPackage(std::string_view json, std::string_view package, int user_id) {
    size_t position = 0;
    while ((position = json.find('{', position)) != std::string_view::npos) {
        const size_t end = json.find('}', position + 1);
        if (end == std::string_view::npos) break;

        const std::string_view object = json.substr(position, end - position + 1);
        const size_t package_key = object.find("\"packageName\"");
        if (package_key != std::string_view::npos) {
            const size_t colon = object.find(':', package_key + 13);
            if (colon != std::string_view::npos) {
                size_t value = SkipWhitespace(object, colon + 1);
                if (value < object.size() && object[value] == '"') {
                    ++value;
                    const size_t quote = object.find('"', value);
                    if (quote != std::string_view::npos &&
                        object.substr(value, quote - value) == package &&
                        JsonUserIdMatches(object, user_id) &&
                        JsonBoolEnabled(object)) {
                        return true;
                    }
                }
            }
        }
        position = end + 1;
    }
    return false;
}

bool ShouldHookApp(const char* nice_name, int uid, int module_dir_fd) {
    if (!nice_name || module_dir_fd < 0) return false;

    const std::string package = ProcessPackage(nice_name);
    if (package != "com.tencent.mobileqq" && package != "com.tencent.tim") {
        return false;
    }

    std::string config;
    if (!ReadTargetConfig(module_dir_fd, config)) {
        LOGE("target_config.json unavailable");
        return false;
    }

    const int user_id = uid / 100000;
    return ConfigEnablesPackage(config, package, user_id);
}

} // namespace qfun

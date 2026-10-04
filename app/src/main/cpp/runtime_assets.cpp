#include "runtime_assets.h"

#include <android/log.h>
#include <dirent.h>
#include <fcntl.h>
#include <sys/file.h>
#include <sys/stat.h>
#include <unistd.h>

#include <cerrno>
#include <cstdio>
#include <cstring>
#include <string>
#include <string_view>

#include <miniz.h>

#define TAG "QFunRuntime"
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, TAG, __VA_ARGS__)

namespace qfun {
namespace {

const char* CurrentAbi() {
#if defined(__aarch64__)
    return "arm64-v8a";
#elif defined(__arm__)
    return "armeabi-v7a";
#elif defined(__x86_64__)
    return "x86_64";
#elif defined(__i386__)
    return "x86";
#else
    return nullptr;
#endif
}

struct LockGuard {
    int fd = -1;
    explicit LockGuard(const std::string& path) {
        fd = open(path.c_str(), O_CREAT | O_RDWR | O_CLOEXEC, 0600);
        if (fd >= 0) flock(fd, LOCK_EX);
    }
    ~LockGuard() {
        if (fd >= 0) {
            flock(fd, LOCK_UN);
            close(fd);
        }
    }
};

std::string ParseUuid(mz_zip_archive* zip) {
    size_t size = 0;
    char* data = reinterpret_cast<char*>(mz_zip_reader_extract_file_to_heap(zip, "module.prop", &size, 0));
    if (!data) return "";

    std::string_view content(data, size);
    std::string uuid;
    size_t pos = content.find("uuid=");
    if (pos != std::string_view::npos) {
        pos += 5;
        size_t end = content.find_first_of("\r\n", pos);
        uuid = std::string(content.substr(pos, end == std::string_view::npos ? end : end - pos));
    }
    mz_free(data);
    return uuid;
}

std::string ReadFdUuid(int fd) {
    if (fd < 0) return "";

    off_t original_offset = lseek(fd, 0, SEEK_CUR);

    int dup_fd = dup(fd);
    if (dup_fd < 0) return "";
    FILE* fp = fdopen(dup_fd, "rb");
    if (!fp) {
        close(dup_fd);
        return "";
    }

    mz_zip_archive zip{};
    std::string uuid;
    if (mz_zip_reader_init_cfile(&zip, fp, 0, 0)) {
        uuid = ParseUuid(&zip);
        mz_zip_reader_end(&zip);
    }
    fclose(fp);

    lseek(fd, original_offset, SEEK_SET);
    return uuid;
}

std::string ReadFileUuid(const std::string& path) {
    struct stat st{};
    if (stat(path.c_str(), &st) != 0 || st.st_size <= 0) return "";

    mz_zip_archive zip{};
    std::string uuid;
    if (mz_zip_reader_init_file(&zip, path.c_str(), 0)) {
        uuid = ParseUuid(&zip);
        mz_zip_reader_end(&zip);
    }
    return uuid;
}

bool RemoveTree(const std::string& path) {
    struct stat st{};
    if (lstat(path.c_str(), &st) != 0) return errno == ENOENT;
    if (!S_ISDIR(st.st_mode)) return unlink(path.c_str()) == 0;

    DIR* dir = opendir(path.c_str());
    if (!dir) return false;

    bool success = true;
    while (dirent* item = readdir(dir)) {
        if (strcmp(item->d_name, ".") == 0 || strcmp(item->d_name, "..") == 0) continue;
        if (!RemoveTree(path + "/" + item->d_name)) success = false;
    }
    closedir(dir);

    return success && rmdir(path.c_str()) == 0;
}

bool CopyModuleApk(int& module_apk_fd, const std::string& apk_path) {
    if (module_apk_fd < 0) return false;

    if (lseek(module_apk_fd, 0, SEEK_SET) < 0) {
        close(module_apk_fd);
        module_apk_fd = -1;
        return false;
    }

    const int output = open(apk_path.c_str(), O_WRONLY | O_CREAT | O_TRUNC | O_CLOEXEC, 0500);
    if (output < 0) {
        close(module_apk_fd);
        module_apk_fd = -1;
        return false;
    }

    bool success = true;
    char buffer[32768];
    while (true) {
        const ssize_t count = read(module_apk_fd, buffer, sizeof(buffer));
        if (count == 0) break;
        if (count < 0) {
            success = false;
            break;
        }

        ssize_t offset = 0;
        while (offset < count) {
            const ssize_t written = write(output, buffer + offset, static_cast<size_t>(count - offset));
            if (written <= 0) {
                success = false;
                break;
            }
            offset += written;
        }
        if (!success) break;
    }

    close(output);
    close(module_apk_fd);
    module_apk_fd = -1;

    if (!success) {
        unlink(apk_path.c_str());
        return false;
    }

    return chmod(apk_path.c_str(), 0500) == 0;
}

bool ExtractNativeLibraries(const std::string& apk_path, const std::string& library_path) {
    const char* abi = CurrentAbi();
    if (apk_path.empty() || library_path.empty() || !abi) return false;

    mz_zip_archive zip{};
    if (!mz_zip_reader_init_file(&zip, apk_path.c_str(), 0)) return false;

    const std::string prefix = std::string("lib/") + abi + "/";
    const mz_uint count = mz_zip_reader_get_num_files(&zip);

    for (mz_uint i = 0; i < count; ++i) {
        char name[512];
        if (!mz_zip_reader_get_filename(&zip, i, name, sizeof(name))) continue;
        if (mz_zip_reader_is_file_a_directory(&zip, i)) continue;

        std::string_view entry_name(name);
        if (entry_name.starts_with(prefix) && entry_name.ends_with(".so")) {
            std::string_view base = entry_name.substr(prefix.size());
            if (base != "libqfun_zygisk.so" && base.find('/') == std::string_view::npos) {
                const std::string target = library_path + "/" + std::string(base);
                if (!mz_zip_reader_extract_to_file(&zip, i, target.c_str(), 0)) {
                    mz_zip_reader_end(&zip);
                    return false;
                }
                chmod(target.c_str(), 0500);
            }
        }
    }

    mz_zip_reader_end(&zip);
    return true;
}

} // namespace

bool DeployRuntimeAssets([[maybe_unused]] JNIEnv* env, int& module_apk_fd, const char* data_dir, RuntimeAssets& assets) {
    if (module_apk_fd < 0 || !data_dir || !*data_dir) return false;

    const std::string runtime_dir = std::string(data_dir) + "/qfun";
    assets.apk_path = runtime_dir + "/module.apk";
    assets.library_path = runtime_dir + "/lib";

    const std::string src_uuid = ReadFdUuid(module_apk_fd);
    if (src_uuid.empty()) {
        close(module_apk_fd);
        module_apk_fd = -1;
        return false;
    }

    auto is_target_valid = [&]() -> bool {
        const std::string dst_uuid = ReadFileUuid(assets.apk_path);
        return !dst_uuid.empty() && src_uuid == dst_uuid;
    };

    if (is_target_valid()) {
        close(module_apk_fd);
        module_apk_fd = -1;
        return true;
    }

    LockGuard lock(std::string(data_dir) + "/qfun.lock");
    if (is_target_valid()) {
        close(module_apk_fd);
        module_apk_fd = -1;
        return true;
    }

    if (!RemoveTree(runtime_dir) || mkdir(runtime_dir.c_str(), 0700) != 0) {
        close(module_apk_fd);
        module_apk_fd = -1;
        return false;
    }

    if (!CopyModuleApk(module_apk_fd, assets.apk_path)) return false;
    if (mkdir(assets.library_path.c_str(), 0700) != 0) return false;

    return ExtractNativeLibraries(assets.apk_path, assets.library_path);
}

} // namespace qfun
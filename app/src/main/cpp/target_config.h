#pragma once

#include <string>
#include <string_view>

namespace qfun {

std::string ProcessPackage(std::string_view process);
bool ReadTargetConfig(int module_dir_fd, std::string& config);
bool ConfigEnablesPackage(std::string_view json, std::string_view package, int user_id);
bool ShouldHookApp(const char* nice_name, int uid, int module_dir_fd);

} // namespace qfun

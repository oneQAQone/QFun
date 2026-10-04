cmake_minimum_required(VERSION 3.28)

if(POLICY CMP0169)
    cmake_policy(SET CMP0169 OLD)
endif()

project(qfun_zygisk)

set(CMAKE_CXX_STANDARD 23)

set(CMAKE_C_VISIBILITY_PRESET hidden)
set(CMAKE_CXX_VISIBILITY_PRESET hidden)
set(CMAKE_VISIBILITY_INLINES_HIDDEN ON)

set(COMMON_FLAGS "-Oz -ffunction-sections -fdata-sections -fno-exceptions -fno-unwind-tables -fno-asynchronous-unwind-tables")
set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} ${COMMON_FLAGS}")
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} ${COMMON_FLAGS} -fno-rtti")

find_package(cxx REQUIRED CONFIG)
find_package(dobby REQUIRED CONFIG)

include(FetchContent)

FetchContent_Declare(
    xdl_source
    GIT_REPOSITORY https://github.com/hexhacking/xDL.git
    GIT_TAG        master
    GIT_SHALLOW    TRUE
)
FetchContent_GetProperties(xdl_source)
if(NOT xdl_source_POPULATED)
    FetchContent_Populate(xdl_source)
    file(GLOB XDL_SRCS "${xdl_source_SOURCE_DIR}/xdl/src/main/cpp/*.c")
    add_library(xdl_static STATIC ${XDL_SRCS})
    target_include_directories(xdl_static PUBLIC "${xdl_source_SOURCE_DIR}/xdl/src/main/cpp/include")
endif()

FetchContent_Declare(
    lsplant_source
    GIT_REPOSITORY https://github.com/LSPosed/LSPlant.git
    GIT_TAG        master
    GIT_SHALLOW    TRUE
    GIT_SUBMODULES ""
)
FetchContent_GetProperties(lsplant_source)
if(NOT lsplant_source_POPULATED)
    FetchContent_Populate(lsplant_source)
endif()

FetchContent_Declare(
    dexbuilder_source
    GIT_REPOSITORY https://github.com/LSPosed/DexBuilder.git
    GIT_TAG        master
    GIT_SHALLOW    TRUE
    SOURCE_DIR     ${lsplant_source_SOURCE_DIR}/lsplant/src/main/jni/external/dex_builder
)
FetchContent_GetProperties(dexbuilder_source)
if(NOT dexbuilder_source_POPULATED)
    FetchContent_Populate(dexbuilder_source)
endif()

set(LSPLANT_BUILD_SHARED OFF CACHE BOOL "Disable shared library" FORCE)
set(LSPLANT_STANDALONE ON CACHE BOOL "Enable standalone libcxx" FORCE)
add_subdirectory(${lsplant_source_SOURCE_DIR}/lsplant/src/main/jni ${CMAKE_CURRENT_BINARY_DIR}/lsplant_build)

FetchContent_Declare(
    miniz_source
    GIT_REPOSITORY https://github.com/richgel999/miniz.git
    GIT_TAG        master
    GIT_SHALLOW    TRUE
)
FetchContent_GetProperties(miniz_source)
if(NOT miniz_source_POPULATED)
    FetchContent_Populate(miniz_source)
    set(BUILD_SHARED_LIBS OFF CACHE BOOL "" FORCE)
    set(BUILD_EXAMPLES OFF CACHE BOOL "" FORCE)
    set(BUILD_TESTS OFF CACHE BOOL "" FORCE)
    set(BUILD_FUZZERS OFF CACHE BOOL "" FORCE)
    add_subdirectory(${miniz_source_SOURCE_DIR} ${CMAKE_CURRENT_BINARY_DIR}/miniz EXCLUDE_FROM_ALL)
endif()

add_library(qfun_zygisk SHARED
    zygisk_module.cpp
    zygisk_loader.cpp
    target_config.cpp
    hook_adapter.cpp
    runtime_assets.cpp
)

target_include_directories(qfun_zygisk PRIVATE
    ${CMAKE_CURRENT_SOURCE_DIR}
    ${lsplant_source_SOURCE_DIR}/lsplant/src/main/jni/include
)

target_link_libraries(qfun_zygisk PRIVATE
    lsplant_static
    cxx::cxx
    dobby::dobby
    xdl_static
    miniz
    log
    android
)

target_link_options(qfun_zygisk PRIVATE
    "-Wl,--gc-sections"
    "-Wl,--strip-all"
    "-Wl,--exclude-libs,ALL"
    "-Wl,-z,max-page-size=16384"
)
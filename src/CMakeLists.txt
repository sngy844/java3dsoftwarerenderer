cmake_minimum_required(VERSION 3.11 FATAL_ERROR)
project(swrast_native)

add_library(gfx_native SHARED swrast_GfxNative.h swrast_GfxNative.c)
target_include_directories(gfx_native PUBLIC "C:/Users/Son Nguyen/.jdks/azul-1.8.0_372/include;C:/Users/Son Nguyen/.jdks/azul-1.8.0_372/include/win32")


string(
    APPEND _cmd
    "$<IF:$<CONFIG:Debug>,"
        "${CMAKE_COMMAND};-E;echo;foo,"
        "${CMAKE_COMMAND};-E;echo_append"
)

add_custom_command(TARGET gfx_native POST_BUILD        # Adds a post-build event to MyTest
    COMMAND ${CMAKE_COMMAND} -E copy  # which executes "cmake - E copy_if_different..."
        "${PROJECT_SOURCE_DIR}/build/Release/gfx_native.dll"      # <--this is in-file
        "${PROJECT_SOURCE_DIR}/../gfx_native.dll")    
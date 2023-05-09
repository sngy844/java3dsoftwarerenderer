cmake_minimum_required(VERSION 3.11 FATAL_ERROR)
project(swrast_native)

add_library(gfx_native SHARED swrast_GfxNative.h swrast_GfxNative.c CGfx.c CGfx.h)
target_include_directories(gfx_native PUBLIC "C:/Users/Son Nguyen/.jdks/azul-1.8.0_372/include;C:/Users/Son Nguyen/.jdks/azul-1.8.0_372/include/win32;C:/Program Files/Java/jdk1.8.0_101/include;C:/Program Files/Java/jdk1.8.0_101/include/win32")

if(MSVC)
    add_custom_command(TARGET gfx_native POST_BUILD        # Adds a post-build event to MyTest
    COMMAND ${CMAKE_COMMAND} -E copy  # which executes "cmake - E copy_if_different..."
        "${PROJECT_SOURCE_DIR}/build/Release/gfx_native.dll"      # <--this is in-file
        "${PROJECT_SOURCE_DIR}/../gfx_native.dll")
else()
    add_custom_command(TARGET gfx_native POST_BUILD        # Adds a post-build event to MyTest
    COMMAND ${CMAKE_COMMAND} -E copy  # which executes "cmake - E copy_if_different..."
        "${PROJECT_SOURCE_DIR}/build/libgfx_native.dll"      # <--this is in-file
        "${PROJECT_SOURCE_DIR}/../gfx_native.dll")
 endif()


if(BUILD_C_DEMO)
 
 find_package(SDL2 REQUIRED)
 add_executable(cdemo main.cpp display.cpp display.h CGfx.c CGfx.h)
 target_link_libraries(cdemo gfx_native ${SDL2_LIBRARIES})
 target_include_directories(cdemo PRIVATE ${SDL2_INCLUDE_DIRS})
 target_compile_definitions(cdemo PRIVATE SDL_FORMAT)
endif()

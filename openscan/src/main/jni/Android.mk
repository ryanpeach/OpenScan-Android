LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

# OpenCV
OPENCV_CAMERA_MODULES:=on
OPENCV_INSTALL_MODULES:=on
include ../../../OpenCV-android-sdk/sdk/native/jni/OpenCV.mk

LOCAL_MODULE    := CaptureJNI
LOCAL_SRC_FILES := CaptureJNI.cpp ../openscan/capture.cpp ../openscan/cvmethods.cpp ../openscan/geometry.cpp ../openscan/focus.cpp ../openscan/support.cpp ../openscan/filters.cpp
include $(BUILD_SHARED_LIBRARY)

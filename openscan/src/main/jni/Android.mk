LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

# OpenCV
OPENCV_CAMERA_MODULES:=on
OPENCV_INSTALL_MODULES:=on
include ../../sdk/native/jni/OpenCV.mk

LOCAL_SRC_FILES := ..\resources\cpp\proxy.c
LOCAL_MODULE    := CaptureJNI
include $(BUILD_SHARED_LIBRARY)
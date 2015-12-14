LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

# OpenCV
OPENCV_CAMERA_MODULES:=on
OPENCV_INSTALL_MODULES:=on
include ../../sdk/native/jni/OpenCV.mk

LOCAL_MODULE    := CaptureJNI
LOCAL_SRC_FILES := CaptureJNI.c
include $(BUILD_SHARED_LIBRARY)
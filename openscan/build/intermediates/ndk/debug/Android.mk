LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE := CaptureJNI
LOCAL_LDFLAGS := -Wl,--build-id
LOCAL_SRC_FILES := \
	/home/ryanpeach/Documents/Workspace/openscan-android/openscan/src/main/jni/Android.mk \
	/home/ryanpeach/Documents/Workspace/openscan-android/openscan/src/main/jni/CaptureJNI.cpp \
	/home/ryanpeach/Documents/Workspace/openscan-android/openscan/src/main/jni/CaptureJNI_Param.hpp \
	/home/ryanpeach/Documents/Workspace/openscan-android/openscan/src/main/jni/Application.mk \
	/home/ryanpeach/Documents/Workspace/openscan-android/openscan/src/main/jni/CaptureJNI.hpp \

LOCAL_C_INCLUDES += /home/ryanpeach/Documents/Workspace/openscan-android/openscan/src/main/jni
LOCAL_C_INCLUDES += /home/ryanpeach/Documents/Workspace/openscan-android/openscan/src/debug/jni

include $(BUILD_SHARED_LIBRARY)

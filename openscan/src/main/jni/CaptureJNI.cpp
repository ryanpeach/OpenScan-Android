#include <jni.h>

#include "opencv2/core.hpp"
#include <vector>
#include "../openscan/capture.hpp"

using namespace std;
using namespace cv;

JNIEXPORT jlong JNICALL Java_com_github_openscan_Capture_createCapture (JNIEnv * env, jobject thiz) {
	Capture out = new Capture();
	return &out;
}

//might work
JNIEXPORT void JNICALL Java_com_github_openscan_Capture_destroyCapture (JNIEnv * env, jobject thiz, jlong ptr) {
	delete[] (Capture*)ptr;
}

//might work
JNIEXPORT void JNICALL Java_com_github_openscan_Capture_setFrame (JNIEnv * env, jobject thiz, jlong ptr, jlong frame) {
	Capture *c = ((Capture*)ptr);
	c->Frame(*(Mat*)frame);
}

//might work
JNIEXPORT void JNICALL Java_com_github_openscan_Capture_runProcess
(JNIEnv * env, jobject thiz, jlong ptr, jlong mat1, jlong mat2, jlong mat3) {
	Capture *c = ((Capture*)ptr);
	auto outV = c->process();
	if (outV.empty()) {return;}
	
	//Declare matrixes and constants
	Mat const* m1 = (Mat*) mat1; Mat const* m2 = (Mat*) mat2; Mat const* m3 = (Mat*) mat3;
	m1 = outV[0]; m2 = outV[1]; m3 = outV[2];
	//int rows = outV[0].rows; int cols = outV[0].cols; auto type = outV[0].type;
	
	//Create in place
	//m1->create(rows, cols, type); m2->create(rows, cols, type); m3->create(rows, cols, type);
	//memcpy(m1->data, outV[0].data, m1->step * m1->rows);
	//memcpy(m2->data, outV[1].data, m2->step * m2->rows);
	//memcpy(m2->data, outV[2].data, m3->step * m3->rows);
}

//should work
JNIEXPORT void JNICALL Java_com_github_openscan_Capture_setValue
  (JNIEnv * env, jobject thiz, jlong ptr, jint param, jdouble value) {
	Capture *c = ((Capture*)ptr);
	c->setValue((Capture::Param)param,(double)value);
}

//should work
JNIEXPORT jint JNICALL Java_com_github_openscan_Capture_getValue
  (JNIEnv * env, jobject thiz, jlong ptr, jint param) {
	  Capture *c = ((Capture*)ptr);
	  return (jint)(c->getValue((Param)param));
}

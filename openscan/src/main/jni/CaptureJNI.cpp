#include <jni.h>

#include "opencv2/core.hpp"
#include <vector>
#include "../openscan/capture.hpp"

using namespace std;
using namespace cv;

JNIEXPORT jlong JNICALL Java_com_github_openscan_Capture_createCapture (JNIEnv * env, jobject thiz) {
	Capture * out = new Capture();
	return (jlong)out;
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
	Mat* m1 = (Mat*) mat1; Mat* m2 = (Mat*) mat2; Mat* m3 = (Mat*) mat3;
	int rows = outV[0].rows; int cols = outV[0].cols; int type = outV[0].type();
	Size s = Size(rows,cols);

	//Create in place
	m1->create(s, type); m2->create(s, type); m3->create(s, type);
	memcpy(m1->data, outV[0].data, m1->step * m1->rows);
	memcpy(m2->data, outV[1].data, m2->step * m2->rows);
	memcpy(m2->data, outV[2].data, m3->step * m3->rows);
}

//should work
JNIEXPORT void JNICALL Java_com_github_openscan_Capture_setValue
  (JNIEnv * env, jobject thiz, jlong ptr, jint param, jdouble value) {
	Capture *c = ((Capture*)ptr);
	c->setValue((OPT::Par)param,(double)value);
}

//should work
JNIEXPORT jint JNICALL Java_com_github_openscan_Capture_getValue
  (JNIEnv * env, jobject thiz, jlong ptr, jint param) {
	  Capture *c = ((Capture*)ptr);
	  return (jint)(c->getValue((OPT::Par)param));
}

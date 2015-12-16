package com.github.openscan;

import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;
import android.util.Log;

public class Capture {

    private static final String TAG = "Capture";

    private long ptr_;
	
    private native long createCapture();
    private native void destroyCapture(long ptr);
    private native void setFrame(long ptr, long frame);
    private native Mat[] runProcess(long ptr, long M1, long M2, long M3);
    
    private native void setValue(long ptr, int param, double value);
    private native int getValue(long ptr, int param);
    
    static {
    	System.loadLibrary("CaptureJNI");
        if (!OpenCVLoader.initDebug()) {
            // Handle initialization error
            Log.e(TAG,"CaptureJNI Instantiation Error");
        }
    }
    
    public enum Param {
    	ANGLETOL(0), DISTTOL(1), POLYTOL(2), ASPECTRATIO(3),
    	SIZERATIO(4), RATIOTOL(5), ETOL1(6), ETOL2(7), ESIZE(8), METHOD(9);
    
    	private final int value;
    	private Param(int value){
    		this.value = value;
    	}
    	
    	public int getValue() {
    		return value;
    	}
    }
    
    public Capture() {
        Log.v(TAG, "Creating Capture.");
        ptr_ = createCapture();
    }
    
    public void destroy() {
        Log.v(TAG, "Closing Capture.");
        destroyCapture(ptr_);
    }
	public void close() {destroy();}
    
    public void Frame(Mat frame) {
        Log.v(TAG, "Setting Frame...");
        setFrame(ptr_, frame.getNativeObjAddr());
    }
    
    public Mat[] process() {
        Log.v(TAG, "Processing Frame...");
		Mat m1 = new Mat();
		Mat m2 = new Mat();
		Mat m3 = new Mat();
    	Mat[] out = runProcess(ptr_, m1.getNativeObjAddr(), m2.getNativeObjAddr(), m3.getNativeObjAddr());
    	return new Mat[]{m1,m2,m3};
    }
    
    public void setValue(Param param, double value) {
        Log.v(TAG, String.format("Setting Param %d to %f", value, param.getValue()));
        setValue(ptr_, param.getValue(), value);
    }

    public int getValue(Param param) {
        int v = getValue(ptr_, param.getValue());
        Log.v(TAG, String.format("Returned Param %d: %f", v, param.getValue()));
        return v;
    }
}

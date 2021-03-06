package com.github.openscan;

import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;
import android.util.Log;

public class Capture {

    private static final String TAG = "Capture";

    private long ptr_;

    static {
        System.loadLibrary("CaptureJNI");
    }

    private native long createCapture();
    private native void destroyCapture(long ptr);
    private native void setFrame(long ptr, long frame);
    private native Mat[] runProcess(long ptr, long M1, long M2, long M3);
    
    private native void setValue(long ptr, int param, double value);
    private native int getValue(long ptr, int param);
    
    public enum Param {
    	ANGLETOL(0), DISTRATIO(1), POLYTOL(2), ASPECTRATIO(3),
    	SIZERATIO(4), RATIOTOL(5);
    
    	private final int value;
        private Param(int v){
            this.value = v;
        }

    	public static Param getParam(int v){
    		switch(v) {
                case 0: return ANGLETOL;
                case 1: return DISTRATIO;
                case 2: return POLYTOL;
                case 3: return ASPECTRATIO;
                case 4: return SIZERATIO;
                case 5: return RATIOTOL;
                default: return null;
            }
    	}
    	
    	public int getValue() {
    		return value;
    	}

        public String toString() {
            switch(value) {
                case 0: return "ANGLETOL";
                case 1: return "DISTRATIO";
                case 2: return "POLYTOL";
                case 3: return "ASPECTRATIO";
                case 4: return "SIZERATIO";
                case 5: return "RATIOTOL";
                default: return null;
            }
        }
    }

    public enum Method {
        FPCORNERS(0), STRONGBORDER(1), REGBORDER(2), AUTOBORDER(3);

        private final int value;
        private Method(int value){
            this.value = value;
        }

        public static Method getMethod(int v){
            switch(v) {
                case 0: return FPCORNERS;
                case 1: return STRONGBORDER;
                case 2: return REGBORDER;
                case 3: return AUTOBORDER;
                default: return null;
            }
        }

        public int getValue() {
            return value;
        }

        public String toString() {
            switch(value) {
                case 0: return "FPCORNERS";
                case 1: return "STRONGBORDER";
                case 2: return "REGBORDER";
                case 3: return "AUTOBORDER";
                default: return null;
            }
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

    public void selectMethod(Method m) {
        Log.v(TAG, "Selecting Method: " + m.toString());
        if (m==null){m = Method.AUTOBORDER;}
        setValue(ptr_, 9, m.getValue());
    }
}

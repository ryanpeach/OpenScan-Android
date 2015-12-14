package com.github.openscan;

import org.opencv.core.Mat;

public class Capture {
    private long ptr_;
	
    private native long createCapture();
    private native void destroyCapture(long ptr);
    private native void setFrame(long ptr, long frame);
    private native Mat[] runProcess(long ptr);
    
    private native void setValue(long ptr, int param, double value);
    private native int getValue(long ptr, int param);
    
    static {
    	System.loadLibrary("Capture");
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
    	ptr_ = createCapture();
    }
    
    public void destroy() {
    	destroyCapture(ptr_);
    }
	public void close() {destroy();}
    
    public void Frame(Mat frame) {
    	setFrame(ptr_, frame.getNativeObjAddr());
    }
    
    public Mat[] process() {
		Mat m1 = new Mat();
		Mat m2 = new Mat();
		Mat m3 = new Mat();
    	Mat[] out = runProcess(ptr_, m1.getNativeObjAddr(), m2.getNativeObjAddr(), m3.getNativeObjAddr());
    	return {m1,m2,m3};
    }
    
    public void setValue(Param param, double value) {
    	setValue(ptr_, param.getValue(), value);
    }

    public int getValue(Param param) {
    	getValue(ptr_, param.getValue());
    }
}

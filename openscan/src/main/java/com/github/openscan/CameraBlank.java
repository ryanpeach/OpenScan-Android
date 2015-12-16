package com.github.openscan;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;

import org.opencv.android.Utils;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.widget.ImageView;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Point;
import org.opencv.core.Scalar;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;

import java.io.ByteArrayOutputStream;


//Source: http://docs.opencv.org/2.4/doc/tutorials/introduction/android_binary_package/dev_with_OCV_on_Android.html#using-opencv-library-within-your-android-project
public class CameraBlank extends Activity implements CameraBridgeViewBase.CvCameraViewListener {

    private static final String TAG = "Camera";

    private Capture C;
    private Mat Preview, Data, thisFrame;
    private ImageView PreviewV, DataV, FrameV;
    private Bitmap PreviewBMP, DataBMP, FrameBMP;
    private boolean devMode = true;

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.v(TAG, "OpenCV Manager Connected!");
                    mOpenCvCameraView.enableView();
                } break;
                default:
                {
                    Log.e(TAG, "OpenCV Manager NOT Connected!");
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    @Override
    public void onResume()
    {
        Log.v(TAG, "Resuming...");
        super.onResume();
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_6, this, mLoaderCallback);
    }

    private CameraBridgeViewBase mOpenCvCameraView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.v(TAG, "Creating Camera!");

        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_camera_blank);
        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.capture_main);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);

        // Initialize Capture
        C = new Capture();

        // Get Views
        FrameV = (ImageView) findViewById(R.id.capture_main);
        PreviewV = (ImageView) findViewById(R.id.capture_preview);
        DataV = (ImageView) findViewById(R.id.capture_data);

        if(!devMode) {DataV.setVisibility(View.INVISIBLE);}
    }

    @Override
    public void onPause()
    {
        Log.v(TAG, "Paused...");
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    public void onDestroy() {
        Log.v(TAG, "Destroying Camera.");
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();

        //Destroy Capture
        C.destroy();
    }

    public void onCameraViewStarted(int width, int height) {
        Log.v(TAG, "Camera View Started.");
    }

    public void onCameraViewStopped() {
        Log.v(TAG, "Camera View Stopped.");
    }

    public Mat onCameraFrame(Mat inputFrame) {
        //Get the frame output from Capture
        C.Frame(inputFrame);
        Mat[] out = C.process();

        //Set preview to out[1] if is not null
        if (!out[1].empty()) {
            Log.i(TAG, "Page Found!");
            Preview = out[2];
            Data = out[1];
            thisFrame = out[0];
        }
        else {thisFrame = inputFrame;}

        // Create Bitmaps
        FrameBMP = Bitmap.createBitmap(thisFrame.cols(), thisFrame.rows(),Bitmap.Config.ARGB_8888);
        PreviewBMP = Bitmap.createBitmap(thisFrame.cols(), thisFrame.rows(),Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(thisFrame, FrameBMP);
        Utils.matToBitmap(Preview, PreviewBMP);

        if(devMode) {
            DataBMP = Bitmap.createBitmap(thisFrame.cols(), thisFrame.rows(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(Data, DataBMP);
            DataV.setImageBitmap(DataBMP);
        }

        // Update Views
        FrameV.setImageBitmap(FrameBMP);
        PreviewV.setImageBitmap(PreviewBMP);

        return thisFrame;
    }

    public void saveImage(View view) {
        if(!Preview.empty()) {
            Log.i(TAG, "Saving Image...");
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            PreviewBMP.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            MediaStore.Images.Media.insertImage(getContentResolver(), PreviewBMP, null, null);
        }
    }

}


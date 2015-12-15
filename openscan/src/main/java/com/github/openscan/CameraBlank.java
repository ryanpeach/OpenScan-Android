package com.github.openscan;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.os.Bundle;
import android.app.Activity;

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

    private Capture C;
    private Mat Preview, thisFrame;
    private ImageView PreviewV, FrameV;
    private Bitmap PreviewBMP, FrameBMP;

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    mOpenCvCameraView.enableView();
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    @Override
    public void onResume()
    {
        super.onResume();
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_6, this, mLoaderCallback);
    }

    private CameraBridgeViewBase mOpenCvCameraView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
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
    }

    @Override
    public void onPause()
    {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();

        //Destroy Capture
        C.destroy();
    }

    public void onCameraViewStarted(int width, int height) {
    }

    public void onCameraViewStopped() {
    }

    public Mat onCameraFrame(Mat inputFrame) {
        //Get the frame output from Capture
        C.Frame(inputFrame);
        Mat[] out = C.process();

        //Set preview to out[1] if is not null
        if (!out[1].empty()) {
            Preview = out[2];
            thisFrame = out[0];
        }
        else {thisFrame = inputFrame;}

        // Create Bitmaps
        FrameBMP = Bitmap.createBitmap(thisFrame.cols(), thisFrame.rows(),Bitmap.Config.ARGB_8888);
        PreviewBMP = Bitmap.createBitmap(thisFrame.cols(), thisFrame.rows(),Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(thisFrame, FrameBMP);
        Utils.matToBitmap(Preview, PreviewBMP);

        // Update Views
        FrameV.setImageBitmap(FrameBMP);
        PreviewV.setImageBitmap(PreviewBMP);

        return thisFrame;
    }

    public void saveImage(View view) {
        if(!Preview.empty()) {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            PreviewBMP.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            MediaStore.Images.Media.insertImage(getContentResolver(), PreviewBMP, null, null);
        }
    }

}


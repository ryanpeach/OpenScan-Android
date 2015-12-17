package com.github.openscan;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.os.Bundle;
import android.app.Activity;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;

import org.opencv.android.Utils;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
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
import android.widget.SeekBar;
import android.widget.Spinner;

import java.io.ByteArrayOutputStream;


//Source: http://docs.opencv.org/2.4/doc/tutorials/introduction/android_binary_package/dev_with_OCV_on_Android.html#using-opencv-library-within-your-android-project
public class CameraBlank extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener,
        View.OnClickListener, View.OnLongClickListener, SeekBar.OnSeekBarChangeListener, AdapterView.OnItemSelectedListener {

    private static final String TAG = "Camera";
    private int seekProgress;

    private Capture C;
    private Mat Preview, Data, thisFrame;
    private Bitmap PreviewBMP, DataBMP, FrameBMP;
    private boolean devMode = true;

    // Views
    private ImageView PreviewV, DataV, FrameV;
    private ImageButton SaveButton;
    private SeekBar AT, DR, PT, SR, RT;
    private Spinner MethodSel;

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
        if(!OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_6, this, mLoaderCallback)){
        	Log.e(TAG,"CaptureJNI Instantiation Error");
        }
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
        Log.v(TAG, "Finding Views...");
        FrameV = (ImageView) findViewById(R.id.capture_main);
        PreviewV = (ImageView) findViewById(R.id.capture_preview);
        DataV = (ImageView) findViewById(R.id.capture_data);
        SaveButton = (ImageButton) findViewById(R.id.save_button);
        MethodSel = (Spinner) findViewById(R.id.method_sel);
        AT = (SeekBar) findViewById(R.id.angletol);
        DR = (SeekBar) findViewById(R.id.distratio);
        PT = (SeekBar) findViewById(R.id.polytol);
        SR = (SeekBar) findViewById(R.id.sizeratio);
        RT = (SeekBar) findViewById(R.id.ratiotol);

        if(!devMode) {
            Log.i(TAG, "DevMode Active.");
            DataV.setVisibility(View.INVISIBLE);}

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.methods, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        MethodSel.setAdapter(adapter);

        // Set Progress
        Log.v(TAG, "Setting SeekBar Progress...");
        AT.setProgress(C.getValue(Capture.Param.ANGLETOL));
        DR.setProgress(C.getValue(Capture.Param.DISTRATIO));
        PT.setProgress(C.getValue(Capture.Param.POLYTOL));
        SR.setProgress(C.getValue(Capture.Param.SIZERATIO));
        RT.setProgress(C.getValue(Capture.Param.RATIOTOL));

        // Set Listeners
        Log.v(TAG, "Setting Listeners...");
        AT.setOnSeekBarChangeListener(this); DR.setOnSeekBarChangeListener(this);
        PT.setOnSeekBarChangeListener(this); SR.setOnSeekBarChangeListener(this);
        RT.setOnSeekBarChangeListener(this);
        SaveButton.setOnClickListener(this); SaveButton.setOnLongClickListener(this);
        MethodSel.setOnItemSelectedListener(this);

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

    // ----------------------- Camera --------------------
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

    public void saveImage(Bitmap img) {
        if(!Preview.empty()) {
            Log.i(TAG, "Saving Image...");
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            PreviewBMP.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            MediaStore.Images.Media.insertImage(getContentResolver(), img, null, null);
        }
    }

    // ----------------------- Buttons --------------------
    @Override
    public void onClick(View v) {
        Log.i(TAG, "Button Click: " + v.getId());
        switch(v.getId()) {
            case R.id.save_button: saveImage(PreviewBMP); break;
        }
    }

    @Override
    public boolean onLongClick(View v) {
        Log.i(TAG, "Long Button Click: " + v.getId());
        switch(v.getId()) {
            case R.id.save_button: saveImage(FrameBMP); return true;
            default: return false;
        }
    }

    // ----------------------- SeekBar --------------------
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        Log.i(TAG, seekBar.getId() + " Stop Tracking Touch: " + seekBar.getProgress());
        switch(seekBar.getId()) {
            case R.id.angletol: C.setValue(Capture.Param.ANGLETOL,seekProgress);break;
            case R.id.distratio: C.setValue(Capture.Param.DISTRATIO,seekProgress);break;
            case R.id.polytol: C.setValue(Capture.Param.POLYTOL,seekProgress);break;
            case R.id.sizeratio: C.setValue(Capture.Param.SIZERATIO,seekProgress);break;
            case R.id.ratiotol: C.setValue(Capture.Param.RATIOTOL,seekProgress);break;
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progressValue, boolean fromUser) {
        Log.i(TAG, seekBar.getId() + " Progress: " + progressValue + "/" + seekBar.getMax());
        seekProgress = progressValue;
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        Log.i(TAG, seekBar.getId() + " Start Tracking Touch.");
    }

    // ----------------------- Spinner --------------------
    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        C.selectMethod(Capture.Method.getMethod(pos));
    }

    public void onNothingSelected(AdapterView<?> parent) {
        C.selectMethod(null);
    }
}


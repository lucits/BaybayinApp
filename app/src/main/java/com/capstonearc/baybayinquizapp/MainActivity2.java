package com.capstonearc.baybayinquizapp;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Surface;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;

import com.capstonearc.baybayinquizapp.detector.TFLiteDetector;
import com.capstonearc.baybayinquizapp.analysis.FullImageAnalyse;
import com.capstonearc.baybayinquizapp.analysis.FullScreenAnalyse;
import com.capstonearc.baybayinquizapp.utils.CameraProcess;
import com.google.common.util.concurrent.ListenableFuture;

public class MainActivity2 extends AppCompatActivity {

    private boolean IS_FULL_SCREEN = true;

    private PreviewView cameraPreviewMatch;
    private PreviewView cameraPreviewWrap;
    private ImageView boxLabelCanvas;
    private Spinner modelSpinner;
    private Switch immersive;
    private TextView inferenceTimeTextView;
    private TextView frameSizeTextView;
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private TFLiteDetector TFLiteDetector;

    private CameraProcess cameraProcess = new CameraProcess();


    protected int getScreenOrientation() {
        switch (getWindowManager().getDefaultDisplay().getRotation()) {
            case Surface.ROTATION_270:
                return 270;
            case Surface.ROTATION_180:
                return 180;
            case Surface.ROTATION_90:
                return 90;
            default:
                return 0;
        }
    }

    private void initModel(String modelName) {
        // 加载模型
        try {
            this.TFLiteDetector = new TFLiteDetector();
            this.TFLiteDetector.setModelFile(modelName);
            this.TFLiteDetector.addGPUDelegate();
            this.TFLiteDetector.initialModel(this);
            Log.i("model", "Success loading model" + this.TFLiteDetector.getModelFile());
        } catch (Exception e) {
            Log.e("image", "load model error: " + e.getMessage() + e.toString());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        // Hide the top status bar when opening the app
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        getWindow().setStatusBarColor(Color.TRANSPARENT);

        //full screen
        cameraPreviewMatch = findViewById(R.id.camera_preview_match);
        cameraPreviewMatch.setScaleType(PreviewView.ScaleType.FILL_START);

        cameraPreviewWrap = findViewById(R.id.camera_preview_wrap);

        // box/label
        boxLabelCanvas = findViewById(R.id.box_label_canvas);

        modelSpinner = findViewById(R.id.model);

        immersive = findViewById(R.id.immersive);

        inferenceTimeTextView = findViewById(R.id.inference_time);
        frameSizeTextView = findViewById(R.id.frame_size);
        cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        if (!cameraProcess.allPermissionsGranted(this)) {
            cameraProcess.requestPermissions(this);
        }

        int rotation = getWindowManager().getDefaultDisplay().getRotation();
        Log.i("image", "rotation: " + rotation);

        cameraProcess.showCameraSupportSize(MainActivity2.this);

        initModel("yolov5s");

        modelSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String model = (String) adapterView.getItemAtPosition(i);
                Toast.makeText(MainActivity2.this, "loading model: " + model, Toast.LENGTH_LONG).show();
                initModel(model);
                if(IS_FULL_SCREEN){
                    cameraPreviewWrap.removeAllViews();
                    FullScreenAnalyse fullScreenAnalyse = new FullScreenAnalyse(MainActivity2.this,
                            cameraPreviewMatch,
                            boxLabelCanvas,
                            rotation,
                            inferenceTimeTextView,
                            frameSizeTextView,
                            TFLiteDetector);
                    cameraProcess.startCamera(MainActivity2.this, fullScreenAnalyse, cameraPreviewMatch);
                }else{
                    cameraPreviewMatch.removeAllViews();
                    FullImageAnalyse fullImageAnalyse = new FullImageAnalyse(
                            MainActivity2.this,
                            cameraPreviewWrap,
                            boxLabelCanvas,
                            rotation,
                            inferenceTimeTextView,
                            frameSizeTextView,
                            TFLiteDetector);
                    cameraProcess.startCamera(MainActivity2.this, fullImageAnalyse, cameraPreviewWrap);
                }


            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        immersive.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                IS_FULL_SCREEN = b;
                if (b) {
                    cameraPreviewWrap.removeAllViews();
                    FullScreenAnalyse fullScreenAnalyse = new FullScreenAnalyse(MainActivity2.this,
                            cameraPreviewMatch,
                            boxLabelCanvas,
                            rotation,
                            inferenceTimeTextView,
                            frameSizeTextView,
                            TFLiteDetector);
                    cameraProcess.startCamera(MainActivity2.this, fullScreenAnalyse, cameraPreviewMatch);

                } else {
                    cameraPreviewMatch.removeAllViews();
                    FullImageAnalyse fullImageAnalyse = new FullImageAnalyse(
                            MainActivity2.this,
                            cameraPreviewWrap,
                            boxLabelCanvas,
                            rotation,
                            inferenceTimeTextView,
                            frameSizeTextView,
                            TFLiteDetector);
                    cameraProcess.startCamera(MainActivity2.this, fullImageAnalyse, cameraPreviewWrap);
                }
            }
        });


    }
}
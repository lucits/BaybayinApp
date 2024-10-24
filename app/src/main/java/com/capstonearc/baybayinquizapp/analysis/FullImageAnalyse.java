package com.capstonearc.baybayinquizapp.analysis;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.view.PreviewView;

import com.capstonearc.baybayinquizapp.R;
import com.capstonearc.baybayinquizapp.detector.TFLiteDetector;
import com.capstonearc.baybayinquizapp.utils.CameraProcess;
import com.capstonearc.baybayinquizapp.utils.ImageProcess;
import com.capstonearc.baybayinquizapp.utils.Recognition;

import java.util.ArrayList;
import java.util.Locale;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class FullImageAnalyse implements ImageAnalysis.Analyzer {

    public static class Result {

        public Result(long costTime, Bitmap bitmap) {
            this.costTime = costTime;
            this.bitmap = bitmap;
        }

        long costTime;
        Bitmap bitmap;
    }

    TextureView boxLabelCanvas;
    PreviewView previewView;
    int rotation;
    private TextView inferenceTimeTextView;
    private TextView frameSizeTextView;
    ImageProcess imageProcess;
    private TFLiteDetector TFLiteDetector;
    private TextToSpeech textToSpeech;

    private boolean isPaused = false;
    private Bitmap frozenBitmap;
    private String frozenTtsText;
    private CameraProcess cameraProcess;
    private ArrayList<Recognition> recognitions;
    private ImageView speakerButton;
    private Button freezeResumeButton;

    public FullImageAnalyse(Context context,
                            PreviewView previewView,
                            TextureView boxLabelCanvas,
                            int rotation,
                            TextView inferenceTimeTextView,
                            TextView frameSizeTextView,
                            TFLiteDetector TFLiteDetector, TextToSpeech textToSpeech) {
        this.previewView = previewView;
        this.boxLabelCanvas = boxLabelCanvas;
        this.rotation = rotation;
        this.inferenceTimeTextView = inferenceTimeTextView;
        this.frameSizeTextView = frameSizeTextView;
        this.imageProcess = new ImageProcess();
        this.TFLiteDetector = TFLiteDetector;

        cameraProcess = new CameraProcess();

        // Initialize TTS
        this.textToSpeech = new TextToSpeech(context, status -> {
            if (status != TextToSpeech.ERROR) {
                // Set language to Filipino
                Locale filipino = new Locale("fil", "PH");
                this.textToSpeech.setLanguage(filipino);
            }
        });

        freezeResumeButton = (Button) ((Activity) context).findViewById(R.id.freeze_button);

        freezeResumeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (freezeResumeButton.getText().toString().equals("Freeze")) {
                    freeze();
                    freezeResumeButton.setText("Resume");
                } else {
                    resume();
                    freezeResumeButton.setText("Freeze");
                }
            }
        });
        speakerButton = (ImageView) ((Activity) context).findViewById(R.id.speaker_button);
        speakerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textToSpeech.speak(frozenTtsText, TextToSpeech.QUEUE_FLUSH, null, null);
            }
        });

        boxLabelCanvas.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                // Draw on the texture view
                Canvas canvas = boxLabelCanvas.lockCanvas();
                if (canvas != null) {
                    canvas.drawColor(Color.TRANSPARENT, android.graphics.PorterDuff.Mode.CLEAR);
                    Paint paint = new Paint();
                    paint.setColor(Color.RED);
                    canvas.drawRect(100, 100, 200, 200, paint);
                    boxLabelCanvas.unlockCanvasAndPost(canvas);
                }
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                return false;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surface) {

            }
        });
    }

    @Override
    public void analyze(@NonNull ImageProxy image) {
        if (isPaused) {
            return;
        }

        int previewHeight = previewView.getHeight();
        int previewWidth = previewView.getWidth();

        Observable.create((ObservableEmitter<Result> emitter) -> {
                    long start = System.currentTimeMillis();

                    byte[][] yuvBytes = new byte[3][];
                    ImageProxy.PlaneProxy[] planes = image.getPlanes();
                    int imageHeight = image.getHeight();
                    int imagewWidth = image.getWidth();

                    imageProcess.fillBytes (planes, yuvBytes);
                    int yRowStride = planes[0].getRowStride();
                    final int uvRowStride = planes[1].getRowStride();
                    final int uvPixelStride = planes[1].getPixelStride();

                    int[] rgbBytes = new int[imageHeight * imagewWidth];
                    imageProcess.YUV420ToARGB8888(
                            yuvBytes[0],
                            yuvBytes[1],
                            yuvBytes[2],
                            imagewWidth,
                            imageHeight,
                            yRowStride,
                            uvRowStride,
                            uvPixelStride,
                            rgbBytes);

                    Bitmap imageBitmap = Bitmap.createBitmap(imagewWidth, imageHeight, Bitmap.Config.ARGB_8888);
                    imageBitmap.setPixels(rgbBytes, 0, imagewWidth, 0, 0, imagewWidth, imageHeight);

                    double scale = Math.max(
                            previewHeight / (double) (rotation % 180 == 0 ? imagewWidth : imageHeight),
                            previewWidth / (double) (rotation % 180 == 0 ? imageHeight : imagewWidth)
                    );
                    Matrix fullScreenTransform = imageProcess.getTransformationMatrix(
                            imagewWidth, imageHeight,
                            (int) (scale * imageHeight), (int) (scale * imagewWidth),
                            rotation % 180 == 0 ? 90 : 0, false
                    );

                    Bitmap fullImageBitmap = Bitmap.createBitmap(imageBitmap, 0, 0, imagewWidth, imageHeight, fullScreenTransform, false);
                    Bitmap cropImageBitmap = Bitmap.createBitmap(fullImageBitmap, 0, 0, previewWidth, previewHeight);

                    Matrix previewToModelTransform =
                            imageProcess.getTransformationMatrix(
                                    cropImageBitmap.getWidth(), cropImageBitmap.getHeight(),
                                    TFLiteDetector.getInputSize().getWidth(),
                                    TFLiteDetector.getInputSize().getHeight(),
                                    0, false);
                    Bitmap modelInputBitmap = Bitmap.createBitmap(cropImageBitmap, 0, 0,
                            cropImageBitmap.getWidth(), cropImageBitmap.getHeight(),
                            previewToModelTransform, false);

                    Matrix modelToPreviewTransform = new Matrix();
                    previewToModelTransform.invert(modelToPreviewTransform);

                    ArrayList<Recognition> recognitions = TFLiteDetector.detect(modelInputBitmap);
                    this.recognitions = recognitions;

                    Canvas canvas = boxLabelCanvas.lockCanvas();
                    if (canvas != null) {
                        Log.d("ANALYZE", "Drawing on canvas");
                        canvas.drawColor(Color.TRANSPARENT, android.graphics.PorterDuff.Mode.CLEAR);
                        Paint boxPaint = new Paint();
                        boxPaint.setStrokeWidth(5);
                        boxPaint.setStyle(Paint.Style.STROKE);
                        boxPaint.setColor(Color.RED);
                        Paint textPain = new Paint();
                        textPain.setTextSize(50);
                        textPain.setColor(Color.WHITE);
                        textPain.setStyle(Paint.Style.FILL);

                        StringBuilder ttsText = new StringBuilder();

                        for (Recognition res : recognitions) {
                            RectF location = res.getLocation();
                            String label = res.getLabelName();
                            float confidence = res.getConfidence();
                            modelToPreviewTransform.mapRect(location);
                            Log.d("ANALYZE", "Drawing box at " + location.left + ", " + location.top);
                            canvas.drawRect(location, boxPaint);
                            canvas.drawText(label + ":" + String.format("%.2f", confidence), location.left, location.top, textPain);
                            ttsText.append(label);
                        }
                        boxLabelCanvas.unlockCanvasAndPost(canvas);
                    } else {
                        Log.e("ANALYZE", "Canvas is null");
                    }
                    long end = System.currentTimeMillis();
                    long costTime = (end - start);
                    image.close();
                    //emitter.onNext(new Result(costTime, emptyCropSizeBitmap));


                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((Result result) -> {
                    //boxLabelCanvas.setImageBitmap(result.bitmap);
                    frameSizeTextView.setText(previewHeight + "x" + previewWidth);
                    inferenceTimeTextView.setText(Long.toString(result.costTime) + "ms");
                });
    }

    public void freeze() {
        isPaused = true;
        previewView.setAlpha(0.5f);
        previewView.setEnabled(false);
        CameraProcess cameraProcess = new CameraProcess();
        cameraProcess.stopCamera(previewView.getContext());
        StringBuilder ttsText = new StringBuilder();
        for (Recognition res : recognitions) {
            ttsText.append(res.getLabelName());
        }
        frozenTtsText = ttsText.toString();
        Log.d("TTS", "Speaking: " + frozenTtsText);
        textToSpeech.speak(frozenTtsText, TextToSpeech.QUEUE_FLUSH, null, null);
        speakerButton.setVisibility(View.VISIBLE); // Add this line to make the speaker button visible
    }

    public void resume() {
        isPaused = false;
        previewView.setAlpha(1.0f);
        previewView.setEnabled(true);
        cameraProcess.startCamera(previewView.getContext(), this, previewView);
        speakerButton.setVisibility(View.GONE); // Add this line to make the speaker button invisible
    }
}
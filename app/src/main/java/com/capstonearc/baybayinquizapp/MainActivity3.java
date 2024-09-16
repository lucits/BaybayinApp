package com.capstonearc.baybayinquizapp;

import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.util.Log;

import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.io.FileInputStream;
import java.util.PriorityQueue;
import java.util.Comparator;

public class MainActivity3 extends AppCompatActivity {

    TextView result, confidence;
    ImageView imageView;
    Button picture;
    int imageSize = 224;
    Interpreter tflite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        result = findViewById(R.id.result);
        confidence = findViewById(R.id.confidence);
        imageView = findViewById(R.id.imageView);
        picture = findViewById(R.id.button);

        try {
            tflite = new Interpreter(loadModelFile());
            Log.d("DEBUG", "Model loaded successfully.");
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("DEBUG", "Error loading model: " + e.getMessage());
        }

        picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, 1);
                } else {
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, 100);
                }
            }
        });
    }

    private MappedByteBuffer loadModelFile() throws IOException {
        AssetFileDescriptor fileDescriptor = this.getAssets().openFd("full_model.tflite");
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    public void classifyImage(Bitmap image) {
        if (tflite == null) {
            Log.e("DEBUG", "TensorFlow Lite Interpreter is not initialized.");
            return;
        }

        try {
            Log.d("DEBUG", "Classifying image...");
            TensorBuffer inputBuffer = TensorBuffer.createFixedSize(new int[]{1, 224, 224, 3}, org.tensorflow.lite.DataType.FLOAT32);
            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4 * imageSize * imageSize * 3);
            byteBuffer.order(ByteOrder.nativeOrder());

            int[] intValues = new int[imageSize * imageSize];
            image.getPixels(intValues, 0, image.getWidth(), 0, 0, image.getWidth(), image.getHeight());

            int pixel = 0;
            for (int i = 0; i < imageSize; i++) {
                for (int j = 0; j < imageSize; j++) {
                    int val = intValues[pixel++];
                    byteBuffer.putFloat(((val >> 16) & 0xFF) * (1.f / 255.f));
                    byteBuffer.putFloat(((val >> 8) & 0xFF) * (1.f / 255.f));
                    byteBuffer.putFloat((val & 0xFF) * (1.f / 255.f));
                }
            }

            inputBuffer.loadBuffer(byteBuffer);

            TensorBuffer outputBuffer = TensorBuffer.createFixedSize(new int[]{1, 59}, org.tensorflow.lite.DataType.FLOAT32); // Adjusted for 59 classes
            tflite.run(inputBuffer.getBuffer(), outputBuffer.getBuffer());

            float[] confidences = outputBuffer.getFloatArray();
            String[] classes = {"A", "O-U", "E-I", "HA", "HE-HI", "HO-HU", "H", "PA", "PE-PI", "PO-PU", "P", "KA", "KE-KI", "KO-KU", "K", "SA", "SE-SI", "SO-SU", "S",
                    "LA", "LE-LI", "LO-LU", "L", "TA", "TE-TI","TO-TU", "T", "NA", "NE-NI", "NO-NU", "N", "BA", "BE-BI", "BO-BU", "B", "MA", "ME-MI", "MO-MU", "M",
                    "GA", "GE-GI", "GO-GU", "G", "DA-RA", "DE-RE DI-RI","DO-RO DU-RU", "D-R", "YA", "YE-YI", "YO-YU", "Y", "NGA", "NGE-NGI", "NGO-NGU", "NG", "WA", "WE-WI", "WO-WU", "W",};

            PriorityQueue<Integer> pq = new PriorityQueue<>(Comparator.comparingDouble(i -> -confidences[i]));

            for (int i = 0; i < confidences.length; i++) {
                pq.offer(i);
            }

            StringBuilder resultText = new StringBuilder();
            StringBuilder confidenceText = new StringBuilder();

            if (!pq.isEmpty()) {
                int topIndex = pq.poll(); // The top class
                resultText.append(classes[topIndex]);
                confidenceText.append(String.format("%s: %.1f%%\n", classes[topIndex], confidences[topIndex] * 100));

                for (int i = 1; i < 4 && !pq.isEmpty(); i++) {
                    int index = pq.poll();
                    confidenceText.append(String.format("%s: %.1f%%\n", classes[index], confidences[index] * 100));
                }
            }

            result.setText(resultText.toString().trim());
            confidence.setText(confidenceText.toString().trim());
            Log.d("DEBUG", "Classification Result: " + resultText.toString().trim());
            Log.d("DEBUG", "Confidence: " + confidenceText.toString().trim());

        } catch (Exception e) {
            Log.e("DEBUG", "Error in classification: " + e.getMessage());
            e.printStackTrace();
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            Bitmap image = (Bitmap) data.getExtras().get("data");
            int dimension = Math.min(image.getWidth(), image.getHeight());
            image = ThumbnailUtils.extractThumbnail(image, dimension, dimension);
            imageView.setImageBitmap(image);

            image = Bitmap.createScaledBitmap(image, imageSize, imageSize, false);
            classifyImage(image);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}

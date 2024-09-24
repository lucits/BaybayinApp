package com.capstonearc.baybayinquizapp;

import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
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
import java.util.HashMap;
import java.util.Locale;
import java.util.PriorityQueue;
import java.util.Comparator;

public class MainActivity3 extends AppCompatActivity {

    TextView result, confidence;
    ImageView imageView;
    Button picture;
    int imageSize = 224;
    Interpreter tflite;

    // Add a TextToSpeech object
    private TextToSpeech textToSpeech;

    // Map for Baybayin pronunciation
    private HashMap<String, String> pronunciationMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        result = findViewById(R.id.result);
        confidence = findViewById(R.id.confidence);
        imageView = findViewById(R.id.imageView);
        picture = findViewById(R.id.button);

        // Initialize the TextToSpeech engine
        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(new Locale("en", "US")); // Set to Tagalog or default to locale
                }
            }
        });

        // Initialize pronunciation map
        initializePronunciationMap();

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

    // Initialize the Baybayin pronunciation map
    private void initializePronunciationMap() {
        pronunciationMap = new HashMap<>();

        // Vowels
        pronunciationMap.put("A", "ah");
        pronunciationMap.put("O-U", "oh - ooh");
        pronunciationMap.put("E-I", "eh - ee");

        // HA Group
        pronunciationMap.put("HA", "ha");
        pronunciationMap.put("HE-HI", "he - hi");
        pronunciationMap.put("HO-HU", "ho - hu");

        // PA Group
        pronunciationMap.put("PA", "pa");
        pronunciationMap.put("PE-PI", "pe - pi");
        pronunciationMap.put("PO-PU", "po - pu");

        // KA Group
        pronunciationMap.put("KA", "ka");
        pronunciationMap.put("KE-KI", "ke - ki");
        pronunciationMap.put("KO-KU", "ko - ku");

        // SA Group
        pronunciationMap.put("SA", "sa");
        pronunciationMap.put("SE-SI", "se - si");
        pronunciationMap.put("SO-SU", "so - su");

        // LA Group
        pronunciationMap.put("LA", "la");
        pronunciationMap.put("LE-LI", "le - li");
        pronunciationMap.put("LO-LU", "lo - lu");

        // TA Group
        pronunciationMap.put("TA", "ta");
        pronunciationMap.put("TE-TI", "te - ti");
        pronunciationMap.put("TO-TU", "to - tu");

        // NA Group
        pronunciationMap.put("NA", "na");
        pronunciationMap.put("NE-NI", "ne - ni");
        pronunciationMap.put("NO-NU", "no - nu");

        // BA Group
        pronunciationMap.put("BA", "ba");
        pronunciationMap.put("BE-BI", "be - bi");
        pronunciationMap.put("BO-BU", "bo - bu");

        // MA Group
        pronunciationMap.put("MA", "ma");
        pronunciationMap.put("ME-MI", "me - mi");
        pronunciationMap.put("MO-MU", "mo - mu");

        // GA Group
        pronunciationMap.put("GA", "ga");
        pronunciationMap.put("GE-GI", "ge - gi");
        pronunciationMap.put("GO-GU", "go - gu");

        // DA-RA Group
        pronunciationMap.put("DA-RA", "da - ra");
        pronunciationMap.put("DE-RE DI-RI", "de - re, di - ri");
        pronunciationMap.put("DO-RO DU-RU", "do - ro, du - ru");

        // YA Group
        pronunciationMap.put("YA", "ya");
        pronunciationMap.put("YE-YI", "ye - yi");
        pronunciationMap.put("YO-YU", "yo - yu");

        // NGA Group
        pronunciationMap.put("NGA", "nga");
        pronunciationMap.put("NGE-NGI", "nge - ngi");
        pronunciationMap.put("NGO-NGU", "ngo - ngu");

        // WA Group
        pronunciationMap.put("WA", "wa");
        pronunciationMap.put("WE-WI", "we - wi");
        pronunciationMap.put("WO-WU", "wo - wu");

        // Standalone Consonants (optional, depending on your use case)
        pronunciationMap.put("H", "h");
        pronunciationMap.put("P", "p");
        pronunciationMap.put("K", "k");
        pronunciationMap.put("S", "s");
        pronunciationMap.put("L", "l");
        pronunciationMap.put("T", "t");
        pronunciationMap.put("N", "n");
        pronunciationMap.put("B", "b");
        pronunciationMap.put("M", "m");
        pronunciationMap.put("G", "g");
        pronunciationMap.put("D-R", "d or r");
        pronunciationMap.put("Y", "y");
        pronunciationMap.put("NG", "ng");
        pronunciationMap.put("W", "w");
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

            textToSpeech.setSpeechRate(0.7f);  // Slower rate (default is 1.0f)
            // Text-to-Speech: Get the correct pronunciation using the map
            String classifiedText = resultText.toString().trim();
            String pronunciation = pronunciationMap.getOrDefault(classifiedText, classifiedText); // Fallback to the raw text if not found in the map
            textToSpeech.speak(pronunciation, TextToSpeech.QUEUE_FLUSH, null, null);  // Speak the pronunciation



        } catch (Exception e) {
            Log.e("DEBUG", "Error in classification: " + e.getMessage());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            Bitmap image = (Bitmap) data.getExtras().get("data");
            int dimension = Math.min(image.getWidth(), image.getHeight());
            image = ThumbnailUtils.extractThumbnail(image, dimension, dimension);
            imageView.setImageBitmap(image);

            Bitmap resized = Bitmap.createScaledBitmap(image, imageSize, imageSize, false);
            classifyImage(resized);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}

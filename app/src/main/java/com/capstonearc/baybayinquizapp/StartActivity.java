package com.capstonearc.baybayinquizapp;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.media.MediaPlayer;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.capstonearc.baybayinquizapp.Activities.MainDashboard;

public class StartActivity extends AppCompatActivity {


    private boolean isMusicPlaying = true;
    private MediaPlayer backgroundMusic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_start);

        // Initialize background music
        backgroundMusic = MediaPlayer.create(this, R.raw.bgmusic);
        backgroundMusic.setLooping(true); // Loop the music

        final AppCompatButton easyBtn = findViewById(R.id.easyBtn);
        final AppCompatButton mediumBtn = findViewById(R.id.mediumBtn);
        final AppCompatButton hardBtn = findViewById(R.id.hardBtn);
        final AppCompatButton battleBtn = findViewById(R.id.battleBtn);

        easyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInstructions("easy");
            }
        });

        mediumBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInstructions("medium");
            }
        });

        hardBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInstructions("hard");
            }
        });
        battleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInstructions("battles");
            }
        });

        ConstraintLayout backProfileBtn = findViewById(R.id.backGameBtn);
        backProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StartActivity.this, MainDashboard.class);
                startActivity(intent);
                finish();
            }
        });





        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


    }


    private void showInstructions(final String level) {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.instructions_dialog);
        dialog.setTitle("Instructions");

        TextView instructionsText = dialog.findViewById(R.id.instructionsText);
        AppCompatButton startBtn = dialog.findViewById(R.id.startBtn);
        final ImageView muteButton = dialog.findViewById(R.id.muteBtn);


        instructionsText.setText("Basahin mabuti ang bawat tanong, piliin ang tamang sagot mula sa apat na opsyon, at tapusin ang pagsusulit bago matapos ang oras.");

        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                startQuiz(level, isMusicPlaying);
            }
        });

        muteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isMusicPlaying) {
                    muteButton.setImageResource(R.drawable.volume_down);
                    stopMusic();
                } else {
                    muteButton.setImageResource(R.drawable.volume_up);
                    startMusic();
                }
                isMusicPlaying = !isMusicPlaying;
            }
        });
        dialog.show();
    }

    private void startQuiz(String difficultyLevel, boolean isMusicPlaying) {

        Intent intent = new Intent(StartActivity.this, MainActivity.class);
        intent.putExtra("difficulty_level", difficultyLevel);
        intent.putExtra("is_music_playing", this.isMusicPlaying); // Pass the mute state
        startActivity(intent);
    }
    private void startMusic() {
        if (backgroundMusic != null) {
            backgroundMusic.start();
        }
    }

    private void stopMusic() {
        if (backgroundMusic != null) {
            backgroundMusic.pause();
        }
    }
    protected void onDestroy() {
        super.onDestroy();
        if (backgroundMusic != null) {
            backgroundMusic.stop();
            backgroundMusic.release();
            backgroundMusic = null;
        }
    }


}

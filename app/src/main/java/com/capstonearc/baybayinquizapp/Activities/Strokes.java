package com.capstonearc.baybayinquizapp.Activities;

import android.app.Dialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.capstonearc.baybayinquizapp.R;

public class Strokes extends AppCompatActivity {

    private Dialog myDialog;
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_strokes);
        myDialog = new Dialog(this);

        ConstraintLayout backStrokesBtn = findViewById(R.id.backStrokesBtn);
        backStrokesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Strokes.this, MainDashboard.class);
                startActivity(intent);
                Strokes.this.finish();
            }
        });

        //popup
        findViewById(R.id.s1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopup(v, Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.a));
            }
        });
        findViewById(R.id.s2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopup(v, Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.ou));
            }
        });
        findViewById(R.id.s3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopup(v, Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.ei));
            }
        });
        findViewById(R.id.k1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopup(v, Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.ha));
            }
        });
        findViewById(R.id.k2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopup(v, Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.pa));
            }
        });
        findViewById(R.id.k3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopup(v, Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.ka));
            }
        });
        findViewById(R.id.k4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopup(v, Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.sa));
            }
        });
        findViewById(R.id.k5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopup(v, Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.la));
            }
        });
        findViewById(R.id.k6).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopup(v, Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.ta));
            }
        });
        findViewById(R.id.k7).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopup(v, Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.na));
            }
        });
        findViewById(R.id.k8).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopup(v, Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.ba));
            }
        });
        findViewById(R.id.k9).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopup(v, Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.ma));
            }
        });
        findViewById(R.id.k10).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopup(v, Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.ga));
            }
        });
        findViewById(R.id.k11).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopup(v, Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.dara));
            }
        });
        findViewById(R.id.k12).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopup(v, Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.ya));
            }
        });
        findViewById(R.id.k13).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopup(v, Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.nga));
            }
        });
        findViewById(R.id.k14).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopup(v, Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.wa));
            }
        });




        //sound
        findViewById(R.id.s1s).setOnClickListener(v -> playSound(R.raw.a_sound));
        findViewById(R.id.s2s).setOnClickListener(v -> playSound(R.raw.ou_sound));
        findViewById(R.id.s3s).setOnClickListener(v -> playSound(R.raw.ei_sound));

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void showPopup(View v, Uri videoUri) {
        VideoDialogFragment dialogFragment = VideoDialogFragment.newInstance(videoUri);
        dialogFragment.show(getSupportFragmentManager(), "VideoDialogFragment");
    }
    private void playSound(int soundResId) {
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
        mediaPlayer = MediaPlayer.create(this, soundResId);
        mediaPlayer.start();
        mediaPlayer.setOnCompletionListener(MediaPlayer::release);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

}
package com.capstonearc.baybayinquizapp.Activities;

import android.app.Dialog;
import android.content.Intent;
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
            }
        });
        findViewById(R.id.s1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopup(v, Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.a_stroke));
            }
        });

        findViewById(R.id.s2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopup(v, Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.samplevideo));
            }
        });



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
}
package com.capstonearc.baybayinquizapp;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.capstonearc.baybayinquizapp.Activities.MainDashboard;

public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_start);

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
                showInstructions("battle");
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

        instructionsText.setText("Here are the instructions for the " + level + " level. Please read them carefully before proceeding.");

        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                startQuiz(level);
            }
        });

        dialog.show();
    }

    private void startQuiz(String difficultyLevel) {
        Intent intent = new Intent(StartActivity.this, MainActivity.class);
        intent.putExtra("difficulty_level", difficultyLevel);
        startActivity(intent);
    }

}

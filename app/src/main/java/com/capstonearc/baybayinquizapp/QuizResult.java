package com.capstonearc.baybayinquizapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import java.util.List;

public class QuizResult extends AppCompatActivity {

    private List<QuestionsList> questionsLists;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_result);

        // Retrieve the difficulty level extra
        String difficultyLevel = getIntent().getStringExtra("difficulty_level");
        Log.d("QuizResult", "Activity created");

        final TextView scoreTV = findViewById(R.id.scoreTV);
        final TextView totalScoreTV = findViewById(R.id.totalScoreTV);
        final TextView correctTV = findViewById(R.id.correctTV);
        final TextView incorrectTV = findViewById(R.id.inCorrectTV);
        final AppCompatButton backActivityBtn = findViewById(R.id.backActivityBtn);
        final AppCompatButton reTakeBtn = findViewById(R.id.retakeQuizBtn);

        // Getting question list from MainActivity
        if (getIntent().hasExtra("questions")) {
            questionsLists = (List<QuestionsList>) getIntent().getSerializableExtra("questions");

            if (!questionsLists.isEmpty()) {
                totalScoreTV.setText("/" + questionsLists.size());
                int correctAnswers = getCorrectAnswers();
                scoreTV.setText(String.valueOf(correctAnswers));
                correctTV.setText(String.valueOf(correctAnswers));
                incorrectTV.setText(String.valueOf(questionsLists.size() - correctAnswers));
            } else {
                // Handle the case where the list of questions is empty
                Toast.makeText(this, "No questions available", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
        } else {
            // Handle the case where the intent does not contain the list of questions
            Toast.makeText(this, "No questions data found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        backActivityBtn.setOnClickListener(v -> {
            // Go back to the start activity
            Intent intent = new Intent(QuizResult.this, StartActivity.class);
            startActivity(intent);
            finish(); // Optional: Finish the current activity if you don't want to keep it in the stack
        });

        reTakeBtn.setOnClickListener(v -> {
            // Restart the quiz by creating a new intent for the MainActivity
            Intent intent = new Intent(QuizResult.this, MainActivity.class);
            // Pass the difficulty level as an extra
            intent.putExtra("difficulty_level", getIntent().getStringExtra("difficulty_level"));
            startActivity(intent);
            // Finish the current activity (QuizResult)
            finish();
        });
    }

    private int getCorrectAnswers() {
        int correctAnswerCount = 0;

        for (QuestionsList question : questionsLists) {
            int userSelectedOption = question.getUserSelectedAnswer();
            int questionAnswer = question.getAnswer();

            if (questionAnswer == userSelectedOption) {
                correctAnswerCount++;
            }
        }
        return correctAnswerCount;
    }
}

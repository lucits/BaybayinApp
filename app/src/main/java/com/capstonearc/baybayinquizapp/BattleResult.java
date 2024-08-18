package com.capstonearc.baybayinquizapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class BattleResult extends AppCompatActivity {

    private List<QuestionsList> questionsLists;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_battle_result);

        // Retrieve the difficulty level extra
        String difficultyLevel = getIntent().getStringExtra("difficulty_level");
        Log.d("BattleResult", "Activity created");

        final TextView scoreTV = findViewById(R.id.scoreTV);
        final TextView totalScoreTV = findViewById(R.id.totalScoreTV);
        final AppCompatButton backActivityBtn = findViewById(R.id.backActivityBtn);
        final AppCompatButton leaderboardBtn = findViewById(R.id.leaderboardBtn);

        // Getting question list from MainActivity
        if (getIntent().hasExtra("questions")) {
            questionsLists = (List<QuestionsList>) getIntent().getSerializableExtra("questions");

            if (!questionsLists.isEmpty()) {
                totalScoreTV.setText("/" + questionsLists.size());
                int correctAnswers = getCorrectAnswers();
                scoreTV.setText(String.valueOf(correctAnswers));

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
            Intent intent = new Intent(BattleResult.this, StartActivity.class);
            startActivity(intent);
            finish(); // Optional: Finish the current activity if you don't want to keep it in the stack
        });

        leaderboardBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the LeaderboardActivity when the button is clicked
                Intent intent = new Intent(BattleResult.this, LeaderboardActivity.class);
                startActivity(intent);
            }
        });
        RecyclerView recyclerViewQuizResults = findViewById(R.id.recyclerViewQuizResults);
        recyclerViewQuizResults.setLayoutManager(new LinearLayoutManager(this));

        List<QuestionsList> questionsLists = (List<QuestionsList>) getIntent().getSerializableExtra("questions");
        QuizResultsAdapter adapter = new QuizResultsAdapter(questionsLists);
        recyclerViewQuizResults.setAdapter(adapter);
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

package com.capstonearc.baybayinquizapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private List<QuestionsList> questionsLists = new ArrayList<>();
    private TextView quizTimer;

    private RelativeLayout option1Layout, option2Layout, option3Layout, option4Layout;
    private TextView option1TV, option2TV, option3TV, option4TV;
    private ImageView option1Icon, option2Icon, option3Icon, option4Icon;

    private TextView questionTV;

    private TextView totalQuestionsTV;
    private TextView currentQuestion;

    private DatabaseReference databaseReference;
    private DatabaseReference userReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;

    private CountDownTimer countDownTimer;
    private int currentQuestionPosition = 0;
    private int selectedOption = 0;
    private Dialog showQuitDialog;
    private MediaPlayer backgroundMusic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Retrieve the mute state from the intent extras
        boolean isMusicPlaying = getIntent().getBooleanExtra("is_music_playing", true);

        // Initialize background music
        backgroundMusic = MediaPlayer.create(this, R.raw.bgmusic);
        backgroundMusic.setLooping(true); // Loop the music

        // Start or stop playing background music based on the mute state
        if (isMusicPlaying) {
            startMusic();
        } else {
            stopMusic();
        }

        FirebaseApp.initializeApp(this);
        quizTimer = findViewById(R.id.quizTimer);

        option1Layout = findViewById(R.id.option1Layout);
        option2Layout = findViewById(R.id.option2Layout);
        option3Layout = findViewById(R.id.option3Layout);
        option4Layout = findViewById(R.id.option4Layout);

        option1TV = findViewById(R.id.option1TV);
        option2TV = findViewById(R.id.option2TV);
        option3TV = findViewById(R.id.option3TV);
        option4TV = findViewById(R.id.option4TV);

        option1Icon = findViewById(R.id.option1Icon);
        option2Icon = findViewById(R.id.option2Icon);
        option3Icon = findViewById(R.id.option3Icon);
        option4Icon = findViewById(R.id.option4Icon);

        questionTV = findViewById(R.id.questionTV);
        totalQuestionsTV = findViewById(R.id.totalQuestionsTV);
        currentQuestion = findViewById(R.id.currentQuestionTV);

        databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://baybayinapp-91dbf-default-rtdb.firebaseio.com/");
        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();

        if (currentUser != null) {
            userReference = databaseReference.child("users").child(currentUser.getUid());
        } else {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            finish();
        }


        final AppCompatButton nextBtn = findViewById(R.id.nextQuestionBtn);

        String difficultyLevel = getIntent().getStringExtra("difficulty_level");
        Log.d("MainActivity", "Difficulty Level: " + difficultyLevel);
        if (difficultyLevel != null) {
            DatabaseReference difficultyRef = databaseReference.child(difficultyLevel);
            difficultyRef.addListenerForSingleValueEvent(new ValueEventListener() {

                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String quizTimeValue = snapshot.child("time").getValue(String.class);
                    int getQuizTime = 0;
                    if (quizTimeValue != null) {
                        getQuizTime = Integer.parseInt(quizTimeValue);
                    }
                    Log.d("FirebaseData", "Quiz Time Value: " + quizTimeValue);

                    if (snapshot.hasChild("questions")) {
                        DataSnapshot questionsSnapshot = snapshot.child("questions");
                        for (DataSnapshot questionSnapshot : questionsSnapshot.getChildren()) {
                            String getQuestion = questionSnapshot.child("question").getValue(String.class);
                            String getOption1 = questionSnapshot.child("option1").getValue(String.class);
                            String getOption2 = questionSnapshot.child("option2").getValue(String.class);
                            String getOption3 = questionSnapshot.child("option3").getValue(String.class);
                            String getOption4 = questionSnapshot.child("option4").getValue(String.class);
                            int getAnswer = Integer.parseInt(questionSnapshot.child("answer").getValue(String.class));

                            QuestionsList questionsList = new QuestionsList(getQuestion, getOption1, getOption2, getOption3, getOption4, getAnswer);
                            questionsLists.add(questionsList);
                        }

                        Log.d("FirebaseData", "Total Questions Retrieved: " + questionsLists.size());

                        if (!questionsLists.isEmpty()) {
                            Collections.shuffle(questionsLists);
                            totalQuestionsTV.setText("/" + questionsLists.size());
                            startQuizTimer(getQuizTime);
                            selectQuestion(currentQuestionPosition);
                        } else {
                            Toast.makeText(MainActivity.this, "No questions available", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "No questions available", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    Toast.makeText(MainActivity.this, "Failed to get data from firebase", Toast.LENGTH_SHORT).show();
                }
            });
        }

        option1Layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedOption = 1;
                selectOption(option1Layout, option1Icon);
            }
        });

        option2Layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedOption = 2;
                selectOption(option2Layout, option2Icon);
            }
        });

        option3Layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedOption = 3;
                selectOption(option3Layout, option3Icon);
            }
        });

        option4Layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedOption = 4;
                selectOption(option4Layout, option4Icon);
            }
        });

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedOption != 0) {
                    questionsLists.get(currentQuestionPosition).setUserSelectedAnswer(selectedOption);
                    selectedOption = 0;
                    currentQuestionPosition++;
                    if (currentQuestionPosition < questionsLists.size()) {
                        selectQuestion(currentQuestionPosition);
                    } else {
                        countDownTimer.cancel();
                        finishQuiz();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Please Select an Answer", Toast.LENGTH_SHORT).show();
                }
            }
        });




        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    // Method to start background music
    private void startMusic() {
        if (backgroundMusic != null && !backgroundMusic.isPlaying()) {
            backgroundMusic.start();
        }
    }

    // Method to stop background music
    private void stopMusic() {
        if (backgroundMusic != null && backgroundMusic.isPlaying()) {
            backgroundMusic.pause();
        }
    }

    // Override onDestroy to release MediaPlayer resources
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (backgroundMusic != null) {
            backgroundMusic.stop();
            backgroundMusic.release();
            backgroundMusic = null;
        }
    }
    @Override
    public void onBackPressed() {
        // Cancel the countdown timer
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        // Show the quit dialog
        showQuitDialog();

    }

    private void showQuitDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("ᜁᜑᜒᜈ᜔ᜆᜓ?");//quit quiz?
        builder.setMessage("Sigurado ka bang gusto mong ihinto ang pagsusulit?");
        builder.setPositiveButton("Oo", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Finish the quiz
                finishQuiz();
            }
        });
        builder.setNegativeButton("Hindi", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Do nothing
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }



    private void finishQuiz() {
        String difficultyLevel = getIntent().getStringExtra("difficulty_level");
        Log.d("MainActivity", "Finish Quiz called with difficulty level: " + difficultyLevel);
        if ("battles".equals(difficultyLevel)) {
            // Save the battle mode score to the database
            saveBattleModeScore();
        }

        Intent intent;
        if ("battles".equals(difficultyLevel)) {
            intent = new Intent(MainActivity.this, BattleResult.class);
        } else {
            intent = new Intent(MainActivity.this, QuizResult.class);
        }
        Bundle bundle = new Bundle();
        bundle.putSerializable("questions", (Serializable) questionsLists);
        bundle.putString("difficulty_level", difficultyLevel);
        intent.putExtras(bundle);
        startActivity(intent);
        finish();
    }

    private void saveBattleModeScore() {
        int score = calculateScore();
        userReference.child("battleModeScore").setValue(score);
    }

    private int calculateScore() {
        int correctAnswers = 0;
        for (QuestionsList question : questionsLists) {
            if (question.getUserSelectedAnswer() == question.getAnswer()) {
                correctAnswers++;
            }
        }
        return correctAnswers * 1; // 1 point for each correct answer
    }

    private void startQuizTimer(int maxTimeInSeconds) {
        countDownTimer = new CountDownTimer(maxTimeInSeconds * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long remainingSeconds = millisUntilFinished / 1000;
                long hours = remainingSeconds / 3600;
                long minutes = (remainingSeconds % 3600) / 60;
                long seconds = remainingSeconds % 60;
                String generateTime = String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds);
                quizTimer.setText(generateTime);
            }

            @Override
            public void onFinish() {
                finishQuiz();
            }
        };
        countDownTimer.start();
    }

    private void selectQuestion(int questionListPosition) {
        if (!questionsLists.isEmpty() && questionListPosition < questionsLists.size()) {
            // Reset the selected option state
            selectedOption = 0;

            questionTV.setText(questionsLists.get(questionListPosition).getQuestion());
            option1TV.setText(questionsLists.get(questionListPosition).getOption1());
            option2TV.setText(questionsLists.get(questionListPosition).getOption2());
            option3TV.setText(questionsLists.get(questionListPosition).getOption3());
            option4TV.setText(questionsLists.get(questionListPosition).getOption4());
            currentQuestion.setText("Question " + (questionListPosition + 1));

            // Reset option styles
            resetOptions();

            // Enable options for the new question
            enableOptions();
        } else {
            Toast.makeText(this, "No questions available", Toast.LENGTH_SHORT).show();
        }
    }

    private void selectOption(RelativeLayout selectedOptionLayout, ImageView selectedOptionIcon) {
        if (!questionsLists.isEmpty()) { // Check if questionsLists is not empty
            // Reset previous selection
            resetOptions();

            // Highlight selected option
            selectedOptionIcon.setImageResource(R.drawable.check);
            selectedOptionLayout.setBackgroundResource(R.drawable.round_back_selected_option);

            // Disable options to prevent further selection
            disableOptions();

            // Get the correct answer
            int correctAnswer = questionsLists.get(currentQuestionPosition).getAnswer();

            // Check if the selected option is correct
            if (selectedOption == correctAnswer) {
                // Highlight correct option
                switch (correctAnswer) {
                    case 1:
                        option1Layout.setBackgroundResource(R.drawable.round_back_correct_option);
                        break;
                    case 2:
                        option2Layout.setBackgroundResource(R.drawable.round_back_correct_option);
                        break;
                    case 3:
                        option3Layout.setBackgroundResource(R.drawable.round_back_correct_option);
                        break;
                    case 4:
                        option4Layout.setBackgroundResource(R.drawable.round_back_correct_option);
                        break;
                }
            } else {
                // Highlight correct option
                switch (correctAnswer) {
                    case 1:
                        option1Layout.setBackgroundResource(R.drawable.round_back_correct_option);
                        break;
                    case 2:
                        option2Layout.setBackgroundResource(R.drawable.round_back_correct_option);
                        break;
                    case 3:
                        option3Layout.setBackgroundResource(R.drawable.round_back_correct_option);
                        break;
                    case 4:
                        option4Layout.setBackgroundResource(R.drawable.round_back_correct_option);
                        break;
                }

                // Highlight incorrect option
                switch (selectedOption) {
                    case 1:
                        option1Layout.setBackgroundResource(R.drawable.round_back_wrong_option);
                        break;
                    case 2:
                        option2Layout.setBackgroundResource(R.drawable.round_back_wrong_option);
                        break;
                    case 3:
                        option3Layout.setBackgroundResource(R.drawable.round_back_wrong_option);
                        break;
                    case 4:
                        option4Layout.setBackgroundResource(R.drawable.round_back_wrong_option);
                        break;
                }
            }
        } else {
            Toast.makeText(this, "No questions available", Toast.LENGTH_SHORT).show();
        }
    }


    private void resetOptions() {
        option1Layout.setBackgroundResource(R.drawable.round_back_white_50);
        option2Layout.setBackgroundResource(R.drawable.round_back_white_50);
        option3Layout.setBackgroundResource(R.drawable.round_back_white_50);
        option4Layout.setBackgroundResource(R.drawable.round_back_white_50);

        option1Icon.setImageResource(R.drawable.round_back_white_100);
        option2Icon.setImageResource(R.drawable.round_back_white_100);
        option3Icon.setImageResource(R.drawable.round_back_white_100);
        option4Icon.setImageResource(R.drawable.round_back_white_100);
    }

    private void enableOptions() {
        option1Layout.setEnabled(true);
        option2Layout.setEnabled(true);
        option3Layout.setEnabled(true);
        option4Layout.setEnabled(true);
    }

    private void disableOptions() {
        option1Layout.setEnabled(false);
        option2Layout.setEnabled(false);
        option3Layout.setEnabled(false);
        option4Layout.setEnabled(false);
    }

    private static final String TAG = "MainActivity";
}

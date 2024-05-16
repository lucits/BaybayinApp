package com.capstonearc.baybayinquizapp.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.capstonearc.baybayinquizapp.Adapter.LessonsAdapter;
import com.capstonearc.baybayinquizapp.Domain.LessonsDomain;
import com.capstonearc.baybayinquizapp.LeaderboardActivity;
import com.capstonearc.baybayinquizapp.R;
import com.capstonearc.baybayinquizapp.StartActivity;

import java.util.ArrayList;

public class MainDashboard extends AppCompatActivity {

    private ProgressDialog progressDialog;
    private RecyclerView.Adapter adapterLessons;
    private RecyclerView recyclerViewLessons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main_dashboard);

        LinearLayout historyBtn = findViewById(R.id.historyBtn);
        historyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainDashboard.this, HistoryCon.class);
                startActivity(intent);
            }
        });


        LinearLayout gameBtn = findViewById(R.id.gameBtn);
        gameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show loading animation
                progressDialog = new ProgressDialog(MainDashboard.this);
                progressDialog.setMessage("Loading...");
                progressDialog.setCancelable(false);
                progressDialog.show();

                // Start new activity after 2 seconds
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(MainDashboard.this, StartActivity.class);
                        startActivity(intent);
                        progressDialog.dismiss();
                    }
                }, 2000); // 2000 milliseconds = 2 seconds
            }
        });

        LinearLayout profileBtn = findViewById(R.id.profileBtn);
        profileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainDashboard.this, Profile.class);
                startActivity(intent);
                finish();
            }
        });

        LinearLayout rankBtn = findViewById(R.id.rankBtn);
        rankBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainDashboard.this, LeaderboardActivity.class);
                startActivity(intent);
            }
        });
        LinearLayout chartBtn = findViewById(R.id.chartBtn);
        chartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainDashboard.this, Chart.class);
                startActivity(intent);
            }
        });
        LinearLayout strokesBtn = findViewById(R.id.strokesBtn);
        strokesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainDashboard.this, Strokes.class);
                startActivity(intent);
            }
        });

        initRecyclerView();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void initRecyclerView() {
        ArrayList<LessonsDomain> items = new ArrayList<>();
        items.add(new LessonsDomain(1, "Unang Gabay", "Ang Mga Titik Ng Bayan", "una"));
        items.add(new LessonsDomain(2, "Ikalawang Gabay", "Pagkukudlit", "ikalawa"));
        items.add(new LessonsDomain(3, "Ikatlong Gabay", "Pagbaybay", "ikatlo"));
        items.add(new LessonsDomain(4, "Ikaapat na Gabay", "Pagbabantas", "ikaapat"));

        recyclerViewLessons = findViewById(R.id.view1);
        recyclerViewLessons.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        adapterLessons = new LessonsAdapter(items);
        recyclerViewLessons.setAdapter(adapterLessons);

        ((LessonsAdapter) adapterLessons).setOnItemClickListener(new LessonsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int id) {
                switch (id) {
                    case 1:
                        Intent intent = new Intent(MainDashboard.this, LessonOne.class);
                        startActivity(intent);
                        break;
                    case 2:
                        Intent intent2 = new Intent(MainDashboard.this, LessonTwo.class);
                        startActivity(intent2);
                        break;
                    case 3:
                        Intent intent3 = new Intent(MainDashboard.this, LessonThree.class);
                        startActivity(intent3);
                        break;
                    case 4:
                        Intent intent4 = new Intent(MainDashboard.this, LessonFour.class);
                        startActivity(intent4);
                        break;
                }
            }
        });
    }
}
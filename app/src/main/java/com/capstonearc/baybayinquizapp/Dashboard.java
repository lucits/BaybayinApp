package com.capstonearc.baybayinquizapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;

public class Dashboard extends AppCompatActivity {

    Button logOutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dashboard);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button logoutButton = findViewById(R.id.logoutButton);


    }
    public void logoutUser(View view) {
        FirebaseAuth.getInstance().signOut();
        // Redirect the user to the login screen or perform any other action
        startActivity(new Intent(Dashboard.this, Login.class));
        finish(); // Optional: Close the dashboard activity
    }
    // Method to handle button click and open StartActivity
    public void openStartActivity(View view) {
        Intent intent = new Intent(this, StartActivity.class);
        startActivity(intent);
    }

}
package com.capstonearc.baybayinquizapp.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.capstonearc.baybayinquizapp.ChangePasswordActivity;
import com.capstonearc.baybayinquizapp.Login;
import com.capstonearc.baybayinquizapp.R;
import com.capstonearc.baybayinquizapp.Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class Profile extends AppCompatActivity {

    private TextView userName, userEmail;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private ProgressDialog progressDialog;
    private ImageView userProfilePic;
    private ImageView changeProfilePicBtn;
    private ConstraintLayout settingBtn, changePasswordBtn, privacyPolicyBtn;
    private boolean isDropdownVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);

        userName = findViewById(R.id.userName);
        userEmail = findViewById(R.id.userEmail);
        userProfilePic = findViewById(R.id.userProfilePic);
        changeProfilePicBtn = findViewById(R.id.changeProfilePicBtn);
        settingBtn = findViewById(R.id.settingBtn);
        changePasswordBtn = findViewById(R.id.changePasswordBtn);
        privacyPolicyBtn = findViewById(R.id.privacyPolicyBtn);

        // Initially hide the dropdown buttons
        changePasswordBtn.setVisibility(View.GONE);
        privacyPolicyBtn.setVisibility(View.GONE);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            databaseReference.child(userId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Users userProfile = snapshot.getValue(Users.class);
                    if (userProfile != null) {
                        String username = userProfile.getUsername();
                        String email = userProfile.getEmail();
                        String profilePictureUrl = userProfile.getProfilePicture();

                        userName.setText(username);
                        userEmail.setText(email);

                        if (profilePictureUrl != null) {
                            Picasso.get().load(profilePictureUrl).into(userProfilePic);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(Profile.this, "Failed to load profile", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(Profile.this, "No user logged in", Toast.LENGTH_SHORT).show();
        }

        ConstraintLayout backProfileBtn = findViewById(R.id.backProfileBtn);
        backProfileBtn.setOnClickListener(v -> {
            Intent intent = new Intent(Profile.this, MainDashboard.class);
            startActivity(intent);
            finish();
        });

        ConstraintLayout logoutBtn = findViewById(R.id.logoutBtn);
        logoutBtn.setOnClickListener(v -> {
            progressDialog = new ProgressDialog(Profile.this);
            progressDialog.setMessage("Signing out...");
            progressDialog.setCancelable(false);
            progressDialog.show();

            new Handler().postDelayed(() -> {
                progressDialog.dismiss();
                logoutUser();
            }, 3000);
        });

        changeProfilePicBtn.setOnClickListener(v -> selectProfilePicture());

        settingBtn.setOnClickListener(v -> {
            if (isDropdownVisible) {
                changePasswordBtn.setVisibility(View.GONE);
                privacyPolicyBtn.setVisibility(View.GONE);
            } else {
                changePasswordBtn.setVisibility(View.VISIBLE);
                privacyPolicyBtn.setVisibility(View.VISIBLE);
            }
            isDropdownVisible = !isDropdownVisible;
        });

        changePasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Profile.this, ChangePasswordActivity.class);
                startActivity(intent);
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

    }

    private void selectProfilePicture() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Profile Picture"), 1);
    }

    private void logoutUser() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(Profile.this, Login.class);
        startActivity(intent);
        finish();
    }


}

package com.capstonearc.baybayinquizapp.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
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

import com.capstonearc.baybayinquizapp.Login;
import com.capstonearc.baybayinquizapp.R;
import com.capstonearc.baybayinquizapp.Users;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class Profile extends AppCompatActivity {

    private TextView userName, userEmail;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private ProgressDialog progressDialog;
    private ImageView userProfilePic;
    private ImageView changeProfilePicBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);

        userName = findViewById(R.id.userName);
        userEmail = findViewById(R.id.userEmail);
        userProfilePic = findViewById(R.id.userProfilePic);
        changeProfilePicBtn = findViewById(R.id.changeProfilePicBtn);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user!= null) {
            String userId = user.getUid();
            databaseReference.child(userId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Users userProfile = snapshot.getValue(Users.class);
                    if (userProfile!= null) {
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
        backProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Profile.this, MainDashboard.class);
                startActivity(intent);
                finish();
            }
        });

        ConstraintLayout logoutBtn = findViewById(R.id.logoutBtn);
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show loading animation
                progressDialog = new ProgressDialog(Profile.this);
                progressDialog.setMessage("Signing out...");
                progressDialog.setCancelable(false);
                progressDialog.show();

                // Start new activity after 2 seconds
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss(); // Dismiss the dialog here
                        logoutUser(v);
                    }
                }, 3000); // 1000 milliseconds = 1 second
            }
        });

        changeProfilePicBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectProfilePicture();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            userProfilePic.setImageURI(imageUri);
            uploadProfilePicture(imageUri);
        }
    }

    private void uploadProfilePicture(Uri imageUri) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference profilePicRef = storageRef.child("profile_pictures/" + firebaseAuth.getCurrentUser().getUid());

        profilePicRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                profilePicRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri downloadUrl) {
                        databaseReference.child(firebaseAuth.getCurrentUser().getUid()).child("profile_picture").setValue(downloadUrl.toString());
                    }
                });
            }
        });
    }

    private void logoutUser(View view) {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(Profile.this, Login.class);
        startActivity(intent);
        finish();
    }
}
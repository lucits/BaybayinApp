package com.capstonearc.baybayinquizapp;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.capstonearc.baybayinquizapp.Activities.Profile;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ChangeProfile extends AppCompatActivity {

    ImageView profilePic;
    TextInputEditText usernameInput, emailInput;
    Button updateProfileBtn;

    ActivityResultLauncher<Intent> imagePickLauncher;
    Uri selectedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_profile);

        profilePic = findViewById(R.id.profile_image_view);
        usernameInput = findViewById(R.id.profile_username);
        emailInput = findViewById(R.id.profile_email);
        updateProfileBtn = findViewById(R.id.profile_update_btn);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users");

        // Initialize the image picker launcher
        imagePickLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null && data.getData() != null) {
                            selectedImageUri = data.getData();
                            setProfilePic(selectedImageUri);
                        }
                    }
                });

        ConstraintLayout backchangeprofileBtn = findViewById(R.id.backchangeprofileBtn);
        backchangeprofileBtn.setOnClickListener(v -> {
            Intent intent = new Intent(ChangeProfile.this, Profile.class);
            startActivity(intent);
            finish();
        });

        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            databaseReference.child(userId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (!isFinishing() && !isDestroyed()) { // Check if the activity is still valid
                        Users userProfile = snapshot.getValue(Users.class);
                        if (userProfile != null) {
                            String username = userProfile.getUsername();
                            String email = userProfile.getEmail();
                            String profilePicUrl = userProfile.getProfilePicUrl();

                            usernameInput.setText(username);
                            emailInput.setText(email);

                            if (profilePicUrl != null) {
                                Glide.with(ChangeProfile.this)
                                        .load(profilePicUrl)
                                        .apply(new RequestOptions().circleCrop())
                                        .into(profilePic);
                            }
                        }
                    }
                }


                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(ChangeProfile.this, "Failed to load profile", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(ChangeProfile.this, "No user logged in", Toast.LENGTH_SHORT).show();
        }



        updateProfileBtn.setOnClickListener(v -> updateBtnClick());

        profilePic.setOnClickListener(v -> {
            ImagePicker.with(this).cropSquare().compress(512).maxResultSize(1080, 1080).createIntent(intent -> {
                imagePickLauncher.launch(intent);
                return null;
            });
        });


    }

    private void setProfilePic(Uri uri) {
        Glide.with(this)
                .load(uri)
                .apply(new RequestOptions().circleCrop())
                .into(profilePic);
    }

    private void updateBtnClick() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users");

        if (user != null) {
            String userId = user.getUid();
            String newUsername = usernameInput.getText().toString();

            if (newUsername.isEmpty() || newUsername.length() < 6) {
                usernameInput.setError("Username length should be at least 6 characters");
                return;
            }

            databaseReference.child(userId).child("username").setValue(newUsername)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(ChangeProfile.this, "Username updated successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ChangeProfile.this, "Failed to update username", Toast.LENGTH_SHORT).show();
                        }
                    });

            if (selectedImageUri != null) {
                StorageReference profilePicRef = getCurrentProfilePicStorageRef();
                if (profilePicRef != null) {
                    profilePicRef.putFile(selectedImageUri)
                            .addOnSuccessListener(taskSnapshot -> profilePicRef.getDownloadUrl().addOnSuccessListener(uri -> {
                                String downloadUrl = uri.toString();
                                databaseReference.child(userId).child("profilePicUrl").setValue(downloadUrl)
                                        .addOnCompleteListener(task -> {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(ChangeProfile.this, "Profile picture updated successfully", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(ChangeProfile.this, "Failed to update profile picture", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }))
                            .addOnFailureListener(e -> Toast.makeText(ChangeProfile.this, "Failed to upload profile picture", Toast.LENGTH_SHORT).show());
                }
            }
        }
    }


    public static void updateToFirestore(){


    }

    public static StorageReference getCurrentProfilePicStorageRef() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();

        if (user != null) {
            String userId = user.getUid();
            return FirebaseStorage.getInstance().getReference().child("profile_pic").child(userId);
        } else {
            return null;
        }
    }

}

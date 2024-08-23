package com.capstonearc.baybayinquizapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.capstonearc.baybayinquizapp.Activities.MainDashboard;
import com.capstonearc.baybayinquizapp.Activities.Profile;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class ChangePasswordActivity extends AppCompatActivity {

    private TextInputEditText oldPassword, newPassword, confirmPassword;
    private Button changePasswordButton;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        oldPassword = findViewById(R.id.oldPassword);
        newPassword = findViewById(R.id.newPassword);
        confirmPassword = findViewById(R.id.confirmPassword);
        changePasswordButton = findViewById(R.id.changePasswordButton);

        auth = FirebaseAuth.getInstance();

        changePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePassword();
            }
        });
        ConstraintLayout backChangepassBtn = findViewById(R.id.backchangepassBtn);
        backChangepassBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChangePasswordActivity.this, Profile.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void changePassword() {
        String oldPass = oldPassword.getText().toString().trim();
        String newPass = newPassword.getText().toString().trim();
        String confirmPass = confirmPassword.getText().toString().trim();

        if (TextUtils.isEmpty(oldPass)) {
            oldPassword.setError("Old password is required");
            return;
        }

        if (TextUtils.isEmpty(newPass)) {
            newPassword.setError("New password is required");
            return;
        }

        if (newPass.length() < 6) {
            newPassword.setError("Password must be at least 6 characters");
            return;
        }

        if (!newPass.equals(confirmPass)) {
            confirmPassword.setError("Passwords do not match");
            return;
        }

        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            AuthCredential credential = EmailAuthProvider
                    .getCredential(user.getEmail(), oldPass);

            user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        user.updatePassword(newPass).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(ChangePasswordActivity.this, "Password updated", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(ChangePasswordActivity.this, "Error updating password", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        Toast.makeText(ChangePasswordActivity.this, "Authentication failed", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}

package com.rcdiarycollegedept.rcstudentdiary;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText emailEditText;
    private Button confirmButton, cancelButton;
    private FirebaseAuth mAuth;
    private DatabaseReference userDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        emailEditText = findViewById(R.id.editTextTextEmailAddress2);
        confirmButton = findViewById(R.id.saveButton);
        cancelButton = findViewById(R.id.cancelButton);
        mAuth = FirebaseAuth.getInstance();
        userDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        confirmButton.setOnClickListener(view -> resetPassword());
        cancelButton.setOnClickListener(view -> finish());
    }

    private void resetPassword() {
        String userEmail = emailEditText.getText().toString().trim();

        if (TextUtils.isEmpty(userEmail)) {
            showToast("Please enter your email address.");
        } else {
            // Check if the email exists in the database
            userDatabase.orderByChild("EmailAddress").equalTo(userEmail)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                // Email exists, send password reset email
                                mAuth.sendPasswordResetEmail(userEmail)
                                        .addOnCompleteListener(task -> {
                                            if (task.isSuccessful()) {
                                                showToast("Password reset email sent. Please check your email.");
                                                finish();
                                            } else {
                                                showToast("Failed to send password reset email. Please try again.");
                                            }
                                        });
                            } else {
                                showToast("Email not found in the database. Please enter a valid email.");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            showToast("Database error. Please try again.");
                        }
                    });
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}

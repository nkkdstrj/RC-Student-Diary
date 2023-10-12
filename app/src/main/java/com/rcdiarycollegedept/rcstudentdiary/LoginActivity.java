package com.rcdiarycollegedept.rcstudentdiary;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private EditText emailEditText, passwordEditText;
    private Button loginButton;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize UI elements and Firebase authentication
        emailEditText = findViewById(R.id.emailLogin);
        passwordEditText = findViewById(R.id.editTextPassword);
        loginButton = findViewById(R.id.LoginButton);
        mAuth = FirebaseAuth.getInstance();

        // Check if the user is already authenticated
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // If the user is already logged in, navigate to MainActivity
            startMainActivity();
        }

        // Set click listener for the login button
        loginButton.setOnClickListener(view -> loginUser());
    }

    private void loginUser() {
        String userEmail = emailEditText.getText().toString();
        String userPassword = passwordEditText.getText().toString();

        if (TextUtils.isEmpty(userEmail) || TextUtils.isEmpty(userPassword)) {
            showToast("Please fill in all the details.");
        } else {
            // Check if the email contains "@" symbol, if not, add "@example.com" to it.
            if (!userEmail.contains("@")) {
                userEmail += "@rc.edu";
            }
            signInUser(userEmail, userPassword);
        }
    }

    private void signInUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        showToast("Login Successful.");
                        startMainActivity();
                    } else {
                        showToast("Login failed. Please check your email and password.");
                    }
                });
    }

    private void showToast(String message) {
        Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    private void startMainActivity() {
        startActivity(new Intent(LoginActivity.this, MainActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
    }
}

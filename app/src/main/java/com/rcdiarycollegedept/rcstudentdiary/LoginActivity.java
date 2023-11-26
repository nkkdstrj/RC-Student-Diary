package com.rcdiarycollegedept.rcstudentdiary;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private EditText studentNumber, passwordEditText;
    private Button loginButton;
    private FirebaseAuth mAuth;
    private TextView ForgotPasswordButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize UI elements and Firebase authentication
        ForgotPasswordButton = findViewById(R.id.forgotPass);
        studentNumber = findViewById(R.id.studNum);
        passwordEditText = findViewById(R.id.editTextPassword);
        loginButton = findViewById(R.id.LoginButton);
        mAuth = FirebaseAuth.getInstance();

        // Check if the user is already authenticated
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            startMainActivity();
        }

        ForgotPasswordButton.setOnClickListener(view -> showForgotPasswordDialog());
        loginButton.setOnClickListener(view -> loginUser());
    }

    private void loginUser() {
        String userNumber = studentNumber.getText().toString().trim();
        String userPassword = passwordEditText.getText().toString().trim();

        if (TextUtils.isEmpty(userNumber) || TextUtils.isEmpty(userPassword)) {
            showToast("Please fill in all the details.");
        } else {
            if (!userNumber.contains("@")) {
                userNumber += "@rc.edu";
            }
            signInUser(userNumber, userPassword);
        }
    }

    private void signInUser(String studnum, String password) {
        mAuth.signInWithEmailAndPassword(studnum, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        showToast("Login Successful.");
                        startMainActivity();
                    } else {
                        showToast("Login failed. Please check your student number and password.");
                    }
                });
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
    private void showForgotPasswordDialog() {
        // Create an instance of the ForgotPassFragment
        ForgotPassFragment forgotPassFragment = new ForgotPassFragment();

        // Get the FragmentManager
        FragmentManager fragmentManager = getSupportFragmentManager();

        // Begin a FragmentTransaction
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        // Add the fragment to the transaction and show it as a dialog
        transaction.add(forgotPassFragment, "ForgotPassFragment");
        transaction.commit();
    }

}

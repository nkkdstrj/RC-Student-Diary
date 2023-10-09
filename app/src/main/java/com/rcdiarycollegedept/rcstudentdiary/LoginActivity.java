package com.rcdiarycollegedept.rcstudentdiary;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    EditText email, passWord;
    Button btn;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = findViewById(R.id.emailLogin);
        passWord = findViewById(R.id.editTextPassword);
        btn = findViewById(R.id.LoginButton);
        mAuth = FirebaseAuth.getInstance();

        btn.setOnClickListener(view -> loginUser());
    }

    private void loginUser() {
        String userEmail = email.getText().toString();
        String userPassword = passWord.getText().toString();

        if (TextUtils.isEmpty(userEmail) || TextUtils.isEmpty(userPassword)) {
            Toast.makeText(LoginActivity.this, "Please fill all the details.", Toast.LENGTH_SHORT).show();
        } else {
            // Check if the email contains "@" symbol, if not, add "@example.com" to it.
            if (!userEmail.contains("@")) {
                userEmail += "@example.com";
            }
            signInUser(userEmail, userPassword);
        }
    }

    private void signInUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        mAuth.getCurrentUser();
                        Toast.makeText(LoginActivity.this, "Login Successful.", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    } else {
                        Toast.makeText(LoginActivity.this, "Login failed. Please check your email and password.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
package com.rcdiarycollegedept.rcstudentdiary;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class LoginActivity extends AppCompatActivity {

    EditText studNum, passWord;
    Button btn;
    DatabaseReference dbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        studNum = findViewById(R.id.editTextStudentNumber);
        passWord = findViewById(R.id.editTextPassword);
        btn = findViewById(R.id.LoginButton);
        dbRef = FirebaseDatabase.getInstance().getReferenceFromUrl("https://rc-student-diary-5cad7-default-rtdb.firebaseio.com/");

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginUser();
            }
        });
    }

    private static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void loginUser() {
        String sNum = studNum.getText().toString();
        String pWord = passWord.getText().toString();

        if (sNum.isEmpty() || pWord.isEmpty()) {
            Toast.makeText(LoginActivity.this, "Please fill all the details.", Toast.LENGTH_SHORT).show();
        } else {
            checkUserCredentials(sNum, pWord);
        }
    }

    private void checkUserCredentials(String sNum, String pWord) {
        dbRef.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild(sNum)) {
                    String storedHashedPassword = snapshot.child(sNum).child("PassWord").getValue(String.class);
                    String hashedPassword = hashPassword(pWord); // Hash the entered password

                    if (hashedPassword != null && hashedPassword.equals(storedHashedPassword)) {
                        Toast.makeText(LoginActivity.this, "Login Successful.", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    } else {
                        Toast.makeText(LoginActivity.this, "Login failed.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "Wrong student number.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle database error, if needed
            }
        });
    }
}

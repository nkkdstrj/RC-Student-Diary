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

public class LoginActivity extends AppCompatActivity {

    EditText studNum, passWord;
    Button btn;

    DatabaseReference dbRef = FirebaseDatabase.getInstance().getReferenceFromUrl("https://rc-student-diary-5cad7-default-rtdb.firebaseio.com/");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        studNum = findViewById(R.id.editTextStudentNumber);
        passWord = findViewById(R.id.editTextPassword);
        btn = findViewById(R.id.LoginButton);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String sNum = studNum.getText().toString();
                String pWord = passWord.getText().toString();

                if (sNum.isEmpty() || pWord.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Please fill all the details.", Toast.LENGTH_SHORT).show();
                } else {
                    dbRef.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.hasChild(sNum)){
                                String getPass = snapshot.child(sNum).child("PassWord").getValue(String.class);

                                if (getPass.equals(pWord)){
                                    Toast.makeText(LoginActivity.this, "Login Successful.", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                }else {
                                    Toast.makeText(LoginActivity.this, "Login failed.", Toast.LENGTH_SHORT).show();
                                }
                            }
                            else {
                                Toast.makeText(LoginActivity.this, "Wrong password.", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
        });
    }
}
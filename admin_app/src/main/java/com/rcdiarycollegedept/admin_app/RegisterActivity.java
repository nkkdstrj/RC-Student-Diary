package com.rcdiarycollegedept.admin_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

public class RegisterActivity extends AppCompatActivity {


    EditText fullName, studNum, phoneNum, passWord, confPass;
    Button regBtn;
    DatabaseReference dbRef = FirebaseDatabase.getInstance().getReferenceFromUrl("https://rc-student-diary-5cad7-default-rtdb.firebaseio.com/");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        fullName = findViewById(R.id.editTextFullName);
        studNum = findViewById(R.id.editTextStudentNumber);
        phoneNum = findViewById(R.id.editTextPhoneNum);
        passWord = findViewById(R.id.editTextPassword);
        confPass = findViewById(R.id.editTextConfirmPass);
        regBtn = findViewById(R.id.RegBtn);

        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String name = fullName.getText().toString();
                String sNum = studNum.getText().toString();
                String pNum = phoneNum.getText().toString();
                String pWord= passWord.getText().toString();
                String conPasWd = confPass.getText().toString();

                if(name.isEmpty() || sNum.isEmpty() || pNum.isEmpty() || pWord.isEmpty() || conPasWd.isEmpty()){
                    Toast.makeText(RegisterActivity.this, "Please fill all details.", Toast.LENGTH_SHORT).show();
                } else if (!pWord.equals(conPasWd)) {
                    Toast.makeText(RegisterActivity.this, "Password don't match.", Toast.LENGTH_SHORT).show();
                } else {

                    dbRef.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.hasChild(sNum)){
                                Toast.makeText(RegisterActivity.this, "Student Number is already registered.", Toast.LENGTH_SHORT).show();
                            } else {
                                dbRef.child("Users").child(sNum).child("FullName").setValue(name);
                                dbRef.child("Users").child(sNum).child("PhoneNumber").setValue(pNum);
                                dbRef.child("Users").child(sNum).child("PassWord").setValue(pWord);

                                Toast.makeText(RegisterActivity.this, "User registered successfully.", Toast.LENGTH_SHORT).show();
                                finish();
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
package com.rcdiarycollegedept.admin_app;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    EditText fullName, studNum, phoneNum, passWord, confPass;
    Button regBtn;
    FirebaseAuth firebaseAuth;
    DatabaseReference dbRef;

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
        firebaseAuth = FirebaseAuth.getInstance();
        dbRef = FirebaseDatabase.getInstance().getReferenceFromUrl("https://rc-student-diary-5cad7-default-rtdb.firebaseio.com/");

        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = fullName.getText().toString();
                String sNum = studNum.getText().toString();
                String pNum = phoneNum.getText().toString();
                String pWord = passWord.getText().toString();
                String conPasWd = confPass.getText().toString();

                if (name.isEmpty() || sNum.isEmpty() || pNum.isEmpty() || pWord.isEmpty() || conPasWd.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Please fill all details.", Toast.LENGTH_SHORT).show();
                } else if (!pWord.equals(conPasWd)) {
                    Toast.makeText(RegisterActivity.this, "Password don't match.", Toast.LENGTH_SHORT).show();
                } else {
                    registerUserWithEmailPassword(name, sNum, pNum, pWord);
                }
            }
        });
    }

    private void registerUserWithEmailPassword(final String name, final String sNum, final String pNum, String password) {
        firebaseAuth.createUserWithEmailAndPassword(sNum + "@example.com", password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        if (user != null) {
                            // Successfully registered, now save additional user data to the database
                            dbRef.child("Users").child(sNum).child("FullName").setValue(name);
                            dbRef.child("Users").child(sNum).child("PhoneNumber").setValue(pNum);

                            Toast.makeText(RegisterActivity.this, "User registered successfully.", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    } else {
                        try {
                            throw task.getException();
                        } catch (FirebaseAuthUserCollisionException e) {
                            Toast.makeText(RegisterActivity.this, "User with this student number already exists.", Toast.LENGTH_SHORT).show();
                        } catch (FirebaseAuthInvalidCredentialsException e) {
                            Toast.makeText(RegisterActivity.this, "Invalid student number format.", Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            Toast.makeText(RegisterActivity.this, "Registration failed. Please try again later.", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }
                });
    }
}

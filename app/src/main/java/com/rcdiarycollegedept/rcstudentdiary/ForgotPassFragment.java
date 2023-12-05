package com.rcdiarycollegedept.rcstudentdiary;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth;


public class ForgotPassFragment extends DialogFragment {

    private DatabaseReference databaseReference;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_forgot_pass, container, false);

        EditText studNumEditText = view.findViewById(R.id.studentNumber);
        EditText emailEditText = view.findViewById(R.id.emailAddress);
        Button submitButton = view.findViewById(R.id.dialog_save);
        Button cancelButton = view.findViewById(R.id.dialog_cancel);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");

        submitButton.setOnClickListener(v -> {
            String studNumber = studNumEditText.getText().toString();
            String emailAd = emailEditText.getText().toString();

            if (!TextUtils.isEmpty(studNumber) && !TextUtils.isEmpty(emailAd)) {
                checkUserInDatabase(studNumber, emailAd);
            } else {
                Toast.makeText(getActivity(), "Please enter student number and email address", Toast.LENGTH_SHORT).show();
            }
        });

        cancelButton.setOnClickListener(v -> {
            dismiss();
        });

        return view;
    }

    private void checkUserInDatabase(String studNumber, String emailAd) {
        databaseReference.orderByChild("StudentNumber").equalTo(studNumber).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        // For each user with matching student number
                        String savedEmail = userSnapshot.child("EmailAddress").getValue(String.class);

                        if (savedEmail != null && savedEmail.equals(emailAd)) {
                            // Email matches, initiate password reset
                            sendPasswordResetEmail(savedEmail);
                            return; // Break out of the loop if a match is found
                        }
                    }
                    // If no match is found after looping through all users
                    Toast.makeText(getActivity(), "Email address does not match", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "Student number not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendPasswordResetEmail(String email) {
        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(getActivity(), "Reset password email sent to " + email, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), "Failed to send reset password email. Check the email address.", Toast.LENGTH_SHORT).show();
                    }
                    dismiss();
                });
    }
}

package com.rcdiarycollegedept.rcstudentdiary;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class HomeFragment extends Fragment {

    private TextView NameTextView;
    private TextView dayTextView;
    private TextView dateTextView; // Add this line
    private DatabaseReference databaseReference;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        NameTextView = rootView.findViewById(R.id.NameTextView);
        dayTextView = rootView.findViewById(R.id.dayTextView);
        dateTextView = rootView.findViewById(R.id.dateTextView); // Initialize dateTextView

        // Get the reference to the Firebase Realtime Database node for FullName
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(getUserStudentNumber()).child("FullName");

        // Call the method to retrieve data from the database
        fetchDataFromFirebase();

        // Set the current day and date
        setDayTextView();
        setDateTextView();

        return rootView;
    }

    private void setDayTextView() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
        String dayOfWeek = dateFormat.format(calendar.getTime());
        dayTextView.setText(dayOfWeek);
    }

    private void setDateTextView() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM d, yyyy", Locale.getDefault());
        String formattedDate = dateFormat.format(calendar.getTime());
        dateTextView.setText(formattedDate);
    }

    private String getUserStudentNumber() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String email = user.getEmail();
            // Extract student number from the email address (assuming it's in the format "studentnumber@example.com")
            return email != null ? email.split("@")[0] : "";
        }
        return "";
    }

    private void fetchDataFromFirebase() {
        // Read data from the Firebase database
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String fullName = dataSnapshot.getValue(String.class);
                    // Update the TextView with the retrieved data
                    NameTextView.setText(fullName);
                } else {
                    // Handle the case where FullName doesn't exist in the database
                    NameTextView.setText("Full name not found");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle the error if the data retrieval is canceled
                NameTextView.setText("Error fetching data");
            }
        });
    }
}


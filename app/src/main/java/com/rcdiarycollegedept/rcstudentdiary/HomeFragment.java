package com.rcdiarycollegedept.rcstudentdiary;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rcdiarycollegedept.rcstudentdiary.databinding.ActivityHomeBinding;

public class HomeFragment extends Fragment {



    private TextView NameTextView;
    private DatabaseReference databaseReference;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        NameTextView = rootView.findViewById(R.id.NameTextView);
        // Get the reference to the Firebase Realtime Database node
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child("FullName");
        // Call the method to retrieve data from the database
        fetchDataFromFirebase();
        return rootView;
    }

    private void fetchDataFromFirebase() {
        // Read data from the Firebase database
        databaseReference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null && task.getResult().getValue() != null) {
                String data = task.getResult().getValue().toString();
                // Update the TextView with the retrieved data
                NameTextView.setText(data);
            }else {
                // Handle the error
                NameTextView.setText("Error");
            }
        });
    }
}
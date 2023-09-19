package com.rcdiarycollegedept.rcstudentdiary;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DiaryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<DiaryDataModel> mList;
    private DiaryDataAdapter adapter;

    // Firebase Database
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.diary_main);

        recyclerView = findViewById(R.id.main_recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        mList = new ArrayList<>();

        // Initialize Firebase Database reference
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Fetch data from Firebase
        fetchDataFromFirebase();
    }

    private void fetchDataFromFirebase() {
        DatabaseReference diaryContentRef = mDatabase.child("diarycontent_btn");

        diaryContentRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                List<DiaryDataModel> mList = new ArrayList<>();
                // Inside onDataChange method when fetching data from Firebase
                for (DataSnapshot buttonSnapshot : dataSnapshot.getChildren()) {
                    String buttonName = buttonSnapshot.child("buttonname").getValue(String.class);

                    // Fetch sub-buttons as a list
                    List<DiaryDataModel> subButtonList = new ArrayList<>();
                    for (DataSnapshot subButtonSnapshot : buttonSnapshot.child("btn_sub_btns").getChildren()) {

                        String subButtonName = subButtonSnapshot.child("sub_btn_name").getValue(String.class);
                        String subButtonAudio = subButtonSnapshot.child("audio").getValue(String.class);
                        String subButtonContent = subButtonSnapshot.child("content").getValue(String.class);
                        int subButtonLayout = subButtonSnapshot.child("layout").getValue(Integer.class);
                        String subButtonPicture = subButtonSnapshot.child("picture").getValue(String.class);

                        subButtonList.add(new DiaryDataModel(subButtonName, subButtonAudio, subButtonContent, subButtonLayout, subButtonPicture));
                    }

// Create a DiaryDataModel object for the main button with sub-buttons
                    mList.add(new DiaryDataModel(buttonName, subButtonList));
                }

                // Initialize the adapter and set it to the RecyclerView
                adapter = new DiaryDataAdapter(mList);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("FirebaseError", "Error fetching data from Firebase: " + databaseError.getMessage());
            }
        });
    }

}
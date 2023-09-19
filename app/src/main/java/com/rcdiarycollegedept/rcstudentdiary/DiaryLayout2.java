package com.rcdiarycollegedept.rcstudentdiary;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class DiaryLayout2 extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.diary_layout_2);


        String content = getIntent().getStringExtra("content");
        String audio = getIntent().getStringExtra("audio");
        String picture = getIntent().getStringExtra("picture");

        // Populate the sub-activity layout with the data as needed
        TextView contentTextView = findViewById(R.id.textViewr);
        contentTextView.setText(content);

    }

}
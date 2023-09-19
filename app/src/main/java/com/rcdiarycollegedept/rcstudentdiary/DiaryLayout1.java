package com.rcdiarycollegedept.rcstudentdiary;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class DiaryLayout1 extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.diary_layout_1);

        String content = getIntent().getStringExtra("content");
        String audio = getIntent().getStringExtra("audio");
        String picture = getIntent().getStringExtra("picture");

        TextView contentTextView = findViewById(R.id.diarytext111);
        contentTextView.setText(content);

        // Load audio and picture as needed
    }
}

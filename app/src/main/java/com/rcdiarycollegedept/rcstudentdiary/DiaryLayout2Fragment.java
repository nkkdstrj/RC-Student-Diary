package com.rcdiarycollegedept.rcstudentdiary;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class DiaryLayout2Fragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_diary_layout2, container, false);

        // Retrieve data from arguments (assuming you pass data using arguments)
        String content = getArguments().getString("content");
        String audio = getArguments().getString("audio");
        String picture = getArguments().getString("picture");

        TextView contentTextView = view.findViewById(R.id.qwer);
        contentTextView.setText(content);

        // Load audio and picture as needed

        return view;
    }
}

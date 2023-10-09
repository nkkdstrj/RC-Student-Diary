package com.rcdiarycollegedept.rcstudentdiary;

import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class DiaryLayout3Fragment extends Fragment {
    public static final String ARG_CONTENT = "content";

    public static DiaryLayout3Fragment newInstance(String content) {
        DiaryLayout3Fragment fragment = new DiaryLayout3Fragment();
        Bundle args = new Bundle();
        args.putString(ARG_CONTENT, content);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_diary_layout3, container, false);
        TextView contentTextView = rootView.findViewById(R.id.contents);

        if (getArguments() != null) {
            String content = getArguments().getString(ARG_CONTENT);
            contentTextView.setText(content);

            contentTextView.setText(Html.fromHtml(content));



        }

        return rootView;
    }
}

package com.rcdiarycollegedept.rcstudentdiary;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class CalendarFragment extends Fragment {

    private CalendarView calendarView;
    private EditText event;

    private String dateSelected;

    public CalendarFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_calendar, container, false);

        calendarView = rootView.findViewById(R.id.CalView);
        event = rootView.findViewById(R.id.event);

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int i, int i1, int i2) {
                dateSelected = Integer.toString(i) + Integer.toString(i1 + 1) + Integer.toString(i2);
                calendarClicked();
            }
        });

        return rootView;
    }

    private void calendarClicked() {
        // Implement your logic here for when a date is clicked.
    }

    public void saveEvent(View view){

    }
}

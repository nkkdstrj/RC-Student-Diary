package com.rcdiarycollegedept.rcstudentdiary;

import android.os.Bundle;
import android.widget.Button;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.UUID;
import android.provider.Settings.Secure;

public class CalendarFragment extends Fragment {

    private CalendarView calendarView;
    private EditText eventText;

    private String dateSelected;
    private DatabaseReference databaseReference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);

        calendarView = view.findViewById(R.id.CalView);
        eventText = view.findViewById(R.id.event);

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int i, int i1, int i2) {
                dateSelected = Integer.toString(i) + Integer.toString(i1 + 1) + Integer.toString(i2);
                calendarClicked();
            }
        });

        databaseReference = FirebaseDatabase.getInstance().getReference("Calendar");

        Button saveButton = view.findViewById(R.id.button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveEvent();
            }
        });

        return view;
    }

    private String generateUniqueKey() {
        String androidId = Secure.getString(getActivity().getContentResolver(), Secure.ANDROID_ID);
        UUID uuid = UUID.randomUUID();
        return androidId + "_" + uuid.toString() + "_" + dateSelected;
    }

    private void calendarClicked() {

        //String uniKey = generateUniqueKey();
        databaseReference.child(dateSelected).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String event = snapshot.getValue(String.class);
                    eventText.setText(event); // Set the event text in the EditText
                } else {
                    eventText.setText(""); // Clear the EditText if no event is found
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle onCancelled
            }
        });
    }



    private void saveEvent() {
        String key = generateUniqueKey();
        databaseReference.child(key).setValue(eventText.getText().toString());
        Toast.makeText(getContext(), "Reminder Saved.", Toast.LENGTH_SHORT).show();
    }
}

package com.rcdiarycollegedept.rcstudentdiary;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
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

public class CalendarFragment extends Fragment {

    private CalendarView calendarView;
    private EditText eventText;

    private String selectedDate;
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    private static final String CHANNEL_ID = "MyChannel";

    @SuppressLint("DefaultLocale")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);

        calendarView = view.findViewById(R.id.CalView);
        eventText = view.findViewById(R.id.event);
        mAuth = FirebaseAuth.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            databaseReference = FirebaseDatabase.getInstance().getReference("Calendar")
                    .child(currentUser.getUid());
        } else {
            // Handle the case where currentUser is null (user not authenticated) as needed
        }

        // Set selectedDate to the current date (YYYYMMDD format)
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        selectedDate = dateFormat.format(calendar.getTime());

        calendarClicked();

        calendarView.setOnDateChangeListener((calendarView, year, month, dayOfMonth) -> {
            selectedDate = String.format("%04d%02d%02d", year, month + 1, dayOfMonth);
            calendarClicked();
        });

        Button saveButton = view.findViewById(R.id.button);
        saveButton.setOnClickListener(v -> saveEvent());

        return view;
    }

    private void calendarClicked() {
        if (databaseReference != null) {
            DatabaseReference dateReference = databaseReference.child(selectedDate);

            dateReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String event = snapshot.getValue(String.class);
                        eventText.setText(event);

                        Calendar calendar = Calendar.getInstance();
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
                        String currentDate = dateFormat.format(calendar.getTime());

                        if (selectedDate.equals(currentDate)) {
                            // No need to showNotification here; remove this line
                        }
                    } else {
                        eventText.setText("");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle database read error if needed
                }
            });
        }
    }

    private void saveEvent() {
        String event = eventText.getText().toString().trim();

        if (!event.isEmpty() && databaseReference != null) {
            DatabaseReference dateReference = databaseReference.child(selectedDate);
            dateReference.setValue(event);

            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
            String currentDate = dateFormat.format(calendar.getTime());

            if (selectedDate.equals(currentDate)) {
                // No need to showNotification here; remove this line
            }

            Toast.makeText(getContext(), "Reminder Saved.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "Event text is empty.", Toast.LENGTH_SHORT).show();
        }
    }
}

package com.rcdiarycollegedept.rcstudentdiary;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TimePicker;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.text.SimpleDateFormat;

public class CalendarFragment extends Fragment implements ReminderAdapter.OnReminderDeleteListener, ReminderAdapter.OnReminderEditListener {

    private String selectedDate;
    private CalendarView calendarView;
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    private ImageButton addReminder;
    private ReminderAdapter reminderAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);

        initializeViews(view);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            databaseReference = FirebaseDatabase.getInstance().getReference("Calendar").child(currentUser.getUid());
        }

        setupCalendarView();
        createNotificationChannel();
        fetchAllReminders();
        return view;
    }

    private void initializeViews(View view) {
        calendarView = view.findViewById(R.id.CalView);
        addReminder = view.findViewById(R.id.addButton);
        reminderAdapter = new ReminderAdapter(this, this);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(reminderAdapter);

        addReminder.setOnClickListener(v -> showReminderDialog());
    }

    private void setupCalendarView() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        selectedDate = dateFormat.format(calendar.getTime());

        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            selectedDate = String.format("%04d%02d%02d", year, month + 1, dayOfMonth);
            fetchAllReminders();
        });
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence channelName = "Reminders";
            String channelId = "RemindersChannel";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
            NotificationManager notificationManager = requireActivity().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void showReminderDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = getLayoutInflater().inflate(R.layout.reminders_layout, null);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        EditText reminderName = dialogView.findViewById(R.id.dialog_reminder_name);
        TimePicker reminderTime = dialogView.findViewById(R.id.dialog_reminder_time);
        Button saveButton = dialogView.findViewById(R.id.dialog_save);
        Button cancelButton = dialogView.findViewById(R.id.dialog_cancel);

        saveButton.setOnClickListener(v -> {
            String eventName = reminderName.getText().toString().trim();
            int hour = reminderTime.getCurrentHour();
            int minute = reminderTime.getCurrentMinute();
            String eventTime = String.format(Locale.getDefault(), "%02d:%02d", hour, minute);

            if (!eventName.isEmpty() && databaseReference != null) {
                DatabaseReference dateReference = databaseReference.child(selectedDate).push();
                Map<String, Object> eventDetails = new HashMap<>();
                eventDetails.put("eventName", eventName);
                eventDetails.put("eventTime", eventTime);
                dateReference.setValue(eventDetails);

                if (selectedDate.equals(getCurrentDate())) {
                    scheduleNotification(eventName, hour, minute);
                }

                Reminder newReminder = new Reminder(dateReference.getKey(), selectedDate, eventName, eventTime);
                reminderAdapter.addReminder(newReminder);
                reminderAdapter.notifyDataSetChanged();
                Toast.makeText(getContext(), "Reminder Saved.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Event name is empty.", Toast.LENGTH_SHORT).show();
            }

            dialog.dismiss();
        });

        cancelButton.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private String getCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        return dateFormat.format(calendar.getTime());
    }

    private void fetchAllReminders() {
        if (databaseReference != null) {
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    reminderAdapter.clear();
                    for (DataSnapshot dateSnapshot : dataSnapshot.getChildren()) {
                        String date = dateSnapshot.getKey();
                        for (DataSnapshot reminderSnapshot : dateSnapshot.getChildren()) {
                            Reminder reminder = reminderSnapshot.getValue(Reminder.class);
                            if (reminder != null) {
                                reminder.setId(reminderSnapshot.getKey());
                                reminder.setDate(date);
                                reminderAdapter.addReminder(reminder);
                            }
                        }
                    }
                    reminderAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle any database error here
                }
            });
        }
    }

    private void scheduleNotification(String eventName, int hour, int minute) {
        Intent notificationIntent = new Intent(getContext(), NotificationReceiver.class);
        notificationIntent.putExtra("eventName", eventName);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                getContext(),
                0,
                notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE
        );

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        AlarmManager alarmManager = (AlarmManager) requireContext().getSystemService(Context.ALARM_SERVICE);
        alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(),
                pendingIntent
        );
    }

    @Override
    public void onDeleteReminder(Reminder reminder) {
        if (databaseReference != null) {
            databaseReference.child(reminder.getDate()).child(reminder.getId()).removeValue();
            reminderAdapter.reminders.remove(reminder);
            reminderAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onEditReminder(Reminder reminder) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = getLayoutInflater().inflate(R.layout.edit_reminder_dialog, null);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        EditText editReminderName = dialogView.findViewById(R.id.dialog_reminder_name);
        TimePicker editReminderTime = dialogView.findViewById(R.id.dialog_reminder_time);
        Button confirmButton = dialogView.findViewById(R.id.dialog_save);
        Button cancelButton = dialogView.findViewById(R.id.dialog_cancel);

        editReminderName.setText(reminder.getEventName());
        String[] timeParts = reminder.getEventTime().split(":");
        int hour = Integer.parseInt(timeParts[0]);
        int minute = Integer.parseInt(timeParts[1]);
        editReminderTime.setHour(hour);
        editReminderTime.setMinute(minute);

        confirmButton.setOnClickListener(v -> {
            String newEventName = editReminderName.getText().toString().trim();
            int newHour = editReminderTime.getHour();
            int newMinute = editReminderTime.getMinute();
            String newEventTime = String.format(Locale.getDefault(), "%02d:%02d", newHour, newMinute);

            if (!newEventName.isEmpty() && databaseReference != null) {
                DatabaseReference reminderRef = databaseReference.child(reminder.getDate()).child(reminder.getId());
                Map<String, Object> updatedData = new HashMap<>();
                updatedData.put("eventName", newEventName);
                updatedData.put("eventTime", newEventTime);
                reminderRef.updateChildren(updatedData);

                scheduleNotification(newEventName, newHour, newMinute);

                reminder.setEventName(newEventName);
                reminder.setEventTime(newEventTime);
                reminderAdapter.notifyDataSetChanged();
                Toast.makeText(getContext(), "Reminder Updated.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Event name is empty.", Toast.LENGTH_SHORT).show();
            }

            dialog.dismiss();
        });

        cancelButton.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getActivity() != null) {
            ((MainActivity) getActivity()).setOnBackPressedListener(() -> {

                if (isVisible()) {

                    getActivity().finish();
                }
            });
        }
    }
}

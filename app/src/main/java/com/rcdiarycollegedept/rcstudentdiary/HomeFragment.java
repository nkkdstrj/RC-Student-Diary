package com.rcdiarycollegedept.rcstudentdiary;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class HomeFragment extends Fragment {

    private TextView nameTextView, dayTextView, dateTextView, triviaText;
    private ProgressBar nameProgressBar, dayProgressBar, dateProgressBar, triviaProgressBar;
    private DatabaseReference databaseReference, triviaReference;
    private RecyclerView publicReminderRecyclerView;
    private PublicReminderAdapter publicReminderAdapter;
    private List<PublicReminder> publicReminderList = new ArrayList<>();
    private List<Integer> usedTriviaIndices = new ArrayList<>();
    private int totalTriviaItems = 0;

    private boolean newReminderFetched = false; // Flag to track new reminders

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        initializeViews(rootView);
        setDayAndDate();
        fetchDataFromFirebase();
        fetchPublicRemindersFromFirebase();
        fetchTotalTriviaItemsCount();
        return rootView;
    }

    private void initializeViews(View rootView) {
        nameTextView = rootView.findViewById(R.id.NameTextView);
        dayTextView = rootView.findViewById(R.id.dayTextView);
        dateTextView = rootView.findViewById(R.id.dateTextView);
        triviaText = rootView.findViewById(R.id.triviaText);

        nameProgressBar = rootView.findViewById(R.id.nameProgressBar);
        dayProgressBar = rootView.findViewById(R.id.dayProgressBar);
        dateProgressBar = rootView.findViewById(R.id.dateProgressBar);
        triviaProgressBar = rootView.findViewById(R.id.triviaProgressBar2);

        nameProgressBar.setVisibility(View.GONE);
        dayProgressBar.setVisibility(View.GONE);
        dateProgressBar.setVisibility(View.GONE);
        triviaProgressBar.setVisibility(View.GONE);

        publicReminderRecyclerView = rootView.findViewById(R.id.publicReminder);
        publicReminderRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        publicReminderAdapter = new PublicReminderAdapter(requireContext(), publicReminderList);
        publicReminderRecyclerView.setAdapter(publicReminderAdapter);
    }

    private void setDayAndDate() {
        SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM d, yyyy", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();
        dayTextView.setText(dayFormat.format(calendar.getTime()));
        dateTextView.setText(dateFormat.format(calendar.getTime()));
    }

    private void fetchDataFromFirebase() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userStudentNumber = (user != null) ? user.getEmail().split("@")[0] : "";
        DatabaseReference userReference = FirebaseDatabase.getInstance().getReference().child("Users")
                .child(userStudentNumber)
                .child("FullName");

        nameProgressBar.setVisibility(View.VISIBLE);
        nameTextView.setVisibility(View.GONE);

        userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String fullName = (dataSnapshot.exists()) ? dataSnapshot.getValue(String.class) : "Full name not found";
                nameTextView.setText(fullName);
                nameProgressBar.setVisibility(View.GONE);
                nameTextView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                nameTextView.setText("Error fetching data");
                nameProgressBar.setVisibility(View.GONE);
                nameTextView.setVisibility(View.VISIBLE);
            }
        });
    }

    private void fetchPublicRemindersFromFirebase() {
        DatabaseReference publicRemindersRef = FirebaseDatabase.getInstance().getReference().child("PublicReminders");

        dayProgressBar.setVisibility(View.VISIBLE);
        dayTextView.setVisibility(View.GONE);

        publicRemindersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dayProgressBar.setVisibility(View.GONE);
                dayTextView.setVisibility(View.VISIBLE);

                publicReminderList.clear();
                for (DataSnapshot reminderSnapshot : dataSnapshot.getChildren()) {
                    for (DataSnapshot eventSnapshot : reminderSnapshot.getChildren()) {
                        PublicReminder publicReminder = eventSnapshot.getValue(PublicReminder.class);

                        if (publicReminder != null) {
                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
                            try {
                                Date date = dateFormat.parse(eventSnapshot.getKey());
                                publicReminder.setDate(date);
                                publicReminderList.add(publicReminder);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                publicReminderAdapter.notifyDataSetChanged();

                // Check for new reminders and send a notification
                if (newReminderFetched) {
                    String eventName = publicReminderList.get(publicReminderList.size() - 1).getEventName();
                    sendNotification(eventName);
                    newReminderFetched = true; // Reset the flag
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                dayProgressBar.setVisibility(View.GONE);
                dayTextView.setVisibility(View.VISIBLE);
            }
        });
    }

    private void fetchTotalTriviaItemsCount() {
        triviaReference = FirebaseDatabase.getInstance().getReference().child("Trivia");
        triviaReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                totalTriviaItems = (dataSnapshot.exists()) ? (int) dataSnapshot.getChildrenCount() : 0;
                fetchRandomTrivia();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                totalTriviaItems = 0;
                fetchRandomTrivia();
            }
        });
    }

    private void fetchRandomTrivia() {
        if (usedTriviaIndices.size() == totalTriviaItems) {
            usedTriviaIndices.clear();
        }

        int randomIndex;
        if (totalTriviaItems > 0) {
            do {
                randomIndex = ThreadLocalRandom.current().nextInt(totalTriviaItems);
            } while (usedTriviaIndices.contains(randomIndex));
        } else {
            randomIndex = -1;
        }

        if (randomIndex != -1) {
            usedTriviaIndices.add(randomIndex);

            String triviaKey = "T" + (randomIndex + 1);
            DatabaseReference randomTriviaReference = triviaReference.child(triviaKey);

            triviaProgressBar.setVisibility(View.VISIBLE);
            triviaText.setVisibility(View.GONE);

            randomTriviaReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String triviaContent = (dataSnapshot.exists()) ? dataSnapshot.child("titleContent").getValue(String.class) : "Error fetching trivia";
                    triviaText.setText(triviaContent);
                    triviaProgressBar.setVisibility(View.GONE);
                    triviaText.setVisibility(View.VISIBLE);

                    // Set the newReminderFetched flag to true when a new reminder is fetched
                    newReminderFetched = true;
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    triviaText.setText("Error fetching trivia");
                    triviaProgressBar.setVisibility(View.GONE);
                    triviaText.setVisibility(View.VISIBLE);
                }
            });
        } else {
            triviaText.setText("No trivia available");
            triviaProgressBar.setVisibility(View.GONE);
            triviaText.setVisibility(View.VISIBLE);
        }
    }

    @SuppressLint("MissingPermission")
    private void sendNotification(String eventName) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("default", "Default Channel", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = requireActivity().getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(requireContext(), "default")
                .setSmallIcon(R.drawable.baseline_notifications_24)
                .setContentTitle("Announcement")
                .setContentText(eventName)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        Notification notification = builder.build();

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(requireContext());
        notificationManager.notify(1, notification);
    }
}

package com.rcdiarycollegedept.rcstudentdiary;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

        // Progress bars for each TextView
        nameProgressBar = rootView.findViewById(R.id.nameProgressBar);
        dayProgressBar = rootView.findViewById(R.id.dayProgressBar);
        dateProgressBar = rootView.findViewById(R.id.dateProgressBar);
        triviaProgressBar = rootView.findViewById(R.id.triviaProgressBar2);

        // Set the initial visibility of progress bars to "GONE"
        nameProgressBar.setVisibility(View.GONE);
        dayProgressBar.setVisibility(View.GONE);
        dateProgressBar.setVisibility(View.GONE);
        triviaProgressBar.setVisibility(View.GONE);

        publicReminderRecyclerView = rootView.findViewById(R.id.publicReminder);
        publicReminderRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        // Pass the context and the publicReminderList to the adapter
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

        // Show the progress bar for the nameTextView and hide the TextView
        nameProgressBar.setVisibility(View.VISIBLE);
        nameTextView.setVisibility(View.GONE);

        userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String fullName = (dataSnapshot.exists()) ? dataSnapshot.getValue(String.class) : "Full name not found";
                nameTextView.setText(fullName);
                nameProgressBar.setVisibility(View.GONE); // Hide the progress bar
                nameTextView.setVisibility(View.VISIBLE); // Show the TextView
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                nameTextView.setText("Error fetching data");
                nameProgressBar.setVisibility(View.GONE); // Hide the progress bar
                nameTextView.setVisibility(View.VISIBLE); // Show the TextView
            }
        });
    }

    private void fetchPublicRemindersFromFirebase() {
        DatabaseReference publicRemindersRef = FirebaseDatabase.getInstance().getReference().child("PublicReminders");

        // Show the progress bar for the dayTextView and hide the TextView
        dayProgressBar.setVisibility(View.VISIBLE);
        dayTextView.setVisibility(View.GONE);

        publicRemindersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dayProgressBar.setVisibility(View.GONE); // Hide the progress bar
                dayTextView.setVisibility(View.VISIBLE); // Show the TextView

                publicReminderList.clear();
                for (DataSnapshot reminderSnapshot : dataSnapshot.getChildren()) {
                    for (DataSnapshot eventSnapshot : reminderSnapshot.getChildren()) {
                        PublicReminder publicReminder = eventSnapshot.getValue(PublicReminder.class);

                        if (publicReminder != null) {
                            // Parse the date from the key into a Date object
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
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors
                dayProgressBar.setVisibility(View.GONE); // Hide the progress bar
                dayTextView.setVisibility(View.VISIBLE); // Show the TextView
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

            // Show the progress bar for the triviaText and hide the TextView
            triviaProgressBar.setVisibility(View.VISIBLE);
            triviaText.setVisibility(View.GONE);

            randomTriviaReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String triviaContent = (dataSnapshot.exists()) ? dataSnapshot.child("titleContent").getValue(String.class) : "Error fetching trivia";
                    triviaText.setText(triviaContent);
                    triviaProgressBar.setVisibility(View.GONE); // Hide the progress bar
                    triviaText.setVisibility(View.VISIBLE); // Show the TextView
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    triviaText.setText("Error fetching trivia");
                    triviaProgressBar.setVisibility(View.GONE); // Hide the progress bar
                    triviaText.setVisibility(View.VISIBLE); // Show the TextView
                }
            });
        } else {
            triviaText.setText("No trivia available");
            triviaProgressBar.setVisibility(View.GONE); // Hide the progress bar
            triviaText.setVisibility(View.VISIBLE); // Show the TextView
        }
    }
}

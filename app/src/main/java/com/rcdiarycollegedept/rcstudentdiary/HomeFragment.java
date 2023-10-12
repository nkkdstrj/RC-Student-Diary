package com.rcdiarycollegedept.rcstudentdiary;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class HomeFragment extends Fragment {

    private TextView NameTextView;
    private TextView dayTextView;
    private TextView dateTextView;
    private TextView triviaText;
    private DatabaseReference databaseReference;
    private DatabaseReference triviaReference;

    private List<Integer> usedTriviaIndices = new ArrayList<>();
    private int totalTriviaItems = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        NameTextView = rootView.findViewById(R.id.NameTextView);
        dayTextView = rootView.findViewById(R.id.dayTextView);
        dateTextView = rootView.findViewById(R.id.dateTextView);
        triviaText = rootView.findViewById(R.id.triviaText);

        // Get the reference to the Firebase Realtime Database node for FullName
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(getUserStudentNumber()).child("FullName");

        // Get the reference to the Firebase Realtime Database node for Trivia
        triviaReference = FirebaseDatabase.getInstance().getReference().child("Trivia");

        // Fetch the total number of trivia items
        fetchTotalTriviaItemsCount();

        // Call the method to retrieve data from the database
        fetchDataFromFirebase();

        // Set the current day and date
        setDayTextView();
        setDateTextView();

        return rootView;
    }

    private void setDayTextView() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
        String dayOfWeek = dateFormat.format(calendar.getTime());
        dayTextView.setText(dayOfWeek);
    }

    private void setDateTextView() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM d, yyyy", Locale.getDefault());
        String formattedDate = dateFormat.format(calendar.getTime());
        dateTextView.setText(formattedDate);
    }

    private String getUserStudentNumber() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String email = user.getEmail();
            // Extract student number from the email address (assuming it's in the format "studentnumber@example.com")
            return email != null ? email.split("@")[0] : "";
        }
        return "";
    }

    private void fetchDataFromFirebase() {
        // Read data from the Firebase database
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String fullName = dataSnapshot.getValue(String.class);
                    // Update the TextView with the retrieved data
                    NameTextView.setText(fullName);
                } else {
                    // Handle the case where FullName doesn't exist in the database
                    NameTextView.setText("Full name not found");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle the error if the data retrieval is canceled
                NameTextView.setText("Error fetching data");
            }
        });
    }

    private void fetchTotalTriviaItemsCount() {
        triviaReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Calculate the total number of trivia items in the database
                    totalTriviaItems = (int) dataSnapshot.getChildrenCount();

                    // Now, you have the correct totalTriviaItems count
                    // Fetch and display random trivia after getting the count
                    fetchRandomTrivia();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle the error if the data retrieval is canceled
                totalTriviaItems = 0; // Set to 0 in case of an error
                // Fetch and display random trivia with a count of 0
                fetchRandomTrivia();
            }
        });
    }

    private void fetchRandomTrivia() {
        if (usedTriviaIndices.size() == totalTriviaItems) {
            // All trivia items have been displayed, reset the usedTriviaIndices
            usedTriviaIndices.clear();
        }

        // Generate a random index for a trivia item
        int randomIndex;
        if (totalTriviaItems > 0) {
            do {
                randomIndex = new Random().nextInt(totalTriviaItems);
            } while (usedTriviaIndices.contains(randomIndex));
        } else {
            randomIndex = -1; // No trivia items available
        }

        if (randomIndex != -1) {
            // Mark the index as used
            usedTriviaIndices.add(randomIndex);

            // Construct the key for the selected trivia item (e.g., "T1", "T2", ...)
            String triviaKey = "T" + (randomIndex + 1); // Adding 1 to convert to 1-based index

            // Fetch the selected trivia item from the database
            triviaReference.child(triviaKey).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // Retrieve the trivia content from the dataSnapshot
                        String triviaContent = dataSnapshot.child("titleContent").getValue(String.class);

                        // Update the TextView with the retrieved trivia content
                        triviaText.setText(triviaContent);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle the error if the data retrieval is canceled
                    triviaText.setText("Error fetching trivia");
                }
            });
        } else {
            // Handle the case where there are no trivia items
            triviaText.setText("No trivia available");
        }
    }
}

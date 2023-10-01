package com.rcdiarycollegedept.rcstudentdiary;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class LoginActivity extends AppCompatActivity {

    EditText email, passWord;
    Button btn;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = findViewById(R.id.emailLogin);
        passWord = findViewById(R.id.editTextPassword);
        btn = findViewById(R.id.LoginButton);
        mAuth = FirebaseAuth.getInstance();

        btn.setOnClickListener(view -> loginUser());
    }

    private void loginUser() {
        String userEmail = email.getText().toString();
        String userPassword = passWord.getText().toString();

        if (TextUtils.isEmpty(userEmail) || TextUtils.isEmpty(userPassword)) {
            Toast.makeText(LoginActivity.this, "Please fill in all the details.", Toast.LENGTH_SHORT).show();
        } else {
            // Check if the email contains "@" symbol, if not, add "@example.com" to it.
            if (!userEmail.contains("@")) {
                userEmail += "@example.com";
            }
            signInUser(userEmail, userPassword);
        }
    }

    private void signInUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        Toast.makeText(LoginActivity.this, "Login Successful.", Toast.LENGTH_SHORT).show();

                        // After successful login, check for upcoming events.
                        checkForUpcomingEvents(user);

                        startActivity(new Intent(LoginActivity.this, MainActivity.class)
                                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                    } else {
                        Toast.makeText(LoginActivity.this, "Login failed. Please check your email and password.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void checkForUpcomingEvents(FirebaseUser user) {
        if (user != null) {
            // Get the current date in the format used in your CalendarFragment (e.g., "yyyyMMdd").
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
            String currentDate = dateFormat.format(calendar.getTime());

            // Check if there are events for the current date in the Firebase Realtime Database.
            DatabaseReference eventsReference = FirebaseDatabase.getInstance().getReference("Calendar")
                    .child(user.getUid())
                    .child(currentDate);

            eventsReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        // If events exist, create and display a notification.
                        String event = snapshot.getValue(String.class);
                        showNotification("Reminder", "Event today: " + event);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle database read error if needed.
                }
            });
        }
    }

    @SuppressLint("MissingPermission")
    private void showNotification(String title, String message) {
        final String CHANNEL_ID = "YourChannelID";
        final String CHANNEL_NAME = "YourChannelName";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("Your channel description");

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.baseline_notifications_24) // Set your notification icon here.
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        notificationManagerCompat.notify(/* Unique notification ID */ 1, builder.build());
    }
}
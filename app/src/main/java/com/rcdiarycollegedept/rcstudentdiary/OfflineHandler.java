package com.rcdiarycollegedept.rcstudentdiary;

import android.app.Application;
import com.google.firebase.database.FirebaseDatabase;

public class OfflineHandler extends Application{

    @Override
    public void onCreate() {
        super.onCreate();

        // Enable Firebase Realtime Database offline data persistence
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}

package com.rcdiarycollegedept.rcstudentdiary;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

public class NoteFragment extends Fragment {

    private FloatingActionButton addReminder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_note, container, false);

        initializeViews(view);

        return view;
    }

    private void initializeViews(View view) {
        addReminder = view.findViewById(R.id.addNote);
        addReminder.setOnClickListener(v -> showReminderDialog());
    }

    private void showReminderDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = getLayoutInflater().inflate(R.layout.add_note_dialog, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();

        EditText noteTitle = dialogView.findViewById(R.id.noteTitle);
        TextInputEditText noteContent = dialogView.findViewById(R.id.editTextNote);

        Button saveButton = dialogView.findViewById(R.id.dialog_save);
        Button cancelButton = dialogView.findViewById(R.id.dialog_cancel);

        saveButton.setOnClickListener(view -> {

            String noteSub = noteTitle.getText().toString();
            String noteCon = noteContent.getText().toString();


        });

        cancelButton.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }
}
package com.rcdiarycollegedept.rcstudentdiary;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NoteFragment extends Fragment implements NoteAdapter.OnNoteClickListener {

    private FloatingActionButton addNotes;
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;

    // Create a list to hold the notes
    private List<Note> notesList = new ArrayList<>();
    private NoteAdapter adapter; // Create an adapter for the RecyclerView

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_note, container, false);

        initializeViews(view);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            databaseReference = FirebaseDatabase.getInstance().getReference("Notes")
                    .child(currentUser.getUid());
        }

        // Initialize the RecyclerView and adapter
        RecyclerView recyclerView = view.findViewById(R.id.noteRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new NoteAdapter(notesList, this); // Pass the fragment as the click listener
        recyclerView.setAdapter(adapter);

        // Load notes from Firebase (add ValueEventListener only once)
        loadNotesFromFirebase();

        return view;
    }

    private void initializeViews(View view) {
        addNotes = view.findViewById(R.id.addNote);
        addNotes.setOnClickListener(v -> showReminderDialog());
    }

    private void showReminderDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = getLayoutInflater().inflate(R.layout.add_note_dialog, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();

        EditText noteTitle = dialogView.findViewById(R.id.noteTitle);
        TextInputEditText noteContent = dialogView.findViewById(R.id.editTextNote);
        TextInputLayout noteContentLayout = dialogView.findViewById(R.id.textInputLayout);

        Button saveButton = dialogView.findViewById(R.id.dialog_save);
        Button cancelButton = dialogView.findViewById(R.id.dialog_cancel);

        saveButton.setOnClickListener(view -> {
            String noteSub = noteTitle.getText().toString();
            String noteCon = noteContent.getText().toString();

            // Check if both the note title and content are not empty
            if (!noteSub.isEmpty() && !noteCon.isEmpty()) {
                // Get the current user
                FirebaseUser currentUser = mAuth.getCurrentUser();
                if (currentUser != null) {
                    String userId = currentUser.getUid();

                    // Create a reference to the "Notes" node directly under the user's UID
                    DatabaseReference userNotesRef = FirebaseDatabase.getInstance().getReference("Notes")
                            .child(userId);

                    // Generate a unique key for the new note
                    String noteId = userNotesRef.push().getKey();

                    // Create a Map to store the note data
                    Map<String, Object> noteData = new HashMap<>();
                    noteData.put("noteTitle", noteSub);
                    noteData.put("noteContent", noteCon);

                    // Save the note directly under the user's UID
                    userNotesRef.child(noteId).setValue(noteData);

                    // Dismiss the dialog
                    dialog.dismiss();

                    // Clear input fields
                    noteTitle.setText("");
                    noteContentLayout.getEditText().setText(""); // Clear the text using the TextInputLayout
                }
            } else {
                // Handle empty fields here, for example, show an error message
                Toast.makeText(requireContext(), "Please fill in both title and content", Toast.LENGTH_SHORT).show();
            }
        });

        cancelButton.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    // Load notes from Firebase and add them to the notesList
    private void loadNotesFromFirebase() {
        notesList.clear(); // Clear the list before loading

        if (databaseReference != null) {
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    notesList.clear(); // Clear the list before adding new data

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Note note = snapshot.getValue(Note.class);
                        if (note != null) {
                            notesList.add(note);
                        }
                    }

                    // Notify the adapter of data change
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle database read error if needed
                }
            });
        }
    }

    @Override
    public void onNoteClick(int position) {
        Note selectedNote = notesList.get(position);
        openEditNoteDialog(selectedNote);
    }

    private void openEditNoteDialog(Note note) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = getLayoutInflater().inflate(R.layout.edit_note_dialog, null);
        builder.setView(dialogView);

        // Initialize views in the edit_note_dialog and populate them with note data
        EditText noteTitle = dialogView.findViewById(R.id.noteTitle);
        TextInputEditText noteContent = dialogView.findViewById(R.id.editTextNote);
        Button saveButton = dialogView.findViewById(R.id.dialog_save);
        Button cancelButton = dialogView.findViewById(R.id.dialog_cancel);
        noteTitle.setText(note.getNoteTitle());
        noteContent.setText(note.getNoteContent());

        AlertDialog dialog = builder.create();

        saveButton.setOnClickListener(view -> {

        });

        cancelButton.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }
}

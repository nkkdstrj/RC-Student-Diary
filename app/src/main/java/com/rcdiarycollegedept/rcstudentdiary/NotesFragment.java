package com.rcdiarycollegedept.rcstudentdiary;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rcdiarycollegedept.rcstudentdiary.databinding.FragmentNotesBinding;
import com.rcdiarycollegedept.rcstudentdiary.databinding.AddNoteDialogBinding;
import com.rcdiarycollegedept.rcstudentdiary.databinding.EditNoteDialogBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NotesFragment extends Fragment {

    private FragmentNotesBinding binding;
    private NoteAdapter notesAdapter;
    private List<Note> noteList;

    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentNotesBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            databaseReference = FirebaseDatabase.getInstance().getReference("Notes")
                    .child(currentUser.getUid());
        }

        initView();

        initRecyclerView();

        retrieveNotesFromFirebase();

        return view;
    }

    private void initView() {
        // Initialize views using View Binding
        binding.addNote.setOnClickListener(v -> showAddNoteDialog());
    }

    private void initRecyclerView() {
        // Initialize RecyclerView and its adapter
        noteList = new ArrayList<>();
        notesAdapter = new NoteAdapter(requireContext(), noteList, new NoteAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Note note) {
                // Handle item click (e.g., edit the note)
                openEditNoteDialog(note);
            }

            @Override
            public void onDeleteClick(Note note) {
                // Handle delete click
                deleteNoteFromFirebase(note);
            }
        });
        binding.noteRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.noteRecyclerView.setAdapter(notesAdapter);
    }

    private void showAddNoteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        AddNoteDialogBinding dialogBinding = AddNoteDialogBinding.inflate(getLayoutInflater());
        builder.setView(dialogBinding.getRoot());

        AlertDialog dialog = builder.create();

        dialogBinding.dialogSave.setOnClickListener(view -> {
            String noteSub = dialogBinding.noteTitle.getText().toString();
            String noteCon = dialogBinding.editTextNote.getText().toString();

            if (!noteSub.isEmpty() && !noteCon.isEmpty()) {
                FirebaseUser currentUser = mAuth.getCurrentUser();
                if (currentUser != null) {
                    String userId = currentUser.getUid();
                    DatabaseReference userNotesRef = FirebaseDatabase.getInstance().getReference("Notes")
                            .child(userId);
                    String noteId = userNotesRef.push().getKey();

                    Map<String, Object> noteData = new HashMap<>();
                    noteData.put("noteTitle", noteSub);
                    noteData.put("noteContent", noteCon);

                    userNotesRef.child(noteId).setValue(noteData);

                    dialog.dismiss();

                    // Clear input fields
                    dialogBinding.noteTitle.setText("");
                    dialogBinding.editTextNote.setText("");
                }
            } else {
                handleEmptyFields();
            }
        });

        dialogBinding.dialogCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void openEditNoteDialog(Note note) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        EditNoteDialogBinding dialogBinding = EditNoteDialogBinding.inflate(getLayoutInflater());
        builder.setView(dialogBinding.getRoot());

        AlertDialog dialog = builder.create();

        dialogBinding.noteTitle.setText(note.getNoteTitle());
        dialogBinding.editTextNote.setText(note.getNoteContent());

        dialogBinding.dialogSave.setOnClickListener(view -> {
            String updatedNoteTitle = dialogBinding.noteTitle.getText().toString();
            String updatedNoteContent = dialogBinding.editTextNote.getText().toString();

            if (!updatedNoteTitle.isEmpty() && !updatedNoteContent.isEmpty()) {
                FirebaseUser currentUser = mAuth.getCurrentUser();
                if (currentUser != null) {
                    String userId = currentUser.getUid();

                    if (note.getNoteId() != null) {
                        DatabaseReference updatedNoteRef = FirebaseDatabase.getInstance().getReference("Notes")
                                .child(userId)
                                .child(note.getNoteId());

                        Map<String, Object> updatedNoteData = new HashMap<>();
                        updatedNoteData.put("noteTitle", updatedNoteTitle);
                        updatedNoteData.put("noteContent", updatedNoteContent);

                        updatedNoteRef.updateChildren(updatedNoteData);

                        dialog.dismiss();
                    }
                }
            } else {
                handleEmptyFields();
            }
        });

        dialogBinding.dialogCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void deleteNoteFromFirebase(Note note) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null && note.getNoteId() != null) {
            String userId = currentUser.getUid();
            DatabaseReference noteRef = FirebaseDatabase.getInstance().getReference("Notes")
                    .child(userId)
                    .child(note.getNoteId());

            noteRef.removeValue().addOnSuccessListener(aVoid -> {
                // Note deleted successfully
                Toast.makeText(requireContext(), "Note deleted", Toast.LENGTH_SHORT).show();
            }).addOnFailureListener(e -> {
                // Handle the failure to delete the note
                Toast.makeText(requireContext(), "Failed to delete note", Toast.LENGTH_SHORT).show();
            });
        }
    }

    private void retrieveNotesFromFirebase() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                noteList.clear();
                for (DataSnapshot noteSnapshot : dataSnapshot.getChildren()) {
                    Note note = noteSnapshot.getValue(Note.class);
                    if (note != null) {
                        note.setNoteId(noteSnapshot.getKey());
                        noteList.add(note);
                    }
                }
                notesAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void handleEmptyFields() {
        Toast.makeText(requireContext(), "Please fill in both title and content", Toast.LENGTH_SHORT).show();
    }
}

package com.rcdiarycollegedept.rcstudentdiary;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {
    private List<Note> notes;
    private OnNoteClickListener onNoteClickListener; // Define a click listener interface

    public interface OnNoteClickListener {
        void onNoteClick(int position);
    }

    public NoteAdapter(List<Note> notes, OnNoteClickListener onNoteClickListener) {
        this.notes = notes;
        this.onNoteClickListener = onNoteClickListener;
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the note item layout and return a new ViewHolder
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.note_item, parent, false);
        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        // Bind data to the ViewHolder's views for each item
        Note note = notes.get(position);
        holder.titleNote.setText(note.getNoteTitle());
        holder.contentNote.setText(note.getNoteContent());
    }

    @Override
    public int getItemCount() {
        // Return the number of items in the data list
        return notes.size();
    }

    public class NoteViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView titleNote;
        public TextView contentNote;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            // Initialize views and set click listener on the item view
            titleNote = itemView.findViewById(R.id.titleNote);
            contentNote = itemView.findViewById(R.id.contentNote);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            // Handle item click by notifying the listener
            if (onNoteClickListener != null) {
                onNoteClickListener.onNoteClick(getAdapterPosition());
            }
        }
    }
}

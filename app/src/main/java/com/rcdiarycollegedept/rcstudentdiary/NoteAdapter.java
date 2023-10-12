package com.rcdiarycollegedept.rcstudentdiary;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.ViewHolder> {

    private List<Note> noteList;
    private Context context;

    // Add an interface for item click handling
    public interface OnItemClickListener {
        void onItemClick(Note note);
        void onDeleteClick(Note note);
    }

    private OnItemClickListener itemClickListener;

    public NoteAdapter(Context context, List<Note> noteList, OnItemClickListener itemClickListener) {
        this.context = context;
        this.noteList = noteList;
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.note_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Note note = noteList.get(position);
        holder.titleNote.setText(note.getNoteTitle());
        holder.contentNote.setText(note.getNoteContent());

        // Set a click listener for the delete button
        holder.imageViewDelete.setOnClickListener(v -> {
            if (itemClickListener != null) {
                itemClickListener.onDeleteClick(note);
            }
        });

        // Set a click listener for the entire note item
        holder.itemView.setOnClickListener(v -> {
            if (itemClickListener != null) {
                itemClickListener.onItemClick(note);
            }
        });
    }

    @Override
    public int getItemCount() {
        return noteList.size();
    }

    // ViewHolder class
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleNote;
        TextView contentNote;
        ImageView imageViewDelete;

        public ViewHolder(View itemView) {
            super(itemView);
            titleNote = itemView.findViewById(R.id.titleNote);
            contentNote = itemView.findViewById(R.id.contentNote);
            imageViewDelete = itemView.findViewById(R.id.imageViewDelete);
        }
    }
}

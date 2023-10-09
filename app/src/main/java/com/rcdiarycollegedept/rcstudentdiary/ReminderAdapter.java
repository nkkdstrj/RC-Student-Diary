package com.rcdiarycollegedept.rcstudentdiary;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ReminderAdapter extends RecyclerView.Adapter<ReminderAdapter.ViewHolder> {
    public List<Reminder> reminders;
    private OnReminderDeleteListener onDeleteListener;

    private OnReminderEditListener onEditListener;// Callback interface

    public interface OnReminderDeleteListener {
        void onDeleteReminder(Reminder reminder);
    }
    public interface OnReminderEditListener {
        void onEditReminder(Reminder reminder);
    }

    public ReminderAdapter(OnReminderDeleteListener onDeleteListener, OnReminderEditListener onEditListener) {
        reminders = new ArrayList<>();
        this.onDeleteListener = onDeleteListener;
        this.onEditListener = onEditListener;
    }

    public void addReminder(Reminder reminder) {
        reminders.add(reminder);
        notifyItemInserted(reminders.size() - 1);
    }

    public void clear() {
        reminders.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.reminder_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Reminder reminder = reminders.get(position);

        // Bind data to TextViews in the ViewHolder
        holder.eventNameTextView.setText(reminder.getEventName());
        holder.eventTimeTextView.setText(reminder.getEventTime());

        // Format the date and set it to textViewDate
        String formattedDate = formatDate(reminder.getDate());
        holder.dateTextView.setText(formattedDate);

        holder.imageViewMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onEditListener.onEditReminder(reminder);
            }
        });

        // Set click listener for the delete ImageView
        holder.imageViewDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDeleteListener.onDeleteReminder(reminder);
            }
        });
    }

    // Create a method to format the date
    private String formatDate(String date) {
        try {
            SimpleDateFormat inputDateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
            SimpleDateFormat outputDateFormat = new SimpleDateFormat("MMMM d, yyyy", Locale.getDefault());

            Date parsedDate = inputDateFormat.parse(date);
            return outputDateFormat.format(parsedDate);
        } catch (ParseException e) {
            e.printStackTrace();
            return date; // Return the original date if formatting fails
        }
    }

    @Override
    public int getItemCount() {
        return reminders.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView eventNameTextView;
        TextView eventTimeTextView;
        TextView dateTextView;
        ImageView imageViewDelete, imageViewMore;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            eventNameTextView = itemView.findViewById(R.id.textViewEventName);
            eventTimeTextView = itemView.findViewById(R.id.textViewEventTime);
            dateTextView = itemView.findViewById(R.id.textViewDate);
            imageViewDelete = itemView.findViewById(R.id.imageViewDelete);
            imageViewMore = itemView.findViewById(R.id.imageViewMore);

        }
    }
}

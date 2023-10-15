package com.rcdiarycollegedept.rcstudentdiary;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class PublicReminderAdapter extends RecyclerView.Adapter<PublicReminderAdapter.PublicReminderViewHolder> {

    private List<PublicReminder> publicReminderList;
    private Context context; // Added context for AlertDialog

    public PublicReminderAdapter(Context context, List<PublicReminder> publicReminderList) {
        this.context = context; // Initialize context
        this.publicReminderList = publicReminderList;
    }

    @Override
    public PublicReminderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.public_reminder_item, parent, false);
        return new PublicReminderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PublicReminderViewHolder holder, int position) {
        PublicReminder publicReminder = publicReminderList.get(position);

        holder.eventNameTextView.setText(publicReminder.getEventName());
        holder.eventTimeTextView.setText(publicReminder.getEventTime());

        // Format the date to "Month day, year"
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM d, yyyy", Locale.getDefault());
        String formattedDate = dateFormat.format(publicReminder.getDate());
        holder.dateTextView.setText(formattedDate);

        // Set an OnClickListener for the item view
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Call a method to display the dialog with the public reminder details
                showPublicReminderDialog(publicReminder);
            }
        });
    }

    @Override
    public int getItemCount() {
        return publicReminderList.size();
    }

    static class PublicReminderViewHolder extends RecyclerView.ViewHolder {
        TextView eventNameTextView;
        TextView eventTimeTextView;
        TextView dateTextView;

        PublicReminderViewHolder(View itemView) {
            super(itemView);
            eventNameTextView = itemView.findViewById(R.id.textViewEventName);
            eventTimeTextView = itemView.findViewById(R.id.textViewEventTime);
            dateTextView = itemView.findViewById(R.id.textViewDate);
        }
    }
    private void showPublicReminderDialog(PublicReminder publicReminder) {
        // Create and display the dialog using AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.public_reminder_info_dialog, null);

        // Get references to the TextViews in the dialog layout
        TextView publicReminderTitle = dialogView.findViewById(R.id.publicReminderTitle);
        TextView publicReminderInfo = dialogView.findViewById(R.id.publicReminderInfo);
        ImageView closeButton = dialogView.findViewById(R.id.imageViewDelete);

        // Set data in the dialog view
        publicReminderTitle.setText(publicReminder.getEventName());
        publicReminderInfo.setText(publicReminder.getEventInfo());

        builder.setView(dialogView);

        // Create the AlertDialog
        final AlertDialog dialog = builder.create();

        // Add an OnClickListener to the closeButton to dismiss the dialog
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }
}

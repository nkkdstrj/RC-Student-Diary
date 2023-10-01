package com.rcdiarycollegedept.rcstudentdiary;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SettingsFragment extends Fragment {

    private Button signOutButton;
    private Button changePassButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        // Initialize the "Sign Out" button
        signOutButton = view.findViewById(R.id.sOut);

        // Initialize the "Change Password" button
        changePassButton = view.findViewById(R.id.changePass);

        // Set an OnClickListener for the "Sign Out" button to show the sign-out dialog
        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSignOutDialog();
            }
        });

        // Set an OnClickListener for the "Change Password" button to show the password change dialog
        changePassButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPasswordChangeDialog();
            }
        });

        return view;
    }

    // Method to show the sign-out dialog
    private void showSignOutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_layout, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();

        // Find the dialog views
        TextView dialogText = dialogView.findViewById(R.id.dialog_text);
        Button confirmButton = dialogView.findViewById(R.id.dialog_save);
        Button cancelButton = dialogView.findViewById(R.id.dialog_cancel);

        // Set OnClickListener for the confirm button
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Perform logout action here, e.g., clear user session/token
                // For example:
                // LogoutUtils.logoutCurrentUser(requireContext());

                // Navigate to the login screen (LoginActivity)
                Intent loginIntent = new Intent(requireContext(), LoginActivity.class);
                loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(loginIntent);

                // Close the SettingsFragment (optional)
                requireActivity().finish();

                // Dismiss the dialog
                dialog.dismiss();
            }
        });

        // Set OnClickListener for the cancel button
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Dismiss the dialog if the user cancels
                dialog.dismiss();
            }
        });

        dialog.show();
    }


    // Method to show the password change dialog
    private void showPasswordChangeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = getLayoutInflater().inflate(R.layout.ch_pass_diag_layout, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();

        // Find the dialog views
        EditText currentPasswordEditText = dialogView.findViewById(R.id.dialog_current_password);
        EditText newPasswordEditText = dialogView.findViewById(R.id.dialog_new_password);
        EditText confirmPasswordEditText = dialogView.findViewById(R.id.dialog_confirm_password);
        Button confirmButton = dialogView.findViewById(R.id.dialog_save);
        Button cancelButton = dialogView.findViewById(R.id.dialog_cancel);

        // Set OnClickListener for the confirm button
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the entered passwords
                String currentPassword = currentPasswordEditText.getText().toString();
                String newPassword = newPasswordEditText.getText().toString();
                String confirmPassword = confirmPasswordEditText.getText().toString();

                // Validate and perform the password change action
                if (newPassword.equals(confirmPassword)) {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                    if (user != null) {
                        // Reauthenticate the user to confirm the current password
                        AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), currentPassword);
                        user.reauthenticate(credential)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        // Password reauthentication successful, now change the password
                                        user.updatePassword(newPassword)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Toast.makeText(requireContext(), "Password updated successfully", Toast.LENGTH_SHORT).show();

                                                        // Log out the user
                                                        FirebaseAuth.getInstance().signOut();

                                                        // Navigate to the login screen (LoginActivity)
                                                        Intent loginIntent = new Intent(requireContext(), LoginActivity.class);
                                                        loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                        startActivity(loginIntent);

                                                        // Close the SettingsFragment (optional)
                                                        requireActivity().finish();

                                                        dialog.dismiss();
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(requireContext(), "Password update failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(requireContext(), "Reauthentication failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                } else {
                    Toast.makeText(requireContext(), "New password and confirm password do not match", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Set OnClickListener for the cancel button
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Dismiss the dialog if the user cancels
                dialog.dismiss();
            }
        });

        dialog.show();
    }
}
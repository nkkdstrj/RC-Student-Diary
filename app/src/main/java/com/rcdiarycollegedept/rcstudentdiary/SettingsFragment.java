package com.rcdiarycollegedept.rcstudentdiary;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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

        signOutButton = view.findViewById(R.id.sOut);
        changePassButton = view.findViewById(R.id.changePass);

        signOutButton.setOnClickListener(v -> showSignOutDialog());
        changePassButton.setOnClickListener(v -> showPasswordChangeDialog());

        return view;
    }

    private void showSignOutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_layout, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();

        Button confirmButton = dialogView.findViewById(R.id.dialog_save);
        Button cancelButton = dialogView.findViewById(R.id.dialog_cancel);

        confirmButton.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            navigateToLoginActivity();
            dialog.dismiss();
        });

        cancelButton.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void showPasswordChangeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = getLayoutInflater().inflate(R.layout.ch_pass_diag_layout, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();

        EditText currentPasswordEditText = dialogView.findViewById(R.id.dialog_current_password);
        EditText newPasswordEditText = dialogView.findViewById(R.id.dialog_new_password);
        EditText confirmPasswordEditText = dialogView.findViewById(R.id.dialog_confirm_password);
        Button confirmButton = dialogView.findViewById(R.id.dialog_save);
        Button cancelButton = dialogView.findViewById(R.id.dialog_cancel);

        confirmButton.setOnClickListener(v -> {
            String currentPassword = currentPasswordEditText.getText().toString();
            String newPassword = newPasswordEditText.getText().toString();
            String confirmPassword = confirmPasswordEditText.getText().toString();

            if (newPassword.equals(confirmPassword)) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                if (user != null) {
                    AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), currentPassword);
                    user.reauthenticate(credential)
                            .addOnSuccessListener(aVoid -> {
                                user.updatePassword(newPassword)
                                        .addOnSuccessListener(aVoid1 -> {
                                            showToast("Password updated successfully");
                                            FirebaseAuth.getInstance().signOut();
                                            navigateToLoginActivity();
                                            dialog.dismiss();
                                        })
                                        .addOnFailureListener(e -> showToast("Password update failed: " + e.getMessage()));
                            })
                            .addOnFailureListener(e -> showToast("Reauthentication failed: " + e.getMessage()));
                }
            } else {
                showToast("New password and confirm password do not match");
            }
        });

        cancelButton.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void navigateToLoginActivity() {
        Intent loginIntent = new Intent(requireContext(), LoginActivity.class);
        loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        requireActivity().finish();
    }

    private void showToast(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }
}
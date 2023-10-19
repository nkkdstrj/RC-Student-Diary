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
import androidx.fragment.app.Fragment;
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

        initializeViews(view);
        setClickListeners();

        return view;
    }

    private void initializeViews(View view) {
        signOutButton = view.findViewById(R.id.sOut);
        changePassButton = view.findViewById(R.id.changePass);
    }

    private void setClickListeners() {
        signOutButton.setOnClickListener(v -> showSignOutDialog());
        changePassButton.setOnClickListener(v -> showPasswordChangeDialog());
    }

    private void showSignOutDialog() {
        AlertDialog.Builder builder = createDialogBuilder(R.layout.dialog_layout);
        AlertDialog dialog = builder.create();

        setDialogButtons(dialog, () -> {
            FirebaseAuth.getInstance().signOut();
            navigateToLoginActivity();
            dialog.dismiss();
        }, dialog::dismiss);

        dialog.show();
    }

    private void showPasswordChangeDialog() {
        AlertDialog.Builder builder = createDialogBuilder(R.layout.ch_pass_diag_layout);
        AlertDialog dialog = builder.create();

        EditText currentPasswordEditText = dialog.findViewById(R.id.dialog_current_password);
        EditText newPasswordEditText = dialog.findViewById(R.id.dialog_new_password);
        EditText confirmPasswordEditText = dialog.findViewById(R.id.dialog_confirm_password);

        setDialogButtons(dialog, () -> {
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
        }, dialog::dismiss);

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

    private AlertDialog.Builder createDialogBuilder(int layoutId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = getLayoutInflater().inflate(layoutId, null);
        builder.setView(dialogView);
        return builder;
    }

    private void setDialogButtons(AlertDialog dialog, Runnable onConfirm, Runnable onCancel) {
        Button confirmButton = dialog.findViewById(R.id.dialog_save);
        Button cancelButton = dialog.findViewById(R.id.dialog_cancel);

        confirmButton.setOnClickListener(v -> {
            onConfirm.run();
        });

        cancelButton.setOnClickListener(v -> {
            onCancel.run();
        });
    }
}

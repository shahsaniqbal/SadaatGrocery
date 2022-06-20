package com.sadaat.groceryapp.ui.Fragments.UserBased.Customers.UnderSettingsFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.sadaat.groceryapp.R;

import java.util.Objects;

public class PasswordChangeFragmentCustomer extends Fragment {

    TextInputEditText edxPass;
    TextInputEditText edxNewPass;
    TextInputEditText edxConfirmNewPass;
    MaterialButton btnUpdate;
    CoordinatorLayout coordinatorLayout;

    public PasswordChangeFragmentCustomer() {
        // Required empty public constructor
    }

    public static PasswordChangeFragmentCustomer newInstance() {

        return new PasswordChangeFragmentCustomer();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.customer_fragment_change_password, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        edxPass = view.findViewById(R.id.oldPass);
        edxNewPass = view.findViewById(R.id.newPass);
        edxConfirmNewPass = view.findViewById(R.id.newPassConfirm);
        btnUpdate = view.findViewById(R.id.btn_update);
        coordinatorLayout = view.findViewById(R.id.coordinator);

        btnUpdate.setOnClickListener(v -> {
            if (analyzeInputs(true)) {
                FirebaseUser user;
                user = FirebaseAuth.getInstance().getCurrentUser();
                final String email = Objects.requireNonNull(user).getEmail();
                AuthCredential credential = EmailAuthProvider.getCredential(email, edxPass.getText().toString());

                user.reauthenticate(credential).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        user.updatePassword(Objects.requireNonNull(edxNewPass.getText()).toString()).addOnCompleteListener(task1 -> {
                            if (!task1.isSuccessful()) {
                                Snackbar snackbar_fail = Snackbar
                                        .make(coordinatorLayout, "Something went wrong. Please try again later", Snackbar.LENGTH_LONG);
                                snackbar_fail.show();
                            } else {
                                Snackbar snackbar_su = Snackbar
                                        .make(coordinatorLayout, "Password Successfully Modified", Snackbar.LENGTH_LONG);
                                snackbar_su.show();

                                PasswordChangeFragmentCustomer.this.requireActivity().getSupportFragmentManager().popBackStack();
                            }
                        });
                    } else {
                        Snackbar snackbar_su = Snackbar
                                .make(coordinatorLayout, "Authentication Failed", Snackbar.LENGTH_LONG);
                        snackbar_su.show();
                    }
                });

            }
        });
    }

    private boolean analyzeInputs(boolean b) {

        if (Objects.requireNonNull(edxPass.getText()).toString().isEmpty()) {
            edxPass.setError("Please Enter Valid Old Password");
            b = false;
        } else if (Objects.requireNonNull(edxNewPass.getText()).toString().isEmpty()) {
            edxNewPass.setError("Please Enter Valid New Password");
            b = false;
        } else if (Objects.requireNonNull(edxConfirmNewPass.getText()).toString().isEmpty()) {
            edxConfirmNewPass.setError("Please Re-enter new Password");
            b = false;
        } else if (!(edxConfirmNewPass.getText().toString().equals(edxNewPass.getText().toString()))) {
            Snackbar snackbar_su = Snackbar
                    .make(coordinatorLayout, "Passwords do not match.", Snackbar.LENGTH_LONG);
            snackbar_su.show();
            b = false;
        } else {
            b = true;
        }

        return b;
    }
}
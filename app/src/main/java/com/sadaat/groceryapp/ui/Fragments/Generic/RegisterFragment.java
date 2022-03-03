package com.sadaat.groceryapp.ui.Fragments.Generic;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sadaat.groceryapp.R;
import com.sadaat.groceryapp.handler.LoginIntentHandler;
import com.sadaat.groceryapp.models.UserModel;
import com.sadaat.groceryapp.temp.FirebaseDataKeys;
import com.sadaat.groceryapp.temp.UserLive;
import com.sadaat.groceryapp.temp.UserTypes;
import com.sadaat.groceryapp.ui.Activities.UsersBased.Admin.MainActivityAdmin;
import com.sadaat.groceryapp.ui.Loaders.LoadingDialogue;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RegisterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegisterFragment extends Fragment {

    MaterialCardView openSignInFragment;
    EditText edxEmail;
    EditText edxName;
    EditText edxPass;
    EditText edxConfirmPass;
    EditText edxMobile;
    MaterialButton btnSignUp;

    LoadingDialogue loadingDialogue;


    public RegisterFragment() {
        // Required empty public constructor
    }

    public static RegisterFragment newInstance(String param1, String param2) {
        RegisterFragment fragment = new RegisterFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.generic_fragment_register, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializers(view);

        openSignInFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity()
                        .getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(
                                R.anim.slide_in,  // enter
                                R.anim.slide_out,  // exit
                                R.anim.slide_in,   // popEnter
                                R.anim.slide_out  // popExit
                        )
                        .replace(R.id.fragment_manager_at_login, LoginFragment.class, null)
                        .commit();
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (analyzeInputsIfEmpty(true)) {

                    loadingDialogue.show("Loading", "Creating New User");

                    // TODO 0001 Update User details Object instead of Null
                    // Code by AHSAN 08-02-2022 TUESDAY 14:12:00

                    FirebaseAuth
                            .getInstance()
                            .createUserWithEmailAndPassword(edxEmail.getText().toString(), edxPass.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        UserModel user = new UserModel(
                                                task.getResult().getUser().getUid(),
                                                UserTypes.Customer,
                                                edxName.getText().toString(),
                                                task.getResult().getUser().getEmail(),
                                                edxMobile.getText().toString(),
                                                null
                                        );

                                        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
                                        firebaseFirestore
                                                .collection(new FirebaseDataKeys().getUsersRef())
                                                .document(user.getUID())
                                                .set(user)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            Toast.makeText(requireActivity(), "User Registration Successful", Toast.LENGTH_SHORT).show();
                                                            UserLive.currentLoggedInUser = user;

                                                            Intent i = new LoginIntentHandler(getActivity(), user.getUserType());

                                                            loadingDialogue.dismiss();

                                                            startActivity(i);
                                                            requireActivity().finish();

                                                        } else {
                                                            Toast.makeText(requireActivity(), "" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                    }
                                    else {
                                        Toast.makeText(requireActivity(), "Error Signing UP \n" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                }
                else{
                    Toast.makeText(requireActivity(), "Issue Analyzing Inputs", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private boolean analyzeInputsIfEmpty(Boolean shouldAnalyze) {
        if (shouldAnalyze) {
            if (edxEmail.getText().toString().isEmpty()) {
                edxEmail.setError("Email Field is mandatory");
                shouldAnalyze = false;
            }
            if (edxName.getText().toString().isEmpty()) {
                edxName.setError("Name Field is mandatory");
                shouldAnalyze = false;
            }
            if (edxMobile.getText().toString().isEmpty()) {
                edxMobile.setError("Mobile Field is mandatory");
                shouldAnalyze = false;
            }
            if (edxPass.getText().toString().isEmpty() || edxConfirmPass.getText().toString().isEmpty()) {
                edxPass.setError("Password Field is mandatory");
                shouldAnalyze = false;
            }
            if (!(edxPass.getText().toString().equals(edxConfirmPass.getText().toString()))) {
                edxPass.setError("Password Fields don't match");
                shouldAnalyze = false;
            }
            if (edxPass.getText().toString().length() < 8) {
                edxPass.setError("Password must be >8 characters long");
                shouldAnalyze = false;
            }
        }
        return shouldAnalyze;
    }

    private void initializers(View v) {
        openSignInFragment = v.findViewById(R.id.btn_register_signIn);

        edxEmail = v.findViewById(R.id.edx_register_email);
        edxName = v.findViewById(R.id.edx_register_full_name);
        edxPass = v.findViewById(R.id.edx_register_pass);
        edxConfirmPass = v.findViewById(R.id.edx_register_confirm_pass);
        edxMobile = v.findViewById(R.id.edx_register_mobile);

        btnSignUp = v.findViewById(R.id.btn_register_register);

        loadingDialogue = new LoadingDialogue(requireActivity());
    }

}
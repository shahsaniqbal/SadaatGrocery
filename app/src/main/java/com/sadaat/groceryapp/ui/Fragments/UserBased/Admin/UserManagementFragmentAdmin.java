package com.sadaat.groceryapp.ui.Fragments.UserBased.Admin;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;

import androidx.annotation.GravityInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.sadaat.groceryapp.R;
import com.sadaat.groceryapp.adapters.UsersItemAdapterAdmin;
import com.sadaat.groceryapp.models.UserModel;
import com.sadaat.groceryapp.temp.FirebaseDataKeys;
import com.sadaat.groceryapp.temp.UserTypes;
import com.sadaat.groceryapp.ui.Loaders.LoadingDialogue;

import java.util.ArrayList;
import java.util.Objects;

public class UserManagementFragmentAdmin extends Fragment {

    FloatingActionButton addUsersButton;
    CustomPopupViewHolder viewHolder;

    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    RecyclerView recyclerView;
    RecyclerView.LayoutManager manager;
    UsersItemAdapterAdmin adapterAdmin;
    LoadingDialogue progressDialog;
    ArrayList<UserModel> allUsers;


    AlertDialog.Builder dialogueBuilder;
    AlertDialog userPopupDialogueBox;
    View popupView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.admin_fragment_user_management, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializer(view);

        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapterAdmin);

        backgroundExecutorForShowingData(view);

        addUsersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handlePopup(view);
            }
        });
    }

    private void handlePopup(View parent) {
        //parent.setVisibility(View.INVISIBLE);
        viewHolder = new CustomPopupViewHolder(popupView);
        userPopupDialogueBox.show();
        viewHolder.getBtnAdd().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewHolder.analyzeInputs(true)) {

                    progressDialog.show("Adding User", "Wait, Creating Delivery Boy Account");

                    FirebaseAuth
                            .getInstance()
                            .createUserWithEmailAndPassword(viewHolder.getTxvEmail().getText().toString(), viewHolder.getTxvPassword().getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {

                                        UserModel newUser = new UserModel(
                                                Objects.requireNonNull(task.getResult().getUser()).getUid(),
                                                UserTypes.DeliveryBoy,
                                                viewHolder.getTxvName().getText().toString(),
                                                viewHolder.getTxvEmail().getText().toString(),
                                                viewHolder.getTxvMobile().getText().toString(),
                                                null);

                                        firebaseFirestore
                                                .collection(new FirebaseDataKeys().getUsersRef())
                                                .document(Objects.requireNonNull(task.getResult().getUser()).getUid())
                                                .set(newUser)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {

                                                        parent.setVisibility(View.VISIBLE);

                                                        if (task.isSuccessful()) {
                                                            adapterAdmin.addUser(newUser);
                                                            viewHolder.setEmptyContentToViews();
                                                            progressDialog.dismiss();

                                                            userPopupDialogueBox.setCancelable(true);
                                                            userPopupDialogueBox.dismiss();

                                                        }
                                                    }
                                                });
                                    }
                                }
                            });
                }
            }
        });
    }

    private void initializer(View view) {
        allUsers = new ArrayList<>();
        addUsersButton = view.findViewById(R.id.btn_admin_add_user);
        recyclerView = view.findViewById(R.id.recycler_users_on_admin_screen);
        manager = new LinearLayoutManager(UserManagementFragmentAdmin.this.requireActivity());

        dialogueBuilder = new AlertDialog.Builder(requireActivity());

        popupView = this.getLayoutInflater().inflate(R.layout.admin_popup_add_users, null, false);
        dialogueBuilder.setView(popupView);

        userPopupDialogueBox = dialogueBuilder.create();

        progressDialog = new LoadingDialogue(requireActivity());

        adapterAdmin = new UsersItemAdapterAdmin(allUsers, false, new UsersItemAdapterAdmin.UserItemClickListenersOnlyChild() {
            @Override
            public void onShowFullDetailsButtonClick(View v, int position) {

            }

            @Override
            public void onUserProfileAndCreditsButtonClick(View v, int position) {

            }

            @Override
            public void onCallToPhoneNumberViaSimButtonClick(View v, int position, String mobileNumber) {

            }
        });

    }

    private void backgroundExecutorForShowingData(View view) {

        progressDialog.show("", "Loading Users");

        firebaseFirestore
                .collection(new FirebaseDataKeys().getUsersRef())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {

                            allUsers.clear();
                            for (DocumentSnapshot d : task.getResult().getDocuments()) {
                                adapterAdmin.addUser(d.toObject(UserModel.class));
                            }

                            view.setVisibility(View.VISIBLE);
                            progressDialog.dismiss();
                        } else {

                        }
                    }
                });


    }

    private class CustomPopupViewHolder {

        private final TextInputEditText txvName;
        private final TextInputEditText txvMobile;
        private final TextInputEditText txvEmail;
        private final TextInputEditText txvPassword;
        private final MaterialButton btnAdd;
        private final MaterialCardView displayPictureCard;
        private final ImageView displayPictureImageView;

        public CustomPopupViewHolder(View popupView) {
            txvName = popupView.findViewById(R.id.edx_add_users_full_name);
            txvMobile = popupView.findViewById(R.id.edx_add_users_mobile);
            txvEmail = popupView.findViewById(R.id.edx_add_users_email);
            txvPassword = popupView.findViewById(R.id.edx_add_users_password);
            btnAdd = popupView.findViewById(R.id.addUser);
            displayPictureCard = popupView.findViewById(R.id.card_add_users_dp);
            displayPictureImageView = popupView.findViewById(R.id.card_add_users_dp_imgv);

        }

        public TextInputEditText getTxvName() {
            return txvName;
        }

        public TextInputEditText getTxvMobile() {
            return txvMobile;
        }

        public TextInputEditText getTxvEmail() {
            return txvEmail;
        }

        public TextInputEditText getTxvPassword() {
            return txvPassword;
        }

        public MaterialButton getBtnAdd() {
            return btnAdd;
        }

        public MaterialCardView getDisplayPictureCard() {
            return displayPictureCard;
        }

        public ImageView getDisplayPictureImageView() {
            return displayPictureImageView;
        }

        public boolean analyzeInputs(Boolean shouldAnalyzeInputs) {
            Boolean eitherInputsAreFine = true;
            if (shouldAnalyzeInputs) {
                if (this.txvName.getText() == null || this.txvName.getText().toString().isEmpty()) {
                    eitherInputsAreFine = false;
                    showError(txvName, "Enter Full Name");
                }
                if (this.txvMobile.getText() == null || this.txvMobile.getText().toString().isEmpty() || this.txvMobile.getText().toString().length() != 11) {

                    eitherInputsAreFine = false;

                    if (this.txvMobile.getText().toString().length() != 11) {
                        showError(txvMobile, "Enter Valid Mobile No.\n11 digits");
                    } else {
                        showError(txvMobile, "Enter Mobile No.");

                    }

                }
                if (this.txvEmail.getText() == null || this.txvEmail.getText().toString().isEmpty() || !this.txvEmail.getText().toString().contains("@")) {
                    eitherInputsAreFine = false;
                    showError(txvEmail, "Enter Valid Email Address");

                }
                if (this.txvPassword.getText() == null || this.txvPassword.getText().toString().isEmpty() || this.txvMobile.getText().toString().length() < 8) {
                    eitherInputsAreFine = false;
                    showError(txvPassword, "At least 8 characters long password");
                }

            }
            return (shouldAnalyzeInputs && eitherInputsAreFine);
        }

        private void showError(EditText editT, String errorMessage) {
            editT.setError(errorMessage);
        }


        public void setEmptyContentToViews() {
            getTxvName().setText("");
            getTxvEmail().setText("");
            getTxvMobile().setText("");
            getTxvPassword().setText("");
        }
    }

}
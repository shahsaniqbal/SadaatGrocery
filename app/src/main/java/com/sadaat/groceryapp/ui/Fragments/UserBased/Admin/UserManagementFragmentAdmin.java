package com.sadaat.groceryapp.ui.Fragments.UserBased.Admin;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.sadaat.groceryapp.R;
import com.sadaat.groceryapp.adapters.admin.UsersItemAdapterAdmin;
import com.sadaat.groceryapp.handler.LeadsActionHandler;
import com.sadaat.groceryapp.models.AppCredits;
import com.sadaat.groceryapp.models.Users.UserModel;
import com.sadaat.groceryapp.models.Users.UserOtherDetailsModel;
import com.sadaat.groceryapp.models.cart.CartModel;
import com.sadaat.groceryapp.models.locations.AddressModel;
import com.sadaat.groceryapp.temp.FirebaseDataKeys;
import com.sadaat.groceryapp.temp.UserTypes;
import com.sadaat.groceryapp.ui.Activities.GenericForAll.LoginNavigatorActivity;
import com.sadaat.groceryapp.ui.Fragments.Generic.DetailedUserViewFragmentGeneric;
import com.sadaat.groceryapp.ui.Loaders.LoadingDialogue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

public class UserManagementFragmentAdmin extends Fragment implements UsersItemAdapterAdmin.UserItemClickListenersOnlyChild {

    private static final int IMAGE_SEL_REQ = 1234;
    FloatingActionButton addUsersButton;
    CustomPopupViewHolder viewHolder;

    //FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    RecyclerView recyclerView;
    //RecyclerView.LayoutManager manager;
    UsersItemAdapterAdmin adapterAdmin;
    LoadingDialogue progressDialog;
    //ArrayList<UserModel> allUsers;

    AlertDialog.Builder dialogueBuilder;
    AlertDialog userPopupDialogueBox;
    View popupView;
    String imageRef = "";
    //private StorageReference storageReference;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.admin_fragment_user_management, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializer(view);

        recyclerView.setLayoutManager(new LinearLayoutManager(UserManagementFragmentAdmin.this.requireActivity()));
        recyclerView.setAdapter(adapterAdmin);

        backgroundExecutorForShowingData(view);

        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).setSubtitle(null);

        addUsersButton.setOnClickListener(v -> handlePopup(view));
    }

    @Override
    public void onPause() {
        super.onPause();
        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).setSubtitle(null);
    }

    private void handlePopup(View parent) {
        //parent.setVisibility(View.INVISIBLE);
        viewHolder = new CustomPopupViewHolder(popupView);
        userPopupDialogueBox.show();
        viewHolder.getBtnAdd().setOnClickListener(v -> {
            if (viewHolder.analyzeInputs(true)) {

                progressDialog.show("Adding User", "Wait, Creating Delivery Boy Account");

                FirebaseAuth
                        .getInstance()
                        .createUserWithEmailAndPassword(Objects.requireNonNull(viewHolder.getTxvEmail().getText()).toString(), Objects.requireNonNull(viewHolder.getTxvPassword().getText()).toString())
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {

                                UserModel newUser = new UserModel(
                                        Objects.requireNonNull(task.getResult().getUser()).getUid(),
                                        UserTypes.DeliveryBoy,
                                        Objects.requireNonNull(viewHolder.getTxvName().getText()).toString(),
                                        viewHolder.getTxvEmail().getText().toString(),
                                        Objects.requireNonNull(viewHolder.getTxvMobile().getText()).toString(),
                                        new UserOtherDetailsModel(imageRef, new AddressModel()),
                                        new CartModel(),
                                        new AppCredits(),
                                        null,
                                        new ArrayList<>(),
                                        new ArrayList<>(),
                                        new ArrayList<>(),
                                        null);

                                FirebaseFirestore.getInstance()
                                        .collection(new FirebaseDataKeys().getUsersRef())
                                        .document(Objects.requireNonNull(task.getResult().getUser()).getUid())
                                        .set(newUser)
                                        .addOnCompleteListener(task1 -> {

                                            StringBuilder action = new StringBuilder();
                                            action.append("You have registered a new delivery boy.\n");
                                            action.append("Name: ");
                                            action.append("(").append(newUser.getFullName()).append(")\n");
                                            action.append("Email: ");
                                            action.append("(").append(newUser.getEmailAddress()).append(")\n");
                                            action.append("Mobile: ");
                                            action.append("(").append(newUser.getMobileNumber()).append(")\n");
                                            action.append("Password: ");
                                            action.append("(").append(viewHolder.getTxvPassword().getText().toString()).append(")\n");

                                            new LeadsActionHandler() {
                                                @Override
                                                public void onSuccessCompleteAction() {

                                                }

                                                @Override
                                                public void onCancelledAction() {

                                                }
                                            }.addAction(action.toString());

                                            parent.setVisibility(View.VISIBLE);

                                            if (task1.isSuccessful()) {
                                                /*adapterAdmin.addUser(newUser);
                                                viewHolder.setEmptyContentToViews();
                                                progressDialog.dismiss();

                                                userPopupDialogueBox.setCancelable(true);
                                                */
                                                userPopupDialogueBox.dismiss();


                                                Toast.makeText(UserManagementFragmentAdmin.this.requireActivity(), "For Security Purposes, Please Signin again", Toast.LENGTH_LONG).show();
                                                FirebaseAuth.getInstance().signOut();
                                                startActivity(new Intent(UserManagementFragmentAdmin.this.requireActivity(), LoginNavigatorActivity.class));
                                                UserManagementFragmentAdmin.this.requireActivity().finish();
                                            }
                                        });
                            }
                        });
            }
        });

        viewHolder.getDisplayPictureImageView().setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(
                    Intent.createChooser(
                            intent,
                            "Select 1:1 Image from here..."),
                    IMAGE_SEL_REQ);
        });

    }

    @SuppressLint("InflateParams")
    private void initializer(View view) {
        //allUsers = new ArrayList<>();
        addUsersButton = view.findViewById(R.id.btn_admin_add_user);
        recyclerView = view.findViewById(R.id.recycler_users_on_admin_screen);
        //manager = new LinearLayoutManager(UserManagementFragmentAdmin.this.requireActivity());

        dialogueBuilder = new AlertDialog.Builder(requireActivity());

        popupView = this.getLayoutInflater().inflate(R.layout.admin_popup_add_users, null, false);
        dialogueBuilder.setView(popupView);

        userPopupDialogueBox = dialogueBuilder.create();

        progressDialog = new LoadingDialogue(requireActivity());

        adapterAdmin = new UsersItemAdapterAdmin(UserManagementFragmentAdmin.this.requireActivity(), new ArrayList<>(), false, this);

        //storageReference = FirebaseStorage.getInstance(FirebaseDataKeys.STORAGE_BUCKET_ADDRESS).getReference();
    }

    private void backgroundExecutorForShowingData(View view) {

        progressDialog.show("", "Loading Users");

        FirebaseFirestore.getInstance()
                .collection(new FirebaseDataKeys().getUsersRef())
                .orderBy("userType", Query.Direction.ASCENDING)
                .get()
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {

                        adapterAdmin.deleteAll();
                        for (DocumentSnapshot d : task.getResult().getDocuments()) {
                            adapterAdmin.addUser(d.toObject(UserModel.class));
                        }

                        view.setVisibility(View.VISIBLE);
                        progressDialog.dismiss();
                    }
                });


    }

    @Override
    public void onShowFullDetailsButtonClick(View v, int position, String uid) {
        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_UM, DetailedUserViewFragmentGeneric.newInstance(uid))
                .addToBackStack("umgmt")
                .commit();
    }

    @Override
    public void onUserProfileAndCreditsButtonClick(View v, int position) {

    }

    @Override
    public void onCallToPhoneNumberViaSimButtonClick(View v, int position, String mobileNumber) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + mobileNumber));
        startActivity(intent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_SEL_REQ
                && resultCode == RESULT_OK
                && data != null
                && data.getData() != null) {

            // Get the Uri of data
            Uri imageData = data.getData();
            try {

                // Setting image on image view using Bitmap
                Bitmap bitmap = MediaStore
                        .Images
                        .Media
                        .getBitmap(
                                this.requireActivity().getContentResolver(),
                                imageData);

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);

                //Setting Image Bitmap to my desired ImageView
                viewHolder.getDisplayPictureImageView().setImageBitmap(bitmap);

                uploadImage(baos.toByteArray());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadImage(byte[] toByteArray) {
        if (imageRef != null) {

            // Code for showing progressDialog while uploading
            progressDialog.show("Uploading", "Image");

            // Defining the child of storageReference
            imageRef = "f/images/users/" + UUID.randomUUID().toString();

            StorageReference ref = FirebaseStorage.getInstance(FirebaseDataKeys.STORAGE_BUCKET_ADDRESS).getReference().child(imageRef);

            ref.putBytes(toByteArray)
                    .addOnSuccessListener(
                            taskSnapshot -> {
                                progressDialog.dismiss();
                                Toast.makeText(
                                        UserManagementFragmentAdmin.this.requireActivity(),
                                        "Image Uploaded!!",
                                        Toast.LENGTH_SHORT
                                ).show();
                            })

                    .addOnFailureListener(e -> {
                        progressDialog.dismiss();
                        Log.e(e.getCause() + "\n", e.getMessage());
                        Toast.makeText(UserManagementFragmentAdmin.this.requireActivity(),
                                "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    })
                    .addOnProgressListener(
                            taskSnapshot -> {
                                double progress
                                        = (100.0
                                        * taskSnapshot.getBytesTransferred()
                                        / taskSnapshot.getTotalByteCount());
                                progressDialog.show(
                                        "Uploaded ",
                                        ""
                                                + (int) progress + "%");
                            });
        }
    }

    private static class CustomPopupViewHolder {

        private final TextInputEditText txvName;
        private final TextInputEditText txvMobile;
        private final TextInputEditText txvEmail;
        private final TextInputEditText txvPassword;
        private final MaterialButton btnAdd;
        //private final MaterialCardView displayPictureCard;
        private final ImageView displayPictureImageView;

        public CustomPopupViewHolder(View popupView) {
            txvName = popupView.findViewById(R.id.edx_add_users_full_name);
            txvMobile = popupView.findViewById(R.id.edx_add_users_mobile);
            txvEmail = popupView.findViewById(R.id.edx_add_users_email);
            txvPassword = popupView.findViewById(R.id.edx_add_users_password);
            btnAdd = popupView.findViewById(R.id.addUser);
            //displayPictureCard = popupView.findViewById(R.id.card_add_users_dp);
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
        /*
        public MaterialCardView getDisplayPictureCard() {
            return displayPictureCard;
        }
        */

        public ImageView getDisplayPictureImageView() {
            return displayPictureImageView;
        }

        public boolean analyzeInputs(Boolean shouldAnalyzeInputs) {
            boolean eitherInputsAreFine = true;
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
            getDisplayPictureImageView().setImageResource(R.drawable.ic_users);
        }
    }

}
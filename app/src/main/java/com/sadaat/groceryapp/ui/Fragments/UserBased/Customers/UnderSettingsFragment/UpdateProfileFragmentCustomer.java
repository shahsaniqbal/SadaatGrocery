package com.sadaat.groceryapp.ui.Fragments.UserBased.Customers.UnderSettingsFragment;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.sadaat.groceryapp.R;
import com.sadaat.groceryapp.temp.FirebaseDataKeys;
import com.sadaat.groceryapp.temp.UserLive;
import com.sadaat.groceryapp.ui.Activities.UsersBased.Customers.MainActivityCustomer;
import com.sadaat.groceryapp.ui.Loaders.LoadingDialogue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

public class UpdateProfileFragmentCustomer extends Fragment {


    private static final int IMAGE_SEL_REQ = 1324;
    TextInputEditText edxFullName;
    TextInputEditText edxMobile;
    ImageView cardUserDP;
    ImageView userDP;
    MaterialButton btnUpdate;
    CoordinatorLayout coordinatorLayout;
    LoadingDialogue dialogue;
    String imagePath = "";
    Uri imageReference;


    public UpdateProfileFragmentCustomer() {
        // Required empty public constructor
    }

    public static UpdateProfileFragmentCustomer newInstance() {

        return new UpdateProfileFragmentCustomer();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.customer_fragment_update_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        edxFullName = view.findViewById(R.id.full_name);
        edxMobile = view.findViewById(R.id.mobile_number);
        cardUserDP = view.findViewById(R.id.add_user_dp);
        userDP = view.findViewById(R.id.userDP);
        btnUpdate = view.findViewById(R.id.btn_update);
        coordinatorLayout = view.findViewById(R.id.coordinator);
        dialogue = new LoadingDialogue(this.requireActivity());

        edxFullName.setText(UserLive.currentLoggedInUser.getFullName());
        edxMobile.setText(UserLive.currentLoggedInUser.getMobileNumber());


        setImageToUser();
        cardUserDP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(
                        Intent.createChooser(
                                intent,
                                "Select Image from here..."),
                        IMAGE_SEL_REQ);
            }
        });


        btnUpdate.setOnClickListener(v -> {
            if (analyzeInputs(true)) {

                FirebaseFirestore.getInstance()
                        .collection(new FirebaseDataKeys().getUsersRef())
                        .document(UserLive.currentLoggedInUser.getUID())
                        .update("fullName", Objects.requireNonNull(edxFullName.getText()).toString(),
                                "mobileNumber", Objects.requireNonNull(edxMobile.getText()).toString())
                        .addOnCompleteListener(t -> {
                            if (t.isSuccessful()) {

                                Toast.makeText(UpdateProfileFragmentCustomer
                                                .this
                                                .requireActivity()
                                        , "Details Updated Successfully", Toast.LENGTH_LONG).show();
                                UpdateProfileFragmentCustomer
                                        .this
                                        .requireActivity()
                                        .getSupportFragmentManager()
                                        .popBackStack();
                            }
                        });

            }
        });
    }

    private void setImageToUser() {

        if (MainActivityCustomer.userImage != null) {
            userDP.setImageBitmap(MainActivityCustomer.userImage);
        } else {

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_SEL_REQ
                && resultCode == RESULT_OK
                && data != null
                && data.getData() != null) {

            // Get the Uri of data
            imageReference = data.getData();
            try {

                // Setting image on image view using Bitmap
                Bitmap bitmap = MediaStore
                        .Images
                        .Media
                        .getBitmap(
                                this.requireActivity().getContentResolver(),
                                imageReference);

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 25, baos);

                //Setting Image Bitmap to my desired ImageView
                userDP.setImageBitmap(bitmap);

                uploadImage(baos.toByteArray());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Modified for this file only
    private void uploadImage(byte[] fileInBytes) {
        if (imageReference != null) {

            // Code for showing progressDialog while uploading
            dialogue.show("Uploading", "Image");

            // Defining the child of storageReference
            imagePath = "f/images/users/" + UUID.randomUUID().toString();

            StorageReference ref = FirebaseStorage.getInstance(FirebaseDataKeys.STORAGE_BUCKET_ADDRESS).getReference().child(imagePath);

            ref.putBytes(fileInBytes)
                    .addOnSuccessListener(
                            taskSnapshot -> {
                                dialogue.dismiss();
                                Toast.makeText(
                                        UpdateProfileFragmentCustomer.this.requireActivity(),
                                        "Image Updated!!",
                                        Toast.LENGTH_SHORT
                                ).show();

                                FirebaseStorage.getInstance(FirebaseDataKeys.STORAGE_BUCKET_ADDRESS).getReference().child(
                                        UserLive.currentLoggedInUser.getDetails().getImageReference()
                                ).delete();

                                UserLive.currentLoggedInUser.getDetails().setImageReference(imagePath);
                                FirebaseFirestore.getInstance()
                                        .collection(new FirebaseDataKeys().getUsersRef())
                                        .document(UserLive.currentLoggedInUser.getUID())
                                        .update("details.imageReference", imagePath)
                                        .addOnCompleteListener(task -> {
                                            if (task.isSuccessful()) {
                                                if (!UserLive.currentLoggedInUser.getDetails()
                                                        .getImageReference().isEmpty()) {

                                                    StorageReference imgRef = FirebaseStorage
                                                            .getInstance(FirebaseDataKeys.STORAGE_BUCKET_ADDRESS)
                                                            .getReference()
                                                            .child(UserLive.currentLoggedInUser.getDetails()
                                                                    .getImageReference());

                                                    final long ONE_MEGABYTE = 1024 * 1024;

                                                    dialogue.show("Please Wait", "Loading User Data");

                                                    imgRef.getBytes(10 * ONE_MEGABYTE).addOnSuccessListener(bytes -> {
                                                        MainActivityCustomer.userImage = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);


                                                        if (MainActivityCustomer.userImage != null) {
                                                            ((ImageView) UpdateProfileFragmentCustomer.this.requireActivity().findViewById(R.id.userDP)).setImageDrawable(userDP.getDrawable());
                                                        } else {

                                                        }
                                                        dialogue.dismiss();
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception exception) {
                                                            //Toast.makeText(UpdateProfileFragmentCustomer.this.requireActivity(), "User Image Load Failed, \n Leave it ", Toast.LENGTH_SHORT).show();
                                                            dialogue.dismiss();
                                                        }
                                                    });
                                                } else {
                                                    dialogue.dismiss();
                                                }
                                            }
                                        });
                            })

                    .addOnFailureListener(e -> {
                        dialogue.dismiss();
                        Log.e(e.getCause() + "\n", e.getMessage());
                        Toast.makeText(UpdateProfileFragmentCustomer.this.requireActivity(),
                                "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    })
                    .addOnProgressListener(
                            taskSnapshot -> {
                                double progress
                                        = (100.0
                                        * taskSnapshot.getBytesTransferred()
                                        / taskSnapshot.getTotalByteCount());
                                dialogue.show(
                                        "Uploaded ", ""
                                                + (int) progress + "%");
                            });
        }
    }

    private boolean analyzeInputs(boolean b) {

        if (Objects.requireNonNull(edxFullName.getText()).toString().isEmpty()) {
            edxFullName.setError("Please Enter Full Name");
            b = false;
        } else if (Objects.requireNonNull(edxMobile.getText()).toString().isEmpty()) {
            edxMobile.setError("Please Enter Valid Mobile Number");
            b = false;
        } else {
            b = true;
        }

        return b;
    }
}
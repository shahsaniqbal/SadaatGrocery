package com.sadaat.groceryapp.ui.Fragments.UserBased.Admin.UnderListingFragmentChildSuper;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.sadaat.groceryapp.R;
import com.sadaat.groceryapp.adapters.generic.SliderAdapter;
import com.sadaat.groceryapp.models.SliderModel;
import com.sadaat.groceryapp.models.SuggestionModel;
import com.sadaat.groceryapp.temp.FirebaseDataKeys;
import com.sadaat.groceryapp.temp.UserLive;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

public class SlidesListFragmentAdmin extends Fragment implements SliderAdapter.OnClickListener {

    private static final int IMAGE_SEL_REQ = 4321;
    FloatingActionButton addSlidesButton;
    CustomPopupViewHolder viewHolder;

    RecyclerView recyclerView;
    SliderAdapter adapter;

    AlertDialog.Builder dialogueBuilder;
    AlertDialog userPopupDialogueBox;
    View popupView;
    String imageRef = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.admin_fragment_slides_list, container, false);
    }
    @Override
    public void onPause() {
        super.onPause();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle(null);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle("Listing -> Customer Home Slides");
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializer(view);

        recyclerView.setLayoutManager(new LinearLayoutManager(SlidesListFragmentAdmin.this.requireActivity()));
        recyclerView.setAdapter(adapter);

        backgroundExecutorForShowingData(view);

        addSlidesButton.setOnClickListener(v -> handlePopup(view));
    }

    private void handlePopup(View parent) {
        //parent.setVisibility(View.INVISIBLE);
        viewHolder = new CustomPopupViewHolder(popupView);
        userPopupDialogueBox.show();
        viewHolder.getBtnAdd().setOnClickListener(v -> {
            if (viewHolder.analyzeInputs(true)) {

                String newID = "S-"+((int)(Math.random()*Math.random()*1000)-256)+"-"+new Date().getTime();

                FirebaseFirestore.getInstance()
                        .collection(new FirebaseDataKeys().getSlidesRef())
                        .document(newID)
                        .set(new SliderModel(newID, Objects.requireNonNull(viewHolder.getTxvTitle().getText()).toString(),
                                Objects.requireNonNull(viewHolder.getTxvDescription().getText()).toString(),
                                imageRef))
                        .addOnCompleteListener(t -> {

                            if (t.isSuccessful()) {
                                userPopupDialogueBox.dismiss();
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
        addSlidesButton = view.findViewById(R.id.fab);
        recyclerView = view.findViewById(R.id.recycler);
        dialogueBuilder = new AlertDialog.Builder(requireActivity());
        popupView = this.getLayoutInflater().inflate(R.layout.admin_popup_add_slides, null, false);
        dialogueBuilder.setView(popupView);
        userPopupDialogueBox = dialogueBuilder.create();
        adapter = new SliderAdapter(new ArrayList<>(), this);
    }

    private void backgroundExecutorForShowingData(View view) {

        FirebaseFirestore.getInstance()
                .collection(new FirebaseDataKeys().getSlidesRef())
                .get()
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {

                        adapter.deleteAll();
                        for (DocumentSnapshot d : task.getResult().getDocuments()) {
                            adapter.addSlide(d.toObject(SliderModel.class));
                        }

                        view.setVisibility(View.VISIBLE);
                    }
                });

        FirebaseFirestore
                .getInstance()
                .collection(new FirebaseDataKeys().getSlidesRef())
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.w("TAG", "Listen failed.", error);
                        return;
                    }

                    if (value != null && value.getDocuments().size()>0) {

                        adapter.deleteAll();
                        for (DocumentSnapshot d :
                                value.getDocuments()) {
                            adapter.addSlide(d.toObject(SliderModel.class));
                        }

                        if (value.getDocuments().size() > 4){
                            addSlidesButton.setVisibility(View.GONE);
                        }
                        else{
                            addSlidesButton.setVisibility(View.VISIBLE);
                        }

                    } else {
                        Log.d("TAG", "source file SliderFragmentAdmin" + " data: null");
                        if ((value != null ? value.getDocuments().size() : 0) == 0){
                            addSlidesButton.setVisibility(View.VISIBLE);
                        }
                    }
                });


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

            imageRef = "f/images/slides/" + UUID.randomUUID().toString();

            StorageReference ref = FirebaseStorage.getInstance(FirebaseDataKeys.STORAGE_BUCKET_ADDRESS).getReference().child(imageRef);

            ref.putBytes(toByteArray)
                    .addOnSuccessListener(
                            taskSnapshot -> {
                                Toast.makeText(
                                        SlidesListFragmentAdmin.this.requireActivity(),
                                        "Image Uploaded!!",
                                        Toast.LENGTH_SHORT
                                ).show();
                            })

                    .addOnFailureListener(e -> {

                        Toast.makeText(SlidesListFragmentAdmin.this.requireActivity(),
                                "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    })
                    .addOnProgressListener(
                            taskSnapshot -> {
                                double progress
                                        = (100.0
                                        * taskSnapshot.getBytesTransferred()
                                        / taskSnapshot.getTotalByteCount());
                            });
        }
    }

    @Override
    public void onDelete(String slideID, String imageRef) {

        FirebaseStorage.getInstance(FirebaseDataKeys.STORAGE_BUCKET_ADDRESS).getReference().child(imageRef)
                .delete();

        FirebaseFirestore.getInstance()
                .collection(new FirebaseDataKeys().getSlidesRef())
                .document(slideID)
                .delete();
    }

    private static class CustomPopupViewHolder {

        private final TextInputEditText txvTitle;
        private final TextInputEditText txvDescription;
        private final MaterialButton btnAdd;
        private final ImageView displayPictureImageView;

        public CustomPopupViewHolder(View popupView) {
            txvTitle = popupView.findViewById(R.id.edx_title);
            txvDescription = popupView.findViewById(R.id.edx_description);
            btnAdd = popupView.findViewById(R.id.addSlide);
            displayPictureImageView = popupView.findViewById(R.id.iv_add_slide_image);
        }

        public TextInputEditText getTxvTitle() {
            return txvTitle;
        }

        public TextInputEditText getTxvDescription() {
            return txvDescription;
        }

        public MaterialButton getBtnAdd() {
            return btnAdd;
        }

        public ImageView getDisplayPictureImageView() {
            return displayPictureImageView;
        }

        public boolean analyzeInputs(Boolean shouldAnalyzeInputs) {
            boolean eitherInputsAreFine = true;
            if (shouldAnalyzeInputs) {
                if (this.txvTitle.getText() == null || this.txvTitle.getText().toString().isEmpty()) {
                    eitherInputsAreFine = false;
                    showError(txvTitle, "Enter Full Title");
                }
                if (this.txvDescription.getText() == null || this.txvDescription.getText().toString().isEmpty()) {
                    eitherInputsAreFine = false;
                    showError(txvDescription, "Write Valid Description");

                }

            }
            return (shouldAnalyzeInputs && eitherInputsAreFine);
        }

        private void showError(EditText editT, String errorMessage) {
            editT.setError(errorMessage);
        }

        public void setEmptyContentToViews() {
            getTxvTitle().setText("");
            getTxvDescription().setText("");
            getDisplayPictureImageView().setImageResource(R.drawable.ic_users);
            //TODO
        }
    }

}
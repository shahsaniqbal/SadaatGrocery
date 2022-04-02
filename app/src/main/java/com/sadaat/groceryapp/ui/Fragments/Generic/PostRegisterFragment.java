package com.sadaat.groceryapp.ui.Fragments.Generic;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.sadaat.groceryapp.R;
import com.sadaat.groceryapp.handler.LoginIntentHandler;
import com.sadaat.groceryapp.models.UserModel;
import com.sadaat.groceryapp.models.Users.UserOtherDetailsModel;
import com.sadaat.groceryapp.models.locations.AddressModel;
import com.sadaat.groceryapp.models.locations.AreaModel;
import com.sadaat.groceryapp.models.locations.CityModel;
import com.sadaat.groceryapp.temp.FirebaseDataKeys;
import com.sadaat.groceryapp.temp.UserLive;
import com.sadaat.groceryapp.ui.Fragments.UserBased.Admin.UnderListingFragmentChildSuper.ItemsListFragmentAdmin;
import com.sadaat.groceryapp.ui.Loaders.LoadingDialogue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

public class PostRegisterFragment extends Fragment implements View.OnClickListener {

    private static final int IMAGE_SEL_REQ = 656;
    String city = "";
    String area = "";
    String imagePath = "";
    Uri imageReference;
    private UserModel model;
    private ArrayList<CityModel> cityModels;
    private AddressModel address;
    //Views
    private ImageView userDP;
    private MaterialTextView edxEmail;
    private MaterialTextView edxName;
    private MaterialTextView edxMobile;
    private TextInputEditText edxAddress1;
    private TextInputEditText edxAddress2;


    private Spinner spinnerCity;
    private Spinner spinnerArea;
    private ArrayAdapter<String> adapterCities;
    private ArrayAdapter<String> adapterAreas;


    private MaterialButton btnSignUp;
    private LoadingDialogue loadingDialogue;
    private String password;

    private FirebaseStorage storage;
    private StorageReference storageReference;


    private int selectedCityIndex = 0;
    //private int selectedAreaIndex = 0;


    public PostRegisterFragment() {
    }

    public PostRegisterFragment(UserModel model, String password) {
        this.model = model;
        this.password = password;
    }

    public static PostRegisterFragment newInstance(UserModel model, String password) {
        return new PostRegisterFragment(model, password);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.generic_fragment_post_register, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializers(view);

        edxName.setText(model.getFullName());
        edxEmail.setText(model.getEmailAddress());
        edxMobile.setText(model.getMobileNumber());

        spinnerCity.setAdapter(adapterCities);
        spinnerArea.setAdapter(adapterAreas);

        loadSpinnersData();

        spinnerCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedCityIndex = i;
                address.setCity(cityModels.get(i));
                loadingDialogue.show("Please wait", "Loading Available areas in "+cityModels.get(i).getName());
                adapterAreas.clear();

                for (AreaModel model: cityModels.get(selectedCityIndex).getAreas()){
                    adapterAreas.add(model.toString());
                    adapterAreas.notifyDataSetChanged();
                }

                loadingDialogue.dismiss();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                address.setCity(null);
            }
        });

        spinnerArea.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                // selectedAreaIndex = i;
                address.setArea(cityModels.get(selectedCityIndex).getAreas().get(i));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                address.setArea(null);
            }
        });


        

        userDP.setOnClickListener(new View.OnClickListener() {
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

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (analyzeInputsIfEmpty(true)) {

                    address.setAddressLine1(edxAddress1.getText().toString());
                    if (!edxAddress2.getText().toString().isEmpty()){
                        address.setAddressLine2(edxAddress2.getText().toString());
                    }
                    else address.setAddressLine2("");

                    model.setDetails(
                            new UserOtherDetailsModel(
                                    imagePath,
                                    address
                            ));

                    //Update User Object
                    loadingDialogue.show("Please Wait", "Saving User Details");
                    FirebaseAuth
                            .getInstance()
                            .createUserWithEmailAndPassword(model.getEmailAddress(), password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        model.setUID(Objects.requireNonNull(task.getResult().getUser()).getUid());
                                        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
                                        firebaseFirestore
                                                .collection(new FirebaseDataKeys().getUsersRef())
                                                .document(model.getUID())
                                                .set(model)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            Toast.makeText(requireActivity(), "User Registration Successful", Toast.LENGTH_SHORT).show();
                                                            UserLive.currentLoggedInUser = model;

                                                            Intent i = new LoginIntentHandler(PostRegisterFragment.this.requireActivity(), model.getUserType());

                                                            loadingDialogue.dismiss();

                                                            startActivity(i);
                                                            requireActivity().finish();

                                                        } else {
                                                            Toast.makeText(requireActivity(), "" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                            loadingDialogue.dismiss();
                                                        }
                                                    }
                                                });

                                    } else {
                                        Toast.makeText(requireActivity(), "Error Signing UP \n" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        loadingDialogue.dismiss();

                                    }
                                }
                            });

                }
                else{
                    Toast.makeText(PostRegisterFragment.this.requireActivity(), "There's some problem with the inputs", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void loadSpinnersData() {
        loadingDialogue.show("Loading", "Available Locations for Delivery");
        FirebaseFirestore
                .getInstance()
                .collection(new FirebaseDataKeys().getLocationsRef())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            for (DocumentSnapshot d: task.getResult().getDocuments()){
                                CityModel model = d.toObject(CityModel.class);
                                cityModels.add(model);
                                Log.e("Data", model.getId()+"...."+model.getName()+"...."+model.getAreas().size());
                                adapterCities.add(model.toString());
                                adapterCities.notifyDataSetChanged();
                            }

                            loadingDialogue.dismiss();
                        } else {
                            loadingDialogue.dismiss();
                            Toast.makeText(PostRegisterFragment.this.requireActivity(), "Error getting Locations, Please try again later", Toast.LENGTH_SHORT).show();
                            openRegisterFragmentAgain();
                        }
                    }
                });
    }

    private void openRegisterFragmentAgain() {
        requireActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .remove(this)
                .commit();
    }

    private boolean analyzeInputsIfEmpty(Boolean shouldAnalyze) {
        if (shouldAnalyze) {
            if (imagePath.isEmpty()) {
                Toast.makeText(this.requireActivity(), "Image is necessary. Please Select your 1:1 Image", Toast.LENGTH_SHORT).show();
                shouldAnalyze = false;
            }
            if (adapterCities.getItem(0).isEmpty()) {
                spinnerCity.setPrompt("City Field is mandatory");
                shouldAnalyze = false;
            }
            if (adapterAreas.getItem(0).isEmpty()) {
                spinnerArea.setPrompt("Area Field is mandatory");
                shouldAnalyze = false;
            }
            if (edxAddress1.getText().toString().isEmpty()) {
                edxAddress1.setError("Address Line 1 is Mandatory");
                shouldAnalyze = false;
            }
        }
        return shouldAnalyze;
    }

    private void initializers(View v) {
        userDP = v.findViewById(R.id.card_add_users_dp_imgv);

        edxEmail = (MaterialTextView) v.findViewById(R.id.edx_add_users_email);
        edxName = (MaterialTextView) v.findViewById(R.id.edx_add_users_full_name);
        edxMobile = (MaterialTextView) v.findViewById(R.id.edx_add_users_mobile);
        edxAddress1 = (TextInputEditText) v.findViewById(R.id.address_line_1);
        edxAddress2 = (TextInputEditText) v.findViewById(R.id.address_line_2);

        spinnerCity = (Spinner) v.findViewById(R.id.city_spinner);
        spinnerArea = (Spinner) v.findViewById(R.id.area_spinner);

        btnSignUp = (MaterialButton) v.findViewById(R.id.addUser_as_customer);
        loadingDialogue = new LoadingDialogue(PostRegisterFragment.this.requireActivity());

        cityModels = new ArrayList<>();

        address = new AddressModel();

        storage = FirebaseStorage.getInstance(FirebaseDataKeys.STORAGE_BUCKET_ADDRESS);
        storageReference = storage.getReference();

        adapterCities = new ArrayAdapter<String>(this.requireActivity(),
                android.R.layout.simple_spinner_item, new ArrayList<>());

        adapterAreas = new ArrayAdapter<String>(this.requireActivity(),
                android.R.layout.simple_spinner_item, new ArrayList<>());



    }

    @Override
    public void onClick(View view) {
        if (analyzeInputsIfEmpty(true)) {

            address.setAddressLine1(edxAddress1.getText().toString());
            if (!edxAddress2.getText().toString().isEmpty()){
                address.setAddressLine2(edxAddress2.getText().toString());
            }
            else address.setAddressLine2("");

            model.setDetails(
                    new UserOtherDetailsModel(
                            imagePath,
                            address
                    ));

            //Update User Object
            loadingDialogue.show("Please Wait", "Saving User Details");
            FirebaseAuth
                    .getInstance()
                    .createUserWithEmailAndPassword(model.getEmailAddress(), password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                model.setUID(Objects.requireNonNull(task.getResult().getUser()).getUid());
                                FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
                                firebaseFirestore
                                        .collection(new FirebaseDataKeys().getUsersRef())
                                        .document(model.getUID())
                                        .set(model)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(requireActivity(), "User Registration Successful", Toast.LENGTH_SHORT).show();
                                                    UserLive.currentLoggedInUser = model;

                                                    Intent i = new LoginIntentHandler(PostRegisterFragment.this.requireActivity(), model.getUserType());

                                                    loadingDialogue.dismiss();

                                                    startActivity(i);
                                                    requireActivity().finish();

                                                } else {
                                                    Toast.makeText(requireActivity(), "" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                    loadingDialogue.dismiss();
                                                }
                                            }
                                        });

                            } else {
                                Toast.makeText(requireActivity(), "Error Signing UP \n" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                loadingDialogue.dismiss();

                            }
                        }
                    });

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
                bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);

                //Setting Image Bitmap to my desired ImageView
                userDP.setImageBitmap(bitmap);

                uploadImage(baos.toByteArray());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadImage(byte[] fileInBytes) {
        if (imageReference != null) {

            // Code for showing progressDialog while uploading
            loadingDialogue.show("Uploading", "Image");

            // Defining the child of storageReference
            imagePath = "f/images/users/" + UUID.randomUUID().toString();

            StorageReference ref = storageReference.child(imagePath);

            ref.putBytes(fileInBytes)
                    .addOnSuccessListener(
                            taskSnapshot -> {
                                loadingDialogue.dismiss();
                                Toast.makeText(
                                        PostRegisterFragment.this.requireActivity(),
                                        "Image Uploaded!!",
                                        Toast.LENGTH_SHORT
                                ).show();
                            })

                    .addOnFailureListener(e -> {
                        loadingDialogue.dismiss();
                        Log.e(e.getCause() + "\n", e.getMessage());
                        Toast.makeText(PostRegisterFragment.this.requireActivity(),
                                "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    })
                    .addOnProgressListener(
                            taskSnapshot -> {
                                double progress
                                        = (100.0
                                        * taskSnapshot.getBytesTransferred()
                                        / taskSnapshot.getTotalByteCount());
                                loadingDialogue.show(
                                        "Uploaded ", ""
                                                + (int) progress + "%");
                            });
        }
    }


}

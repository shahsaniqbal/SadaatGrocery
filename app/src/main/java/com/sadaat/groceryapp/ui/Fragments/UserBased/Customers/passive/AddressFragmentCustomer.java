package com.sadaat.groceryapp.ui.Fragments.UserBased.Customers.passive;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

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
import com.sadaat.groceryapp.handler.LeadsActionHandler;
import com.sadaat.groceryapp.handler.LoginIntentHandler;
import com.sadaat.groceryapp.models.Users.UserOtherDetailsModel;
import com.sadaat.groceryapp.models.locations.AddressModel;
import com.sadaat.groceryapp.models.locations.AreaModel;
import com.sadaat.groceryapp.models.locations.CityModel;
import com.sadaat.groceryapp.temp.FirebaseDataKeys;
import com.sadaat.groceryapp.temp.UserLive;
import com.sadaat.groceryapp.ui.Fragments.Generic.PostRegisterFragment;
import com.sadaat.groceryapp.ui.Fragments.UserBased.Customers.AccountFragmentCustomer;
import com.sadaat.groceryapp.ui.Loaders.LoadingDialogue;

import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class AddressFragmentCustomer extends Fragment {

    private ArrayList<CityModel> cityModels;
    private AddressModel address;
    private TextInputEditText edxAddress1;
    private TextInputEditText edxAddress2;


    private Spinner spinnerCity;
    private Spinner spinnerArea;
    private ArrayAdapter<String> adapterCities;
    private ArrayAdapter<String> adapterAreas;


    private LoadingDialogue loadingDialogue;

    MaterialButton btnUpdate;



    private int selectedCityIndex = 0;
    //private int selectedAreaIndex = 0;


    public AddressFragmentCustomer() {
        // Required empty public constructor
    }
    public static AddressFragmentCustomer newInstance() {
        return new AddressFragmentCustomer();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.customer_fragment_address_update, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);

        btnUpdate = v.findViewById(R.id.btn_update);

        edxAddress1 = v.findViewById(R.id.address_line_1);
        edxAddress2 = v.findViewById(R.id.address_line_2);

        spinnerCity = v.findViewById(R.id.city_spinner);
        spinnerArea = v.findViewById(R.id.area_spinner);

        loadingDialogue = new LoadingDialogue(AddressFragmentCustomer.this.requireActivity());

        cityModels = new ArrayList<>();

        address = new AddressModel();

        adapterCities = new ArrayAdapter<String>(this.requireActivity(),
                android.R.layout.simple_spinner_item, new ArrayList<>());

        adapterAreas = new ArrayAdapter<String>(this.requireActivity(),
                android.R.layout.simple_spinner_item, new ArrayList<>());

        edxAddress1.setText(UserLive.currentLoggedInUser.getDetails().getAddress().getAddressLine1());
        edxAddress2.setText(UserLive.currentLoggedInUser.getDetails().getAddress().getAddressLine2());


        spinnerCity.setAdapter(adapterCities);
        spinnerArea.setAdapter(adapterAreas);


        loadingDialogue.show("Loading", "Available Locations for Delivery");
        FirebaseFirestore
                .getInstance()
                .collection(new FirebaseDataKeys().getLocationsRef())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            for (DocumentSnapshot d : task.getResult().getDocuments()) {
                                CityModel model = d.toObject(CityModel.class);
                                cityModels.add(model);
                                adapterCities.add(model.toString());
                                adapterCities.notifyDataSetChanged();
                            }

                            loadingDialogue.dismiss();
                        } else {
                            loadingDialogue.dismiss();
                            Toast.makeText(AddressFragmentCustomer.this.requireActivity(), "Error getting Locations, Please try again later", Toast.LENGTH_SHORT).show();
                            requireActivity()
                                    .getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.fl_customer_account, new AccountFragmentCustomer(), "account")
                                    .commit();
                        }
                    }
                });

        spinnerCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedCityIndex = i;
                address.setCity(cityModels.get(i));
                loadingDialogue.show("Please wait", "Loading Available areas in " + cityModels.get(i).getName());
                adapterAreas.clear();

                for (AreaModel model : cityModels.get(selectedCityIndex).getAreas()) {
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

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (analyzeInputsIfEmpty(true)) {

                    address.setAddressLine1(edxAddress1.getText().toString());
                    if (!edxAddress2.getText().toString().isEmpty()) {
                        address.setAddressLine2(edxAddress2.getText().toString());
                    } else address.setAddressLine2("");

                    //Update User Object
                    loadingDialogue.show("Please Wait", "Updating User Details");

                    FirebaseFirestore.getInstance()
                            .collection(new FirebaseDataKeys().getUsersRef())
                            .document(UserLive.currentLoggedInUser.getUID())
                            .update("details.address", address)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(requireActivity(), "User Address Update Successful", Toast.LENGTH_SHORT).show();

                                        loadingDialogue.dismiss();


                                        StringBuilder action = new StringBuilder();
                                        action.append("Customer ");
                                        action.append("(").append(UserLive.currentLoggedInUser.getFullName()).append(") ");
                                        action.append("Changes the Location to ");
                                        action.append("(").append(address.toString()).append(") ");

                                        new LeadsActionHandler() {
                                            @Override
                                            public void onSuccessCompleteAction() {

                                            }

                                            @Override
                                            public void onCancelledAction() {

                                            }
                                        }
                                        .addAction(action.toString());

                                        requireActivity()
                                                .getSupportFragmentManager()
                                                .beginTransaction()
                                                .replace(R.id.fl_customer_account, new AccountFragmentCustomer(), "account")
                                                .commit();

                                    } else {
                                        Toast.makeText(requireActivity(), "" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        loadingDialogue.dismiss();
                                    }
                                }
                            });

                } else {
                    Toast.makeText(AddressFragmentCustomer.this.requireActivity(), "There's some problem with the inputs", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private boolean analyzeInputsIfEmpty(boolean shouldAnalyze) {
        if (shouldAnalyze) {

            if (adapterCities.getCount() > 0 && adapterAreas.getCount() > 0) {
                if (adapterCities.getItem(0).isEmpty()) {
                    spinnerCity.setPrompt("City Field is mandatory");
                    shouldAnalyze = false;
                }
                if (adapterAreas.getItem(0).isEmpty()) {
                    spinnerArea.setPrompt("Area Field is mandatory");
                    shouldAnalyze = false;
                }
            }
            else{
                shouldAnalyze = false;
            }
            if (edxAddress1.getText().toString().isEmpty()) {
                edxAddress1.setError("Address Line 1 is Mandatory");
                shouldAnalyze = false;
            }
        }
        return shouldAnalyze;

    }
}
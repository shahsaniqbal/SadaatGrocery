package com.sadaat.groceryapp.ui.Fragments.UserBased.Admin.UnderListingFragmentChildSuper;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.sadaat.groceryapp.R;
import com.sadaat.groceryapp.adapters.admin.locations.AreasItemAdapterAdmin;
import com.sadaat.groceryapp.adapters.admin.locations.CitiesItemAdapterAdmin;
import com.sadaat.groceryapp.models.locations.AreaModel;
import com.sadaat.groceryapp.models.locations.CityModel;
import com.sadaat.groceryapp.temp.FirebaseDataKeys;
import com.sadaat.groceryapp.ui.Loaders.LoadingDialogue;

import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class LocationsListFragmentAdmin extends Fragment implements
        CitiesItemAdapterAdmin.CitiesItemAdapterListener,
        View.OnClickListener,
        AreasItemAdapterAdmin.AreasItemAdapterListener {


    final CollectionReference LOCATIONS_COLLECTION_REFERENCE = FirebaseFirestore.getInstance().collection(new FirebaseDataKeys().getLocationsRef());
    FloatingActionButton addCitiesButtonOnFragment;

    AlertDialog.Builder dialogueBuilder;
    AlertDialog itemPopupDialogueBox;
    View popupView;

    CustomPopupViewHolder customPopupViewHolder;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager manager;
    CitiesItemAdapterAdmin adapterAdmin;
    ArrayList<CityModel> list;

    LoadingDialogue progressDialog;
    int action = 0;
    String docID = "";
    CityModel cityModel = null;
    AreaIndexDataHolder areaIndexDataHolder;

    public LocationsListFragmentAdmin() {
    }


    public static LocationsListFragmentAdmin newInstance() {
        LocationsListFragmentAdmin fragment = new LocationsListFragmentAdmin();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.admin_fragment_locations_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.addCitiesButtonOnFragment = view.findViewById(R.id.fab);
        this.recyclerView = view.findViewById(R.id.recycler_locations);
        this.manager = new LinearLayoutManager(LocationsListFragmentAdmin.this.requireActivity());

        this.list = new ArrayList<>();
        this.areaIndexDataHolder = new AreaIndexDataHolder();

        this.progressDialog = new LoadingDialogue(LocationsListFragmentAdmin.this.requireActivity());

        this.popupView = this.getLayoutInflater().inflate(R.layout.admin_popup_add_locations, null, false);

        this.customPopupViewHolder = new CustomPopupViewHolder(popupView);

        this.dialogueBuilder = new AlertDialog.Builder(requireActivity());

        this.dialogueBuilder.setView(popupView);
        this.itemPopupDialogueBox = dialogueBuilder.create();

        this.progressDialog.show("Please Wait", "Fetching Locations data");

        this.adapterAdmin = new CitiesItemAdapterAdmin(
                LocationsListFragmentAdmin.this.requireActivity(),
                list,
                this,
                this
        );


        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapterAdmin);

        backgroundExecutorForShowingData(view);

        //Popup Display
        addCitiesButtonOnFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle("Listing -> Locations -> Add");

                customPopupViewHolder.setViewsReadyForAction(CustomPopupViewHolder.ACTION_ADD_MAIN_CATEGORY);
                action = CustomPopupViewHolder.ACTION_ADD_MAIN_CATEGORY;
                itemPopupDialogueBox.show();

            }
        });

        customPopupViewHolder.getAddCategoryButton().setOnClickListener(this);
    }

    private void backgroundExecutorForShowingData(View view) {

        LOCATIONS_COLLECTION_REFERENCE
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            adapterAdmin.deleteAll();
                            list.clear();
                            list = new ArrayList<>();
                            list.addAll(task.getResult().toObjects(CityModel.class));
                            adapterAdmin.addAll(list);
                            view.setVisibility(View.VISIBLE);

                            progressDialog.dismiss();

                        } else {
                            Toast.makeText(LocationsListFragmentAdmin.this.requireActivity(), "Error Fetching Activities Data", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    }
                });

    }

    @Override
    public void onPause() {
        super.onPause();
        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).setSubtitle(null);
    }

    @Override
    public void onResume() {
        super.onResume();
        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).setSubtitle("Listing -> Locations");
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == customPopupViewHolder.getAddCategoryButton().getId()) {
            if (action == CustomPopupViewHolder.ACTION_ADD_MAIN_CATEGORY) {

                if (customPopupViewHolder.getEdxCate().getText().toString().isEmpty()) {
                    customPopupViewHolder.getEdxCate().setError("Location name Cannot be Empty");
                } else {
                    String title = customPopupViewHolder.getEdxCate().getText().toString();
                    LOCATIONS_COLLECTION_REFERENCE
                            .add(new CityModel(
                                    "",
                                    title,
                                    new ArrayList<AreaModel>()
                            ))
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle("Listing -> Locations");
                                    backgroundExecutorForShowingData(LocationsListFragmentAdmin.this.getView());
                                    itemPopupDialogueBox.dismiss();

                                    documentReference.update("id", documentReference.getId());
                                }
                            });
                }


            }
            else if (action == CustomPopupViewHolder.ACTION_ADD_SUB_CATEGORY) {

                if (customPopupViewHolder.inputAnalyzer(true)) {
                    AreaModel subCategory = new AreaModel(
                            "",
                            customPopupViewHolder.getEdxCate().getText().toString()
                    );
                    LOCATIONS_COLLECTION_REFERENCE
                            .document(docID)
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    subCategory.setId(getRandomDocumentID(((String) Objects.requireNonNull(task.getResult().get("name"))).substring(0, 2)));
                                    LOCATIONS_COLLECTION_REFERENCE.document(docID).update("areas", FieldValue.arrayUnion(subCategory));
                                    LOCATIONS_COLLECTION_REFERENCE.document(docID).update("areasCount", FieldValue.increment(1));
                                    backgroundExecutorForShowingData(LocationsListFragmentAdmin.this.getView());
                                    itemPopupDialogueBox.dismiss();
                                }
                            });
                }

            }
            else if (action == CustomPopupViewHolder.ACTION_UPDATE_MAIN_CATEGORY) {
                if (customPopupViewHolder.inputAnalyzer(true)) {

                    cityModel.setName(customPopupViewHolder.getEdxCate().getText().toString());

                    progressDialog.show("Please Wait", "While We Are Updating Data");

                    LOCATIONS_COLLECTION_REFERENCE
                            .document(cityModel.getId())
                            .set(cityModel)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        itemPopupDialogueBox.dismiss();
                                        Toast.makeText(requireActivity(), "Updated Successfully", Toast.LENGTH_SHORT).show();
                                        customPopupViewHolder.setViewsEmpty();
                                        backgroundExecutorForShowingData(LocationsListFragmentAdmin.this.getView());

                                    } else if (task.isCanceled()) {
                                        itemPopupDialogueBox.dismiss();
                                        Toast.makeText(requireActivity(), "Update Failed", Toast.LENGTH_SHORT).show();
                                        customPopupViewHolder.setViewsEmpty();
                                    }
                                    progressDialog.dismiss();
                                }
                            });
                }

            }
            else if (action == CustomPopupViewHolder.ACTION_UPDATE_SUB_CATEGORY) {
                if (customPopupViewHolder.inputAnalyzer(true)) {

                    areaIndexDataHolder.getAreaModel().setName(customPopupViewHolder.getEdxCate().getText().toString());

                    progressDialog.show("Please Wait", "While We Are Updating Area Data");

                    LOCATIONS_COLLECTION_REFERENCE
                            .document(areaIndexDataHolder.getMainDocID())
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                    if (task.isSuccessful()) {
                                        cityModel = task.getResult().toObject(CityModel.class);

                                        if (cityModel != null) {

                                            if (cityModel.getAreas() != null) {
                                                cityModel.getAreas().remove(areaIndexDataHolder.getSubDocIndex());
                                                cityModel.getAreas().add(areaIndexDataHolder.getSubDocIndex(), areaIndexDataHolder.getAreaModel());
                                            }

                                        }
                                        LOCATIONS_COLLECTION_REFERENCE
                                                .document(areaIndexDataHolder.getMainDocID())
                                                .set(cityModel)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            itemPopupDialogueBox.dismiss();
                                                            Toast.makeText(requireActivity(), "Updated Successfully", Toast.LENGTH_SHORT).show();
                                                            customPopupViewHolder.setViewsEmpty();
                                                            backgroundExecutorForShowingData(LocationsListFragmentAdmin.this.getView());

                                                        } else if (task.isCanceled()) {
                                                            itemPopupDialogueBox.dismiss();
                                                            Toast.makeText(requireActivity(), "Update Failed", Toast.LENGTH_SHORT).show();
                                                            customPopupViewHolder.setViewsEmpty();
                                                        }
                                                        progressDialog.dismiss();

                                                    }
                                                });
                                    }
                                }
                            });
                }

            }

            customPopupViewHolder.setViewsEmpty();

        }
    }

    private String getRandomDocumentID(String mainCategoryName) {

        return mainCategoryName + "-X-" +((int) (Math.random() * 11) + 1) + "-X-" + new Date().getTime();

    }

    @Override
    public void onUpdateAreaItemClick(View v, int position, String mainDocID, int subDocIndex, AreaModel areaModel) {
        action = CustomPopupViewHolder.ACTION_UPDATE_SUB_CATEGORY;
        customPopupViewHolder.setViewsReadyForAction(action);

        areaIndexDataHolder.setMainDocID(mainDocID);
        areaIndexDataHolder.setAreaModel(areaModel);
        areaIndexDataHolder.setSubDocIndex(subDocIndex);

        customPopupViewHolder.getEdxCate().setText(areaModel.getName());

        itemPopupDialogueBox.show();
    }

    @Override
    public void onDeleteAreaItemClick(View v, int position, String mainDocID, int subDocIndex, AreaModel areaToDelete) {

        Log.e("DATA",""+mainDocID+"."+subDocIndex);

        LOCATIONS_COLLECTION_REFERENCE
                .document(mainDocID)
                .update("areas", FieldValue.arrayRemove(areaToDelete))
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            LOCATIONS_COLLECTION_REFERENCE.document(mainDocID).update("areasCount", FieldValue.increment(-1));

                            backgroundExecutorForShowingData(LocationsListFragmentAdmin.this.getView());
                        }
                        else
                            Toast.makeText(LocationsListFragmentAdmin.this.requireActivity(), "Task not Successful", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onAddAreaItemClickOverCity(View v, int position, String docID) {
        this.docID = docID;
        action = CustomPopupViewHolder.ACTION_ADD_SUB_CATEGORY;
        customPopupViewHolder.setViewsReadyForAction(CustomPopupViewHolder.ACTION_ADD_SUB_CATEGORY);
        itemPopupDialogueBox.show();
    }

    @Override
    public void onUpdateCityItemClick(View v, int position, CityModel cityModel) {
        this.cityModel = cityModel;

        action = CustomPopupViewHolder.ACTION_UPDATE_MAIN_CATEGORY;
        customPopupViewHolder.getEdxCate().setText(cityModel.getName());

        customPopupViewHolder.setViewsReadyForAction(CustomPopupViewHolder.ACTION_UPDATE_MAIN_CATEGORY);
        itemPopupDialogueBox.show();
    }

    @Override
    public void onDeleteCityItemClick(View v, int position, String docID, String title) {
        LOCATIONS_COLLECTION_REFERENCE
                .document(docID)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        adapterAdmin.deleteCategory(position);
                    }
                });
    }

    public static class CustomPopupViewHolder {

        public static final int ACTION_ADD_MAIN_CATEGORY = 0;
        public static final int ACTION_ADD_SUB_CATEGORY = 1;
        public static final int ACTION_UPDATE_MAIN_CATEGORY = 2;
        public static final int ACTION_UPDATE_SUB_CATEGORY = 3;
        private final TextInputEditText edxCate;
        private final MaterialButton addCategory;

        public CustomPopupViewHolder(View view) {
            edxCate = view.findViewById(R.id.cateTitle);
            addCategory = view.findViewById(R.id.addCategory);
        }

        public TextInputEditText getEdxCate() {
            return edxCate;
        }

        public MaterialButton getAddCategoryButton() {
            return addCategory;
        }

        void setViewsReadyForAction(int ACTION_CODE) {
            if (ACTION_CODE == ACTION_ADD_MAIN_CATEGORY) {
                edxCate.setHint("Enter City Name");
                addCategory.setText("Add City");

            } else if (ACTION_CODE == ACTION_ADD_SUB_CATEGORY) {
                edxCate.setHint("Enter Area Name");
                addCategory.setText("Add Area");

            } else if (ACTION_CODE == ACTION_UPDATE_MAIN_CATEGORY) {
                edxCate.setHint("Enter New Name");
                addCategory.setText("Update City");
            } else if (ACTION_CODE == ACTION_UPDATE_SUB_CATEGORY) {
                edxCate.setHint("Enter New Name");
                addCategory.setText("Update Area");

            }
        }

        public boolean inputAnalyzer(boolean b) {
            //TODO Modify this method
            return true;
        }

        public void setViewsEmpty() {
            edxCate.setText("");
        }
    }

    private class AreaIndexDataHolder {
        private String mainDocID;
        private int subDocIndex;
        private AreaModel areaModel;

        public AreaIndexDataHolder() {
            this.mainDocID = "SX7JBR81GmqlHh6MD51M";
            this.subDocIndex = 0;
            this.areaModel = new AreaModel();
            this.areaModel.setId(this.mainDocID);
        }


        public String getMainDocID() {
            return mainDocID;
        }

        public void setMainDocID(String mainDocID) {
            this.mainDocID = mainDocID;
        }

        public int getSubDocIndex() {
            return subDocIndex;
        }

        public void setSubDocIndex(int subDocIndex) {
            this.subDocIndex = subDocIndex;
        }

        public AreaModel getAreaModel() {
            return areaModel;
        }

        public void setAreaModel(AreaModel areaModel) {
            this.areaModel = areaModel;
        }
    }
}
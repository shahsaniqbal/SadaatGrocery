package com.sadaat.groceryapp.ui.Fragments.UserBased.Admin.UnderListingFragmentChildSuper;

import android.app.AlertDialog;
import android.os.Bundle;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.sadaat.groceryapp.R;
import com.sadaat.groceryapp.adapters.CategoriesItemAdapterAdmin;
import com.sadaat.groceryapp.models.CategoriesModel;
import com.sadaat.groceryapp.temp.FirebaseDataKeys;
import com.sadaat.groceryapp.ui.Loaders.LoadingDialogue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CategoriesListFragmentAdmin extends Fragment implements CategoriesItemAdapterAdmin.CategoriesItemAdapterListener {

    FloatingActionButton addCategoriesButtonOnFragment;

    AlertDialog.Builder dialogueBuilder;
    AlertDialog itemPopupDialogueBox;
    View popupView;

    CollectionReference menuCollectionReference = FirebaseFirestore.getInstance().collection(new FirebaseDataKeys().getMenuRef());

    RecyclerView recyclerView;
    RecyclerView.LayoutManager manager;

    CategoriesItemAdapterAdmin adapterAdmin;
    ArrayList<CategoriesModel> list;

    LoadingDialogue progressDialog;

    CustomPopupViewHolder customPopupViewHolder;

    public CategoriesListFragmentAdmin() {
    }

    public static CategoriesListFragmentAdmin newInstance() {
        CategoriesListFragmentAdmin fragment = new CategoriesListFragmentAdmin();
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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.admin_fragment_categories_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        addCategoriesButtonOnFragment = view.findViewById(R.id.fab);
        recyclerView = view.findViewById(R.id.recycler_categories);
        manager = new LinearLayoutManager(CategoriesListFragmentAdmin.this.requireActivity());

        list = new ArrayList<>();
        progressDialog = new LoadingDialogue(CategoriesListFragmentAdmin.this.requireActivity());

        popupView = this.getLayoutInflater().inflate(R.layout.admin_popup_add_categories, null, false);

        customPopupViewHolder = new CustomPopupViewHolder(popupView);

        dialogueBuilder = new AlertDialog.Builder(requireActivity());

        dialogueBuilder.setView(popupView);
        itemPopupDialogueBox = dialogueBuilder.create();

        progressDialog.show("Please Wait", "While We Are Fetching Categories for you");


        adapterAdmin = new CategoriesItemAdapterAdmin(list, this);


        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapterAdmin);

        backgroundExecutorForShowingData(view);

        //Popup Display
        addCategoriesButtonOnFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle("Listing -> Categories -> Add");

                itemPopupDialogueBox.show();

                customPopupViewHolder.setViewsReadyForAction(CustomPopupViewHolder.ACTION_ADD_MAIN_CATEGORY);

                customPopupViewHolder.getAddCategoryButton().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (customPopupViewHolder.getEdxCate().getText().toString().isEmpty()) {
                            customPopupViewHolder.getEdxCate().setError("Category or SubCategory Title Cannot be Empty");

                        } else {
                            String title = customPopupViewHolder.getEdxCate().getText().toString();
                            String desc = customPopupViewHolder.getEdxCateDescription().getText().toString();

                            menuCollectionReference
                                    .add(new CategoriesModel(title, desc, true))
                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {

                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {
                                            ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle("Listing -> Categories");
                                            backgroundExecutorForShowingData(view);
                                            itemPopupDialogueBox.dismiss();

                                            documentReference.update("docID", documentReference.getId());
                                        }
                                    });
                        }
                    }
                });
            }
        });
    }

    private void backgroundExecutorForShowingData(View view) {

        menuCollectionReference
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            adapterAdmin.deleteAll();
                            list.clear();
                            list = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                adapterAdmin.addCategory(
                                        new CategoriesModel(
                                                document.getId(),
                                                (String) document.get("title"),
                                                (String) document.get("description"),
                                                (Boolean) document.get("hasSubcategories"),
                                                (ArrayList<CategoriesModel>) document.get("subCategories")
                                        )

                                );
                            }

                            view.setVisibility(View.VISIBLE);

                            Toast.makeText(CategoriesListFragmentAdmin.this.requireActivity(), "" + list.size(), Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();

                        } else {

                        }
                    }
                });


    }

    @Override
    public void onPause() {
        super.onPause();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle(null);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle("Listing -> Categories");
    }

    @Override
    public void onAddSubCategoryItemClick(View v, int position) {
        boolean isParent = false;
        customPopupViewHolder.setViewsReadyForAction(CustomPopupViewHolder.ACTION_ADD_SUB_CATEGORY);


    }

    @Override
    public void onUpdateSubCategoryItemClick(View v, int position, CategoriesModel categoriesModel) {

        itemPopupDialogueBox.show();
        customPopupViewHolder.getEdxCate().setText(categoriesModel.getTitle());
        customPopupViewHolder.getEdxCateDescription().setText(categoriesModel.getDescription());

        if (categoriesModel.isParent()) {
            customPopupViewHolder.setViewsReadyForAction(CustomPopupViewHolder.ACTION_UPDATE_MAIN_CATEGORY);

            customPopupViewHolder.getAddCategoryButton().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (customPopupViewHolder.inputAnalyzer(true)) {

                        categoriesModel.setTitle(customPopupViewHolder.getEdxCate().getText().toString());
                        categoriesModel.setDescription(customPopupViewHolder.getEdxCateDescription().getText().toString());

                        progressDialog.show("Please Wait", "While We Are Updating Data");

                        menuCollectionReference
                                .document(categoriesModel.getDocID())
                                .set(categoriesModel)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            itemPopupDialogueBox.dismiss();
                                            Toast.makeText(requireActivity(), "Updated Successfully", Toast.LENGTH_SHORT).show();
                                            customPopupViewHolder.setViewsEmpty();
                                            backgroundExecutorForShowingData(CategoriesListFragmentAdmin.this.getView());

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

        } else {
            customPopupViewHolder.setViewsReadyForAction(CustomPopupViewHolder.ACTION_UPDATE_SUB_CATEGORY);
        }



    }

    @Override
    public void onDeleteSubCategoryItemClick(View v, int position, String docID, String title) {
        menuCollectionReference
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
        private final TextInputEditText edxCateDescription;
        private final MaterialButton addCategory;

        public CustomPopupViewHolder(View view) {
            edxCate = view.findViewById(R.id.cateTitle);
            edxCateDescription = view.findViewById(R.id.cateDesc);
            addCategory = view.findViewById(R.id.addCategory);
        }

        public TextInputEditText getEdxCate() {
            return edxCate;
        }

        public TextInputEditText getEdxCateDescription() {
            return edxCateDescription;
        }

        public MaterialButton getAddCategoryButton() {
            return addCategory;
        }

        void setViewsReadyForAction(int ACTION_CODE) {
            if (ACTION_CODE == ACTION_ADD_MAIN_CATEGORY) {
                edxCate.setHint("Enter Category Name");
                edxCateDescription.setHint("Enter Category Description\n(Optional)");
                addCategory.setText("Add Category");

            } else if (ACTION_CODE == ACTION_ADD_SUB_CATEGORY) {
                edxCate.setHint("Enter Subcategory Name");
                edxCateDescription.setHint("Enter Subcategory Description\n(Optional)");
                addCategory.setText("Add Subcategory");

            } else if (ACTION_CODE == ACTION_UPDATE_MAIN_CATEGORY) {
                edxCate.setHint("Enter New Name");
                edxCateDescription.setHint("Enter New Description for Category\n(Optional)");
                addCategory.setText("Update Category");
            } else if (ACTION_CODE == ACTION_UPDATE_SUB_CATEGORY) {
                edxCate.setHint("Enter New Name");
                edxCateDescription.setHint("Enter New Description for Subcategory\n(Optional)");
                addCategory.setText("Update Subcategory");

            }
        }

        public boolean inputAnalyzer(boolean b) {
            //TODO Modify this method
            return true;
        }

        public void setViewsEmpty() {

        }
    }


/*

        NO NEED OF ASYNCTASK
        AHSAN 08-03-2022

    class CustomSync extends AsyncTask<View, Object, Object>{


        View mainView;

        @Override
        protected Object doInBackground(View... views) {

            mainView = (View) views[0];


            return null;
        }

        @Override
        protected void onPreExecute() {

            super.onPreExecute();


        }

        @Override
        protected void onPostExecute(Object o) {

            super.onPostExecute(o);

            adapterAdmin.notifyDataSetChanged();


        }
    }
*/

}
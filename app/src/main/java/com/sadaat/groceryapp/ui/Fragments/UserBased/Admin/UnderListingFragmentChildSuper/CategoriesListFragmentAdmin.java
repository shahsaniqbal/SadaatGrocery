package com.sadaat.groceryapp.ui.Fragments.UserBased.Admin.UnderListingFragmentChildSuper;

import static android.app.Activity.RESULT_OK;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.sadaat.groceryapp.R;
import com.sadaat.groceryapp.adapters.admin.category.CategoriesItemAdapterAdmin;
import com.sadaat.groceryapp.adapters.admin.category.SubcategoriesItemAdapterAdmin;
import com.sadaat.groceryapp.models.CategoriesModel;
import com.sadaat.groceryapp.temp.FirebaseDataKeys;
import com.sadaat.groceryapp.ui.Loaders.LoadingDialogue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

public class CategoriesListFragmentAdmin extends Fragment implements
        CategoriesItemAdapterAdmin.CategoriesItemAdapterListener,
        View.OnClickListener,
        SubcategoriesItemAdapterAdmin.SubCategoriesItemAdapterListener {

    //Menu | Categories & Subcategories Reference
    final CollectionReference MENU_COLLECTION_REFERENCE = FirebaseFirestore.getInstance().collection(new FirebaseDataKeys().getMenuRef());
    private final int IMAGE_SEL_REQ = 233;
    //Button for Adding Categories
    FloatingActionButton addCategoriesButtonOnFragment;
    //Popup Form For Adding & Updating Categories & Subcategories
    AlertDialog.Builder dialogueBuilder;
    AlertDialog itemPopupDialogueBox;
    View popupView;
    CustomPopupViewHolder customPopupViewHolder;
    //For Listing Categories
    RecyclerView recyclerView;
    RecyclerView.LayoutManager manager;
    CategoriesItemAdapterAdmin adapterAdmin;
    ArrayList<CategoriesModel> list;
    LoadingDialogue progressDialog;
    int action = 0;
    String docID = "";
    CategoriesModel categoriesModel = null;
    SubCategoryIndexDataHolder categoryIndexDataHolder;
    private Uri imageFetchUri;
    private final StorageReference storageRef = FirebaseStorage.getInstance().getReference();

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
        categoryIndexDataHolder = new SubCategoryIndexDataHolder();

        progressDialog = new LoadingDialogue(CategoriesListFragmentAdmin.this.requireActivity());

        popupView = this.getLayoutInflater().inflate(R.layout.admin_popup_add_categories, null, false);

        customPopupViewHolder = new CustomPopupViewHolder(popupView);

        dialogueBuilder = new AlertDialog.Builder(requireActivity());

        dialogueBuilder.setView(popupView);
        itemPopupDialogueBox = dialogueBuilder.create();

        progressDialog.show("Please Wait", "While We Are Fetching Categories for you");


        adapterAdmin = new CategoriesItemAdapterAdmin(
                CategoriesListFragmentAdmin.this.requireActivity(),
                list,
                this,
                this
        );


        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapterAdmin);

        backgroundExecutorForShowingData(view);


        //Popup Display
        addCategoriesButtonOnFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle("Listing -> Categories -> Add");

                customPopupViewHolder.setViewsReadyForAction(CustomPopupViewHolder.ACTION_ADD_MAIN_CATEGORY);
                action = CustomPopupViewHolder.ACTION_ADD_MAIN_CATEGORY;
                itemPopupDialogueBox.show();

            }
        });

        customPopupViewHolder.getAddCategoryButton().setOnClickListener(this);
        customPopupViewHolder.getImgViewAddImageToCategory().setOnClickListener(this);
    }

    private void backgroundExecutorForShowingData(View view) {

        MENU_COLLECTION_REFERENCE
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            adapterAdmin.deleteAll();
                            list.clear();
                            list = new ArrayList<>();
                            list.addAll(task.getResult().toObjects(CategoriesModel.class));
                            adapterAdmin.addAll(list);
                            view.setVisibility(View.VISIBLE);

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
    public void onAddSubCategoryItemClickOverCategory(View v, int position, String docID) {
        this.docID = docID;
        action = CustomPopupViewHolder.ACTION_ADD_SUB_CATEGORY;
        customPopupViewHolder.setViewsReadyForAction(CustomPopupViewHolder.ACTION_ADD_SUB_CATEGORY);
        itemPopupDialogueBox.show();
    }

    @Override
    public void onUpdateCategoryItemClick(View v, int position, CategoriesModel categoriesModel) {
        this.categoriesModel = categoriesModel;
        if (categoriesModel.isParent()) {
            action = CustomPopupViewHolder.ACTION_UPDATE_MAIN_CATEGORY;
        } else {
            action = CustomPopupViewHolder.ACTION_UPDATE_SUB_CATEGORY;
        }
        customPopupViewHolder.getEdxCate().setText(categoriesModel.getTitle());
        customPopupViewHolder.getEdxCateDescription().setText(categoriesModel.getDescription());
        customPopupViewHolder.setViewsReadyForAction(CustomPopupViewHolder.ACTION_UPDATE_MAIN_CATEGORY);
        itemPopupDialogueBox.show();

    }

    @Override
    public void onDeleteCategoryItemClick(View v, int position, String docID, String title) {
        MENU_COLLECTION_REFERENCE
                .document(docID)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        adapterAdmin.deleteCategory(position);
                    }
                });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == customPopupViewHolder.getAddCategoryButton().getId()) {
            if (action == CustomPopupViewHolder.ACTION_ADD_MAIN_CATEGORY) {

                if (customPopupViewHolder.getEdxCate().getText().toString().isEmpty()) {
                    customPopupViewHolder.getEdxCate().setError("Category or SubCategory Title Cannot be Empty");
                } else {
                    String title = customPopupViewHolder.getEdxCate().getText().toString();
                    String desc = customPopupViewHolder.getEdxCateDescription().getText().toString();
                    MENU_COLLECTION_REFERENCE
                            .add(new CategoriesModel(title, desc, true))
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle("Listing -> Categories");
                                    backgroundExecutorForShowingData(CategoriesListFragmentAdmin.this.getView());
                                    itemPopupDialogueBox.dismiss();

                                    documentReference.update("docID", documentReference.getId());
                                }
                            });
                }


            } else if (action == CustomPopupViewHolder.ACTION_ADD_SUB_CATEGORY) {

                if (customPopupViewHolder.inputAnalyzer(true)) {
                    CategoriesModel subCategory = new CategoriesModel(
                            customPopupViewHolder.getEdxCate().getText().toString(),
                            customPopupViewHolder.getEdxCateDescription().getText().toString(),
                            false,
                            false,
                            null
                    );
                    MENU_COLLECTION_REFERENCE.document(docID).update("hasSubcategories", true).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            MENU_COLLECTION_REFERENCE
                                    .document(docID)
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            subCategory.setDocID(getRandomDocumentID(((String) task.getResult().get("title")).substring(0, 2)));
                                            MENU_COLLECTION_REFERENCE.document(docID).update("subCategories", FieldValue.arrayUnion(subCategory));
                                            MENU_COLLECTION_REFERENCE.document(docID).update("subCategoriesCount", FieldValue.increment(1));
                                            backgroundExecutorForShowingData(CategoriesListFragmentAdmin.this.getView());
                                            itemPopupDialogueBox.dismiss();
                                        }
                                    });
                        }
                    });
                }

            } else if (action == CustomPopupViewHolder.ACTION_UPDATE_MAIN_CATEGORY) {
                if (customPopupViewHolder.inputAnalyzer(true)) {

                    categoriesModel.setTitle(customPopupViewHolder.getEdxCate().getText().toString());
                    categoriesModel.setDescription(customPopupViewHolder.getEdxCateDescription().getText().toString());

                    progressDialog.show("Please Wait", "While We Are Updating Data");

                    MENU_COLLECTION_REFERENCE
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

            } else if (action == CustomPopupViewHolder.ACTION_UPDATE_SUB_CATEGORY) {
                if (customPopupViewHolder.inputAnalyzer(true)) {

                    categoryIndexDataHolder.getCategoriesModel().setTitle(customPopupViewHolder.getEdxCate().getText().toString());
                    categoryIndexDataHolder.getCategoriesModel().setDescription(customPopupViewHolder.getEdxCateDescription().getText().toString());

                    progressDialog.show("Please Wait", "While We Are Updating Subcategory Data");

                    MENU_COLLECTION_REFERENCE
                            .document(new SubCategoryIndexDataHolder().getMainDocID())
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    categoriesModel = task.getResult().toObject(CategoriesModel.class);

                                    categoriesModel.getSubCategories().remove(categoryIndexDataHolder.getSubDocIndex());
                                    categoriesModel.getSubCategories().add(categoryIndexDataHolder.getSubDocIndex(), categoryIndexDataHolder.getCategoriesModel());

                                    MENU_COLLECTION_REFERENCE
                                            .document(categoryIndexDataHolder.getMainDocID())
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
                            });
                }

            }

            customPopupViewHolder.setViewsEmpty();

        } else if (v.getId() == customPopupViewHolder.getImgViewAddImageToCategory().getId()) {

            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(
                    Intent.createChooser(
                            intent,
                            "Select Image from here..."),
                    IMAGE_SEL_REQ);

        }
    }

    private String getRandomDocumentID(String mainCategoryName) {

        return mainCategoryName + ((int) (Math.random() * 11) + 1) + "-X-" + new Date().getTime();

    }

    @Override
    public void onUpdateSubCategoryItemClick(View v, int position, String mainDocID, int subDocIndex, CategoriesModel categoriesModel) {
        action = CustomPopupViewHolder.ACTION_UPDATE_SUB_CATEGORY;
        customPopupViewHolder.setViewsReadyForAction(action);

        categoryIndexDataHolder.setMainDocID(mainDocID);
        categoryIndexDataHolder.setCategoriesModel(categoriesModel);
        categoryIndexDataHolder.setSubDocIndex(subDocIndex);

        customPopupViewHolder.getEdxCate().setText(categoriesModel.getTitle());
        customPopupViewHolder.getEdxCateDescription().setText(categoriesModel.getDescription());

        itemPopupDialogueBox.show();
    }

    @Override
    public void onDeleteSubCategoryItemClick(View v, int position, String mainDocID, int subDocIndex, CategoriesModel subCategoryToDelete) {
        MENU_COLLECTION_REFERENCE
                .document(mainDocID)
                .update("subCategories", FieldValue.arrayRemove(subCategoryToDelete))
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            MENU_COLLECTION_REFERENCE.document(mainDocID).update("subCategoriesCount", FieldValue.increment(-1));
                            backgroundExecutorForShowingData(CategoriesListFragmentAdmin.this.getView());
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
            imageFetchUri = data.getData();
            try {

                // Setting image on image view using Bitmap
                Bitmap bitmap = MediaStore
                        .Images
                        .Media
                        .getBitmap(
                                this.requireActivity().getContentResolver(),
                                imageFetchUri);
                customPopupViewHolder.getImgViewAddImageToCategory().setImageBitmap(bitmap);

                uploadImage();
            } catch (IOException e) {
                // Log the exception
                e.printStackTrace();
            }
        }
    }

    private void uploadImage() {
        if (imageFetchUri != null) {

            // Code for showing progressDialog while uploading
            progressDialog.show("Uploading", "Image");

            // Defining the child of storageReference
            StorageReference ref
                    = storageRef
                    .child(
                            "images/"
                                    + UUID.randomUUID().toString());

            // adding listeners on upload
            // or failure of image
            ref.putFile(imageFetchUri)
                    .addOnSuccessListener(
                            new OnSuccessListener<UploadTask.TaskSnapshot>() {

                                @Override
                                public void onSuccess(
                                        UploadTask.TaskSnapshot taskSnapshot) {

                                    // Image uploaded successfully
                                    // Dismiss dialog
                                    progressDialog.dismiss();
                                    Toast
                                            .makeText(CategoriesListFragmentAdmin.this.requireActivity(),
                                                    "Image Uploaded!!",
                                                    Toast.LENGTH_SHORT)
                                            .show();
                                }
                            })

                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            // Error, Image not uploaded
                            progressDialog.dismiss();
                            Toast.makeText(CategoriesListFragmentAdmin.this.requireActivity(),
                                    "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(
                            new OnProgressListener<UploadTask.TaskSnapshot>() {

                                // Progress Listener for loading
                                // percentage on the dialog box
                                @Override
                                public void onProgress(
                                        UploadTask.TaskSnapshot taskSnapshot) {
                                    double progress
                                            = (100.0
                                            * taskSnapshot.getBytesTransferred()
                                            / taskSnapshot.getTotalByteCount());
                                    progressDialog.show(
                                            "Uploaded ", ""
                                                    + (int) progress + "%");
                                }
                            });

        }
    }

    public static class CustomPopupViewHolder {

        public static final int ACTION_ADD_MAIN_CATEGORY = 0;
        public static final int ACTION_ADD_SUB_CATEGORY = 1;
        public static final int ACTION_UPDATE_MAIN_CATEGORY = 2;
        public static final int ACTION_UPDATE_SUB_CATEGORY = 3;
        private final TextInputEditText edxCate;
        private final TextInputEditText edxCateDescription;
        private final MaterialButton addCategory;
        private final ImageView imgvAddImageToCategory;

        public CustomPopupViewHolder(View view) {
            edxCate = view.findViewById(R.id.cateTitle);
            edxCateDescription = view.findViewById(R.id.cateDesc);
            addCategory = view.findViewById(R.id.addCategory);
            imgvAddImageToCategory = view.findViewById(R.id.add_category_image);
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

        public ImageView getImgViewAddImageToCategory() {
            return imgvAddImageToCategory;
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
            edxCate.setText("");
            edxCateDescription.setText("");
        }
    }

    private class SubCategoryIndexDataHolder {
        private String mainDocID;
        private int subDocIndex;
        private CategoriesModel categoriesModel;

        public SubCategoryIndexDataHolder() {
            this.mainDocID = "SX7JBR81GmqlHh6MD51M";
            this.subDocIndex = 0;
            this.categoriesModel = new CategoriesModel();
            this.categoriesModel.setDocID(this.mainDocID);
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

        public CategoriesModel getCategoriesModel() {
            return categoriesModel;
        }

        public void setCategoriesModel(CategoriesModel categoriesModel) {
            this.categoriesModel = categoriesModel;
        }
    }
}
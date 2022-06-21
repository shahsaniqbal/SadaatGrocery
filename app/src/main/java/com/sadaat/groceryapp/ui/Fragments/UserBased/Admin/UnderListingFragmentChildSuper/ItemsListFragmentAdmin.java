package com.sadaat.groceryapp.ui.Fragments.UserBased.Admin.UnderListingFragmentChildSuper;

import static android.app.Activity.RESULT_OK;
import static android.view.View.GONE;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Spinner;
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
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.sadaat.groceryapp.R;
import com.sadaat.groceryapp.adapters.admin.ItemsDisplayAdapterAdmin;
import com.sadaat.groceryapp.models.categories.CategoriesModel;
import com.sadaat.groceryapp.models.Items.ItemModel;
import com.sadaat.groceryapp.models.Items.CategoryBindingModel;
import com.sadaat.groceryapp.models.Items.OtherDataForItem;
import com.sadaat.groceryapp.models.Items.PriceGroup;
import com.sadaat.groceryapp.models.Items.QtyUnitModel;
import com.sadaat.groceryapp.models.StockEntry;
import com.sadaat.groceryapp.models.categories.SubCategoriesModel;
import com.sadaat.groceryapp.temp.FirebaseDataKeys;
import com.sadaat.groceryapp.ui.Fragments.Generic.DetailedItemFragmentGeneric;
import com.sadaat.groceryapp.ui.Loaders.LoadingDialogue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

public class ItemsListFragmentAdmin extends Fragment implements ItemsDisplayAdapterAdmin.ItemClickListeners {

    private final int IMAGE_SEL_REQ = 234;
    FloatingActionButton addItemsButton;
    ItemsListFragmentAdmin.CustomPopupViewHolder viewHolder;
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    RecyclerView recyclerView;
    RecyclerView.LayoutManager manager;
    ItemsDisplayAdapterAdmin adapterAdmin;
    LoadingDialogue progressDialog;
    AlertDialog.Builder dialogueBuilder;
    AlertDialog itemPopupDialogueBox;
    View popupView;

    ArrayList<CategoriesModel> categoriesList;

    int categorySelected;
    int subCategorySelected;
    ArrayAdapter<String> categorySpinnerAdapter;
    ArrayAdapter<String> subcategorySpinnerAdapter;

    FirebaseStorage storage;
    StorageReference storageRef;
    MaterialButton addStockButton;
    //Uploaded Image path on Firebase Storage
    String imageResource = "";
    AlertDialog.Builder dialogueBuilderForStock;
    AlertDialog stockPopupDialogueBox;
    View stockPopupView;
    private Uri imageFetchUri;
    private MaterialTextView txvOldStock;


    public ItemsListFragmentAdmin() {

    }

    public static ItemsListFragmentAdmin newInstance() {
        ItemsListFragmentAdmin fragment = new ItemsListFragmentAdmin();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.admin_fragment_items_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializer(view);

        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapterAdmin);
        backgroundExecutorForShowingData(view);
        addItemsButton.setOnClickListener(v -> handlePopup(ActionConstants.ACTION_ADD, null, -1));

        viewHolder.getImageViewAddImageToItem().setOnClickListener(view12 -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(
                    Intent.createChooser(
                            intent,
                            "Select Image from here..."),
                    IMAGE_SEL_REQ);
        });


        viewHolder.getComboBoxCategory().setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view1, int position, long id) {

                categorySelected = position;
                subcategorySpinnerAdapter.clear();

                for (SubCategoriesModel subCategory :
                        categoriesList.get(position).getSubCategories()) {
                    subcategorySpinnerAdapter.add(subCategory.getTitle());
                    subcategorySpinnerAdapter.notifyDataSetChanged();
                }

                viewHolder.getComboBoxSubCategory().setVisibility(View.VISIBLE);


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        viewHolder.getComboBoxSubCategory().setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view1, int position, long id) {

                subCategorySelected = position;

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }

    @Override
    public void onPause() {
        super.onPause();
        Objects.requireNonNull(((AppCompatActivity) Objects.requireNonNull(requireActivity())).getSupportActionBar()).setSubtitle(null);
    }

    @Override
    public void onResume() {
        super.onResume();
        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).setSubtitle("Listing -> Items");
    }

    private void initializer(View view) {
        addItemsButton = view.findViewById(R.id.btn_admin_add_items);
        recyclerView = view.findViewById(R.id.admin_recycler_items);
        manager = new LinearLayoutManager(ItemsListFragmentAdmin.this.requireActivity());

        dialogueBuilder = new AlertDialog.Builder(requireActivity());

        popupView = this.getLayoutInflater().inflate(R.layout.admin_popup_add_items, null, false);
        dialogueBuilder.setView(popupView);

        itemPopupDialogueBox = dialogueBuilder.create();

        viewHolder = new CustomPopupViewHolder(popupView);

        progressDialog = new LoadingDialogue(requireActivity());

        adapterAdmin = new ItemsDisplayAdapterAdmin(new ArrayList<>(), this);

        categorySelected = 0;
        subCategorySelected = 0;

        categoriesList = new ArrayList<>();
        //subcategoriesList = new ArrayList<>();

        categorySpinnerAdapter = new ArrayAdapter<>(this.requireActivity(), android.R.layout.simple_spinner_item, new ArrayList<>());
        subcategorySpinnerAdapter = new ArrayAdapter<>(this.requireActivity(), android.R.layout.simple_spinner_item, new ArrayList<>());

        viewHolder.getComboBoxCategory().setAdapter(categorySpinnerAdapter);
        viewHolder.getComboBoxSubCategory().setAdapter(subcategorySpinnerAdapter);

        storage = FirebaseStorage.getInstance(FirebaseDataKeys.STORAGE_BUCKET_ADDRESS);
        storageRef = storage.getReference();

        dialogueBuilderForStock = new AlertDialog.Builder(ItemsListFragmentAdmin.this.requireActivity());
        stockPopupView = this.getLayoutInflater().inflate(R.layout.admin_popup_add_stock, null, false);
        dialogueBuilderForStock.setView(stockPopupView);
        stockPopupDialogueBox = dialogueBuilderForStock.create();

        addStockButton = stockPopupView.findViewById(R.id.addStock);
        txvOldStock = stockPopupView.findViewById(R.id.old_stock);

    }

    private void backgroundExecutorForShowingData(View view) {


        firebaseFirestore
                .collection(new FirebaseDataKeys().getItemsRef())
                .get()
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {

                        for (DocumentSnapshot d : task.getResult().getDocuments()) {
                            adapterAdmin.addItem(d.toObject(ItemModel.class));
                        }

                        view.setVisibility(View.VISIBLE);
                        progressDialog.dismiss();
                    } else {
                        Toast.makeText(ItemsListFragmentAdmin.this.requireActivity(), "Failed to Show Data", Toast.LENGTH_SHORT).show();
                    }
                });


    }

    @SuppressLint("SetTextI18n")
    private void handlePopup(int action, ItemModel oldModelToUpdate, int position) {


        viewHolder = new CustomPopupViewHolder(popupView);
        itemPopupDialogueBox.show();


        loadSpinnersData();


        Toast.makeText(ItemsListFragmentAdmin.this.requireActivity(),
                "For the better performance on Customer Side\n Click on Image Icon & Upload 1:1 Size Image",
                Toast.LENGTH_LONG).show();

        if (action == ActionConstants.ACTION_UPDATE) {

            popupView.findViewById(R.id.row_category).setVisibility(GONE);
            popupView.findViewById(R.id.row_subcategory).setVisibility(GONE);

            viewHolder.getAddItemButton().setText("Update Item");

            viewHolder.getEdxName().setText(oldModelToUpdate.getName());
            viewHolder.getEdxDesc().setText(oldModelToUpdate.getDescription());
            viewHolder.getEdxUnit().setText(oldModelToUpdate.getQty().getUnit());
            viewHolder.getEdxQty().setText("" + oldModelToUpdate.getQty().getQty());
            viewHolder.getEdxRetailPrice().setText("" + oldModelToUpdate.getPrices().getRetailPrice());
            viewHolder.getEdxSalePrice().setText("" + oldModelToUpdate.getPrices().getSalePrice());
            viewHolder.getEdxCardHolderDiscount().setText("" + oldModelToUpdate.getOtherDetails().getSpecialDiscountForCardHolder());
            viewHolder.getEdxSecurityCharges().setText("" + oldModelToUpdate.getOtherDetails().getSecurityCharges());
            viewHolder.getEdxStock().setVisibility(GONE);
            viewHolder.getEdxMaxQtyPerOrder().setText("" + oldModelToUpdate.getMaxQtyPerOrder());

            if (!oldModelToUpdate.getOtherDetails().getImageLink().equals("")) {
                viewHolder.edxDirectLink.setText(oldModelToUpdate.getOtherDetails().getImageLink());
                StorageReference imgRef = storageRef.child(oldModelToUpdate.getOtherDetails().getImageLink());
                final long ONE_MEGABYTE = 1024 * 1024;
                imgRef.getBytes(10 * ONE_MEGABYTE).addOnSuccessListener(bytes -> viewHolder.getImageViewAddImageToItem().setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length))).addOnFailureListener(exception -> Toast.makeText(ItemsListFragmentAdmin.this.requireActivity(), "Image Load Failed, \n Leave it or use a new one", Toast.LENGTH_SHORT).show());

            }

            if (imageResource.isEmpty()){
                imageResource = oldModelToUpdate.getOtherDetails().getImageLink();
            }

            viewHolder.getAddItemButton().setOnClickListener(v -> {
                if (viewHolder.analyzeInputs(true)) {
                    int max = 25;

                    if (!Objects.requireNonNull(viewHolder.getEdxMaxQtyPerOrder().getText()).toString().isEmpty())
                        max = Integer.parseInt(viewHolder.getEdxMaxQtyPerOrder().getText().toString());


                    oldModelToUpdate.setName(Objects.requireNonNull(viewHolder.getEdxName().getText()).toString());
                    oldModelToUpdate.setDescription(Objects.requireNonNull(viewHolder.getEdxDesc().getText()).toString());

                    //Hide the category and subcategory update data on Item Update
                    /*
                    oldModelToUpdate.setCategoryBinding(new CategoryBindingModel(
                            categoriesList.get(categorySelected).getDocID(),
                            categoriesList.get(categorySelected).getSubCategories().get(subCategorySelected).getDocID()
                    ));
                    */

                    oldModelToUpdate.setPrices(new PriceGroup(
                            Double.parseDouble(Objects.requireNonNull(viewHolder.getEdxRetailPrice().getText()).toString()),
                            Double.parseDouble(Objects.requireNonNull(viewHolder.getEdxSalePrice().getText()).toString())
                    ));
                    oldModelToUpdate.setOtherDetails(new OtherDataForItem(
                            Double.parseDouble(Objects.requireNonNull(viewHolder.getEdxSecurityCharges().getText()).toString()),
                            oldModelToUpdate.getOtherDetails().getStock(),
                            Double.parseDouble(Objects.requireNonNull(viewHolder.getEdxCardHolderDiscount().getText()).toString()),
                            imageResource
                    ));
                    oldModelToUpdate.setQty(new QtyUnitModel(
                            Double.parseDouble(Objects.requireNonNull(viewHolder.getEdxQty().getText()).toString()),
                            Objects.requireNonNull(viewHolder.getEdxUnit().getText()).toString()
                    ));
                    oldModelToUpdate.setMaxQtyPerOrder(max);

                    implementOnClickOnButtonClickOnPopup(ActionConstants.ACTION_UPDATE, oldModelToUpdate, position);

                }
            });

        }

        else if (action == ActionConstants.ACTION_ADD) {

            popupView.findViewById(R.id.row_category).setVisibility(View.VISIBLE);
            popupView.findViewById(R.id.row_subcategory).setVisibility(View.VISIBLE);

            viewHolder.getAddItemButton().setText(R.string.add_item);
            viewHolder.getEdxStock().setVisibility(View.VISIBLE);
            viewHolder.getAddItemButton().setOnClickListener(v -> {
                if (viewHolder.analyzeInputs(true)) {
                    int max = 25;
                    ArrayList<StockEntry> stocks = new ArrayList<>();
                    stocks.add(new StockEntry(new Date(), Integer.parseInt(Objects.requireNonNull(viewHolder.getEdxStock().getText()).toString())));

                    if (!Objects.requireNonNull(viewHolder.getEdxMaxQtyPerOrder().getText()).toString().isEmpty())
                        max = Integer.parseInt(viewHolder.getEdxMaxQtyPerOrder().getText().toString());

                    implementOnClickOnButtonClickOnPopup(ActionConstants.ACTION_ADD, new ItemModel(
                            "",
                            Objects.requireNonNull(viewHolder.getEdxName().getText()).toString(),
                            Objects.requireNonNull(viewHolder.getEdxDesc().getText()).toString(),
                            new CategoryBindingModel(
                                    categoriesList.get(categorySelected).getDocID(),
                                    categoriesList.get(categorySelected).getSubCategories().get(subCategorySelected).getDocID()
                            ),
                            new PriceGroup(
                                    Double.parseDouble(Objects.requireNonNull(viewHolder.getEdxRetailPrice().getText()).toString()),
                                    Double.parseDouble(Objects.requireNonNull(viewHolder.getEdxSalePrice().getText()).toString())
                            ),
                            new OtherDataForItem(
                                    Double.parseDouble(Objects.requireNonNull(viewHolder.getEdxSecurityCharges().getText()).toString()),
                                    Integer.parseInt(Objects.requireNonNull(viewHolder.getEdxStock().getText()).toString()),
                                    Double.parseDouble(Objects.requireNonNull(viewHolder.getEdxCardHolderDiscount().getText()).toString()),
                                    imageResource
                            ),
                            new QtyUnitModel(
                                    Double.parseDouble(Objects.requireNonNull(viewHolder.getEdxQty().getText()).toString()),
                                    Objects.requireNonNull(viewHolder.getEdxUnit().getText()).toString()
                            ),
                            stocks,
                            max,
                            false
                    ), -1);


                }
            });

            viewHolder.setViewsEmpty();

        }
    }

    @Override
    public PopupMenu setItemButtonPopupAtPosition(View v, int position, ItemModel itemModel) {

        PopupMenu popupMenu = new PopupMenu(
                requireActivity(),
                Objects.requireNonNull(manager.findViewByPosition(position)).findViewById(v.getId())
        );

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            popupMenu.setForceShowIcon(true);
        }
        popupMenu.getMenuInflater().inflate(R.menu.admin_option_menu_for_items_display, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_update_item) {
                onUpdateDetailsButtonClick(item.getActionView(), position, itemModel);
            } else if (item.getItemId() == R.id.action_delete_item) {
                onDeleteButtonClick(item.getActionView(), position, itemModel);
            } else if (item.getItemId() == R.id.action_add_stock) {
                onAddStockButtonClick(item.getActionView(), position, itemModel);
            } else if (item.getItemId() == R.id.action_show_full_modal_item) {
                onShowFullDetailsButtonClick(itemModel);
            }
            return true;
        });

        return popupMenu;

    }

    @Override
    public void onDeleteButtonClick(View v, int position, ItemModel modelToDelete) {
        FirebaseFirestore
                .getInstance()
                .collection(new FirebaseDataKeys().getItemsRef())
                .document(modelToDelete.getID())
                .delete()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        adapterAdmin.deleteItem(position);
                        Toast.makeText(requireActivity(), modelToDelete.getName() + " has been deleted", Toast.LENGTH_LONG).show();
                    }
                });
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onAddStockButtonClick(View v, int position, ItemModel model) {
        txvOldStock.setText("" + model.getOtherDetails().getStock());
        stockPopupDialogueBox.show();

        addStockButton.setOnClickListener(view -> {
            int newStockNumber;
            if (Objects.requireNonNull(((TextInputEditText) stockPopupView.findViewById(R.id.new_stock)).getText()).toString().isEmpty()) {
                newStockNumber = 0;
                ((TextInputEditText) stockPopupView.findViewById(R.id.new_stock)).setText("" + 0);
            } else {
                newStockNumber = Integer.parseInt(Objects.requireNonNull(((TextInputEditText) stockPopupView.findViewById(R.id.new_stock)).getText()).toString());
            }

            if (newStockNumber != 0) {
                StockEntry stockEntry = new StockEntry(new Date(), newStockNumber);

                FirebaseFirestore.getInstance()
                        .collection(new FirebaseDataKeys().getItemsRef())
                        .document(model.getID())
                        .update(
                                "otherDetails.stock", FieldValue.increment(newStockNumber),
                                "stockEntries", FieldValue.arrayUnion(stockEntry)
                        )
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(ItemsListFragmentAdmin.this.requireActivity(), "Stock Added Successfully", Toast.LENGTH_SHORT).show();
                                model.getOtherDetails().setStock(model.getOtherDetails().getStock() + newStockNumber);
                                adapterAdmin.notifyItemChanged(position);

                                stockPopupDialogueBox.dismiss();
                            } else {
                                Toast.makeText(ItemsListFragmentAdmin.this.requireActivity(), "Problem Updating Stock " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            } else {
                Toast.makeText(ItemsListFragmentAdmin.this.requireActivity(), "No Changes have been made, Thanks", Toast.LENGTH_SHORT).show();
                stockPopupDialogueBox.dismiss();
            }


            ((TextInputEditText) stockPopupView.findViewById(R.id.new_stock)).setText("");

        });

    }

    @Override
    public void onUpdateDetailsButtonClick(View v, int position, ItemModel oldModelToUpdate) {
        handlePopup(ActionConstants.ACTION_UPDATE, oldModelToUpdate, position);
    }

    private void implementOnClickOnButtonClickOnPopup(final int CURRENT_ACTION, ItemModel model, int position) {
        if (CURRENT_ACTION == ActionConstants.ACTION_ADD) {
            firebaseFirestore
                    .collection(new FirebaseDataKeys().getItemsRef())
                    .add(model)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {

                            firebaseFirestore
                                    .collection(new FirebaseDataKeys().getItemsRef())
                                    .document(task.getResult().getId())
                                    .update("id", task.getResult().getId())
                                    .addOnCompleteListener(task1 -> {
                                        adapterAdmin.addItem(model);
                                        progressDialog.dismiss();

                                        itemPopupDialogueBox.setCancelable(true);
                                        itemPopupDialogueBox.dismiss();
                                    });
                        }
                    });
        } else if (CURRENT_ACTION == ActionConstants.ACTION_UPDATE) {
            firebaseFirestore
                    .collection(new FirebaseDataKeys().getItemsRef())
                    .document(model.getID())
                    .set(model)
                    .addOnCompleteListener(task -> {
                        progressDialog.dismiss();
                        adapterAdmin.updateItem(position, model);
                        backgroundExecutorForShowingData(ItemsListFragmentAdmin.this.getView());
                        itemPopupDialogueBox.setCancelable(true);
                        itemPopupDialogueBox.dismiss();

                    });
        }
        itemPopupDialogueBox.dismiss();
        viewHolder.setViewsEmpty();

        imageResource = "";
        viewHolder.getImageViewAddImageToItem().setImageResource(R.mipmap.grocery_items);
    }

    @Override
    public void onShowFullDetailsButtonClick(ItemModel modelToShow) {
        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.items_fl, DetailedItemFragmentGeneric.newInstance(modelToShow))
                .addToBackStack("item_list")
                .commit();
    }

    @Override
    public void onIsHotButtonClick(boolean isHot, ImageButton view, String itemID, int position) {
        FirebaseFirestore
                .getInstance()
                .collection(new FirebaseDataKeys().getItemsRef())
                .document(itemID)
                .update("hot", isHot);
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

                ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 15, byteStream);

                //Setting Image Bitmap to my desired ImageView
                viewHolder.getImageViewAddImageToItem().setImageBitmap(bitmap);

                uploadImage(byteStream.toByteArray());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private void uploadImage(byte[] fileInBytes) {
        if (imageFetchUri != null) {

            // Code for showing progressDialog while uploading
            progressDialog.show("Uploading", "Image");

            // Defining the child of storageReference
            imageResource = "f/images/items/" + UUID.randomUUID().toString();

            StorageReference ref = storageRef.child(imageResource);

            ref.putBytes(fileInBytes)
                    .addOnSuccessListener(
                            taskSnapshot -> {
                                progressDialog.dismiss();
                                viewHolder.getEdxDirectLink().setText(imageResource);
                                Toast.makeText(
                                        ItemsListFragmentAdmin.this.requireActivity(),
                                        "Image Uploaded!!",
                                        Toast.LENGTH_SHORT
                                ).show();
                            })

                    .addOnFailureListener(e -> {
                        progressDialog.dismiss();
                        Log.e(e.getCause() + "\n", e.getMessage());
                        Toast.makeText(ItemsListFragmentAdmin.this.requireActivity(),
                                "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    })
                    .addOnProgressListener(
                            taskSnapshot -> {
                                double progress
                                        = (100.0
                                        * taskSnapshot.getBytesTransferred()
                                        / taskSnapshot.getTotalByteCount());
                                progressDialog.show(
                                        "Uploaded ", ""
                                                + (int) progress + "%");
                            });
        }
    }

    private void loadSpinnersData() {
        progressDialog.show("Loading", "Available Categories for Items");
        categoriesList.clear();
        categoriesList = new ArrayList<>();
        FirebaseFirestore
                .getInstance()
                .collection(new FirebaseDataKeys().getMenuRef())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        for (DocumentSnapshot d : task.getResult().getDocuments()) {
                            CategoriesModel model = d.toObject(CategoriesModel.class);
                            categoriesList.add(model);
                            assert model != null;
                            Log.e("Data", model.getDocID() + "...." + model.getTitle() + "...." + model.getSubCategories().size());
                            categorySpinnerAdapter.add(model.toString());
                            categorySpinnerAdapter.notifyDataSetChanged();
                        }
                        viewHolder.getComboBoxCategory().setVisibility(View.VISIBLE);
                        progressDialog.dismiss();
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(ItemsListFragmentAdmin.this.requireActivity(), "Error getting Locations, Please try again later", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private static class ActionConstants {
        public static final int ACTION_ADD = 0;
        public static final int ACTION_UPDATE = 1;
    }

    public class CustomPopupViewHolder {

        private final TextInputEditText edxName;

        private final TextInputEditText edxQty;
        private final TextInputEditText edxUnit;

        private final Spinner comboBoxCategory;
        private final Spinner comboBoxSubCategory;

        private final TextInputEditText edxDesc;

        private final TextInputEditText edxStock;

        private final TextInputEditText edxRetailPrice;
        private final TextInputEditText edxSalePrice;

        private final TextInputEditText edxSecurityCharges;
        private final TextInputEditText edxCardHolderDiscount;

        private final TextInputEditText edxMaxQtyPerOrder;
        private final TextInputEditText edxDirectLink;

        private final ImageView imageViewAddImageToItem;

        private final MaterialButton addItemButton;


        public CustomPopupViewHolder(View view) {

            edxName = view.findViewById(R.id.edx_add_items_title);
            edxDesc = view.findViewById(R.id.edx_add_items_desc);
            edxRetailPrice = view.findViewById(R.id.edx_add_items_retail);
            edxSalePrice = view.findViewById(R.id.edx_add_items_sale);
            edxStock = view.findViewById(R.id.edx_add_items_stock);
            edxQty = view.findViewById(R.id.edx_add_items_qty);
            edxUnit = view.findViewById(R.id.edx_add_items_unit);
            edxSecurityCharges = view.findViewById(R.id.edx_add_items_security);
            edxCardHolderDiscount = view.findViewById(R.id.edx_add_items_extra_card);

            comboBoxCategory = view.findViewById(R.id.admin_items_category_spinner);
            comboBoxSubCategory = view.findViewById(R.id.admin_items_subcategory_spinner);

            imageViewAddImageToItem = view.findViewById(R.id.add_item_image);

            edxMaxQtyPerOrder = view.findViewById(R.id.edx_add_items_max_qty_per_order);
            edxDirectLink = view.findViewById(R.id.edx_add_items_direct_link);

            addItemButton = view.findViewById(R.id.addItem);
        }

        public TextInputEditText getEdxName() {
            return edxName;
        }

        public TextInputEditText getEdxQty() {
            return edxQty;
        }

        public TextInputEditText getEdxUnit() {
            return edxUnit;
        }

        public Spinner getComboBoxCategory() {
            return comboBoxCategory;
        }

        public Spinner getComboBoxSubCategory() {
            return comboBoxSubCategory;
        }

        public TextInputEditText getEdxDesc() {
            return edxDesc;
        }

        public TextInputEditText getEdxStock() {
            return edxStock;
        }

        public TextInputEditText getEdxRetailPrice() {
            return edxRetailPrice;
        }

        public TextInputEditText getEdxSalePrice() {
            return edxSalePrice;
        }

        public TextInputEditText getEdxSecurityCharges() {
            return edxSecurityCharges;
        }

        public TextInputEditText getEdxCardHolderDiscount() {
            return edxCardHolderDiscount;
        }

        public MaterialButton getAddItemButton() {
            return addItemButton;
        }

        public ImageView getImageViewAddImageToItem() {
            return imageViewAddImageToItem;
        }

        public TextInputEditText getEdxMaxQtyPerOrder() {
            return edxMaxQtyPerOrder;
        }

        public TextInputEditText getEdxDirectLink() {
            return edxDirectLink;
        }

        public boolean analyzeInputs(boolean b) {

            /*if (imageResource.isEmpty() && action){
                Toast.makeText(ItemsListFragmentAdmin.this.requireActivity(), "Please Select a 1:1 Image", Toast.LENGTH_SHORT).show();
                b = false;
            }*/

            if (edxName.getText().toString().isEmpty()) {
                edxName.setError(edxName.getHint().toString() + " field can't be empty");
                b = false;
            } else if (edxQty.getText().toString().isEmpty()) {
                edxQty.setError(edxQty.getHint().toString() + " field can't be empty");
                b = false;
            } else if (edxUnit.getText().toString().isEmpty()) {
                edxUnit.setError(edxUnit.getHint().toString() + " field can't be empty");
                b = false;

            } else if (categorySelected == -1) {
                Toast.makeText(ItemsListFragmentAdmin.this.requireActivity(), "Please Select Category", Toast.LENGTH_SHORT).show();
                b = false;
            } else if (subCategorySelected == -1) {
                Toast.makeText(ItemsListFragmentAdmin.this.requireActivity(), "Please Select Subcategory", Toast.LENGTH_SHORT).show();
                b = false;
            } else if (edxDesc.getText().toString().isEmpty()) {
                edxDesc.setText("");
            } else if (edxRetailPrice.getText().toString().isEmpty()) {
                edxRetailPrice.setError(edxRetailPrice.getHint().toString() + " field can't be empty");
                b = false;
            } else if (edxSalePrice.getText().toString().isEmpty()) {
                edxSalePrice.setError(edxSalePrice.getHint().toString() + " field can't be empty");
                b = false;
            } else if (edxSecurityCharges.getText().toString().isEmpty()) {
                edxSecurityCharges.setError(edxSecurityCharges.getHint().toString() + " field can't be empty");
                b = false;
            } else if (edxCardHolderDiscount.getText().toString().isEmpty()) {
                edxCardHolderDiscount.setError(edxCardHolderDiscount.getHint().toString() + " field can't be empty");
                b = false;
            } else if (edxStock.getText().toString().isEmpty()) {
                edxStock.setText("" + 0);
                edxStock.setError(edxStock.getHint().toString() + " field can't be empty");
                b = false;
            } else if (edxMaxQtyPerOrder.getText().toString().isEmpty()) {
                edxMaxQtyPerOrder.setText("" + 25);
            }

            return b;
        }

        @SuppressLint("SetTextI18n")
        public void setViewsEmpty() {
            edxName.setText("");
            edxQty.setText("");
            edxUnit.setText("");
            edxDesc.setText("");
            edxStock.setText("");
            edxRetailPrice.setText("");
            edxSalePrice.setText("");
            edxSecurityCharges.setText("");
            edxCardHolderDiscount.setText("");
            edxStock.setText("" + 0);
            edxDirectLink.setText("");
            edxMaxQtyPerOrder.setText("");
            //imageViewAddImageToItem.setImageResource(R.mipmap.grocery_items);
            categorySelected = -1;
            subCategorySelected = -1;
            imageResource = "";
            categorySpinnerAdapter.clear();
            subcategorySpinnerAdapter.clear();
        }


    }

}
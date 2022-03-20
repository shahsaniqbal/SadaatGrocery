package com.sadaat.groceryapp.ui.Fragments.UserBased.Admin.UnderListingFragmentChildSuper;

import static android.view.View.GONE;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.sadaat.groceryapp.R;
import com.sadaat.groceryapp.adapters.admin.ItemsDisplayAdapterAdmin;
import com.sadaat.groceryapp.models.CategoriesModel;
import com.sadaat.groceryapp.models.ItemModel;
import com.sadaat.groceryapp.models.Items.CategoryBindingModel;
import com.sadaat.groceryapp.models.Items.OtherDataForItem;
import com.sadaat.groceryapp.models.Items.PriceGroup;
import com.sadaat.groceryapp.models.Items.QtyUnitModel;
import com.sadaat.groceryapp.temp.FirebaseDataKeys;
import com.sadaat.groceryapp.ui.Loaders.LoadingDialogue;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ItemsListFragmentAdmin extends Fragment implements ItemsDisplayAdapterAdmin.ItemClickListeners {

    FloatingActionButton addItemsButton;
    ItemsListFragmentAdmin.CustomPopupViewHolder viewHolder;

    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    RecyclerView recyclerView;
    RecyclerView.LayoutManager manager;

    ItemsDisplayAdapterAdmin adapterAdmin;
    LoadingDialogue progressDialog;
    ArrayList<ItemModel> allItems;

    AlertDialog.Builder dialogueBuilder;
    AlertDialog itemPopupDialogueBox;
    View popupView;

    ArrayList<CategoriesModel> subcategories;

    ArrayList<String> categoriesList;
    ArrayList<String> subcategoriesList;

    String categoryIDSelected;
    String subCategoryIDSelected;

    ArrayAdapter<String> categorySpinnerAdapter;
    ArrayAdapter<String> subcategorySpinnerAdapter;

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
        addItemsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handlePopup(view, ActionConstants.ACTION_ADD, null);
            }
        });


        progressDialog.show("Please Wait", "Loading Categories");


        FirebaseFirestore.getInstance().collection(new FirebaseDataKeys().getMenuRef())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<DocumentSnapshot> categoriesListRaw = task.getResult().getDocuments();


                            ArrayList<CategoriesModel> categories = new ArrayList<>();


                            for (DocumentSnapshot d :
                                    categoriesListRaw) {

                                CategoriesModel category = new CategoriesModel(d.getId(), Objects.requireNonNull(d.toObject(CategoriesModel.class)));
                                categories.add(category);
                                categorySpinnerAdapter.add(category.toString());
                            }

                            viewHolder.getComboBoxCategory().setVisibility(View.VISIBLE);
                            categoryIDSelected = Objects.requireNonNull(categoriesListRaw.get(0).toObject(CategoriesModel.class)).getDocID();

                            progressDialog.dismiss();

                            viewHolder.getComboBoxCategory().setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                                    categoryIDSelected = categories.get(position).getDocID();
                                    subcategories = categories.get(position).getSubCategories();

                                    subcategorySpinnerAdapter.clear();
                                    progressDialog.show("Please Wait", "Loading Subcategories of " + categories.get(position).getTitle());

                                    for (CategoriesModel subCategory :
                                            subcategories) {
                                        subcategorySpinnerAdapter.add(subCategory.toString());
                                    }

                                    progressDialog.dismiss();

                                    viewHolder.getComboBoxSubCategory().setVisibility(View.VISIBLE);

                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {

                                }
                            });
                            viewHolder.getComboBoxSubCategory().setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    subCategoryIDSelected = subcategories.get(position).getDocID();
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {

                                }
                            });


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
        ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle("Listing -> Items");
    }

    private void initializer(View view) {
        allItems = new ArrayList<>();
        addItemsButton = view.findViewById(R.id.btn_admin_add_items);
        recyclerView = view.findViewById(R.id.admin_recycler_items);
        manager = new LinearLayoutManager(ItemsListFragmentAdmin.this.requireActivity());

        dialogueBuilder = new AlertDialog.Builder(requireActivity());

        popupView = this.getLayoutInflater().inflate(R.layout.admin_popup_add_items, null, false);
        dialogueBuilder.setView(popupView);

        itemPopupDialogueBox = dialogueBuilder.create();

        viewHolder = new CustomPopupViewHolder(popupView);

        progressDialog = new LoadingDialogue(requireActivity());

        adapterAdmin = new ItemsDisplayAdapterAdmin(allItems, this, requireActivity());

        categoryIDSelected = "";
        subCategoryIDSelected = "";

        categoriesList = new ArrayList<>();
        subcategoriesList = new ArrayList<>();

        categorySpinnerAdapter = new ArrayAdapter<String>(requireActivity(), android.R.layout.simple_spinner_item, categoriesList);
        subcategorySpinnerAdapter = new ArrayAdapter<String>(requireActivity(), android.R.layout.simple_spinner_item, subcategoriesList);

        viewHolder.getComboBoxCategory().setAdapter(categorySpinnerAdapter);
        viewHolder.getComboBoxSubCategory().setAdapter(subcategorySpinnerAdapter);

    }

    private void backgroundExecutorForShowingData(View view) {

        progressDialog.show("Please Wait", "Loading Items");

        firebaseFirestore
                .collection(new FirebaseDataKeys().getItemsRef())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {

                            allItems.clear();
                            for (DocumentSnapshot d : task.getResult().getDocuments()) {
                                adapterAdmin.addItem(d.toObject(ItemModel.class));
                            }

                            view.setVisibility(View.VISIBLE);
                            progressDialog.dismiss();
                        } else {
                            Toast.makeText(ItemsListFragmentAdmin.this.requireActivity(), "Failed to Show Data", Toast.LENGTH_SHORT).show();
                        }
                    }
                });


    }

    private void handlePopup(View parent, int action, ItemModel oldModelToUpdate) {


        viewHolder = new CustomPopupViewHolder(popupView);
        itemPopupDialogueBox.show();

        if (action == ActionConstants.ACTION_UPDATE) {

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


            viewHolder.getAddItemButton().setOnClickListener(new View.OnClickListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onClick(View v) {
                    if (viewHolder.analyzeInputs(true)) {


                        oldModelToUpdate.setName(Objects.requireNonNull(viewHolder.getEdxName().getText()).toString());
                        oldModelToUpdate.setDescription(Objects.requireNonNull(viewHolder.getEdxDesc().getText()).toString());
                        oldModelToUpdate.setCategoryBinding(new CategoryBindingModel(
                                categoryIDSelected,
                                subCategoryIDSelected
                        ));
                        oldModelToUpdate.setPrices(new PriceGroup(
                                Double.parseDouble(Objects.requireNonNull(viewHolder.getEdxRetailPrice().getText()).toString()),
                                Double.parseDouble(Objects.requireNonNull(viewHolder.getEdxSalePrice().getText()).toString())
                        ));
                        oldModelToUpdate.setOtherDetails(new OtherDataForItem(
                                Double.parseDouble(Objects.requireNonNull(viewHolder.getEdxSecurityCharges().getText()).toString()),
                                oldModelToUpdate.getOtherDetails().getStock(),
                                Double.parseDouble(Objects.requireNonNull(viewHolder.getEdxCardHolderDiscount().getText()).toString()),
                                viewHolder.getImgLink()
                        ));
                        oldModelToUpdate.setQty(new QtyUnitModel(
                                Double.parseDouble(Objects.requireNonNull(viewHolder.getEdxQty().getText()).toString()),
                                Objects.requireNonNull(viewHolder.getEdxUnit().getText()).toString()
                        ));

                        implementOnClickOnButtonClickOnPopup(ActionConstants.ACTION_UPDATE, oldModelToUpdate);

                    }
                }
            });

        } else if (action == ActionConstants.ACTION_ADD) {
            viewHolder.getAddItemButton().setText(R.string.add_item);
            viewHolder.getEdxStock().setVisibility(View.VISIBLE);
            viewHolder.getAddItemButton().setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (viewHolder.analyzeInputs(true)) {

                        implementOnClickOnButtonClickOnPopup(ActionConstants.ACTION_ADD, new ItemModel(
                                Objects.requireNonNull(viewHolder.getEdxName().getText()).toString(),
                                Objects.requireNonNull(viewHolder.getEdxDesc().getText()).toString(),
                                new CategoryBindingModel(
                                        categoryIDSelected,
                                        subCategoryIDSelected
                                ),
                                new PriceGroup(
                                        Double.parseDouble(viewHolder.getEdxRetailPrice().getText().toString()),
                                        Double.parseDouble(viewHolder.getEdxSalePrice().getText().toString())
                                ),
                                new OtherDataForItem(
                                        Double.parseDouble(viewHolder.getEdxSecurityCharges().getText().toString()),
                                        Integer.parseInt(viewHolder.getEdxStock().getText().toString()),
                                        Double.parseDouble(viewHolder.getEdxCardHolderDiscount().getText().toString()),
                                        viewHolder.getImgLink()
                                ),
                                new QtyUnitModel(
                                        Double.parseDouble(viewHolder.getEdxQty().getText().toString()),
                                        viewHolder.getEdxUnit().getText().toString()
                                )
                        ));


                    }
                }
            });

        }
    }

    @Override
    public PopupMenu setItemButtonPopupAtPosition(View v, int position, ItemModel itemModel) {

        PopupMenu popupMenu = new PopupMenu(
                requireActivity(),
                manager.findViewByPosition(position).findViewById(v.getId())
        );

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            popupMenu.setForceShowIcon(true);
        }
        popupMenu.getMenuInflater().inflate(R.menu.admin_option_menu_for_items_display, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
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
            }
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
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            adapterAdmin.deleteItem(position);
                            Toast.makeText(requireActivity(), modelToDelete.getName() + " has been deleted", Toast.LENGTH_LONG).show();
                        }
                    }

                });
    }

    @Override
    public void onAddStockButtonClick(View v, int position, ItemModel model) {

    }

    @Override
    public void onUpdateDetailsButtonClick(View v, int position, ItemModel oldModelToUpdate) {

        handlePopup(ItemsListFragmentAdmin.this.getView(), ActionConstants.ACTION_UPDATE, oldModelToUpdate);

    }

    private void implementOnClickOnButtonClickOnPopup(final int CURRENT_ACTION, ItemModel model) {
        if (CURRENT_ACTION == ActionConstants.ACTION_ADD) {
            firebaseFirestore
                    .collection(new FirebaseDataKeys().getItemsRef())
                    .add(model)
                    .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            if (task.isSuccessful()) {

                                firebaseFirestore
                                        .collection(new FirebaseDataKeys().getItemsRef())
                                        .document(task.getResult().getId())
                                        .update("id", task.getResult().getId())
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                adapterAdmin.addItem(model);
                                                progressDialog.dismiss();

                                                itemPopupDialogueBox.setCancelable(true);
                                                itemPopupDialogueBox.dismiss();
                                            }
                                        });
/*

                                adapterAdmin.addItem(model);
                                progressDialog.dismiss();

                                itemPopupDialogueBox.setCancelable(true);
                                itemPopupDialogueBox.dismiss();
*/

                            }
                        }
                    });
        } else if (CURRENT_ACTION == ActionConstants.ACTION_UPDATE) {
            firebaseFirestore
                    .collection(new FirebaseDataKeys().getItemsRef())
                    .document(model.getID())
                    .set(model)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            progressDialog.dismiss();
                            backgroundExecutorForShowingData(ItemsListFragmentAdmin.this.getView());
                            itemPopupDialogueBox.setCancelable(true);
                            itemPopupDialogueBox.dismiss();

                        }
                    });
        }
        itemPopupDialogueBox.dismiss();
        viewHolder.setViewsEmpty();
    }

    @Override
    public void onShowFullDetailsButtonClick(ItemModel modelToShow) {

    }

    public static class CustomPopupViewHolder {

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

        //private final ImageView imageDisplayItemImage;

        private final MaterialButton addItemButton;
        private final View mainView;


        public CustomPopupViewHolder(View view) {

            this.mainView = view;

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

            //imageDisplayItemImage = (ImageView) view.findViewById(R.id.item_item_image_admin);

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

        public View getMainView() {
            return mainView;
        }

        public boolean analyzeInputs(boolean b) {
            return b;
        }

        public String getImgLink() {
            return "null";
        }

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
        }
    }

    private class ActionConstants {
        public static final int ACTION_ADD = 0;
        public static final int ACTION_UPDATE = 1;
    }
}
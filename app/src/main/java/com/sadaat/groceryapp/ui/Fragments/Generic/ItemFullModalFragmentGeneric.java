package com.sadaat.groceryapp.ui.Fragments.Generic;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.sadaat.groceryapp.R;
import com.sadaat.groceryapp.models.categories.CategoriesModel;
import com.sadaat.groceryapp.models.Items.ItemModel;
import com.sadaat.groceryapp.temp.FirebaseDataKeys;
import com.sadaat.groceryapp.temp.UserLive;
import com.sadaat.groceryapp.temp.UserTypes;
import com.sadaat.groceryapp.ui.Loaders.LoadingDialogue;

public class ItemFullModalFragmentGeneric extends Fragment {

    MaterialTextView txvMainCategory;
    MaterialTextView txvSubCategory;
    MaterialTextView txvName;
    MaterialTextView txvUnit;
    MaterialTextView txvAlternate;
    MaterialTextView txvDescription;
    MaterialTextView txvSKU;
    MaterialTextView txvRetail;
    MaterialTextView txvSale;
    MaterialTextView txvCOD_SC;
    MaterialTextView txvCardDisc;
    MaterialTextView txvMaxQty;
    ImageView imgItemView;
    boolean hasTriedLoadingPicture = false;
    boolean hasTriedLoadingCategoriesData = false;
    private ItemModel itemModel;
    private int alternateQty;
    private boolean shouldShowQtyInCart;
    private LoadingDialogue loadingDialogue;

    public ItemFullModalFragmentGeneric() {
        // Required empty public constructor
    }

    public ItemFullModalFragmentGeneric(ItemModel itemModel) {
        this.itemModel = itemModel;
        this.alternateQty = alternateQty;
    }

    public static ItemFullModalFragmentGeneric newInstance(ItemModel itemModel) {
        ItemFullModalFragmentGeneric fragment = new ItemFullModalFragmentGeneric(itemModel);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.generic_fragment_item_full_modal, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init_(view);

        loadingDialogue.show("Please Wait", "While We are loading item Data for you");

        txvName.setText(itemModel.getName());
        txvDescription.setText(itemModel.getDescription());
        txvUnit.setText(itemModel.getQty().toString());
        txvSKU.setText(itemModel.getID());

        txvRetail.setText("" + itemModel.getPrices().getRetailPrice());
        txvSale.setText("" + itemModel.getPrices().getSalePrice());
        txvCardDisc.setText("" + itemModel.getOtherDetails().getSpecialDiscountForCardHolder());
        txvCOD_SC.setText("" + itemModel.getOtherDetails().getSecurityCharges());

        if (itemModel.getMaxQtyPerOrder() != -1) {
            txvMaxQty.setText("" + itemModel.getMaxQtyPerOrder());
        } else txvMaxQty.setText("-");


        if (shouldShowQtyInCart) {
            if (UserLive.currentLoggedInUser.getCart().getCartItems().get(itemModel.getID()) != null) {
                alternateQty = UserLive.currentLoggedInUser.getCart().getCartItems().get(itemModel.getID()).getQty();
            } else alternateQty = 0;

            txvAlternate.setText("" + alternateQty + " in Cart");
        } else {
            alternateQty = itemModel.getOtherDetails().getStock();
            txvAlternate.setText("" + alternateQty + " in Stock");
        }

        if (!itemModel
                .getOtherDetails()
                .getImageLink().equals("")) {

            StorageReference imgRef = FirebaseStorage
                    .getInstance(FirebaseDataKeys.STORAGE_BUCKET_ADDRESS)
                    .getReference()
                    .child(itemModel
                            .getOtherDetails()
                            .getImageLink());

            final long ONE_MEGABYTE = 1024 * 1024;

            imgRef.getBytes(10 * ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    imgItemView.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
                    hasTriedLoadingPicture = true;
                    dismissDialogue();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    hasTriedLoadingPicture = true;
                    dismissDialogue();
                    //Toast.makeText(mContext, "Image Load Failed, \n Leave it or use a new one", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            hasTriedLoadingPicture = true;
            dismissDialogue();
            Toast.makeText(ItemFullModalFragmentGeneric.this.requireActivity(), "No Image Found", Toast.LENGTH_SHORT).show();
        }

        FirebaseFirestore.getInstance()
                .collection(new FirebaseDataKeys().getMenuRef())
                .document(itemModel.getCategoryBinding().getDocIDCategory())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            CategoriesModel categoriesModel = task.getResult().toObject(CategoriesModel.class);
                            txvMainCategory.setText(categoriesModel.getTitle());

                            for (int i = 0; i < categoriesModel.getSubCategories().size(); i++) {
                                if (categoriesModel.getSubCategories().get(i).getDocID().equalsIgnoreCase(itemModel.getCategoryBinding().getSubCategoryID())) {
                                    txvSubCategory.setText(categoriesModel.getSubCategories().get(i).getTitle());
                                    break;
                                }
                            }
                        } else {
                            txvMainCategory.setText("Uncategorized");
                            txvSubCategory.setText("Uncategorized");

                        }

                        hasTriedLoadingCategoriesData = true;
                        dismissDialogue();
                    }
                });


    }

    private void init_(View v) {
        txvMainCategory = v.findViewById(R.id.txv_detailed_item_main_category); //DONE
        txvSubCategory = v.findViewById(R.id.txv_detailed_item_sub_category); //DONE
        txvName = v.findViewById(R.id.txv_detailed_item_name); //DONE
        txvUnit = v.findViewById(R.id.txv_detailed_item_unit); //DONE
        txvAlternate = v.findViewById(R.id.txv_detailed_item_alternate); //DONE
        txvDescription = v.findViewById(R.id.txv_detailed_item_description); //DONE
        txvSKU = v.findViewById(R.id.txv_detailed_item_sku); //DONE
        txvRetail = v.findViewById(R.id.txv_detailed_item_retail); //DONE
        txvSale = v.findViewById(R.id.txv_detailed_item_sale); //DONE
        txvCOD_SC = v.findViewById(R.id.txv_detailed_item_cod); //DONE
        txvCardDisc = v.findViewById(R.id.txv_detailed_item_card_ed); //DONE
        txvMaxQty = v.findViewById(R.id.txv_detailed_item_max_qty_per_order); //DONE
        imgItemView = v.findViewById(R.id.imgv_detailed_item); //DONE

        loadingDialogue = new LoadingDialogue(ItemFullModalFragmentGeneric.this.requireActivity());
        shouldShowQtyInCart = UserLive.currentLoggedInUser.getUserType().equals(UserTypes.Customer);
    }

    void dismissDialogue() {
        if (hasTriedLoadingCategoriesData && hasTriedLoadingPicture) loadingDialogue.dismiss();

    }
}
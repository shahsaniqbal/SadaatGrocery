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
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.sadaat.groceryapp.R;
import com.sadaat.groceryapp.models.cart.CartItemModel;
import com.sadaat.groceryapp.models.categories.CategoriesModel;
import com.sadaat.groceryapp.models.Items.ItemModel;
import com.sadaat.groceryapp.syncronizer.CustomerCartSynchronizer;
import com.sadaat.groceryapp.temp.FirebaseDataKeys;
import com.sadaat.groceryapp.temp.UserLive;
import com.sadaat.groceryapp.temp.UserTypes;
import com.sadaat.groceryapp.ui.Loaders.LoadingDialogue;

import java.text.MessageFormat;
import java.util.Objects;

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

    MaterialCardView cardAddToCart;

    public ItemFullModalFragmentGeneric() {
        // Required empty public constructor
    }

    public ItemFullModalFragmentGeneric(ItemModel itemModel) {
        this.itemModel = itemModel;
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

        txvRetail.setText(MessageFormat.format("{0}", itemModel.getPrices().getRetailPrice()));
        txvSale.setText(MessageFormat.format("{0}", itemModel.getPrices().getSalePrice()));
        txvCardDisc.setText(MessageFormat.format("{0}", itemModel.getOtherDetails().getSpecialDiscountForCardHolder()));
        txvCOD_SC.setText(MessageFormat.format("{0}", itemModel.getOtherDetails().getSecurityCharges()));


        if (UserLive.currentLoggedInUser.getUserType().equalsIgnoreCase(UserTypes.Admin)){
            cardAddToCart.setVisibility(View.GONE);
        }

        if (shouldShowQtyInCart) {

            updateCartItemCountDataForCustomer();


        } else {
            alternateQty = itemModel.getOtherDetails().getStock();
            txvAlternate.setText(MessageFormat.format("{0} in Stock", alternateQty));
        }

        cardAddToCart.setOnClickListener(v->{
            UserLive.currentLoggedInUser.getCart().modifyCartItem(new CartItemModel(itemModel, 1));

            CustomerCartSynchronizer.synchronize(UserLive.currentLoggedInUser.getUID(),
                    UserLive.currentLoggedInUser.getCart());

            updateCartItemCountDataForCustomer();
        });

        if (itemModel.getMaxQtyPerOrder() != -1) {
            txvMaxQty.setText(MessageFormat.format("{0}", itemModel.getMaxQtyPerOrder()));
        } else txvMaxQty.setText("-");



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

            imgRef.getBytes(10 * ONE_MEGABYTE).addOnSuccessListener(bytes -> {
                imgItemView.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
                hasTriedLoadingPicture = true;
                dismissDialogue();
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
                            if (categoriesModel != null) {
                                txvMainCategory.setText(categoriesModel.getTitle());
                            }

                            if (categoriesModel != null) {
                                for (int i = 0; i < categoriesModel.getSubCategories().size(); i++) {
                                    if (categoriesModel.getSubCategories().get(i).getDocID().equalsIgnoreCase(itemModel.getCategoryBinding().getSubCategoryID())) {
                                        txvSubCategory.setText(categoriesModel.getSubCategories().get(i).getTitle());
                                        break;
                                    }
                                }
                            }
                        } else {
                            txvMainCategory.setText(R.string.Uncategorized);
                            txvSubCategory.setText(R.string.Uncategorized);

                        }

                        hasTriedLoadingCategoriesData = true;
                        dismissDialogue();
                    }
                });


    }

    private void updateCartItemCountDataForCustomer() {
        if (UserLive.currentLoggedInUser.getCart().getCartItems().get(itemModel.getID()) != null) {
            alternateQty = Objects.requireNonNull(UserLive.currentLoggedInUser.getCart().getCartItems().get(itemModel.getID())).getQty();

            cardAddToCart.setVisibility(View.GONE);

        } else {
            alternateQty = 0;
            cardAddToCart.setVisibility(View.VISIBLE);
        }

        txvAlternate.setText("" + alternateQty + " in Cart");
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

        cardAddToCart = v.findViewById(R.id.card_add_to_cart);

        loadingDialogue = new LoadingDialogue(ItemFullModalFragmentGeneric.this.requireActivity());
        shouldShowQtyInCart = UserLive.currentLoggedInUser.getUserType().equals(UserTypes.Customer);
    }

    void dismissDialogue() {
        if (hasTriedLoadingCategoriesData && hasTriedLoadingPicture) loadingDialogue.dismiss();

    }
}
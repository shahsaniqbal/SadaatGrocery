package com.sadaat.groceryapp.ui.Fragments.UserBased.Customers.passive;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.sadaat.groceryapp.R;
import com.sadaat.groceryapp.adapters.customer.ItemsDisplayAdapterCustomer;
import com.sadaat.groceryapp.models.Items.ItemModel;
import com.sadaat.groceryapp.models.Items.CategoryBindingModel;
import com.sadaat.groceryapp.models.cart.CartItemModel;
import com.sadaat.groceryapp.syncronizer.CustomerCartSynchronizer;
import com.sadaat.groceryapp.temp.FirebaseDataKeys;
import com.sadaat.groceryapp.temp.UserLive;
import com.sadaat.groceryapp.ui.Fragments.Generic.DetailedItemFragmentGeneric;
import com.sadaat.groceryapp.ui.Loaders.LoadingDialogue;

import java.util.ArrayList;

public class ItemsFragmentCustomer extends Fragment implements ItemsDisplayAdapterCustomer.ItemClickListeners {

    private static final String ARG_PARAM1 = "mainCategoryID";
    private static final String ARG_PARAM2 = "subCategoryID";
    private static final String ARG_PARAM3 = "title";
    private static final String ARG_PARAM4 = "description";

    private String KEY = "categoryBinding";

    private final CollectionReference ITEMS_COLLECTION_REF = FirebaseFirestore.getInstance().collection(new FirebaseDataKeys().getItemsRef());
    private CategoryBindingModel categoryBindingModel;

    private String mMainCategoryID;
    private String mSubCategoryID;
    private String mTitle;
    private String mDescription;

    private MaterialTextView txvTitle;
    private MaterialTextView txvDescription;
    private RecyclerView recyclerViewItems;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<ItemModel> items;
    private ItemsDisplayAdapterCustomer displayAdapterItem;

    private LoadingDialogue progressDialogue;

    public ItemsFragmentCustomer() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param mainCategoryID Parameter 1.
     * @param subCategoryID Parameter 2.
     * @param title Parameter 3.
     * @param description Parameter 4.
     * @return A new instance of fragment ItemsFragmentCustomer.
     */
    public static ItemsFragmentCustomer newInstance(String mainCategoryID, String subCategoryID, String title, String description) {
        ItemsFragmentCustomer fragment = new ItemsFragmentCustomer();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, mainCategoryID);
        args.putString(ARG_PARAM2, subCategoryID);
        args.putString(ARG_PARAM3, title);
        args.putString(ARG_PARAM4, description);


        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mMainCategoryID = getArguments().getString(ARG_PARAM1);
            mSubCategoryID = getArguments().getString(ARG_PARAM2);
            mTitle = getArguments().getString(ARG_PARAM3);
            mDescription = getArguments().getString(ARG_PARAM4);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.customer_fragment_items, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializations(view);

        UserLive.currentLoggedInUser.getCart().eliminateCartByLatestStock();

        txvTitle.setText(mTitle);
        txvDescription.setText(mDescription);

        recyclerViewItems.setLayoutManager(layoutManager);
        recyclerViewItems.setAdapter(displayAdapterItem);

        progressDialogue.show("Please Wait", "Loading Items of "+mTitle);


        ITEMS_COLLECTION_REF
                .whereEqualTo(KEY, categoryBindingModel)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            displayAdapterItem.addAll(task.getResult().toObjects(ItemModel.class));
                            progressDialogue.dismiss();
                        }
                        else{
                            Toast.makeText(ItemsFragmentCustomer.this.requireActivity(), "Error Loading Items", Toast.LENGTH_SHORT).show();
                            progressDialogue.dismiss();
                        }
                    }
                });

    }

    private void initializations(View vParent) {
        recyclerViewItems = vParent.findViewById(R.id.recycler_customer_items);
        txvTitle = vParent.findViewById(R.id.customer_items_subcategory_title);
        txvDescription = vParent.findViewById(R.id.customer_items_subcategory_description);
        layoutManager = new GridLayoutManager(ItemsFragmentCustomer.this.requireActivity(),3);
        items = new ArrayList<>();
        displayAdapterItem = new ItemsDisplayAdapterCustomer(items, this, ItemsFragmentCustomer.this.requireActivity());

        categoryBindingModel = new CategoryBindingModel(mMainCategoryID, mSubCategoryID);

        progressDialogue = new LoadingDialogue(ItemsFragmentCustomer.this.requireActivity());
    }


    @Override
    public CartItemModel indicateItemCountChange(ItemModel item, int quantity) {
        return new CartItemModel(item,quantity);
    }

    @Override
    public void prepareCart(CartItemModel cartItemModel) {
        UserLive.currentLoggedInUser.getCart().modifyCartItem(cartItemModel);

        CustomerCartSynchronizer.synchronize(UserLive.currentLoggedInUser.getUID(),
                UserLive.currentLoggedInUser.getCart());


        Log.e("CART", UserLive.currentLoggedInUser.getCart().toString());
    }

    @Override
    public void onClick(ItemModel model) {
        getParentFragmentManager()
                .beginTransaction()
                .replace(R.id.flFragmentCustomer, DetailedItemFragmentGeneric.newInstance(model))
                .addToBackStack("items")
                .commit();
    }
}
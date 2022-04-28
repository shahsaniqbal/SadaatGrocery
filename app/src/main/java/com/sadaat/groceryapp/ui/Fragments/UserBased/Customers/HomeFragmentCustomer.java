package com.sadaat.groceryapp.ui.Fragments.UserBased.Customers;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.sadaat.groceryapp.R;
import com.sadaat.groceryapp.adapters.customer.ItemsDisplayAdapterCustomer;
import com.sadaat.groceryapp.models.Items.CategoryBindingModel;
import com.sadaat.groceryapp.models.Items.ItemModel;
import com.sadaat.groceryapp.models.cart.CartItemModel;
import com.sadaat.groceryapp.syncronizer.CustomerCartSynchronizer;
import com.sadaat.groceryapp.temp.FirebaseDataKeys;
import com.sadaat.groceryapp.temp.UserLive;
import com.sadaat.groceryapp.ui.Fragments.Generic.ItemFullModalFragmentGeneric;
import com.sadaat.groceryapp.ui.Fragments.UserBased.Customers.passive.ItemsFragmentCustomer;
import com.sadaat.groceryapp.ui.Loaders.LoadingDialogue;

import java.util.ArrayList;

public class HomeFragmentCustomer extends Fragment implements ItemsDisplayAdapterCustomer.ItemClickListeners {

    private final CollectionReference ITEMS_COLLECTION_REF = FirebaseFirestore.getInstance().collection(new FirebaseDataKeys().getItemsRef());

    private RecyclerView recyclerViewHotItems;
    private RecyclerView.LayoutManager layoutManagerForHotItems;
    private ItemsDisplayAdapterCustomer displayAdapterHotItems;

    private LoadingDialogue progressDialogue;

    public HomeFragmentCustomer() {
        // Required empty public constructor
    }

    public static HomeFragmentCustomer newInstance(String param1, String param2) {

        return new HomeFragmentCustomer();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.customer_fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializations(view);

        showHotProducts(view);

    }

    private void showHotProducts(View view) {

        UserLive.currentLoggedInUser.getCart().eliminateCartByLatestStock();

        recyclerViewHotItems.setLayoutManager(layoutManagerForHotItems);
        recyclerViewHotItems.setAdapter(displayAdapterHotItems);

        progressDialogue.show("Please Wait", "Loading Hot Items");


        ITEMS_COLLECTION_REF
                .whereEqualTo("hot", true)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            displayAdapterHotItems.addAll(task.getResult().toObjects(ItemModel.class));
                        }
                        else{
                            Toast.makeText(HomeFragmentCustomer.this.requireActivity(), "Error Loading Items", Toast.LENGTH_SHORT).show();
                        }
                        progressDialogue.dismiss();
                    }
                });
    }


    private void initializations(View vParent) {
        recyclerViewHotItems = vParent.findViewById(R.id.recycler_customer_items);
        layoutManagerForHotItems = new LinearLayoutManager(HomeFragmentCustomer.this.requireActivity(), LinearLayoutManager.HORIZONTAL, false);
        displayAdapterHotItems = new ItemsDisplayAdapterCustomer(new ArrayList<>(), this, HomeFragmentCustomer.this.requireActivity());
        progressDialogue = new LoadingDialogue(HomeFragmentCustomer.this.requireActivity());
    }


    /*
    * Hot Items Override
    */
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
                .replace(R.id.flFragmentCustomer, ItemFullModalFragmentGeneric.newInstance(model))
                .addToBackStack("items")
                .commit();
    }
}
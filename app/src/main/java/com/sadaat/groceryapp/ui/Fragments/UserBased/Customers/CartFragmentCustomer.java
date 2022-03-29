package com.sadaat.groceryapp.ui.Fragments.UserBased.Customers;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sadaat.groceryapp.R;
import com.sadaat.groceryapp.adapters.customer.CartItemDisplayAdapterCustomer;
import com.sadaat.groceryapp.models.ItemModel;
import com.sadaat.groceryapp.models.cart.CartItemModel;
import com.sadaat.groceryapp.syncronizer.CustomerCartSynchronizer;
import com.sadaat.groceryapp.temp.UserLive;

import java.util.HashMap;

public class CartFragmentCustomer extends Fragment implements CartItemDisplayAdapterCustomer.ItemClickListeners {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private RecyclerView recyclerView;
    private CartItemDisplayAdapterCustomer cartAdapter;
    private RecyclerView.LayoutManager layoutManager;

    public CartFragmentCustomer() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CartFragmentCustomer.
     */
    // TODO: Rename and change types and number of parameters
    public static CartFragmentCustomer newInstance(String param1, String param2) {
        CartFragmentCustomer fragment = new CartFragmentCustomer();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.customer_fragment_cart, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init_(view);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(cartAdapter);
    }

    private void init_(View v){
        recyclerView = v.findViewById(R.id.recycler_cart_items);
        cartAdapter = new CartItemDisplayAdapterCustomer(CartFragmentCustomer.this.requireActivity(),this);
        layoutManager = new LinearLayoutManager(CartFragmentCustomer.this.requireActivity());

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

        cartAdapter.notifyDataSetChanged();
        updateFragment();

        Log.e("CART", UserLive.currentLoggedInUser.getCart().toString());
    }

    private void updateFragment() {

    }
}
package com.sadaat.groceryapp.ui.Fragments.UserBased.Admin.UnderHomeFragmentSuper;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.card.MaterialCardView;
import com.sadaat.groceryapp.R;
import com.sadaat.groceryapp.temp.order_management.OrderStatus;

public class OrderMenuFragmentAdmin extends Fragment implements View.OnClickListener {

    MaterialCardView cardInitiated;
    MaterialCardView cardPacking;
    MaterialCardView cardDelivering;
    MaterialCardView cardDelivered;
    MaterialCardView cardCancelled;
    MaterialCardView cardNotReceived;

    public OrderMenuFragmentAdmin() {
        // Required empty public constructor
    }

    public static OrderMenuFragmentAdmin newInstance() {

        return new OrderMenuFragmentAdmin();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.admin_fragment_order_menu, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        cardInitiated = view.findViewById(R.id.initiated);
        cardPacking = view.findViewById(R.id.packing);
        cardDelivering = view.findViewById(R.id.delivering);
        cardDelivered = view.findViewById(R.id.delivered);
        cardCancelled = view.findViewById(R.id.cancelled);
        cardNotReceived = view.findViewById(R.id.notReceived);

        cardInitiated.setOnClickListener(this);
        cardPacking.setOnClickListener(this);
        cardDelivering.setOnClickListener(this);
        cardCancelled.setOnClickListener(this);
        cardNotReceived.setOnClickListener(this);
        cardDelivered.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        String currentStatus = OrderStatus.INITIATED;
        if (view.getId() == cardInitiated.getId()){
            currentStatus = OrderStatus.INITIATED;
        }
        else if (view.getId() == cardPacking.getId()){
            currentStatus = OrderStatus.PACKING;
        }
        else if (view.getId() == cardDelivering.getId()){
            currentStatus = OrderStatus.DELIVERING;
        }
        else if (view.getId() == cardDelivered.getId()){
            currentStatus = OrderStatus.DELIVERED;
        }
        else if (view.getId() == cardCancelled.getId()){
            currentStatus = OrderStatus.CANCELLED;
        }
        else if (view.getId() == cardNotReceived.getId()){
            currentStatus = OrderStatus.NOT_RECEIVED;
        }

        OrderMenuFragmentAdmin
                .this
                .requireActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.flFragmentChild, OrdersListFragmentAdmin.newInstance(currentStatus))
                .addToBackStack("order_menu")
                .commit();

    }
}
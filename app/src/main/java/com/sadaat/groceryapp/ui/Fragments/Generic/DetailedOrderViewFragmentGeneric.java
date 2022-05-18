package com.sadaat.groceryapp.ui.Fragments.Generic;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sadaat.groceryapp.R;
import com.sadaat.groceryapp.adapters.generic.DetailedOrderItemsListAdapter;
import com.sadaat.groceryapp.adapters.generic.DetailedOrderStatusListAdapter;
import com.sadaat.groceryapp.models.orders.OrderModel;
import com.sadaat.groceryapp.temp.FirebaseDataKeys;
import com.sadaat.groceryapp.ui.Loaders.LoadingDialogue;

import java.text.MessageFormat;
import java.util.ArrayList;

public class DetailedOrderViewFragmentGeneric extends Fragment {

    private OrderModel orderModel;
    private LoadingDialogue loading;

    private MaterialTextView txvOrderID;
    private MaterialTextView txvCustomerName;
    private MaterialTextView txvDeliveryBoy;
    private MaterialTextView txvLocation;
    private MaterialTextView txvAmount;
    private MaterialTextView txvRemaining;
    private MaterialTextView txvMethod;
    private MaterialTextView txvReleasingAppCredits;
    private MaterialTextView txvPartialAppCredits;
    private MaterialTextView txvDiscount;
    private MaterialTextView txvCurrentStatus;
    private MaterialTextView txvReleasedAppCredits;
    private MaterialTextView txvTransactionID;
    private RecyclerView recyclerItems;
    private RecyclerView recyclerStatuses;

    private RecyclerView.LayoutManager managerForItems;
    private RecyclerView.LayoutManager managerForStatuses;

    private DetailedOrderItemsListAdapter adapterItems;
    private DetailedOrderStatusListAdapter adapterStatuses;

    public DetailedOrderViewFragmentGeneric() {
        // Required empty public constructor
    }


    public DetailedOrderViewFragmentGeneric(String orderModelID) {

        FirebaseFirestore
                .getInstance()
                .collection(new FirebaseDataKeys().getOrdersRef())
                .document(orderModelID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            orderModel = task.getResult().toObject(OrderModel.class);
                        }
                    }
                });

    }

    public DetailedOrderViewFragmentGeneric(OrderModel orderModel) {
        this.orderModel = orderModel;
    }


    public static DetailedOrderViewFragmentGeneric newInstance(OrderModel orderModel) {
        return new DetailedOrderViewFragmentGeneric(orderModel);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.generic_fragment_detailed_order_view, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initialize(view);
        setData();

        for (String s : orderModel.getOrderDetails().getCartItems().keySet()) {
            adapterItems.addItem(orderModel.getOrderDetails().getCartItems().get(s));
        }
    }

    private void setData() {

        loading.show("Please Wait", "While Updating Order Related Data");
        txvOrderID.setText(
                orderModel.getOrderID()
        );

        txvLocation.setText(
                orderModel.getDeliveryLocation()
        );

        txvAmount.setText(
                MessageFormat.format("{0} Rs.", orderModel.getTotalOrderAmountInRetail())
        );

        txvRemaining.setText(
                MessageFormat.format("{0} Rs.", orderModel.getRemainingPaymentToPayAtDelivery())
        );

        txvMethod.setText(
                orderModel.getPaymentThrough().getPaymentThroughMethod().toUpperCase()
        );

        txvReleasingAppCredits.setText(
                MessageFormat.format("{0} Credits", orderModel.getReleasingAppCredits())
        );

        txvPartialAppCredits.setText(
                MessageFormat.format("{0} Credits", orderModel.getPaymentThrough().getAppCreditsUsed())
        );

        txvDiscount.setText(
                MessageFormat.format("{0} Rs.", orderModel.getReleasingAppCredits())
        );

        txvCurrentStatus.setText(
                orderModel.getStatusUpdates().get(orderModel.getStatusUpdates().size() - 1).getStatus()
        );

        txvReleasedAppCredits.setText(
                MessageFormat.format("{0} Credits", orderModel.getReleasedAppCredits())
        );

        if (orderModel.getPaymentThrough().getTransactionID() != null) {
            txvTransactionID.setText(
                    orderModel.getPaymentThrough().getTransactionID()
            );
        }

        recyclerItems.setLayoutManager(managerForItems);
        recyclerStatuses.setLayoutManager(managerForStatuses);

        recyclerItems.setAdapter(adapterItems);
        recyclerStatuses.setAdapter(adapterStatuses);

        FirebaseFirestore
                .getInstance()
                .collection(new FirebaseDataKeys().getUsersRef())
                .document(orderModel.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful())
                            txvCustomerName.setText(task.getResult().get("fullName", String.class));
                        else txvCustomerName.setText(orderModel.getUid());

                        if (orderModel.getCurrentDeliveryBoyUID().isEmpty()){
                            txvDeliveryBoy.setText("");
                        }

                        else{
                            FirebaseFirestore
                                    .getInstance()
                                    .collection(new FirebaseDataKeys().getUsersRef())
                                    .document(orderModel.getCurrentDeliveryBoyUID())
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful())
                                                txvDeliveryBoy.setText(task.getResult().get("fullName", String.class));
                                            else
                                                txvCustomerName.setText(orderModel.getCurrentDeliveryBoyUID());

                                        }
                                    });
                        }


                    }
                });

        loading.dismiss();

    }

    private void initialize(View v) {
        loading = new LoadingDialogue(DetailedOrderViewFragmentGeneric.this.requireActivity());

        txvOrderID = v.findViewById(R.id.d_order_orderID);
        txvCustomerName = v.findViewById(R.id.d_order_customerName);
        txvDeliveryBoy = v.findViewById(R.id.d_order_deliveryBoy);
        txvLocation = v.findViewById(R.id.d_order_location);
        recyclerItems = v.findViewById(R.id.d_order_recyclerItems);
        txvAmount = v.findViewById(R.id.d_order_amount);
        txvRemaining = v.findViewById(R.id.d_order_remaining);
        txvMethod = v.findViewById(R.id.d_order_orderMethod);
        txvReleasingAppCredits = v.findViewById(R.id.d_order_releasing);
        txvPartialAppCredits = v.findViewById(R.id.d_order_partial);
        txvDiscount = v.findViewById(R.id.d_order_discount);
        recyclerStatuses = v.findViewById(R.id.d_order_recyclerStatus);
        txvCurrentStatus = v.findViewById(R.id.d_order_currentStatus);
        txvReleasedAppCredits = v.findViewById(R.id.d_order_releasedCredits);
        txvTransactionID = v.findViewById(R.id.d_order_transaction);

        managerForItems = new LinearLayoutManager(this.requireActivity());
        managerForStatuses = new LinearLayoutManager(this.requireActivity());

        adapterItems = new DetailedOrderItemsListAdapter(this.requireActivity(), new ArrayList<>());
        adapterStatuses = new DetailedOrderStatusListAdapter(this.requireActivity(), orderModel.getStatusUpdates());

        ((MaterialTextView)v.findViewById(R.id.order_item_status_status)).setTypeface(null, Typeface.BOLD);
        ((MaterialTextView)v.findViewById(R.id.order_item_status_sr)).setTypeface(null, Typeface.BOLD);
        ((MaterialTextView)v.findViewById(R.id.order_item_status_timestamp)).setTypeface(null, Typeface.BOLD);

        ((MaterialTextView)v.findViewById(R.id.customer_item_cart_name)).setTypeface(null, Typeface.BOLD);
        ((MaterialTextView)v.findViewById(R.id.customer_item_cart_unit_price)).setTypeface(null, Typeface.BOLD);
        ((MaterialTextView)v.findViewById(R.id.txv_item_qty_customer_items)).setTypeface(null, Typeface.BOLD);
        ((MaterialTextView)v.findViewById(R.id.customer_item_cart_total)).setTypeface(null, Typeface.BOLD);
    }
}
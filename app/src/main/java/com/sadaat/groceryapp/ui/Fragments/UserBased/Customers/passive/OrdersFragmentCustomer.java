package com.sadaat.groceryapp.ui.Fragments.UserBased.Customers.passive;

import static android.content.ContentValues.TAG;
import static android.view.View.GONE;

import android.os.Bundle;
import android.util.Log;
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
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.sadaat.groceryapp.R;
import com.sadaat.groceryapp.adapters.customer.OrderItemDisplayAdapterCustomer;
import com.sadaat.groceryapp.models.Users.UserModel;
import com.sadaat.groceryapp.models.orders.OrderModel;
import com.sadaat.groceryapp.temp.FirebaseDataKeys;
import com.sadaat.groceryapp.temp.UserLive;
import com.sadaat.groceryapp.temp.order_management.OrderStatus;
import com.sadaat.groceryapp.ui.Fragments.Generic.DetailedOrderView;
import com.sadaat.groceryapp.ui.Loaders.LoadingDialogue;

import java.text.MessageFormat;
import java.util.ArrayList;

public class OrdersFragmentCustomer extends Fragment implements OrderItemDisplayAdapterCustomer.ItemClickListeners {

    CollectionReference USER_REF = FirebaseFirestore.getInstance().collection(UserLive.currentLoggedInUser.getUID());
    CollectionReference ORDERS_REF = FirebaseFirestore.getInstance().collection(new FirebaseDataKeys().getOrdersRef());
    LoadingDialogue dialogue;
    private OrderModel currentLiveOrder;
    private MaterialTextView txvOrderID;
    private MaterialTextView txvAmount;
    private MaterialTextView txvRemaining;
    private MaterialTextView txvMethod;
    private MaterialTextView txvRelease;
    private MaterialTextView txvOrderStatus;
    private MaterialTextView txvAddress;
    private MaterialTextView txvDeliveryBoy;
    private MaterialTextView txvTimeInitiated;
    private MaterialCardView btnCancel;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager manager;
    private OrderItemDisplayAdapterCustomer adapter;


    public OrdersFragmentCustomer() {
        // Required empty public constructor
    }

    public static OrdersFragmentCustomer newInstance() {
        return new OrdersFragmentCustomer();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.customer_fragment_orders, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dialogue = new LoadingDialogue(this.requireActivity());
        initializeViews(view);

        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);

        ORDERS_REF
                .whereEqualTo("uid", UserLive.currentLoggedInUser.getUID())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            for (QueryDocumentSnapshot q :
                                    task.getResult()) {
                                adapter.addItem(q.toObject(OrderModel.class));
                            }

                        }

                    }
                });

        updateView(view);

        if (!UserLive.currentLoggedInUser.getCurrentActiveOrder().isEmpty()) {
            ORDERS_REF.document(UserLive.currentLoggedInUser.getCurrentActiveOrder()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot snapshot,
                                    @Nullable FirebaseFirestoreException e) {
                    if (e != null) {
                        Log.w(TAG, "Listen failed.", e);
                        return;
                    }

                    if (snapshot != null && snapshot.exists()) {
                        //TODO snapshot set
                        currentLiveOrder = snapshot.toObject(OrderModel.class);
                        assert currentLiveOrder != null;
                        if (currentLiveOrder.getCurrentStatus().equalsIgnoreCase(OrderStatus.DELIVERED) || currentLiveOrder.getCurrentStatus().equalsIgnoreCase(OrderStatus.CANCELLED)) {
                            currentLiveOrder = null;
                            USER_REF.document(UserLive.currentLoggedInUser.getUID())
                                    .update("currentActiveOrder", "").addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    USER_REF.document(UserLive.currentLoggedInUser.getUID())
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    if (task.isSuccessful())
                                                        UserLive.currentLoggedInUser = task.getResult().toObject(UserModel.class);
                                                }
                                            });

                                }
                            });
                        }
                        updateView(view);

                    } else {
                        Log.d(TAG, "Current data: null");
                    }
                }
            });
        }
    }

    private void initializeViews(View view) {
        txvOrderID = view.findViewById(R.id.admin_order_item_orderID);
        txvAmount = view.findViewById(R.id.admin_order_item_amount);
        txvRemaining = view.findViewById(R.id.admin_order_item_remaining);
        txvMethod = view.findViewById(R.id.admin_order_item_method);
        txvRelease = view.findViewById(R.id.admin_order_item_releasing);
        txvOrderStatus = view.findViewById(R.id.admin_order_item_status);
        txvAddress = view.findViewById(R.id.admin_order_item_address);
        txvDeliveryBoy = view.findViewById(R.id.admin_order_item_deliveryBoy);
        txvTimeInitiated = view.findViewById(R.id.admin_order_item_time);
        btnCancel = view.findViewById(R.id.cancel_order_button);

        recyclerView = view.findViewById(R.id.recycler);
        dialogue = new LoadingDialogue(this.requireActivity());
        manager = new LinearLayoutManager(this.requireActivity());
        adapter = new OrderItemDisplayAdapterCustomer(new ArrayList<>(), this, this.requireActivity());
    }


    private void updateView(View v) {
        if (currentLiveOrder == null) {
            v.findViewById(R.id.layout_for_live_order).setVisibility(GONE);
            v.findViewById(R.id.layout_for_no_live_order).setVisibility(View.VISIBLE);
        } else {
            v.findViewById(R.id.layout_for_live_order).setVisibility(View.VISIBLE);
            v.findViewById(R.id.layout_for_no_live_order).setVisibility(GONE);


            // Data Setters
            txvOrderID.setText(currentLiveOrder.getOrderID());
            txvAddress.setText(currentLiveOrder.getDeliveryLocation());
            txvAmount.setText(MessageFormat.format("{0} Rs.", currentLiveOrder.getTotalOrderAmountInRetail()));
            txvRemaining.setText(MessageFormat.format("{0} Rs.", currentLiveOrder.getRemainingPaymentToPayAtDelivery()));
            txvMethod.setText(MessageFormat.format("{0}", currentLiveOrder.getPaymentThrough().getPaymentThroughMethod().toUpperCase()));
            txvRelease.setText(MessageFormat.format("{0} Credits", currentLiveOrder.getReleasingAppCredits()));
            txvOrderStatus.setText(currentLiveOrder.getCurrentStatus());
            txvTimeInitiated.setText(currentLiveOrder.getStatusUpdates().get(0).getTimeStamp().toString());

            if (!currentLiveOrder.getCurrentDeliveryBoyUID().isEmpty()) {
                FirebaseFirestore
                        .getInstance()
                        .collection(new FirebaseDataKeys().getUsersRef())
                        .document(currentLiveOrder.getCurrentDeliveryBoyUID())
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    UserModel userModel = task.getResult().toObject(UserModel.class);
                                    txvDeliveryBoy.setText(task.getResult().get("fullName", String.class));
                                } else {
                                    txvDeliveryBoy.setText("");
                                }
                            }
                        });
            } else {
                txvDeliveryBoy.setText("");
            }

            if (currentLiveOrder.getCurrentStatus().equalsIgnoreCase(OrderStatus.INITIATED)) {
                btnCancel.setVisibility(View.VISIBLE);
            } else {
                btnCancel.setVisibility(View.GONE);
            }

        }
    }

    @Override
    public void onFullItemClick(OrderModel orderModel) {
        requireActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.flFragmentCustomer, DetailedOrderView.newInstance(orderModel))
                .addToBackStack("orders_list")
                .commit();
    }
}
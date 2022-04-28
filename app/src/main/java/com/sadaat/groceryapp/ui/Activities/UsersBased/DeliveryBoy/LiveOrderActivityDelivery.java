package com.sadaat.groceryapp.ui.Activities.UsersBased.DeliveryBoy;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sadaat.groceryapp.R;
import com.sadaat.groceryapp.adapters.deliveryboy.LiveOrderDisplayAdapterDeliveryBoy;
import com.sadaat.groceryapp.handler.OrderHandler;
import com.sadaat.groceryapp.models.cart.CartItemModel;
import com.sadaat.groceryapp.models.orders.OrderModel;
import com.sadaat.groceryapp.models.orders.StatusModel;
import com.sadaat.groceryapp.temp.FirebaseDataKeys;
import com.sadaat.groceryapp.temp.UserLive;
import com.sadaat.groceryapp.temp.order_management.OrderStatus;
import com.sadaat.groceryapp.ui.Loaders.LoadingDialogue;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class LiveOrderActivityDelivery extends AppCompatActivity implements LiveOrderDisplayAdapterDeliveryBoy.ItemClickListeners {

    RecyclerView recyclerView;
    LiveOrderDisplayAdapterDeliveryBoy adapter;
    RecyclerView.LayoutManager manager;

    MaterialTextView txvOrdersCount;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.delivery_activity_live_order);
        Objects.requireNonNull(getSupportActionBar()).hide();

        txvOrdersCount = findViewById(R.id.d_live_orders_count);
        adapter = new LiveOrderDisplayAdapterDeliveryBoy(new ArrayList<>(), this, this);
        manager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView = findViewById(R.id.d_live_orders_list);

        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);

        FirebaseFirestore.getInstance()
                .collection(new FirebaseDataKeys().getOrdersRef())
                .whereEqualTo("currentDeliveryBoyUID", UserLive.currentLoggedInUser.getUID())
                .whereEqualTo("currentStatus", OrderStatus.DELIVERING)
                .addSnapshotListener((snapshot, e) -> {
                    if (e != null) {
                        Log.e(TAG, "Listen failed.", e);
                        return;
                    }

                    adapter.deleteAll();

                    if (snapshot != null) {

                        txvOrdersCount.setText(MessageFormat.format("{0}", snapshot.size()));

                        if (snapshot.size() > 0) {
                            findViewById(R.id.row_for_no_orders).setVisibility(View.GONE);
                            findViewById(R.id.row_for_orders).setVisibility(View.VISIBLE);

                            for (DocumentSnapshot d :
                                    snapshot.getDocuments()) {

                                adapter.addItem(d.toObject(OrderModel.class));
                            }

                        } else if (snapshot.size() == 0) {
                            findViewById(R.id.row_for_no_orders).setVisibility(View.VISIBLE);
                            findViewById(R.id.row_for_orders).setVisibility(View.GONE);

                        } else {
                            Log.d(TAG, "Current data: null");
                        }
                    }
                });
    }

    @Override
    public void onCallButtonClick(String mobileNumber) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + mobileNumber));
        startActivity(intent);
    }

    /**
     * Update Order Status -> Delivered
     * Update Delivery Boy Pending App Credits
     * Update Order Receiving Status
     */
    @Override
    public void onReceivedButtonClick(String orderID, StatusModel newStatusModel, String uid, double creditsToAddToUserMainWallet) {
        // Credits Related Work will be done in Admin Release Button

        FirebaseFirestore
                .getInstance()
                .collection(new FirebaseDataKeys().getUsersRef())
                .document(UserLive.currentLoggedInUser.getUID())
                .update(
                        "credits.pendingCredits", FieldValue.increment(creditsToAddToUserMainWallet),
                        "credits.owningCredits", FieldValue.increment(((-1) * creditsToAddToUserMainWallet))
                )
                .addOnCompleteListener(task -> FirebaseFirestore
                        .getInstance()
                        .collection(new FirebaseDataKeys().getOrdersRef())
                        .document(orderID)
                        .update(
                                "receivingStatus", "Received",
                                "statusUpdates", FieldValue.arrayUnion(newStatusModel),
                                "currentStatus", newStatusModel.getStatus()
                        )
                        .addOnCompleteListener(task1 -> {

                        }));


    }

    /**
     * Update Order Status -> Didn't Receive
     * Update OrderStatus Updates
     * Update Order Receiving Status
     * Update User's Pending App Credits -> Decrement
     * Re Stock Items
     */
    @Override
    public void onDidNotReceiveButtonClick(String orderID, StatusModel newStatus, String uid, double creditsToARemoveFromUserPending, HashMap<String, CartItemModel> itemsToRestock) {
        LoadingDialogue dialogue = new LoadingDialogue(LiveOrderActivityDelivery.this);
        dialogue.show("Please Wait", "We are cancelling and restocking items");

        new OrderHandler().restock(itemsToRestock, newStatus.getTimeStamp());
        FirebaseFirestore.getInstance()
                .collection(new FirebaseDataKeys().getUsersRef())
                .document(uid)
                .update(
                        "credits.pendingCredits", FieldValue.increment(creditsToARemoveFromUserPending)
                )
                .addOnCompleteListener(task -> FirebaseFirestore
                        .getInstance()
                        .collection(new FirebaseDataKeys().getOrdersRef())
                        .document(orderID)
                        .update(
                                "receivingStatus", "Not Received",
                                "statusUpdates", FieldValue.arrayUnion(newStatus),
                                "currentStatus", newStatus.getStatus()
                        )
                        .addOnCompleteListener(task1 -> dialogue.dismiss()));
    }
}
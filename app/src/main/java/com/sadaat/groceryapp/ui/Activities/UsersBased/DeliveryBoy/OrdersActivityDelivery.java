package com.sadaat.groceryapp.ui.Activities.UsersBased.DeliveryBoy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.sadaat.groceryapp.R;
import com.sadaat.groceryapp.adapters.deliveryboy.OrderItemDisplayAdapterDeliveryBoy;
import com.sadaat.groceryapp.models.orders.OrderModel;
import com.sadaat.groceryapp.temp.FirebaseDataKeys;
import com.sadaat.groceryapp.temp.UserLive;
import com.sadaat.groceryapp.temp.order_management.OrderStatus;

import java.util.ArrayList;
import java.util.Objects;

public class OrdersActivityDelivery extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager manager;
    OrderItemDisplayAdapterDeliveryBoy adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.delivery_activity_orders);

        Objects.requireNonNull(getSupportActionBar()).hide();

        recyclerView = findViewById(R.id.delivery_boy_orders_list);
        manager = new LinearLayoutManager(this);
        adapter = new OrderItemDisplayAdapterDeliveryBoy(new ArrayList<>(), this);


        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);

        FirebaseFirestore.getInstance()
                .collection(new FirebaseDataKeys().getOrdersRef())
                .whereEqualTo("currentDeliveryBoyUID", UserLive.currentLoggedInUser.getUID())
                .whereEqualTo("currentStatus", OrderStatus.DELIVERED)
                .whereEqualTo("releasedAppCredits", 0.0)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            if (task.getResult().size()>0){
                                findViewById(R.id.txv_detailed_item_alternate).setVisibility(View.GONE);

                                for (QueryDocumentSnapshot q: task.getResult()){
                                    adapter.addItem(q.toObject(OrderModel.class));
                                }

                            }
                            else {
                                findViewById(R.id.txv_detailed_item_alternate).setVisibility(View.VISIBLE);
                            }

                        }
                    }
                });
    }
}
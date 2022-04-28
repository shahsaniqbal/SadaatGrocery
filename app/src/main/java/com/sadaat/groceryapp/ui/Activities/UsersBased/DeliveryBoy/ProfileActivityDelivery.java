package com.sadaat.groceryapp.ui.Activities.UsersBased.DeliveryBoy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.sadaat.groceryapp.R;
import com.sadaat.groceryapp.models.orders.OrderModel;
import com.sadaat.groceryapp.temp.FirebaseDataKeys;
import com.sadaat.groceryapp.temp.UserLive;
import com.sadaat.groceryapp.temp.order_management.OrderStatus;
import com.sadaat.groceryapp.ui.Loaders.LoadingDialogue;

import java.text.MessageFormat;
import java.util.Objects;

public class ProfileActivityDelivery extends AppCompatActivity {

    long totalOrdersDelivered = 0;
    long totalOrdersInQueue = 0;

    double pendingPaymentToReceive = 0.0;
    double paymentReceivedAndNeedToPayToAdmin = 0.0;

    LoadingDialogue dialogue;
    FirebaseDataKeys keys;

    MaterialTextView txvName;
    MaterialTextView txvEmail;
    MaterialTextView txvMobile;
    MaterialTextView txvTotalOrdersDelivered;
    MaterialTextView txvOrdersInQueue;
    MaterialTextView txvPendingPayments;
    MaterialTextView txvPaymentsReceivedAndNeedToPayToTheAdmin;

    ImageView imgUserImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.delivery_activity_profile);
        dialogue = new LoadingDialogue(this);
        Objects.requireNonNull(getSupportActionBar()).hide();

        txvName = findViewById(R.id.delivery_profile_userName);
        txvEmail = findViewById(R.id.delivery_profile_userEmail);
        txvMobile = findViewById(R.id.delivery_profile_mobile);
        txvTotalOrdersDelivered = findViewById(R.id.delivery_profile_orders_delivered);
        txvOrdersInQueue = findViewById(R.id.delivery_profile_orders_in_queue);
        txvPendingPayments = findViewById(R.id.delivery_profile_orders_pending_payments);
        txvPaymentsReceivedAndNeedToPayToTheAdmin = findViewById(R.id.delivery_profile_orders_payments_received);
        imgUserImage = findViewById(R.id.delivery_profile_userImage);

        keys = new FirebaseDataKeys();

        dialogue.show("Please Wait", "We are calculating results");
        updateViewsData();

        FirebaseFirestore
                .getInstance()
                .collection(keys.getOrdersRef())
                .whereEqualTo("currentDeliveryBoyUID", UserLive.currentLoggedInUser.getUID())
                .whereEqualTo("currentStatus", OrderStatus.DELIVERING)
                .whereEqualTo("releasedAppCredits",0.0)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            for (QueryDocumentSnapshot d:
                                 task.getResult()) {

                                OrderModel m = d.toObject(OrderModel.class);
                                totalOrdersInQueue ++;
                                pendingPaymentToReceive += m.getRemainingPaymentToPayAtDelivery();
                            }

                            updateViewsData();
                            dialogue.dismiss();
                        }
                    }
                });

        FirebaseFirestore
                .getInstance()
                .collection(keys.getOrdersRef())
                .whereEqualTo("currentDeliveryBoyUID", UserLive.currentLoggedInUser.getUID())
                .whereEqualTo("currentStatus", OrderStatus.DELIVERED)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            totalOrdersDelivered = task.getResult().size();
                            for (QueryDocumentSnapshot d:
                                    task.getResult()) {

                                OrderModel m = d.toObject(OrderModel.class);

                                if (m.getReleasedAppCredits() == 0.0){
                                    paymentReceivedAndNeedToPayToAdmin += m.getRemainingPaymentToPayAtDelivery();
                                }
                            }
                        }

                        updateViewsData();
                        dialogue.dismiss();
                    }
                });

    }

    private void updateViewsData() {
        txvName.setText(UserLive.currentLoggedInUser.getFullName());
        txvEmail.setText(UserLive.currentLoggedInUser.getEmailAddress());
        txvMobile.setText(UserLive.currentLoggedInUser.getMobileNumber());

        if (MainActivityDelivery.userImage!=null){
            imgUserImage.setImageBitmap(MainActivityDelivery.userImage);
        }
        else{

        }

        txvOrdersInQueue.setText(MessageFormat.format("{0}", totalOrdersInQueue));
        txvTotalOrdersDelivered.setText(MessageFormat.format("{0}", totalOrdersDelivered));
        txvPendingPayments.setText(MessageFormat.format("{0} Rs.", pendingPaymentToReceive));
        txvPaymentsReceivedAndNeedToPayToTheAdmin.setText(MessageFormat.format("{0} Rs.", paymentReceivedAndNeedToPayToAdmin));

    }


}
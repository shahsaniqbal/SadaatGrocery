package com.sadaat.groceryapp.ui.Fragments.UserBased.Customers.passive;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sadaat.groceryapp.R;
import com.sadaat.groceryapp.models.Users.UserModel;
import com.sadaat.groceryapp.models.cart.CartModel;
import com.sadaat.groceryapp.models.orders.OrderModel;
import com.sadaat.groceryapp.temp.FirebaseDataKeys;
import com.sadaat.groceryapp.temp.UserLive;
import com.sadaat.groceryapp.temp.order_management.PaymentMethods;
import com.sadaat.groceryapp.ui.Fragments.Generic.DetailedOrderView;
import com.sadaat.groceryapp.ui.Loaders.LoadingDialogue;

public class OrderGatewayFragmentCustomer extends Fragment {

    OrderModel orderModel;
    LoadingDialogue dialogue;

    public OrderGatewayFragmentCustomer(OrderModel mOrder) {
        // Required empty public constructor
        orderModel = mOrder;
    }

    public static OrderGatewayFragmentCustomer newInstance(OrderModel orderModel) {

        return new OrderGatewayFragmentCustomer(orderModel);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.customer_fragment_order_gateway, container, false);


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dialogue = new LoadingDialogue(this.requireActivity());
        if (orderModel.getPaymentThrough().getPaymentThroughMethod().equalsIgnoreCase(PaymentMethods.COD)) {
            processOrder();
        } else {
            openPaymentCardOptions();
        }
    }

    private void openPaymentCardOptions() {

        // Build Web view calling Payment API
        // Post Order

    }

    private void processOrder() {
        dialogue.show("Please Wait", "While processing your order");
        postOrder();
    }

    private void postOrder() {

        CollectionReference ITEMS_REFERENCE = FirebaseFirestore.getInstance().collection(new FirebaseDataKeys().getItemsRef());
        CollectionReference USERS_REFERENCE = FirebaseFirestore.getInstance().collection(new FirebaseDataKeys().getUsersRef());
        CollectionReference ORDERS_REFERENCE = FirebaseFirestore.getInstance().collection(new FirebaseDataKeys().getOrdersRef());

        // Eliminate Item Qty from Stock

        dialogue.show("Please Wait", "Eliminating Stock");

        for (String itemKey : orderModel.getOrderDetails().getCartItems().keySet()) {
            int qty = orderModel.getOrderDetails().getCartItems().get(itemKey).getQty();

            ITEMS_REFERENCE.document(itemKey).update(
                    "otherDetails.stock", FieldValue.increment((-1) * qty)
            );
        }

        // Post Order


        dialogue.show("Please Wait", "Creating your order");

        ORDERS_REFERENCE
                .add(orderModel)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        // Set Order ID
                        if (task.isSuccessful()) {
                            orderModel.setOrderID(task.getResult().getId());
                            ORDERS_REFERENCE.document(orderModel.getOrderID())
                                    .update("orderID", orderModel.getOrderID()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    // Update Order Details IDs to User
                                    // Remove User Cart
                                    // Increment Pending App Credits
                                    // Decrement Wallet App Credits
                                    // Update UserLive.currentLoggedInUser
                                    USERS_REFERENCE
                                            .document(UserLive.currentLoggedInUser.getUID())
                                            .update(
                                                    "currentActiveOrder", orderModel.getOrderID(),
                                                    "orders", FieldValue.arrayUnion(orderModel.getOrderID()),
                                                    "cart", new CartModel(),
                                                    "credits.pendingCredits", FieldValue.increment((long) orderModel.getReleasingAppCredits()),
                                                    "credits.owningCredits", FieldValue.increment((long) ((-1) * orderModel.getPaymentThrough().getAppCreditsUsed()))
                                            )
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {

                                                    USERS_REFERENCE
                                                            .document(UserLive.currentLoggedInUser.getUID())
                                                            .get()
                                                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                    UserLive.currentLoggedInUser = task.getResult().toObject(UserModel.class);
                                                                    dialogue.dismiss();
                                                                    OrderGatewayFragmentCustomer.this.requireActivity()
                                                                            .getSupportFragmentManager()
                                                                            .beginTransaction()
                                                                            .replace(R.id.flFragmentCustomer, OrdersFragmentCustomer.newInstance())
                                                                            .commit();
                                                                }
                                                            });
                                                }
                                            });
                                }
                            });

                        }
                    }
                });


    }

}
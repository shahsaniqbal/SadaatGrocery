package com.sadaat.groceryapp.handler;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sadaat.groceryapp.models.Items.ItemModel;
import com.sadaat.groceryapp.models.StockEntry;
import com.sadaat.groceryapp.models.Users.UserModel;
import com.sadaat.groceryapp.models.cart.CartItemModel;
import com.sadaat.groceryapp.models.orders.OrderModel;
import com.sadaat.groceryapp.models.orders.StatusModel;
import com.sadaat.groceryapp.temp.FirebaseDataKeys;
import com.sadaat.groceryapp.temp.UserLive;
import com.sadaat.groceryapp.temp.order_management.OrderStatus;
import com.sadaat.groceryapp.ui.Loaders.LoadingDialogue;

import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

public class OrderHandler {

    public OrderHandler() {
    }

    /**
     * Decrement User's Pending App Credits
     * User Current Live Order Set to Empty
     * Loop through Items, and restock items
     */
    public void cancelOrder(String UID, OrderModel model, Context mContext) {
        LoadingDialogue dialogue = new LoadingDialogue(mContext);
    }

    public void restock(HashMap<String, CartItemModel> cartItems, Date d) {

        for (String key: cartItems.keySet()){
            CartItemModel m = Objects.requireNonNull(cartItems.get(key));

            FirebaseFirestore
                    .getInstance()
                    .collection(new FirebaseDataKeys().getItemsRef())
                    .document(m.getModel().getID())
                    .update(
                            "stockEntries", FieldValue.arrayUnion(new StockEntry(d, m.getQty())),
                            "otherDetails.stock", FieldValue.increment(m.getQty())
                    );
        }

    }
}

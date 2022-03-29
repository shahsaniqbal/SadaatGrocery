package com.sadaat.groceryapp.syncronizer;

import com.google.firebase.firestore.FirebaseFirestore;
import com.sadaat.groceryapp.models.cart.CartModel;
import com.sadaat.groceryapp.temp.FirebaseDataKeys;
import com.sadaat.groceryapp.temp.UserLive;

public class CustomerCartSynchronizer {
    public static void synchronize(final String UID, CartModel cart){
        FirebaseFirestore
                .getInstance()
                .collection(new FirebaseDataKeys().getUsersRef())
                .document(UserLive.currentLoggedInUser.getUID())
                .update("cart",cart);
    }
}

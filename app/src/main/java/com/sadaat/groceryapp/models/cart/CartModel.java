package com.sadaat.groceryapp.models.cart;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sadaat.groceryapp.models.Items.ItemModel;
import com.sadaat.groceryapp.temp.FirebaseDataKeys;
import com.sadaat.groceryapp.temp.UserLive;

import java.util.Date;
import java.util.HashMap;

public class CartModel {
    private HashMap<String, CartItemModel> cartItems;
    private Date timeStamp;

    private double netTotalRetailPrice = 0;
    private double netTotalSalePrice = 0;
    private double netTotalSecurityCharges = 0;
    private double netTotalCardDiscount = 0;

    public CartModel() {
        this.cartItems = new HashMap<>(1);
    }

    public CartModel(HashMap<String, CartItemModel> cartItems, Date timeStamp) {
        this.cartItems = cartItems;
        handleTimeStamp(timeStamp);
        handleOverallSum();
    }

    private void handleTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;

    }

    public void modifyCartItem(CartItemModel cartItem) {
        cartItems.put(cartItem.getModel().getID(), cartItem);

        String[] ks = new String[cartItems.keySet().size()];
        ks = cartItems.keySet().toArray(ks);

        for (String k : ks) {
            if (cartItems.get(k) != null) {
                if (cartItems.get(k).getQty()==0){
                    cartItems.remove(k);
                }
            }
        }

        //handleTimeStamp(new Date());
        handleOverallSum();
        handleTimeStamp(new Date());
    }

    public void eliminateCartByLatestStock() {
        for (String k : UserLive
                .currentLoggedInUser
                .getCart()
                .getCartItems()
                .keySet()) {

            FirebaseFirestore
                    .getInstance()
                    .collection(new FirebaseDataKeys().getItemsRef())
                    .document(UserLive.currentLoggedInUser.getCart().getCartItems().get(k).getModel().getID())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                int stock = task.getResult().toObject(ItemModel.class).getOtherDetails().getStock();
                                if (stock == 0) {
                                    UserLive.currentLoggedInUser.getCart().getCartItems().get(k).setQty(0);
                                }
                                if (stock < UserLive.currentLoggedInUser.getCart().getCartItems().get(k).getQty()) {
                                    UserLive.currentLoggedInUser.getCart().getCartItems().get(k).setQty(stock);
                                }
                                UserLive.currentLoggedInUser.getCart().getCartItems().get(k).getModel().getOtherDetails().setStock(stock);

                            } else {
                                UserLive.currentLoggedInUser.getCart().getCartItems().get(k).setQty(0);
                            }

                            modifyCartItem(UserLive.currentLoggedInUser.getCart().getCartItems().get(k));
                        }
                    });

        }
        handleOverallSum();
    }

    private void handleOverallSum() {
        netTotalRetailPrice = 0;
        netTotalSalePrice = 0;
        netTotalSecurityCharges = 0;
        netTotalCardDiscount = 0;
        for (String k : cartItems.keySet()) {
            netTotalRetailPrice += cartItems.get(k).getTotalRetailPrice();
            netTotalSalePrice += cartItems.get(k).getTotalSalePrice();
            netTotalCardDiscount += cartItems.get(k).getTotalCardDiscount();
            netTotalSecurityCharges += cartItems.get(k).getTotalSecurityCharges();
        }
    }

    public HashMap<String, CartItemModel> getCartItems() {
        return cartItems;
    }

    public void setCartItems(HashMap<String, CartItemModel> cartItems) {
        this.cartItems = cartItems;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }

    public double getNetTotalRetailPrice() {
        return netTotalRetailPrice;
    }

    public void setNetTotalRetailPrice(double netTotalRetailPrice) {
        this.netTotalRetailPrice = netTotalRetailPrice;
    }

    public double getNetTotalSalePrice() {
        return netTotalSalePrice;
    }

    public void setNetTotalSalePrice(double netTotalSalePrice) {
        this.netTotalSalePrice = netTotalSalePrice;
    }

    public double getNetTotalSecurityCharges() {
        return netTotalSecurityCharges;
    }

    public void setNetTotalSecurityCharges(double netTotalSecurityCharges) {
        this.netTotalSecurityCharges = netTotalSecurityCharges;
    }

    public double getNetTotalCardDiscount() {
        return netTotalCardDiscount;
    }

    public void setNetTotalCardDiscount(double netTotalCardDiscount) {
        this.netTotalCardDiscount = netTotalCardDiscount;
    }

    @Override
    public String toString() {
        return "CartModel{" +
                "cartItems=" + cartItems +
                ", netTotalRetailPrice=" + netTotalRetailPrice +
                ", netTotalSalePrice=" + netTotalSalePrice +
                ", netTotalSecurityCharges=" + netTotalSecurityCharges +
                ", netTotalCardDiscount=" + netTotalCardDiscount +
                '}';
    }
}

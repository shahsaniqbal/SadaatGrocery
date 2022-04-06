package com.sadaat.groceryapp.models.Users;

import com.sadaat.groceryapp.models.AppCredits;
import com.sadaat.groceryapp.models.SuggestionModel;
import com.sadaat.groceryapp.models.Users.UserOtherDetailsModel;
import com.sadaat.groceryapp.models.cart.CartModel;
import com.sadaat.groceryapp.models.orders.OrderModel;

import java.util.ArrayList;

public class UserModel {

    private String UID;
    private String UserType;
    private String fullName;
    private String emailAddress;
    private String mobileNumber;
    private UserOtherDetailsModel details;
    private CartModel cart;
    private AppCredits credits;
    private String currentActiveOrder;
    private ArrayList<String> orders;
    private ArrayList<SuggestionModel> suggestions;


    public UserModel() {

        this.cart = new CartModel();
        this.orders = new ArrayList<>();
        this.credits = new AppCredits();
        this.suggestions = new ArrayList<>();
    }


    public UserModel(String UID, String userType, String fullName, String emailAddress, String mobileNumber, UserOtherDetailsModel details, CartModel cart, AppCredits credits, String currentActiveOrder, ArrayList<String> orders, ArrayList<SuggestionModel> suggestions) {
        this.UID = UID;
        UserType = userType;
        this.fullName = fullName;
        this.emailAddress = emailAddress;
        this.mobileNumber = mobileNumber;
        this.details = details;
        this.cart = cart;
        this.credits = credits;
        this.currentActiveOrder = currentActiveOrder;
        this.orders = orders;
        this.suggestions = suggestions;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public UserOtherDetailsModel getDetails() {
        return details;
    }

    public void setDetails(UserOtherDetailsModel details) {
        this.details = details;
    }

    public String getUserType() {
        return UserType;
    }

    public void setUserType(String userType) {
        UserType = userType;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public CartModel getCart() {
        return cart;
    }

    public void setCart(CartModel cart) {
        this.cart = cart;
    }

    public AppCredits getCredits() {
        return credits;
    }

    public void setCredits(AppCredits credits) {
        this.credits = credits;
    }

    public ArrayList<SuggestionModel> getSuggestions() {
        return suggestions;
    }

    public void setSuggestions(ArrayList<SuggestionModel> suggestions) {
        this.suggestions = suggestions;
    }

    public String getCurrentActiveOrder() {
        return currentActiveOrder;
    }

    public void setCurrentActiveOrder(String currentActiveOrder) {
        this.currentActiveOrder = currentActiveOrder;
    }

    public ArrayList<String> getOrders() {
        return orders;
    }

    public void setOrders(ArrayList<String> orders) {
        this.orders = orders;
    }

    @Override
    public String toString() {
        return "UserModel{" +
                "UID='" + UID + '\'' +
                ", fullName='" + fullName + '\'' +
                ", emailAddress='" + emailAddress + '\'' +
                '}';
    }

}

package com.sadaat.groceryapp.models;

import com.sadaat.groceryapp.models.Users.UserOtherDetailsModel;
import com.sadaat.groceryapp.models.cart.CartModel;

public class UserModel {

    private String UID;
    private String UserType;
    private String fullName;
    private String emailAddress;
    private String mobileNumber;
    private UserOtherDetailsModel details;
    private CartModel cart;

    public UserModel() {

        this.cart = new CartModel();
    }

/*
    public UserModel(String UID, String userType, String fullName, String emailAddress, UserOtherDetailsModel details) {
        this.UID = UID;
        this.UserType = userType;
        this.fullName = fullName;
        this.emailAddress = emailAddress;
        this.details = details;
    }
*/

    public UserModel(String UID, String userType, String fullName, String emailAddress, String mobileNumber, UserOtherDetailsModel details) {
        this();
        this.UID = UID;
        this.UserType = userType;
        this.fullName = fullName;
        this.emailAddress = emailAddress;
        this.mobileNumber = mobileNumber;
        this.details = details;
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

    @Override
    public String toString() {
        return "UserModel{" +
                "UID='" + UID + '\'' +
                ", fullName='" + fullName + '\'' +
                ", emailAddress='" + emailAddress + '\'' +
                '}';
    }

}

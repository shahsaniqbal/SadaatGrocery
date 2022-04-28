package com.sadaat.groceryapp.handler;

import android.content.Context;
import android.content.Intent;

import com.sadaat.groceryapp.temp.UserLive;
import com.sadaat.groceryapp.temp.UserTypes;
import com.sadaat.groceryapp.ui.Activities.UsersBased.Admin.MainActivityAdmin;
import com.sadaat.groceryapp.ui.Activities.UsersBased.Customers.MainActivityCustomer;
import com.sadaat.groceryapp.ui.Activities.UsersBased.DeliveryBoy.MainActivityDelivery;

/**
 * This Class is being coded by AHSAN IQBAL on 09-02-2022 - 11:50 pm
 * The Purpose of this class is to Extend through Intent and Navigation of User based on Login User Type
 * <p>
 * Modification
 * Constructor(Context, String UserType) -> Super(Context, Activity)
 */

public class LoginIntentHandler extends Intent {

    public LoginIntentHandler(Context context, String UserType) {
        Class<?> toNavigateTo = null;

        if (UserType.equalsIgnoreCase(UserTypes.Customer)) {
            toNavigateTo = MainActivityCustomer.class;
            UserLive.currentLoggedInUser.getCart().eliminateCartByLatestStock();
        } else if (UserType.equalsIgnoreCase(UserTypes.DeliveryBoy))
            toNavigateTo = MainActivityDelivery.class;
        else if (UserType.equalsIgnoreCase(UserTypes.Admin))
            toNavigateTo = MainActivityAdmin.class;

        setClass(context, toNavigateTo);
    }
}
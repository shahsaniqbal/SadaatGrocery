package com.sadaat.groceryapp.handler;

import android.content.Context;
import android.content.Intent;

import com.sadaat.groceryapp.temp.UserTypes;
import com.sadaat.groceryapp.ui.Activities.UsersBased.Admin.MainActivityAdmin;
import com.sadaat.groceryapp.ui.Activities.UsersBased.Customers.MainActivityCustomer;

/**
 * This Class is being coded by AHSAN IQBAL on 09-02-2022 - 11:50 pm
 * The Purpose of this class is to Extend through Intent and Navigation of User based on Login User Type
 *
 * Modification
 * Constructor(Context, String UserType) -> Super(Context, Activity)
 * */

public class LoginIntentHandler extends Intent {

    //TODO 0006 To Change the Classes After Making User based Main Activities

    private final Class<?> CLASS_IF_ADMIN = MainActivityAdmin.class;
    private final Class<?> CLASS_IF_CUSTOMER = MainActivityAdmin.class;
    private final Class<?> CLASS_IF_DELIVERY_BOY = MainActivityAdmin.class;


    public LoginIntentHandler(Context context, String UserType) {
        Class<?> toNavigateTo = null;

        if (UserType.equalsIgnoreCase(UserTypes.Customer)) toNavigateTo = CLASS_IF_CUSTOMER;
        else if (UserType.equalsIgnoreCase(UserTypes.DeliveryBoy)) toNavigateTo = CLASS_IF_ADMIN;
        else if (UserType.equalsIgnoreCase(UserTypes.Admin)) toNavigateTo = CLASS_IF_DELIVERY_BOY;

        setClass(context, toNavigateTo);
    }
}
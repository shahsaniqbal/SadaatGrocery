package com.sadaat.groceryapp.temp;

import com.sadaat.groceryapp.R;

public class UserTypes {
    public static final String Admin = "admin-user";
    public static final  String Customer = "customer-user";
    public static final  String DeliveryBoy = "delivery-boy-user";

    public static int getRelevantUserAvatar(String userType){
       if (userType.equalsIgnoreCase(Customer)){
           return R.mipmap.buyer_symbol_actor;
       }
       else if (userType.equalsIgnoreCase(DeliveryBoy)){
           return R.mipmap.delivery_boy_symbol_actor;
       }
       else if (userType.equalsIgnoreCase(Admin)){
           return R.mipmap.admin_symbol_actor;
       }
       else{
           return R.mipmap.logo;
       }
    }
}

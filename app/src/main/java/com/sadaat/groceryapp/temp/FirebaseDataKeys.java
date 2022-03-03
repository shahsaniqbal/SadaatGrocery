package com.sadaat.groceryapp.temp;

public class FirebaseDataKeys {
    final String CATEGORIES = "Menu";
    final String USERS = "Users";
    final String ITEMS = "Items";

    public String getMenuRef(){
        return CATEGORIES;
    }
    public String getUsersRef(){
        return USERS;
    }
    public String getItemsRef() { return ITEMS; }

}

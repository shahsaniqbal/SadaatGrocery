package com.sadaat.groceryapp.models;

import java.util.ArrayList;

public class MenuModel {
    CategoriesModel category;

    ArrayList<ItemModel> items;


    public MenuModel() {
    }

    public MenuModel(CategoriesModel category, ArrayList<ItemModel> items) {
        this.category = category;
        this.items = items;
    }

    public CategoriesModel getCategory() {
        return category;
    }

    public void setCategory(CategoriesModel category) {
        this.category = category;
    }

    public ArrayList<ItemModel> getItems() {
        return items;
    }

    public void setItems(ArrayList<ItemModel> items) {
        this.items = items;
    }
}

package com.sadaat.groceryapp.models;

import java.util.ArrayList;

public class ItemIDListModel {
    ArrayList<String> items;

    public ItemIDListModel() {
    }

    public ItemIDListModel(ArrayList<String> items) {
        this.items = items;
    }

    public ArrayList<String> getItems() {
        return items;
    }

    public void setItems(ArrayList<String> items) {
        this.items = items;
    }
}

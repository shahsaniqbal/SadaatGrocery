package com.sadaat.groceryapp.models.Items;

import com.sadaat.groceryapp.models.ItemModel;

public class ItemComboForDeal {
    private ItemModel item;
    private int qty;
    private double itemSubtotal;

    public ItemComboForDeal() {
    }

    public ItemComboForDeal(ItemModel item, int qty) {
        this.item = item;
        this.qty = qty;
        itemSubtotal = (Double) item.getPrices().getRetailPrice() * this.qty;
    }

    public ItemModel getItem() {
        return item;
    }

    public void setItem(ItemModel item) {
        this.item = item;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public double getItemSubtotal() {
        return itemSubtotal;
    }

    public void setItemSubtotal(double itemSubtotal) {
        this.itemSubtotal = itemSubtotal;
    }
}

package com.sadaat.groceryapp.models.cart;

import com.sadaat.groceryapp.models.ItemModel;

public class CartItemModel {
    private ItemModel model;
    private int qty;
    private double totalSalePrice = 0;
    private double totalRetailPrice = 0;
    private double totalSecurityCharges = 0;
    private double totalCardDiscount = 0;

    public CartItemModel() {
    }

    public CartItemModel(ItemModel model, int qty) {
        this.model = model;
        this.qty = qty;
        calculateOtherPrices();
    }

    private void calculateOtherPrices() {
        totalRetailPrice = ((Double) model.getPrices().getRetailPrice() * qty);
        totalSalePrice = ((Double) model.getPrices().getSalePrice() * qty);
        totalCardDiscount =  ((Double) model.getOtherDetails().getSpecialDiscountForCardHolder() * qty);
        totalSecurityCharges =  ((Double) model.getOtherDetails().getSecurityCharges() * qty);
    }

    public ItemModel getModel() {
        return model;
    }

    public int getQty() {
        return qty;
    }

    public double getTotalSalePrice() {
        return totalSalePrice;
    }

    public double getTotalRetailPrice() {
        return totalRetailPrice;
    }

    public double getTotalSecurityCharges() {
        return totalSecurityCharges;
    }

    public double getTotalCardDiscount() {
        return totalCardDiscount;
    }

    public void setModel(ItemModel model) {
        this.model = model;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public void setTotalSalePrice(double totalSalePrice) {
        this.totalSalePrice = totalSalePrice;
    }

    public void setTotalRetailPrice(double totalRetailPrice) {
        this.totalRetailPrice = totalRetailPrice;
    }

    public void setTotalSecurityCharges(double totalSecurityCharges) {
        this.totalSecurityCharges = totalSecurityCharges;
    }

    public void setTotalCardDiscount(double totalCardDiscount) {
        this.totalCardDiscount = totalCardDiscount;
    }

    @Override
    public String toString() {
        return "CartItemModel{" +
                "model=" + model +
                ", qty=" + qty +
                ", totalSalePrice=" + totalSalePrice +
                ", totalRetailPrice=" + totalRetailPrice +
                ", totalSecurityCharges=" + totalSecurityCharges +
                ", totalCardDiscount=" + totalCardDiscount +
                '}';
    }
}

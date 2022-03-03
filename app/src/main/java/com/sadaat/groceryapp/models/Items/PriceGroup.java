package com.sadaat.groceryapp.models.Items;

public class PriceGroup {
    private double retailPrice;
    private double discountedPrice;
    private double salePrice;

    public PriceGroup() {
    }

    public PriceGroup(double retailPrice, double discountedPrice, double salePrice) {
        this.retailPrice = retailPrice;
        this.discountedPrice = discountedPrice;
        this.salePrice = salePrice;
    }
    public PriceGroup(double retailPrice, double salePrice) {
        this.retailPrice = retailPrice;
        this.discountedPrice = this.salePrice  = salePrice;
    }

    public double getRetailPrice() {
        return retailPrice;
    }

    public void setRetailPrice(double retailPrice) {
        this.retailPrice = retailPrice;
    }

    public double getDiscountedPrice() {
        return discountedPrice;
    }

    public void setDiscountedPrice(double discountedPrice) {
        this.discountedPrice = discountedPrice;
    }

    public double getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(double salePrice) {
        this.salePrice = salePrice;
    }
}

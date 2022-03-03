package com.sadaat.groceryapp.models.Items;

public class OtherDataForItem {

    double securityCharges;
    int stock;
    private String imageLink;
    double specialDiscountForCardHolder;

    public OtherDataForItem() {
    }

    public OtherDataForItem(double securityCharges, int stock, double specialDiscountForCardHolder) {
        this.securityCharges = securityCharges;
        this.stock = stock;
        this.specialDiscountForCardHolder = specialDiscountForCardHolder;
        this.imageLink = "";
    }

    public OtherDataForItem(double securityCharges, int stock, double specialDiscountForCardHolder, String imageLink) {
        this.securityCharges = securityCharges;
        this.stock = stock;
        this.specialDiscountForCardHolder = specialDiscountForCardHolder;
        this.imageLink = imageLink;
    }

    public double getSecurityCharges() {
        return securityCharges;
    }

    public void setSecurityCharges(double securityCharges) {
        this.securityCharges = securityCharges;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }

    public double getSpecialDiscountForCardHolder() {
        return specialDiscountForCardHolder;
    }

    public void setSpecialDiscountForCardHolder(double specialDiscountForCardHolder) {
        this.specialDiscountForCardHolder = specialDiscountForCardHolder;
    }
}

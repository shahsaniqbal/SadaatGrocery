package com.sadaat.groceryapp.models;

import com.sadaat.groceryapp.models.Items.ItemComboForDeal;
import com.sadaat.groceryapp.models.Items.OtherDataForItem;
import com.sadaat.groceryapp.models.Items.PriceGroup;

import java.util.ArrayList;

public class DealsModel extends PriceGroup {
    private String title;
    private String shortDesc;
    private String longDesc;

    private ArrayList<ItemComboForDeal> items;
    private OtherDataForItem otherDetails;

    public DealsModel(String title, String shortDesc, String longDesc, ArrayList<ItemComboForDeal> items, OtherDataForItem otherDetails) {
        this.title = title;
        this.shortDesc = shortDesc;
        this.longDesc = longDesc;
        this.items = items;
        this.otherDetails = otherDetails;

        double retailSum = 0.0;

        for (ItemComboForDeal item: items){
            retailSum+=item.getItemSubtotal();
        }

        super.setRetailPrice(retailSum);
    }

    public DealsModel(String title, String shortDesc, String longDesc, ArrayList<ItemComboForDeal> items, OtherDataForItem otherDetails, double discountedPrice, double salePrice ) {
        this(title, shortDesc,longDesc,items,otherDetails);
        super.setDiscountedPrice(discountedPrice);
        super.setSalePrice(salePrice);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getShortDesc() {
        return shortDesc;
    }

    public void setShortDesc(String shortDesc) {
        this.shortDesc = shortDesc;
    }

    public String getLongDesc() {
        return longDesc;
    }

    public void setLongDesc(String longDesc) {
        this.longDesc = longDesc;
    }

    public ArrayList<ItemComboForDeal> getItems() {
        return items;
    }

    public void setItems(ArrayList<ItemComboForDeal> items) {
        this.items = items;
    }

    public OtherDataForItem getOtherDetails() {
        return otherDetails;
    }

    public void setOtherDetails(OtherDataForItem otherDetails) {
        this.otherDetails = otherDetails;
    }

    @Override
    public double getRetailPrice() {
        return super.getRetailPrice();
    }

    @Override
    public void setRetailPrice(double retailPrice) {
        super.setRetailPrice(retailPrice);
    }

    @Override
    public double getDiscountedPrice() {
        return super.getDiscountedPrice();
    }

    @Override
    public void setDiscountedPrice(double discountedPrice) {
        super.setDiscountedPrice(discountedPrice);
    }

    @Override
    public double getSalePrice() {
        return super.getSalePrice();
    }

    @Override
    public void setSalePrice(double salePrice) {
        super.setSalePrice(salePrice);
    }
}

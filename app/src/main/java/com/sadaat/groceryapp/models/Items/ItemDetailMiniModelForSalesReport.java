package com.sadaat.groceryapp.models.Items;

public class ItemDetailMiniModelForSalesReport {
    String itemID;
    String itemName;
    String unitModel;
    long qty;

    public ItemDetailMiniModelForSalesReport(String itemID, String itemName, String unitModel, long qty) {
        this.itemID = itemID;
        this.itemName = itemName;
        this.unitModel = unitModel;
        this.qty = qty;
    }

    public String getItemID() {
        return itemID;
    }

    public void setItemID(String itemID) {
        this.itemID = itemID;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getUnitModel() {
        return unitModel;
    }

    public void setUnitModel(String unitModel) {
        this.unitModel = unitModel;
    }

    public long getQty() {
        return qty;
    }

    public void setQty(long qty) {
        this.qty = qty;
    }
}

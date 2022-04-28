package com.sadaat.groceryapp.models.Items;

import com.sadaat.groceryapp.models.StockEntry;

import java.util.ArrayList;

public class ItemModel {
    private boolean isDeal = false;
    private String ID = "";
    private String name;
    private String description;

    private CategoryBindingModel categoryBinding;
    private PriceGroup prices;
    private OtherDataForItem otherDetails;
    private QtyUnitModel qty;
    private ArrayList<StockEntry> stockEntries;
    private int maxQtyPerOrder = -1;
    private boolean hot = true;

    public ItemModel() {
    }

    /*public ItemModel(String name, String description, CategoryBindingModel categoryBinding, PriceGroup prices, OtherDataForItem otherDetails, QtyUnitModel qty) {
        this.name = name;
        this.description = description;
        this.categoryBinding = categoryBinding;
        this.prices = prices;
        this.otherDetails = otherDetails;
        this.qty = qty;
    }

    public ItemModel(String ID, String name, String description, CategoryBindingModel categoryBinding, PriceGroup prices, OtherDataForItem otherDetails, QtyUnitModel qty) {
        this.ID = ID;
        this.name = name;
        this.description = description;
        this.categoryBinding = categoryBinding;
        this.prices = prices;
        this.otherDetails = otherDetails;
    }

    public ItemModel(String name, String description, CategoryBindingModel categoryBinding, PriceGroup prices, OtherDataForItem otherDetails, QtyUnitModel qty, int maxQtyPerOrder) {
        this.ID = ID;
        this.name = name;
        this.description = description;
        this.categoryBinding = categoryBinding;
        this.prices = prices;
        this.otherDetails = otherDetails;
        this.qty = qty;
        this.maxQtyPerOrder = maxQtyPerOrder;
    }

    public ItemModel(String ID, String name, String description, CategoryBindingModel categoryBinding, PriceGroup prices, OtherDataForItem otherDetails, QtyUnitModel qty, int maxQtyPerOrder) {
        this.ID = ID;
        this.name = name;
        this.description = description;
        this.categoryBinding = categoryBinding;
        this.prices = prices;
        this.otherDetails = otherDetails;
        this.qty = qty;
        this.maxQtyPerOrder = maxQtyPerOrder;
    }
*/

    public ItemModel(String ID,
                     String name,
                     String description,
                     CategoryBindingModel categoryBinding,
                     PriceGroup prices,
                     OtherDataForItem otherDetails,
                     QtyUnitModel qty,
                     ArrayList<StockEntry> stockEntries,
                     int maxQtyPerOrder,
                     boolean hot) {
        this.ID = ID;
        this.name = name;
        this.description = description;
        this.categoryBinding = categoryBinding;
        this.prices = prices;
        this.otherDetails = otherDetails;
        this.qty = qty;
        this.stockEntries = stockEntries;
        this.maxQtyPerOrder = maxQtyPerOrder;
        this.hot = hot;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public CategoryBindingModel getCategoryBinding() {
        return categoryBinding;
    }

    public void setCategoryBinding(CategoryBindingModel categoryBinding) {
        this.categoryBinding = categoryBinding;
    }

    public PriceGroup getPrices() {
        return prices;
    }

    public void setPrices(PriceGroup prices) {
        this.prices = prices;
    }

    public OtherDataForItem getOtherDetails() {
        return otherDetails;
    }

    public void setOtherDetails(OtherDataForItem otherDetails) {
        this.otherDetails = otherDetails;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public QtyUnitModel getQty() {
        return qty;
    }

    public void setQty(QtyUnitModel qty) {
        this.qty = qty;
    }

    public int getMaxQtyPerOrder() {
        return maxQtyPerOrder;
    }

    public void setMaxQtyPerOrder(int maxQtyPerOrder) {
        this.maxQtyPerOrder = maxQtyPerOrder;
    }

    public ArrayList<StockEntry> getStockEntries() {
        return stockEntries;
    }

    public void setStockEntries(ArrayList<StockEntry> stockEntries) {
        this.stockEntries = stockEntries;
    }

    public boolean isDeal() {
        return isDeal;
    }

    public void setDeal(boolean deal) {
        isDeal = deal;
    }

    public boolean isHot() {
        return hot;
    }

    public void setHot(boolean hot) {
        this.hot = hot;
    }

    @Override
    public String toString() {
        return "ItemModel{" +
                "name='" + name + '\'' +
                ", hot=" + hot +
                '}';
    }
}

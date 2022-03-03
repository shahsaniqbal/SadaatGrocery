package com.sadaat.groceryapp.models.Items;

public class QtyUnitModel {
    private double qty;
    private String unit;

    public QtyUnitModel() {
    }

    public QtyUnitModel(double qty, String unit) {
        this.qty = qty;
        this.unit = unit;
    }

    public double getQty() {
        return qty;
    }

    public void setQty(double qty) {
        this.qty = qty;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    @Override
    public String toString() {
        return qty + " " + unit;
    }
}

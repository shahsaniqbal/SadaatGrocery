package com.sadaat.groceryapp.models;


public class StockEntry {
    private String timeStamp;
    private int stockIncrement;

    public StockEntry() {
    }

    public StockEntry(String timeStamp, int stockIncrement) {
        this.timeStamp = timeStamp;
        this.stockIncrement = stockIncrement;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public int getStockIncrement() {
        return stockIncrement;
    }

    public void setStockIncrement(int stockIncrement) {
        this.stockIncrement = stockIncrement;
    }

    
}

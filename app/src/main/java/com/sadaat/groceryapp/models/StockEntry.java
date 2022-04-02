package com.sadaat.groceryapp.models;


import java.util.Date;

public class StockEntry {
    private Date timeStamp;
    private int stockIncrement;

    public StockEntry() {
    }

    public StockEntry(Date timeStamp, int stockIncrement) {
        this.timeStamp = timeStamp;
        this.stockIncrement = stockIncrement;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }

    public int getStockIncrement() {
        return stockIncrement;
    }

    public void setStockIncrement(int stockIncrement) {
        this.stockIncrement = stockIncrement;
    }
}

package com.sadaat.groceryapp.models;

public class AppCredits {
    private long pendingCredits;
    private long owningCredits;

    public AppCredits() {

        this.pendingCredits = 0;
        this.owningCredits = 0;
    }

    public AppCredits(long pendingCredits, long owningCredits) {
        this.pendingCredits = pendingCredits;
        this.owningCredits = owningCredits;
    }

    public long getPendingCredits() {
        return pendingCredits;
    }

    public void setPendingCredits(long pendingCredits) {
        this.pendingCredits = pendingCredits;
    }

    public long getOwningCredits() {
        return owningCredits;
    }

    public void setOwningCredits(long owningCredits) {
        this.owningCredits = owningCredits;
    }
}

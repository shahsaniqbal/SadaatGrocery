package com.sadaat.groceryapp.models;

import java.util.Date;

public class LeadsModel {
    private Date timeStamp;
    private String action;

    public LeadsModel() {
    }

    public LeadsModel(Date timeStamp, String action) {
        this.timeStamp = timeStamp;
        this.action = action;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
}

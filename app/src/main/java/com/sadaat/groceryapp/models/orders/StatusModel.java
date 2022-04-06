package com.sadaat.groceryapp.models.orders;

import java.util.Date;

public class StatusModel {
    private String status;
    private Date timeStamp;

    public StatusModel() {
    }

    public StatusModel(String status, Date timeStamp) {
        this.status = status;
        this.timeStamp = timeStamp;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }
}

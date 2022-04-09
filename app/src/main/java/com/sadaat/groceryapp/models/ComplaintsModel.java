package com.sadaat.groceryapp.models;

import java.util.Date;

public class ComplaintsModel {
    private String complaintID;
    private String orderID;
    private String complaintTitle;
    private String complaintMessage;
    private Date complaintIssueDate;

    private String replyTitle;
    private String replyMessage;
    private Date replyResolvedDate;
    private boolean isResolved;

    public ComplaintsModel() {
    }

    public ComplaintsModel(String complaintID, String orderID, String complaintTitle, String complaintMessage, Date complaintIssueDate, String replyTitle, String replyMessage, Date replyResolvedDate, boolean isResolved) {
        this.complaintID = complaintID;
        this.orderID = orderID;
        this.complaintTitle = complaintTitle;
        this.complaintMessage = complaintMessage;
        this.complaintIssueDate = complaintIssueDate;
        this.replyTitle = replyTitle;
        this.replyMessage = replyMessage;
        this.replyResolvedDate = replyResolvedDate;
        this.isResolved = isResolved;
    }

    public String getComplaintID() {
        return complaintID;
    }

    public void setComplaintID(String complaintID) {
        this.complaintID = complaintID;
    }

    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public String getComplaintTitle() {
        return complaintTitle;
    }

    public void setComplaintTitle(String complaintTitle) {
        this.complaintTitle = complaintTitle;
    }

    public String getComplaintMessage() {
        return complaintMessage;
    }

    public void setComplaintMessage(String complaintMessage) {
        this.complaintMessage = complaintMessage;
    }

    public Date getComplaintIssueDate() {
        return complaintIssueDate;
    }

    public void setComplaintIssueDate(Date complaintIssueDate) {
        this.complaintIssueDate = complaintIssueDate;
    }

    public String getReplyTitle() {
        return replyTitle;
    }

    public void setReplyTitle(String replyTitle) {
        this.replyTitle = replyTitle;
    }

    public String getReplyMessage() {
        return replyMessage;
    }

    public void setReplyMessage(String replyMessage) {
        this.replyMessage = replyMessage;
    }

    public Date getReplyResolvedDate() {
        return replyResolvedDate;
    }

    public void setReplyResolvedDate(Date replyResolvedDate) {
        this.replyResolvedDate = replyResolvedDate;
    }

    public boolean isResolved() {
        return isResolved;
    }

    public void setResolved(boolean resolved) {
        isResolved = resolved;
    }
}

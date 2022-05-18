package com.sadaat.groceryapp.models;

import java.util.Date;

public class SuggestionModel {

    private String suggestionID;
    private String uid;
    private String userFullName;
    private String userContactNumber;
    private String userEmail;

    private String suggestionTitle;
    private String suggestionDetails;
    private Date suggestedDate;

    private double feedback;

    private String replyMessage;
    private Date replyDate;

    public SuggestionModel() {
    }

    public SuggestionModel(String suggestionID, String uid, String userFullName, String userContactNumber, String userEmail, String suggestionTitle, String suggestionDetails, Date suggestedDate, double feedback, String replyMessage, Date replyDate) {
        this.suggestionID = suggestionID;
        this.uid = uid;
        this.userFullName = userFullName;
        this.userContactNumber = userContactNumber;
        this.userEmail = userEmail;
        this.suggestionTitle = suggestionTitle;
        this.suggestionDetails = suggestionDetails;
        this.suggestedDate = suggestedDate;
        this.feedback = feedback;
        this.replyMessage = replyMessage;
        this.replyDate = replyDate;
    }

    public String getSuggestionID() {
        return suggestionID;
    }

    public void setSuggestionID(String suggestionID) {
        this.suggestionID = suggestionID;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUserFullName() {
        return userFullName;
    }

    public void setUserFullName(String userFullName) {
        this.userFullName = userFullName;
    }

    public String getUserContactNumber() {
        return userContactNumber;
    }

    public void setUserContactNumber(String userContactNumber) {
        this.userContactNumber = userContactNumber;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getSuggestionTitle() {
        return suggestionTitle;
    }

    public void setSuggestionTitle(String suggestionTitle) {
        this.suggestionTitle = suggestionTitle;
    }

    public String getSuggestionDetails() {
        return suggestionDetails;
    }

    public void setSuggestionDetails(String suggestionDetails) {
        this.suggestionDetails = suggestionDetails;
    }

    public Date getSuggestedDate() {
        return suggestedDate;
    }

    public void setSuggestedDate(Date suggestedDate) {
        this.suggestedDate = suggestedDate;
    }

    public double getFeedback() {
        return feedback;
    }

    public void setFeedback(double feedback) {
        this.feedback = feedback;
    }

    public String getReplyMessage() {
        return replyMessage;
    }

    public void setReplyMessage(String replyMessage) {
        this.replyMessage = replyMessage;
    }

    public Date getReplyDate() {
        return replyDate;
    }

    public void setReplyDate(Date replyDate) {
        this.replyDate = replyDate;
    }
}

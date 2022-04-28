package com.sadaat.groceryapp.models.orders;

import com.sadaat.groceryapp.models.ComplaintsModel;
import com.sadaat.groceryapp.models.cart.CartModel;

import java.util.ArrayList;

public class OrderModel {
    private String orderID;
    private CartModel orderDetails; // With Save Items, Unit Price, Qty etc
    private ArrayList<StatusModel> statusUpdates; //

    private String currentStatus;

    private String uid; //
    private String currentDeliveryBoyUID; //

    private PaymentThrough paymentThrough; //
    private double remainingPaymentToPayAtDelivery;  //

    private ArrayList<ComplaintsModel> complaints;//
    private String receivingStatus; //

    private double totalOrderAmountInRetail; //
    private double releasingAppCredits; //
    private double releasedAppCredits = 0.0; //

    private String deliveryLocation;

    public OrderModel() {

        orderID = "";

    }

    public OrderModel(String orderID,
                      CartModel orderDetails,
                      ArrayList<StatusModel> statusUpdates,
                      String currentStatus,
                      String uid,
                      String currentDeliveryBoyUID,
                      PaymentThrough paymentThrough,
                      double remainingPaymentToPayAtDelivery,
                      ArrayList<ComplaintsModel> complaints,
                      String receivingStatus,
                      double totalOrderAmountInRetail,
                      double releasingAppCredits,
                      double releasedAppCredits,
                      String deliveryLocation) {
        this.orderID = orderID;
        this.orderDetails = orderDetails;
        this.statusUpdates = statusUpdates;
        this.currentStatus = currentStatus;
        this.uid = uid;
        this.currentDeliveryBoyUID = currentDeliveryBoyUID;
        this.paymentThrough = paymentThrough;
        this.remainingPaymentToPayAtDelivery = remainingPaymentToPayAtDelivery;
        this.complaints = complaints;
        this.receivingStatus = receivingStatus;
        this.totalOrderAmountInRetail = totalOrderAmountInRetail;
        this.releasingAppCredits = releasingAppCredits;
        this.releasedAppCredits = releasedAppCredits;
        this.deliveryLocation = deliveryLocation;
    }

    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public CartModel getOrderDetails() {
        return orderDetails;
    }

    public void setOrderDetails(CartModel orderDetails) {
        this.orderDetails = orderDetails;
    }

    public ArrayList<StatusModel> getStatusUpdates() {
        return statusUpdates;
    }

    public void setStatusUpdates(ArrayList<StatusModel> statusUpdates) {
        this.statusUpdates = statusUpdates;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getCurrentDeliveryBoyUID() {
        return currentDeliveryBoyUID;
    }

    public void setCurrentDeliveryBoyUID(String currentDeliveryBoyUID) {
        this.currentDeliveryBoyUID = currentDeliveryBoyUID;
    }

    public PaymentThrough getPaymentThrough() {
        return paymentThrough;
    }

    public void setPaymentThrough(PaymentThrough paymentThrough) {
        this.paymentThrough = paymentThrough;
    }

    public double getRemainingPaymentToPayAtDelivery() {
        return remainingPaymentToPayAtDelivery;
    }

    public void setRemainingPaymentToPayAtDelivery(double remainingPaymentToPayAtDelivery) {
        this.remainingPaymentToPayAtDelivery = remainingPaymentToPayAtDelivery;
    }

    public ArrayList<ComplaintsModel> getComplaints() {
        return complaints;
    }

    public void setComplaints(ArrayList<ComplaintsModel> complaints) {
        this.complaints = complaints;
    }

    public String getReceivingStatus() {
        return receivingStatus;
    }

    public void setReceivingStatus(String receivingStatus) {
        this.receivingStatus = receivingStatus;
    }

    public double getTotalOrderAmountInRetail() {
        return totalOrderAmountInRetail;
    }

    public void setTotalOrderAmountInRetail(double totalOrderAmountInRetail) {
        this.totalOrderAmountInRetail = totalOrderAmountInRetail;
    }

    public double getReleasingAppCredits() {
        return releasingAppCredits;
    }

    public void setReleasingAppCredits(double releasingAppCredits) {
        this.releasingAppCredits = releasingAppCredits;
    }

    public String getDeliveryLocation() {
        return deliveryLocation;
    }

    public void setDeliveryLocation(String deliveryLocation) {
        this.deliveryLocation = deliveryLocation;
    }

    public double getReleasedAppCredits() {
        return releasedAppCredits;
    }

    public void setReleasedAppCredits(double releasedAppCredits) {
        this.releasedAppCredits = releasedAppCredits;
    }

    public String getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(String currentStatus) {
        this.currentStatus = currentStatus;
    }

    @Override
    public String toString() {
        return "OrderModel{" +
                "orderID='" + orderID + '\'' +
                ", orderDetails=" + orderDetails +
                ", currentStatus='" + currentStatus + '\'' +
                ", uid='" + uid + '\'' +
                ", currentDeliveryBoyUID='" + currentDeliveryBoyUID + '\'' +
                ", remainingPaymentToPayAtDelivery=" + remainingPaymentToPayAtDelivery +
                ", totalOrderAmountInRetail=" + totalOrderAmountInRetail +
                ", releasingAppCredits=" + releasingAppCredits +
                ", releasedAppCredits=" + releasedAppCredits +
                ", deliveryLocation='" + deliveryLocation + '\'' +
                '}';
    }
}

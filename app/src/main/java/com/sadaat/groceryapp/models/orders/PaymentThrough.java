package com.sadaat.groceryapp.models.orders;

public class PaymentThrough {
    private String paymentThroughMethod;
    private long appCreditsUsed;
    private String transactionID;

    public PaymentThrough(String paymentThroughMethod, long appCreditsUsed, String transactionID) {
        this.paymentThroughMethod = paymentThroughMethod;
        this.appCreditsUsed = appCreditsUsed;
        this.transactionID = transactionID;
    }

    public PaymentThrough() {
    }

    public String getPaymentThroughMethod() {
        return paymentThroughMethod;
    }

    public void setPaymentThroughMethod(String paymentThroughMethod) {
        this.paymentThroughMethod = paymentThroughMethod;
    }

    public long getAppCreditsUsed() {
        return appCreditsUsed;
    }

    public void setAppCreditsUsed(long appCreditsUsed) {
        this.appCreditsUsed = appCreditsUsed;
    }

    public String getTransactionID() {
        return transactionID;
    }

    public void setTransactionID(String transactionID) {
        this.transactionID = transactionID;
    }
}

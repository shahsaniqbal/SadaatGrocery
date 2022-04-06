package com.sadaat.groceryapp.models.orders;

public class PaymentThrough {
    private String paymentThroughMethod;
    private long appCreditsUsed;

    public PaymentThrough(String paymentThroughMethod, long appCreditsUsed) {
        this.paymentThroughMethod = paymentThroughMethod;
        this.appCreditsUsed = appCreditsUsed;
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
}

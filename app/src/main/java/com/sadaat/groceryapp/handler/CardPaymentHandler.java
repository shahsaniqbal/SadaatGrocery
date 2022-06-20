package com.sadaat.groceryapp.handler;

import java.util.HashMap;

public class CardPaymentHandler {

    private final String privateKey;

    public CardPaymentHandler(final String privateKey) {
        this.privateKey = privateKey;
    }

    public void cardExecutor(){

        //Merchant.privatekey = privateKey;

        String message;

        try {
            HashMap<String, String> billing = new HashMap<String, String>();
            billing.put("name", "Testing Tester");
            billing.put("addrLine1", "xvxcvxcvxcvcx");
            billing.put("city", "Columbus");
            billing.put("state", "Ohio");
            billing.put("country", "USA");
            billing.put("zipCode", "43230");
            billing.put("email", "tester@2co.com");
            billing.put("phone", "555-555-5555");

            HashMap<String, Object> request = new HashMap<String, Object>();
            request.put("sellerId", "1817037");
            request.put("merchantOrderId", "test123");
            request.put("token", "MGI4OTU0OTQtMDIxNi00YThlLTliOTctZjg1YmJiMzg0MjA3");
            request.put("currency", "USD");
            request.put("total", "1.00");
            request.put("billingAddr", billing);

            //Authorization response = TwocheckoutCharge.authorize(request);
            //message = response.getResponseMsg();
        } catch (Exception e) {
            message = e.toString();
        }

        //Log.e("TAG FOR PAYMENT TESTING", message);
    }
}

package com.sadaat.groceryapp.ui.Fragments.UserBased.Customers.passive;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sadaat.groceryapp.R;
import com.sadaat.groceryapp.handler.LeadsActionHandler;
import com.sadaat.groceryapp.models.Users.UserModel;
import com.sadaat.groceryapp.models.cart.CartModel;
import com.sadaat.groceryapp.models.orders.OrderModel;
import com.sadaat.groceryapp.temp.FirebaseDataKeys;
import com.sadaat.groceryapp.temp.UserLive;
import com.sadaat.groceryapp.temp.order_management.PaymentMethods;
import com.sadaat.groceryapp.ui.Fragments.UserBased.Customers.UnderAccountFragment.OrdersFragmentCustomer;
import com.sadaat.groceryapp.ui.Loaders.LoadingDialogue;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class OrderGatewayFragmentCustomer extends Fragment {

    private static final String BASE_REQUEST_API_URL = "https://sandboxpay.api.ptecho.com";
    private static final String paymentReturnUrlSuccess = "http://localhost/success.php";
    private static final String paymentReturnUrlReject = "http://localhost/reject.php";
    private final String MERCHANT_ENCRYPTED_ID = "D0EKNfMlkPc1NcW9Y9Pfzi51Anl2";

    OrderModel orderModel;
    LoadingDialogue dialogue;

    private WebView mWebView;
    private String price;

    private TextView txvWebLogs;


    public OrderGatewayFragmentCustomer(OrderModel mOrder) {
        // Required empty public constructor
        orderModel = mOrder;
    }

    public static OrderGatewayFragmentCustomer newInstance(OrderModel orderModel) {

        return new OrderGatewayFragmentCustomer(orderModel);
    }

    public static String php_hash_hmac(String data, String secret) {
        String returnString = "";
        try {
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
            sha256_HMAC.init(secret_key);
            byte[] res = sha256_HMAC.doFinal(data.getBytes());
            returnString = bytesToHex(res);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return returnString;
    }

    public static String bytesToHex(byte[] bytes) {
        final char[] hexArray = "0123456789abcdef".toCharArray();
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0, v; j < bytes.length; j++) {
            v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.customer_fragment_order_gateway, container, false);


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dialogue = new LoadingDialogue(this.requireActivity());
        mWebView = view.findViewById(R.id.wv);
        txvWebLogs = view.findViewById(R.id.txv_weblogs);
        txvWebLogs.setText("");

        if (orderModel.getPaymentThrough().getPaymentThroughMethod().equalsIgnoreCase(PaymentMethods.COD)) {
            processCashOrder();
            setLog("Processing Cash Order of Rs. "+orderModel.getTotalOrderAmountInRetail());
        } else {
            processCardOrder();
            setLog("Processing Card Order of Rs. "+orderModel.getTotalOrderAmountInRetail());
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void processCardOrder() {

        mWebView.setVisibility(View.VISIBLE);

        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            webSettings.setSafeBrowsingEnabled(false);
        }

        mWebView.setWebViewClient(new MyWebViewClient());
        webSettings.setDomStorageEnabled(true);
        //mWebView.addJavascriptInterface(new FormDataInterface(), "FORMOUT");

        price = "" + ((long) orderModel.getTotalOrderAmountInRetail()) + ".00";

        String merchant = MERCHANT_ENCRYPTED_ID;
        String password = "";
        String returnURLSuccess = paymentReturnUrlSuccess;
        String returnURLReject = paymentReturnUrlReject;
        String pp_Amount = price;
        String pp_Currency = "PKR";

        String postData = "";

        try {
            postData += "merchant" + "=" + merchant + "&";
            postData += "password" + "=" + password + "&";
            postData += "amount" + "=" + pp_Amount;

            /*
            postData += URLEncoder.encode("pp_Currency", "UTF-8")
                    + "=" + URLEncoder.encode(pp_Currency, "UTF-8") + "&";
            postData += URLEncoder.encode("ReturnURLSuccess", "UTF-8")
                    + "=" + URLEncoder.encode(returnURLSuccess, "UTF-8") + "&";
            postData += URLEncoder.encode("ReturnURLReject", "UTF-8")
                    + "=" + URLEncoder.encode(returnURLReject, "UTF-8") + "&";
            */

        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.e("AhmadLogs: postData : ", postData);

        appendWebLog("Sending Request on sandboxpay.api.ptecho.com for Card");
        mWebView.loadUrl(BASE_REQUEST_API_URL + "?" + postData);
    }

    private void processCashOrder() {
        mWebView.setVisibility(View.INVISIBLE);
        dialogue.show("Please Wait", "While processing your order");
        postOrder("");
    }

    private void postOrder(String txID) {

        appendWebLog("Managing and Posting your order");

        CollectionReference ITEMS_REFERENCE = FirebaseFirestore.getInstance().collection(new FirebaseDataKeys().getItemsRef());
        CollectionReference USERS_REFERENCE = FirebaseFirestore.getInstance().collection(new FirebaseDataKeys().getUsersRef());
        CollectionReference ORDERS_REFERENCE = FirebaseFirestore.getInstance().collection(new FirebaseDataKeys().getOrdersRef());

        // Eliminate Item Qty from Stock

        dialogue.show("Please Wait", "Eliminating Stock");

        for (String itemKey : orderModel.getOrderDetails().getCartItems().keySet()) {
            int qty = orderModel.getOrderDetails().getCartItems().get(itemKey).getQty();

            ITEMS_REFERENCE.document(itemKey).update(
                    "otherDetails.stock", FieldValue.increment(((-1) * qty))
            );
        }

        // Post Order


        dialogue.show("Please Wait", "Creating your order");

        if (!txID.isEmpty()) {
            orderModel.getPaymentThrough().setTransactionID(txID);
        }

        String newOrderID = generateOrderID();
        orderModel.setOrderID(newOrderID);

        ORDERS_REFERENCE
                .document(newOrderID)
                .set(orderModel)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // Update Order Details IDs to User
                            // Remove User Cart
                            // Increment Pending App Credits
                            // Decrement Wallet App Credits
                            // Update UserLive.currentLoggedInUser
                            USERS_REFERENCE
                                    .document(UserLive.currentLoggedInUser.getUID())
                                    .update(
                                            "currentActiveOrder", orderModel.getOrderID(),
                                            "orders", FieldValue.arrayUnion(orderModel.getOrderID()),
                                            "cart", new CartModel(),
                                            "credits.pendingCredits", FieldValue.increment((long) orderModel.getReleasingAppCredits()),
                                            "credits.owningCredits", FieldValue.increment((long) ((-1) * orderModel.getPaymentThrough().getAppCreditsUsed()))
                                    )
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            USERS_REFERENCE
                                                    .document(UserLive.currentLoggedInUser.getUID())
                                                    .get()
                                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                            UserLive.currentLoggedInUser = task.getResult().toObject(UserModel.class);
                                                            dialogue.dismiss();
                                                            appendWebLog("Order Posted Successfully");

                                                            StringBuilder action = new StringBuilder();
                                                            action.append("Customer ");
                                                            action.append("(").append(UserLive.currentLoggedInUser.getFullName()).append(") ");
                                                            action.append("Placed the Order ");
                                                            action.append("(").append(newOrderID).append(") ");
                                                            action.append("of Rs. ");
                                                            action.append("").append(orderModel.getTotalOrderAmountInRetail()).append(" ");
                                                            action.append("through ");
                                                            action.append("").append(orderModel.getPaymentThrough().getPaymentThroughMethod()).append("");


                                                            new LeadsActionHandler() {
                                                                @Override
                                                                public void onSuccessCompleteAction() {

                                                                }

                                                                @Override
                                                                public void onCancelledAction() {

                                                                }
                                                            }.addAction(action.toString());

                                                            OrderGatewayFragmentCustomer.this.requireActivity()
                                                                    .getSupportFragmentManager()
                                                                    .beginTransaction()
                                                                    .replace(R.id.flFragmentCustomer, OrdersFragmentCustomer.newInstance())
                                                                    .commit();
                                                        }
                                                    });
                                        }
                                    });
                        }
                    }
                });


    }

    private String generateOrderID() {

        StringBuilder newOrderIDBuilder = new StringBuilder();
        newOrderIDBuilder.append(UserLive.currentLoggedInUser.getFullName().toUpperCase().charAt(0));
        newOrderIDBuilder.append(UserLive.currentLoggedInUser.getFullName().toUpperCase().charAt(1));
        newOrderIDBuilder.append(UserLive.currentLoggedInUser.getFullName().toUpperCase().charAt(UserLive.currentLoggedInUser.getFullName().length()-2));
        newOrderIDBuilder.append(UserLive.currentLoggedInUser.getFullName().toUpperCase().charAt(UserLive.currentLoggedInUser.getFullName().length()-1));
        newOrderIDBuilder.append("-");
        String time = "" + new Date().getTime();
        time = time.substring(3, 7);
        newOrderIDBuilder.append(time);
        newOrderIDBuilder.append("-");
        String len = "" + UserLive.currentLoggedInUser.getOrders().size();
        int toAddZeros = 5 - len.length();
        for (int i=0; i<toAddZeros; i++){
            newOrderIDBuilder.append(0);
        }
        newOrderIDBuilder.append(len);

        return newOrderIDBuilder.toString();
    }

    private void doubleCheckCardPaymentResults(Map<String, String> results) {

        String successCode = results.get("isSuccess");
        if (Objects.equals(successCode, "true")) {
            appendWebLog("Transaction ID: \n" + results.get("transaction"));
            appendWebLog("Message: \n" + results.get("message"));
            Toast.makeText(OrderGatewayFragmentCustomer.this.requireActivity(), "Payment Success", Toast.LENGTH_SHORT).show();
            postOrder(results.get("transaction"));
        } else {
            Toast.makeText(OrderGatewayFragmentCustomer.this.requireActivity(), results.get("error"), Toast.LENGTH_SHORT).show();
            appendWebLog("Payment Rejection");
            appendWebLog("Message: \n" + results.get("error"));
        }
    }

    private class MyWebViewClient extends WebViewClient {

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            if (url.contains(paymentReturnUrlSuccess) || url.contains(paymentReturnUrlReject)) {
                Log.e("On Page Started", "AhmadLogs: return url cancelling");

                Map<String, String> returnData = new HashMap<>();
                String webResponse = url.split("[?]")[1];
                String[] paramsData = webResponse.split("[&]");
                for (String param :
                        paramsData) {
                    String keyValues[] = param.split("[=]");
                    returnData.put(keyValues[0], keyValues[1]);
                }

                doubleCheckCardPaymentResults(returnData);

                view.stopLoading();
                return;
            }
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
        }
    }

    // private class FormDataInterface {
    //     @JavascriptInterface
    //     public void processFormData(String url, String formData) {
    //         Log.e("FORM DATA PROCESSING", "Sadaat: Url:" + url + " form data " + formData);
    //         Map<String, String> results = new HashMap<>();
    //         if (url.equals(paymentReturnUrl)) {
    //             String[] values = formData.split("&");
    //             for (String pair : values) {
    //                 String[] nameValue = pair.split("=");
    //                 if (nameValue.length == 2) {
    //                     results.put(nameValue[0], nameValue[1]);
    //                 }
    //             }
    //
    //             doubleCheckCardPaymentResults(results);
    //
    //             return;
    //         }
    //     }
    // }

    private void setLog(String log) {
        txvWebLogs.setText(log);
    }
    private void appendWebLog(String log) {
        txvWebLogs.setText(String.format("%s...\n%s", txvWebLogs.getText().toString(), log));
    }

}
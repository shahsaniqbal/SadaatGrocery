package com.sadaat.groceryapp.ui.Fragments.UserBased.Customers.passive;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sadaat.groceryapp.R;
import com.sadaat.groceryapp.models.Users.UserModel;
import com.sadaat.groceryapp.models.cart.CartModel;
import com.sadaat.groceryapp.models.orders.OrderModel;
import com.sadaat.groceryapp.temp.FirebaseDataKeys;
import com.sadaat.groceryapp.temp.UserLive;
import com.sadaat.groceryapp.temp.order_management.PaymentMethods;
import com.sadaat.groceryapp.ui.Loaders.LoadingDialogue;

public class OrderGatewayFragmentCustomer extends Fragment {

    OrderModel orderModel;
    LoadingDialogue dialogue;

    private WebView mWebView;

    private final String TWO_CO_sid         = "252773590658";
    private final String TWO_CO_SecretWord  = "zH*Jj9v$QuqVH4v&4#HzzexHhHUs4N@kx7xay?Gp&7zN6d%g9EQG#j*T?nKr@pyg";
    private static final String paymentReturnUrl="http://localhost/2checkout/index.php";

    private static String price="";


    public OrderGatewayFragmentCustomer(OrderModel mOrder) {
        // Required empty public constructor
        orderModel = mOrder;
    }

    public static OrderGatewayFragmentCustomer newInstance(OrderModel orderModel) {

        return new OrderGatewayFragmentCustomer(orderModel);
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

        if (orderModel.getPaymentThrough().getPaymentThroughMethod().equalsIgnoreCase(PaymentMethods.COD)) {
            processOrder();
        } else {
            openPaymentCardOptions();
        }
    }

    private void openPaymentCardOptions() {

        // Build Web view calling Payment API
        // Post Order
        cardPaymentOrderProcess();

    }

    @SuppressLint("SetJavaScriptEnabled")
    private void cardPaymentOrderProcess() {
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        mWebView.setWebViewClient(new MyWebViewClient());
        webSettings.setDomStorageEnabled(true);

        price = ""+orderModel.getOrderDetails().getNetTotalRetailPrice();


        String sid = TWO_CO_sid;
        String mode = "2CO";
        String li_0_name = "invoice123";
        String li_0_price = price;
        String card_holder_name = "John Doe";
        String street_address = "123 Test Address";
        String street_address2 = "Suite 200";
        String city = "Lahore";
        String state = "Punjab";
        String zip = "54000";
        String country = "Pakistan";
        String email = "test@example.com";
        String phone = "03001234567";
        String demo = "Y";

        String postData = "";

        postData += "sid"+ "=" + sid + "&";
        postData += "mode" + "=" + mode + "&";
        postData += "li_0_name" + "=" + li_0_name + "&";
        postData += "li_0_price" + "=" + li_0_price + "&";
        postData += "card_holder_name" + "=" + card_holder_name + "&";
        postData += "street_address" + "=" + street_address + "&";
        postData += "street_address2" + "=" + street_address2 + "&";
        postData += "city" + "=" + city + "&";
        postData += "state" + "=" + state + "&";
        postData += "zip" + "=" + zip + "&";
        postData += "country" + "=" + country + "&";
        postData += "email" + "=" + email + "&";
        postData += "phone" + "=" + phone + "&";
        postData += "demo" + "=" + demo;

        mWebView.postUrl("https://www.2checkout.com/checkout/purchase", postData.getBytes());
    }

    private void processOrder() {
        dialogue.show("Please Wait", "While processing your order");
        postOrder();
    }

    private void postOrder() {

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

        ORDERS_REFERENCE
                .add(orderModel)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        // Set Order ID
                        if (task.isSuccessful()) {
                            orderModel.setOrderID(task.getResult().getId());
                            ORDERS_REFERENCE.document(orderModel.getOrderID())
                                    .update("orderID", orderModel.getOrderID()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
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
                            });

                        }
                    }
                });


    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            System.out.println("AhmadLogs: onPageStarted - url : " +url);

            String redirect_url = url.substring(0, paymentReturnUrl.length());
            String response = url.substring(paymentReturnUrl.length(), url.length());

            System.out.println("AhmadLogs: onPageStarted - redirect_url : " +redirect_url);
            System.out.println("AhmadLogs: onPageStarted - response : " +response);

            if(redirect_url.equals(paymentReturnUrl)) {
                System.out.println("AhmadLogs: return url cancelling");
                view.stopLoading();

                String[] values = response.split("&");
                for (String pair : values) {
                    String[] nameValue = pair.split("=");
                    if (nameValue.length == 2) {
                        System.out.println("AhmadLogs: Name:" + nameValue[0] + " value:" + nameValue[1]);
                        //i.putExtra(nameValue[0], nameValue[1]);
                        postOrder();
                        return;
                    }
                }

                return;
            }
            return;
            //super.onPageStarted(view, url, favicon);
        }
    }
}
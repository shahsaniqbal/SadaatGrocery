package com.sadaat.groceryapp.ui.Fragments.UserBased.Customers.passive;

import static android.content.ContentValues.TAG;
import static android.view.View.GONE;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.sadaat.groceryapp.R;
import com.sadaat.groceryapp.adapters.customer.OrderItemDisplayAdapterCustomer;
import com.sadaat.groceryapp.models.ComplaintsModel;
import com.sadaat.groceryapp.models.StockEntry;
import com.sadaat.groceryapp.models.cart.CartItemModel;
import com.sadaat.groceryapp.models.orders.OrderModel;
import com.sadaat.groceryapp.models.orders.StatusModel;
import com.sadaat.groceryapp.temp.FirebaseDataKeys;
import com.sadaat.groceryapp.temp.UserLive;
import com.sadaat.groceryapp.temp.order_management.OrderStatus;
import com.sadaat.groceryapp.temp.order_management.PaymentMethods;
import com.sadaat.groceryapp.ui.Fragments.Generic.DetailedOrderViewFragmentGeneric;
import com.sadaat.groceryapp.ui.Loaders.LoadingDialogue;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class OrdersFragmentCustomer extends Fragment implements OrderItemDisplayAdapterCustomer.ItemClickListeners {

    CollectionReference USER_REF = FirebaseFirestore.getInstance().collection(UserLive.currentLoggedInUser.getUID());
    CollectionReference ORDERS_REF = FirebaseFirestore.getInstance().collection(new FirebaseDataKeys().getOrdersRef());
    LoadingDialogue dialogue;
    private OrderModel currentLiveOrder;
    private MaterialTextView txvOrderID;
    private MaterialTextView txvAmount;
    private MaterialTextView txvRemaining;
    private MaterialTextView txvMethod;
    private MaterialTextView txvRelease;
    private MaterialTextView txvOrderStatus;
    private MaterialTextView txvAddress;
    private MaterialTextView txvDeliveryBoy;
    private MaterialTextView txvTimeInitiated;
    private MaterialCardView btnCancel;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager manager;
    private OrderItemDisplayAdapterCustomer adapter;

    AlertDialog.Builder dialogueBuilderForComplaints;
    AlertDialog complaintPopupDialogueBox;
    ViewHolderPopupAddComplaint viewHolderForComplaints;
    View popupView;



    public OrdersFragmentCustomer() {
        // Required empty public constructor
    }

    public static OrdersFragmentCustomer newInstance() {
        return new OrdersFragmentCustomer();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.customer_fragment_orders, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dialogue = new LoadingDialogue(this.requireActivity());

        initializeViews(view);

        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);

        ORDERS_REF
                .whereEqualTo("uid", UserLive.currentLoggedInUser.getUID())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        for (QueryDocumentSnapshot q :
                                task.getResult()) {
                            adapter.addItem(q.toObject(OrderModel.class));
                        }

                    }

                });

        updateView(view);

        if (!UserLive.currentLoggedInUser.getCurrentActiveOrder().isEmpty()) {
            ORDERS_REF.document(UserLive.currentLoggedInUser.getCurrentActiveOrder()).addSnapshotListener((snapshot, e) -> {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    currentLiveOrder = snapshot.toObject(OrderModel.class);
                    assert currentLiveOrder != null;
                    if (currentLiveOrder.getCurrentStatus().equalsIgnoreCase(OrderStatus.DELIVERED) || currentLiveOrder.getCurrentStatus().equalsIgnoreCase(OrderStatus.CANCELLED)) {
                        UserLive.currentLoggedInUser.setCurrentActiveOrder("");
                        currentLiveOrder = null;
                        USER_REF.document(UserLive.currentLoggedInUser.getUID())
                                .update("currentActiveOrder", "");
                    }
                    updateView(view);

                } else {
                    Log.d(TAG, "Current data: null");
                }
            });
        }

        btnCancel.setOnClickListener(v -> {
            if (currentLiveOrder != null) {
                onCancelOrder();
            }
        });
    }

    private void onCancelOrder() {

        if (currentLiveOrder != null) {

            dialogue.show("Please Wait", "Cancelling Order");

            UserLive.currentLoggedInUser.getCredits().setPendingCredits((long) UserLive.currentLoggedInUser.getCredits().getPendingCredits() - (long) currentLiveOrder.getReleasingAppCredits());
            UserLive.currentLoggedInUser.setCurrentActiveOrder("");

            FirebaseFirestore.getInstance()
                    .collection(new FirebaseDataKeys().getUsersRef())
                    .document(currentLiveOrder.getUid())
                    .update(
                            "currentActiveOrder", "",
                            "credits.pendingCredits", UserLive.currentLoggedInUser.getCredits().getPendingCredits())
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {

                            ArrayList<CartItemModel> list = new ArrayList<>();
                            for (String k : currentLiveOrder.getOrderDetails().getCartItems().keySet()) {

                                list.add(currentLiveOrder.getOrderDetails().getCartItems().get(k));

                            }

                            for (int i = 0; i < list.size(); i++) {

                                int finalI = i;
                                FirebaseFirestore
                                        .getInstance()
                                        .collection(new FirebaseDataKeys().getItemsRef())
                                        .document(Objects.requireNonNull(list.get(i).getModel().getID()))
                                        .update("otherDetails.stock", FieldValue.increment(Objects.requireNonNull(list.get(i)).getQty()),
                                                "stockEntries", FieldValue.arrayUnion(
                                                        new StockEntry(new Date(), Objects.requireNonNull(list.get(i)).getQty())
                                                ))
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful() && finalI == list.size()-1) {

                                                    FirebaseFirestore.getInstance()
                                                            .collection(new FirebaseDataKeys().getOrdersRef())
                                                            .document(currentLiveOrder.getOrderID())
                                                            .update("currentStatus", OrderStatus.CANCELLED,
                                                                    "statusUpdates", FieldValue.arrayUnion(
                                                                            new StatusModel(OrderStatus.CANCELLED, new Date())
                                                                    ))
                                                            .addOnCompleteListener(task1 -> {
                                                                if (task1.isSuccessful()) {
                                                                    dialogue.dismiss();
                                                                }
                                                            });
                                                }
                                            }
                                        });
                            }


                        }
                    });

        }

    }

    private void initializeViews(View view) {
        txvOrderID = view.findViewById(R.id.admin_order_item_orderID);
        txvAmount = view.findViewById(R.id.admin_order_item_amount);
        txvRemaining = view.findViewById(R.id.admin_order_item_remaining);
        txvMethod = view.findViewById(R.id.admin_order_item_method);
        txvRelease = view.findViewById(R.id.admin_order_item_releasing);
        txvOrderStatus = view.findViewById(R.id.admin_order_item_status);
        txvAddress = view.findViewById(R.id.admin_order_item_address);
        txvDeliveryBoy = view.findViewById(R.id.admin_order_item_deliveryBoy);
        txvTimeInitiated = view.findViewById(R.id.admin_order_item_time);
        btnCancel = view.findViewById(R.id.cancel_order_button);

        recyclerView = view.findViewById(R.id.recycler);
        dialogue = new LoadingDialogue(this.requireActivity());
        manager = new LinearLayoutManager(this.requireActivity());
        adapter = new OrderItemDisplayAdapterCustomer(new ArrayList<>(), this, this.requireActivity());

        dialogueBuilderForComplaints = new AlertDialog.Builder(requireActivity());

        popupView = this.getLayoutInflater().inflate(R.layout.customer_popup_add_order_complaint, null, false);
        dialogueBuilderForComplaints.setView(popupView);

        complaintPopupDialogueBox = dialogueBuilderForComplaints.create();

        viewHolderForComplaints = new ViewHolderPopupAddComplaint(popupView);

    }

    private void updateView(View v) {
        if (currentLiveOrder == null) {
            v.findViewById(R.id.layout_for_live_order).setVisibility(GONE);
            v.findViewById(R.id.layout_for_no_live_order).setVisibility(View.VISIBLE);
        } else {
            v.findViewById(R.id.layout_for_live_order).setVisibility(View.VISIBLE);
            v.findViewById(R.id.layout_for_no_live_order).setVisibility(GONE);


            // Data Setters
            txvOrderID.setText(currentLiveOrder.getOrderID());
            txvAddress.setText(currentLiveOrder.getDeliveryLocation());
            txvAmount.setText(MessageFormat.format("{0} Rs.", currentLiveOrder.getTotalOrderAmountInRetail()));
            txvRemaining.setText(MessageFormat.format("{0} Rs.", currentLiveOrder.getRemainingPaymentToPayAtDelivery()));
            txvMethod.setText(MessageFormat.format("{0}", currentLiveOrder.getPaymentThrough().getPaymentThroughMethod().toUpperCase()));
            txvRelease.setText(MessageFormat.format("{0} Credits", currentLiveOrder.getReleasingAppCredits()));
            txvOrderStatus.setText(currentLiveOrder.getCurrentStatus());
            txvTimeInitiated.setText(currentLiveOrder.getStatusUpdates().get(0).getTimeStamp().toString());

            if (!currentLiveOrder.getCurrentDeliveryBoyUID().isEmpty()) {
                FirebaseFirestore
                        .getInstance()
                        .collection(new FirebaseDataKeys().getUsersRef())
                        .document(currentLiveOrder.getCurrentDeliveryBoyUID())
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                txvDeliveryBoy.setText(task.getResult().get("fullName", String.class));
                            } else {
                                txvDeliveryBoy.setText("");
                            }
                        });
            } else {

                txvDeliveryBoy.setText("");

            }

            if (currentLiveOrder.getCurrentStatus().equalsIgnoreCase(OrderStatus.INITIATED) && currentLiveOrder.getPaymentThrough().getPaymentThroughMethod().equalsIgnoreCase(PaymentMethods.COD)) {
                btnCancel.setVisibility(View.VISIBLE);
            } else {
                btnCancel.setVisibility(View.GONE);
            }

        }
    }

    @Override
    public void onFullItemClick(OrderModel orderModel) {
        requireActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.flFragmentCustomer, DetailedOrderViewFragmentGeneric.newInstance(orderModel))
                .addToBackStack("orders_list")
                .commit();
    }

    @Override
    public void onComplaintButtonClick(String orderID, boolean whetherHasAnyComplaint, int position) {
        if (whetherHasAnyComplaint){
            Toast.makeText(OrdersFragmentCustomer.this.requireActivity(), "Complaint With the Order already exists", Toast.LENGTH_SHORT).show();
        }

        else{
            complaintPopupDialogueBox.show();
            viewHolderForComplaints.getTxvOrderID().setText(orderID);

            /*
            * Prepare Complaint Model
            * Update Complaint ID to OrderModel Locally
            * Update Complaint ID to OrderModel Cloud
            * */

            viewHolderForComplaints.getBtnPost().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (viewHolderForComplaints.getEdxComplaintTitle().getText().toString().isEmpty()){
                        viewHolderForComplaints.getEdxComplaintTitle().setError("Title Cannot be Empty");
                    }
                    else if (viewHolderForComplaints.getEdxComplaintMessage().getText().toString().isEmpty()){
                        viewHolderForComplaints.getEdxComplaintMessage().setError("Please Type your Complaint Message");
                    }
                    else{
                        ComplaintsModel model = new ComplaintsModel();
                        model.setComplaintTitle(viewHolderForComplaints.getEdxComplaintTitle().getText().toString());
                        model.setComplaintMessage(viewHolderForComplaints.getEdxComplaintMessage().getText().toString());
                        model.setOrderID(orderID);
                        model.setComplaintID("C_"+orderID);
                        model.setComplaintIssueDate(new Date());
                        model.setUid(UserLive.currentLoggedInUser.getUID());

                        FirebaseFirestore
                                .getInstance()
                                .collection(new FirebaseDataKeys().getComplaintsRef())
                                .document(model.getComplaintID())
                                .set(model)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){

                                            FirebaseFirestore
                                                    .getInstance()
                                                    .collection(new FirebaseDataKeys().getOrdersRef())
                                                    .document(orderID)
                                                    .update("complaintID", model.getComplaintID())
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()){
                                                                adapter.getLocalDataSet().get(position).setComplaintID(model.getComplaintID());
                                                                adapter.notifyItemChanged(position);
                                                                complaintPopupDialogueBox.dismiss();
                                                            }
                                                        }
                                                    });

                                        }
                                    }
                                });
                    }
                }
            });

        }
    }

    private class ViewHolderPopupAddComplaint {

        private MaterialTextView txvOrderID;
        private TextInputEditText edxComplaintTitle;
        private TextInputEditText edxComplaintMessage;
        private MaterialButton btnPost;

        public ViewHolderPopupAddComplaint(View mainView) {
            txvOrderID = mainView.findViewById(R.id.txv_order_id);
            edxComplaintTitle = mainView.findViewById(R.id.edx_complaint_title);
            edxComplaintMessage = mainView.findViewById(R.id.edx_complaint_message);
            btnPost = mainView.findViewById(R.id.btn_post);

        }

        public MaterialTextView getTxvOrderID() {
            return txvOrderID;
        }

        public TextInputEditText getEdxComplaintTitle() {
            return edxComplaintTitle;
        }

        public TextInputEditText getEdxComplaintMessage() {
            return edxComplaintMessage;
        }

        public MaterialButton getBtnPost() {
            return btnPost;
        }
    }
}
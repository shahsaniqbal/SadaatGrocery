package com.sadaat.groceryapp.ui.Fragments.UserBased.Admin.UnderHomeFragmentSuper;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.sadaat.groceryapp.R;
import com.sadaat.groceryapp.adapters.admin.OrderItemDisplayAdapterAdmin;
import com.sadaat.groceryapp.models.orders.OrderModel;
import com.sadaat.groceryapp.models.orders.StatusModel;
import com.sadaat.groceryapp.temp.FirebaseDataKeys;
import com.sadaat.groceryapp.temp.order_management.OrderStatus;
import com.sadaat.groceryapp.ui.Fragments.Generic.DetailedOrderViewFragmentGeneric;
import com.sadaat.groceryapp.ui.Fragments.UserBased.Admin.DeliveryBoysListToSetForOrder;
import com.sadaat.groceryapp.ui.Loaders.LoadingDialogue;

import java.util.ArrayList;
import java.util.Date;

public class OrdersListFragmentAdmin extends Fragment implements OrderItemDisplayAdapterAdmin.ItemClickListeners {

    private static final String ARG_PARAM = "orders_type";

    View vNoOrders;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager manager;
    OrderItemDisplayAdapterAdmin adapterAdmin;
    LoadingDialogue progressDialog;
    private String mOrderType;

    MaterialTextView txvOrderType;

    public OrdersListFragmentAdmin() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static OrdersListFragmentAdmin newInstance(String mOrderType) {

        OrdersListFragmentAdmin fragment = new OrdersListFragmentAdmin();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM, mOrderType);
        fragment.setArguments(args);
        return fragment;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mOrderType = getArguments().getString(ARG_PARAM);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.admin_fragment_orders_list, container, false);
    }


    @Override
    public void onPause() {
        super.onPause();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle(null);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle("Orders");
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initialize(view);

        txvOrderType.setText(mOrderType + " Orders");

        fetchAndNotifyAllData();

    }


    private void initialize(View view) {
        txvOrderType = view.findViewById(R.id.txv_order_type_title);
        this.recyclerView = view.findViewById(R.id.recycler_order_items);
        this.manager = new LinearLayoutManager(OrdersListFragmentAdmin.this.requireActivity());
        this.progressDialog = new LoadingDialogue(OrdersListFragmentAdmin.this.requireActivity());
        vNoOrders = view.findViewById(R.id.row_for_no_orders);

        this.adapterAdmin = new OrderItemDisplayAdapterAdmin(
                new ArrayList<>(),
                this,
                OrdersListFragmentAdmin.this.requireActivity()
        );


        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapterAdmin);

    }

    private void fetchAndNotifyAllData() {

        this.progressDialog.show("Please Wait", "While We Are Fetching Orders for you");
        adapterAdmin.deleteAll();


        FirebaseFirestore
                .getInstance()
                .collection(new FirebaseDataKeys().getOrdersRef())
                .whereEqualTo("currentStatus", mOrderType)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult().size() > 0) {

                                vNoOrders.setVisibility(View.GONE);

                                for (DocumentSnapshot q : task.getResult().getDocuments()) {
                                    adapterAdmin.addItem(q.toObject(OrderModel.class));
                                }

                                progressDialog.dismiss();
                            } else {
                                progressDialog.dismiss();

                            }

                        }

                        if (adapterAdmin.getLocalDataSet().size() == 0) {
                            vNoOrders.setVisibility(View.VISIBLE);
                        }

                    }
                });

    }

    @Override
    public void onFullItemClick(OrderModel orderModel) {
        requireActivity().
                getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.nav_host_fragment_content_main_activity_admin, DetailedOrderViewFragmentGeneric.newInstance(orderModel))
                .addToBackStack("order_list")
                .commit();
    }

    @Override
    public void onAssignDeliveryBoyButtonClick(OrderModel orderModel) {
        requireActivity().
                getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.nav_host_fragment_content_main_activity_admin, DeliveryBoysListToSetForOrder.newInstance(orderModel.getOrderID(), orderModel.getRemainingPaymentToPayAtDelivery()))
                .commit();
    }

    /**
     * User Pending -> Wallet App Credits
     * Order Released App Credits setting
     */
    @Override
    public void onReleaseAppCreditsButtonClick(OrderModel orderModel, double releasingAppCredits) {
        FirebaseFirestore
                .getInstance()
                .collection(new FirebaseDataKeys().getUsersRef())
                .document(orderModel.getUid())
                .update("credits.pendingCredits", FieldValue.increment(((-1) * (long) releasingAppCredits)),
                        "credits.owningCredits", FieldValue.increment(releasingAppCredits))
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        FirebaseFirestore
                                .getInstance()
                                .collection(new FirebaseDataKeys().getOrdersRef())
                                .document(orderModel.getOrderID())
                                .update("releasedAppCredits", releasingAppCredits)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        fetchAndNotifyAllData();
                                    }
                                });
                    }
                });
    }

    @Override
    public void onPackButtonClick(int viewPosition, String orderID) {

        StatusModel statusModel = new StatusModel(OrderStatus.PACKING, new Date());

        FirebaseFirestore
                .getInstance()
                .collection(new FirebaseDataKeys().getOrdersRef())
                .document(orderID)
                .update("currentStatus", OrderStatus.PACKING,
                        "statusUpdates",
                        FieldValue.arrayUnion(statusModel))
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        fetchAndNotifyAllData();
                    }
                });
    }

    private void setAllCardsToColorWhite() {
        /*cardInitiated.setCardBackgroundColor(getResources().getColor(R.color.white));
        cardPacking.setCardBackgroundColor(getResources().getColor(R.color.white));
        cardDelivering.setCardBackgroundColor(getResources().getColor(R.color.white));
        cardDelivered.setCardBackgroundColor(getResources().getColor(R.color.white));
        cardCancelled.setCardBackgroundColor(getResources().getColor(R.color.white));
        cardNotReceived.setCardBackgroundColor(getResources().getColor(R.color.white));*/
    }
}
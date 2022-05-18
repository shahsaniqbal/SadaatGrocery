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
import com.google.android.material.card.MaterialCardView;
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

public class OrdersFragmentAdmin extends Fragment implements OrderItemDisplayAdapterAdmin.ItemClickListeners, View.OnClickListener {

    final CollectionReference MENU_COLLECTION_REFERENCE = FirebaseFirestore.getInstance().collection(new FirebaseDataKeys().getMenuRef());

    AlertDialog.Builder dialogueBuilder;
    View popupView;
    View vNoOrders;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager manager;
    OrderItemDisplayAdapterAdmin adapterAdmin;
    ArrayList<OrderModel> list;

    LoadingDialogue progressDialog;

    String currentStatus = OrderStatus.INITIATED;

    MaterialCardView cardInitiated, cardPacking, cardDelivering, cardDelivered, cardCancelled, cardNotReceived;


    public OrdersFragmentAdmin() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static OrdersFragmentAdmin newInstance(String param1, String param2) {

        return new OrdersFragmentAdmin();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.admin_fragment_orders, container, false);
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

        cardInitiated.setOnClickListener(this);
        cardPacking.setOnClickListener(this);
        cardDelivering.setOnClickListener(this);
        cardCancelled.setOnClickListener(this);
        cardNotReceived.setOnClickListener(this);
        cardDelivered.setOnClickListener(this);

        onClick(cardInitiated);

        /*group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {

            }
        });*/
    }


    private void initialize(View view) {
        this.recyclerView = view.findViewById(R.id.recycler_order_items);
        this.manager = new LinearLayoutManager(OrdersFragmentAdmin.this.requireActivity());

        this.list = new ArrayList<>();

        this.progressDialog = new LoadingDialogue(OrdersFragmentAdmin.this.requireActivity());

        dialogueBuilder = new AlertDialog.Builder(this.requireActivity());

        vNoOrders = view.findViewById(R.id.row_for_no_orders);

        dialogueBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });


        dialogueBuilder.setIcon(R.mipmap.logo);
        dialogueBuilder.setTitle("We are really sorry");



        // this.popupView = this.getLayoutInflater().inflate(R.layout.admin_popup_add_categories, null, false);
        // this.customPopupViewHolder = new CategoriesListFragmentAdmin.CustomPopupViewHolder(popupView);
        // this.dialogueBuilder = new AlertDialog.Builder(requireActivity());
        // this.dialogueBuilder.setView(popupView);
        // this.itemPopupDialogueBox = dialogueBuilder.create();

        this.adapterAdmin = new OrderItemDisplayAdapterAdmin(
                list,
                this,
                OrdersFragmentAdmin.this.requireActivity()
        );


        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapterAdmin);

        cardInitiated = view.findViewById(R.id.initiated);
        cardPacking = view.findViewById(R.id.packing);
        cardDelivering = view.findViewById(R.id.delivering);
        cardDelivered = view.findViewById(R.id.delivered);
        cardCancelled = view.findViewById(R.id.cancelled);
        cardNotReceived = view.findViewById(R.id.notReceived);


    }

    private void fetchAndNotifyAllData() {

        this.progressDialog.show("Please Wait", "While We Are Fetching Orders for you");
        adapterAdmin.deleteAll();


        FirebaseFirestore
                .getInstance()
                .collection(new FirebaseDataKeys().getOrdersRef())
                .whereEqualTo("currentStatus", currentStatus)
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
                            }

                            else {
                                progressDialog.dismiss();

                            }

                        }

                        if (adapterAdmin.getLocalDataSet().size()==0){
                            vNoOrders.setVisibility(View.VISIBLE);
                            /*dialogueBuilder.setMessage("There are no such Orders for \"" + currentStatus + "\" status");
                            dialogueBuilder.show();*/

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

    @Override
    public void onClick(View view) {

        setAllCardsToColorWhite();

        if (view.getId() == cardInitiated.getId()){
            currentStatus = OrderStatus.INITIATED;
            cardInitiated.setCardBackgroundColor(getResources().getColor(R.color.light_grey));
            fetchAndNotifyAllData();
        }
        else if (view.getId() == cardPacking.getId()){

            cardPacking.setCardBackgroundColor(getResources().getColor(R.color.light_grey));
            currentStatus = OrderStatus.PACKING;
            fetchAndNotifyAllData();
        }
        else if (view.getId() == cardDelivering.getId()){

            cardDelivering.setCardBackgroundColor(getResources().getColor(R.color.light_grey));
            currentStatus = OrderStatus.DELIVERING;
            fetchAndNotifyAllData();
        }
        else if (view.getId() == cardDelivered.getId()){

            cardDelivered.setCardBackgroundColor(getResources().getColor(R.color.light_grey));
            currentStatus = OrderStatus.DELIVERED;
            fetchAndNotifyAllData();
        }
        else if (view.getId() == cardCancelled.getId()){

            cardCancelled.setCardBackgroundColor(getResources().getColor(R.color.light_grey));
            currentStatus = OrderStatus.CANCELLED;
            fetchAndNotifyAllData();
        }
        else if (view.getId() == cardNotReceived.getId()){

            cardNotReceived.setCardBackgroundColor(getResources().getColor(R.color.light_grey));
            currentStatus = OrderStatus.NOT_RECEIVED;
            fetchAndNotifyAllData();
        }

    }

    private void setAllCardsToColorWhite() {
        cardInitiated.setCardBackgroundColor(getResources().getColor(R.color.white));
        cardPacking.setCardBackgroundColor(getResources().getColor(R.color.white));
        cardDelivering.setCardBackgroundColor(getResources().getColor(R.color.white));
        cardDelivered.setCardBackgroundColor(getResources().getColor(R.color.white));
        cardCancelled.setCardBackgroundColor(getResources().getColor(R.color.white));
        cardNotReceived.setCardBackgroundColor(getResources().getColor(R.color.white));
    }
}
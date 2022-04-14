package com.sadaat.groceryapp.ui.Fragments.UserBased.Admin.UnderHomeFragmentSuper;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.sadaat.groceryapp.R;
import com.sadaat.groceryapp.adapters.admin.OrderItemDisplayAdapterAdmin;
import com.sadaat.groceryapp.adapters.admin.category.CategoriesItemAdapterAdmin;
import com.sadaat.groceryapp.models.categories.CategoriesModel;
import com.sadaat.groceryapp.models.orders.OrderModel;
import com.sadaat.groceryapp.models.orders.StatusModel;
import com.sadaat.groceryapp.temp.FirebaseDataKeys;
import com.sadaat.groceryapp.temp.order_management.OrderStatus;
import com.sadaat.groceryapp.ui.Fragments.Generic.DetailedOrderView;
import com.sadaat.groceryapp.ui.Fragments.Generic.ItemFullModalFragmentGeneric;
import com.sadaat.groceryapp.ui.Fragments.UserBased.Admin.DeliveryBoysListToSetForOrder;
import com.sadaat.groceryapp.ui.Fragments.UserBased.Admin.UnderListingFragmentChildSuper.CategoriesListFragmentAdmin;
import com.sadaat.groceryapp.ui.Loaders.LoadingDialogue;

import java.util.ArrayList;
import java.util.Date;

public class OrdersFragmentAdmin extends Fragment implements OrderItemDisplayAdapterAdmin.ItemClickListeners {

    final CollectionReference MENU_COLLECTION_REFERENCE = FirebaseFirestore.getInstance().collection(new FirebaseDataKeys().getMenuRef());

    AlertDialog.Builder dialogueBuilder;
    AlertDialog itemPopupDialogueBox;
    View popupView;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager manager;
    OrderItemDisplayAdapterAdmin adapterAdmin;
    ArrayList<OrderModel> list;

    LoadingDialogue progressDialog;


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

        fetchAndNotifyAllData();
    }


    private void initialize(View view) {
        this.recyclerView = view.findViewById(R.id.recycler_order_items);
        this.manager = new LinearLayoutManager(OrdersFragmentAdmin.this.requireActivity());

        this.list = new ArrayList<>();

        this.progressDialog = new LoadingDialogue(OrdersFragmentAdmin.this.requireActivity());

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

    }

    private void fetchAndNotifyAllData() {

        this.progressDialog.show("Please Wait", "While We Are Fetching Orders for you");

        FirebaseFirestore
                .getInstance()
                .collection(new FirebaseDataKeys().getOrdersRef())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            for (QueryDocumentSnapshot q:task.getResult()){
                                adapterAdmin.addItem(q.toObject(OrderModel.class));
                            }
                        }
                        progressDialog.dismiss();
                    }
                });

    }

    @Override
    public void onFullItemClick(OrderModel orderModel) {
        requireActivity().
                getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.nav_host_fragment_content_main_activity_admin, DetailedOrderView.newInstance(orderModel))
                .addToBackStack("order_list")
                .commit();
    }

    @Override
    public void onAssignDeliveryBoyButtonClick(OrderModel orderModel) {
        requireActivity().
                getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.nav_host_fragment_content_main_activity_admin, DeliveryBoysListToSetForOrder.newInstance(orderModel.getOrderID()))
                .commit();
    }

    @Override
    public void onReleaseAppCreditsButtonClick(OrderModel orderModel, double releasingAppCredits) {

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
                        if (task.isSuccessful()){
                            adapterAdmin.getLocalDataSet().get(viewPosition).getStatusUpdates().add(statusModel);
                            adapterAdmin.notifyItemChanged(viewPosition);
                        }
                    }
                });
    }
}
package com.sadaat.groceryapp.adapters.admin;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sadaat.groceryapp.R;
import com.sadaat.groceryapp.models.Users.UserModel;
import com.sadaat.groceryapp.models.orders.OrderModel;
import com.sadaat.groceryapp.temp.FirebaseDataKeys;
import com.sadaat.groceryapp.temp.order_management.OrderStatus;
import com.sadaat.groceryapp.ui.Loaders.LoadingDialogue;

import java.util.ArrayList;

public class OrderItemDisplayAdapterAdmin extends RecyclerView.Adapter<OrderItemDisplayAdapterAdmin.ViewHolder> {

    private final ArrayList<OrderModel> localDataSet;
    private final int LAYOUT_FILE = R.layout.admin_item_order_list;
    public ItemClickListeners customOnClickListener;
    private Context mContext;
    private LoadingDialogue progressDialogue;


    public OrderItemDisplayAdapterAdmin(ArrayList<OrderModel> localDataSet, ItemClickListeners customOnClickListener) {
        this.localDataSet = localDataSet;
        this.customOnClickListener = customOnClickListener;
    }

    public OrderItemDisplayAdapterAdmin(ArrayList<OrderModel> localDataSet, ItemClickListeners customOnClickListener, Context mContext) {
        this.localDataSet = localDataSet;
        this.customOnClickListener = customOnClickListener;
        this.mContext = mContext;
        this.progressDialogue = new LoadingDialogue(mContext);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(LAYOUT_FILE, viewGroup, false);

        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {

        // Data Setters
        viewHolder.getTxvOrderID().setText(localDataSet.get(viewHolder.getAdapterPosition()).getOrderID());
        viewHolder.getTxvAddress().setText(localDataSet.get(viewHolder.getAdapterPosition()).getDeliveryLocation());
        viewHolder.getTxvAmount().setText("" + localDataSet.get(viewHolder.getAdapterPosition()).getTotalOrderAmountInRetail() + " Rs.");
        viewHolder.getTxvRemaining().setText("" + localDataSet.get(viewHolder.getAdapterPosition()).getRemainingPaymentToPayAtDelivery() + " Rs.");
        viewHolder.getTxvMethod().setText("" + localDataSet.get(viewHolder.getAdapterPosition()).getPaymentThrough().getPaymentThroughMethod().toUpperCase());
        viewHolder.getTxvRelease().setText("" + localDataSet.get(viewHolder.getAdapterPosition()).getReleasingAppCredits() + " Credits");
        viewHolder.getTxvOrderStatus().setText("" + localDataSet.get(viewHolder.getAdapterPosition()).getStatusUpdates().get(localDataSet.get(viewHolder.getAdapterPosition()).getStatusUpdates().size() - 1).getStatus());
        viewHolder.getTxvTimeInitiated().setText("" + localDataSet.get(viewHolder.getAdapterPosition()).getStatusUpdates().get(0).getTimeStamp().toString());

        FirebaseFirestore
                .getInstance()
                .collection(new FirebaseDataKeys().getUsersRef())
                .document(localDataSet.get(viewHolder.getAdapterPosition()).getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            UserModel userModel = task.getResult().toObject(UserModel.class);
                            if (userModel != null) {
                                viewHolder.getTxvCustomerName().setText(userModel.getFullName());
                            }
                        } else {
                            viewHolder.getTxvCustomerName().setText(localDataSet.get(viewHolder.getAdapterPosition()).getUid());
                        }
                    }
                });

        if (!localDataSet.get(viewHolder.getAdapterPosition()).getCurrentDeliveryBoyUID().isEmpty()) {
            FirebaseFirestore
                    .getInstance()
                    .collection(new FirebaseDataKeys().getUsersRef())
                    .document(localDataSet.get(viewHolder.getAdapterPosition()).getCurrentDeliveryBoyUID())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                UserModel userModel = task.getResult().toObject(UserModel.class);
                                viewHolder.getTxvDeliveryBoy().setText(task.getResult().get("fullName", String.class));
                            } else {
                                viewHolder.getTxvDeliveryBoy().setText("");
                            }
                        }
                    });
        } else {
            viewHolder.getTxvDeliveryBoy().setText("");
        }

        if (localDataSet.get(viewHolder.getAdapterPosition()).getCurrentStatus().equalsIgnoreCase(OrderStatus.INITIATED)) {
            viewHolder.getBtnStartPacking().setVisibility(View.VISIBLE);
        }

        else{
            viewHolder.getBtnStartPacking().setVisibility(View.GONE);
        }

        if (viewHolder.getTxvDeliveryBoy().getText().toString().isEmpty()) {
            viewHolder.getMainView().findViewById(R.id.delivery_boy_row).setVisibility(View.GONE);
            viewHolder.getBtnAddDeliveryBoy().setVisibility(View.VISIBLE);
        } else {
            viewHolder.getMainView().findViewById(R.id.delivery_boy_row).setVisibility(View.VISIBLE);
            viewHolder.getBtnAddDeliveryBoy().setVisibility(View.GONE);
        }

        if (localDataSet.get(viewHolder.getAdapterPosition()).getCurrentStatus().equalsIgnoreCase(OrderStatus.PACKING)){
            viewHolder.getBtnAddDeliveryBoy().setVisibility(View.VISIBLE);
        }
        else{
            viewHolder.getBtnAddDeliveryBoy().setVisibility(View.GONE);
        }

        if (
                (localDataSet.get(viewHolder.getAdapterPosition()).getReleasingAppCredits() -
                        localDataSet.get(viewHolder.getAdapterPosition()).getReleasedAppCredits()) > 0
                        && localDataSet.get(viewHolder.getAdapterPosition()).getCurrentStatus().equalsIgnoreCase(OrderStatus.DELIVERED)
        ) {
            viewHolder.getBtnReleaseAppCredits().setVisibility(View.VISIBLE);
        } else {
            viewHolder.getBtnReleaseAppCredits().setVisibility(View.GONE);
        }


        //Listeners
        viewHolder.getMainView().setOnClickListener(view -> customOnClickListener.onFullItemClick(localDataSet.get(viewHolder.getAdapterPosition())));
        viewHolder.getBtnAddDeliveryBoy().setOnClickListener(view -> customOnClickListener.onAssignDeliveryBoyButtonClick(localDataSet.get(viewHolder.getAdapterPosition())));
        viewHolder.getBtnReleaseAppCredits().setOnClickListener(view -> customOnClickListener.onReleaseAppCreditsButtonClick(localDataSet.get(viewHolder.getAdapterPosition()), localDataSet.get(viewHolder.getAdapterPosition()).getReleasingAppCredits()));
        viewHolder.getBtnStartPacking().setOnClickListener(v -> customOnClickListener.onPackButtonClick(viewHolder.getAdapterPosition(), localDataSet.get(viewHolder.getAdapterPosition()).getOrderID()));
    }

    @Override
    public int getItemCount() {
        return localDataSet.size();
    }

    public void addItem(OrderModel model) {
        localDataSet.add(model);
        notifyItemInserted(localDataSet.size() - 1);
    }

    public void deleteItem(int index) {
        localDataSet.remove(index);
        notifyItemRemoved(index);
    }

    public void deleteAll() {
        int size = localDataSet.size();
        localDataSet.clear();
        notifyItemRangeRemoved(0, size);
    }

    public void updateItem(int index, OrderModel newModel) {
        localDataSet.remove(index);
        localDataSet.add(index, newModel);
        notifyItemChanged(index);
    }

    public interface ItemClickListeners {
        void onFullItemClick(OrderModel orderModel);

        void onAssignDeliveryBoyButtonClick(OrderModel orderModel);

        void onReleaseAppCreditsButtonClick(OrderModel orderModel, double releasingAppCredits);

        void onPackButtonClick(int viewPosition, String orderID);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final View mainView;
        private final MaterialTextView txvOrderID;
        private final MaterialTextView txvCustomerName;
        private final MaterialTextView txvAmount;
        private final MaterialTextView txvRemaining;
        private final MaterialTextView txvMethod;
        private final MaterialTextView txvRelease;
        private final MaterialTextView txvOrderStatus;
        private final MaterialTextView txvAddress;
        private final MaterialTextView txvDeliveryBoy;
        private final MaterialCardView btnAddDeliveryBoy;
        private final MaterialCardView btnReleaseAppCredits;
        private final MaterialTextView txvTimeInitiated;
        private final MaterialCardView btnStartPacking;


        public ViewHolder(View view) {
            super(view);

            mainView = view.findViewById(R.id.admin_order_item_mainView);
            txvOrderID = view.findViewById(R.id.admin_order_item_orderID);
            txvAmount = view.findViewById(R.id.admin_order_item_amount);
            txvRemaining = view.findViewById(R.id.admin_order_item_remaining);
            txvMethod = view.findViewById(R.id.admin_order_item_method);
            txvRelease = view.findViewById(R.id.admin_order_item_releasing);
            txvOrderStatus = view.findViewById(R.id.admin_order_item_status);
            txvAddress = view.findViewById(R.id.admin_order_item_address);
            txvDeliveryBoy = view.findViewById(R.id.admin_order_item_deliveryBoy);
            btnAddDeliveryBoy = view.findViewById(R.id.admin_order_item_assign_button);
            btnReleaseAppCredits = view.findViewById(R.id.admin_order_item_release_credits_btn);
            txvCustomerName = view.findViewById(R.id.admin_order_item_customerName);
            txvTimeInitiated = view.findViewById(R.id.admin_order_item_time);
            btnStartPacking = view.findViewById(R.id.admin_order_item_start_packing);

        }

        public View getMainView() {
            return mainView;
        }

        public MaterialTextView getTxvOrderID() {
            return txvOrderID;
        }

        public MaterialTextView getTxvCustomerName() {
            return txvCustomerName;
        }

        public MaterialTextView getTxvAmount() {
            return txvAmount;
        }

        public MaterialTextView getTxvRemaining() {
            return txvRemaining;
        }

        public MaterialTextView getTxvMethod() {
            return txvMethod;
        }

        public MaterialTextView getTxvRelease() {
            return txvRelease;
        }

        public MaterialTextView getTxvOrderStatus() {
            return txvOrderStatus;
        }

        public MaterialTextView getTxvAddress() {
            return txvAddress;
        }

        public MaterialTextView getTxvDeliveryBoy() {
            return txvDeliveryBoy;
        }

        public MaterialCardView getBtnAddDeliveryBoy() {
            return btnAddDeliveryBoy;
        }

        public MaterialCardView getBtnReleaseAppCredits() {
            return btnReleaseAppCredits;
        }

        public MaterialTextView getTxvTimeInitiated() {
            return txvTimeInitiated;
        }

        public MaterialCardView getBtnStartPacking() {
            return btnStartPacking;
        }
    }

    public ArrayList<OrderModel> getLocalDataSet() {
        return localDataSet;
    }
}
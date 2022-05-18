package com.sadaat.groceryapp.adapters.customer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;
import com.sadaat.groceryapp.R;
import com.sadaat.groceryapp.models.orders.OrderModel;
import com.sadaat.groceryapp.temp.order_management.OrderStatus;
import com.sadaat.groceryapp.ui.Loaders.LoadingDialogue;

import java.util.ArrayList;

public class OrderItemDisplayAdapterCustomer extends RecyclerView.Adapter<OrderItemDisplayAdapterCustomer.ViewHolder> {

    private final ArrayList<OrderModel> localDataSet;
    private final int LAYOUT_FILE = R.layout.customer_item_order_item_recycler;
    public ItemClickListeners customOnClickListener;
    private Context mContext;
    private LoadingDialogue progressDialogue;


    public OrderItemDisplayAdapterCustomer(ArrayList<OrderModel> localDataSet, ItemClickListeners customOnClickListener) {
        this.localDataSet = localDataSet;
        this.customOnClickListener = customOnClickListener;
    }

    public OrderItemDisplayAdapterCustomer(ArrayList<OrderModel> localDataSet, ItemClickListeners customOnClickListener, Context mContext) {
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
        viewHolder.getTxvAmount().setText("" + localDataSet.get(viewHolder.getAdapterPosition()).getTotalOrderAmountInRetail() + " Rs.");
        viewHolder.getTxvOrderStatus().setText("" + localDataSet.get(viewHolder.getAdapterPosition()).getStatusUpdates().get(localDataSet.get(viewHolder.getAdapterPosition()).getStatusUpdates().size() - 1).getStatus());

        //Listeners
        viewHolder.getMainView().setOnClickListener(view -> customOnClickListener.onFullItemClick(localDataSet.get(viewHolder.getAdapterPosition())));

        if (localDataSet.get(viewHolder.getAdapterPosition()).getCurrentStatus().equalsIgnoreCase(OrderStatus.DELIVERED) &&
                localDataSet.get(viewHolder.getAdapterPosition()).getReleasedAppCredits() == 0.0
        ) {
            viewHolder.getCardPostComplaint().setVisibility(View.VISIBLE);
        } else {
            viewHolder.getCardPostComplaint().setVisibility(View.GONE);
        }

        viewHolder.getCardPostComplaint().setOnClickListener(v -> customOnClickListener.onComplaintButtonClick(
                localDataSet.get(viewHolder.getAdapterPosition()).getOrderID(),
                !localDataSet.get(viewHolder.getAdapterPosition()).getComplaintID().equalsIgnoreCase(""),
                viewHolder.getAdapterPosition()));
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

    public ArrayList<OrderModel> getLocalDataSet() {
        return localDataSet;
    }

    public interface ItemClickListeners {
        void onFullItemClick(OrderModel orderModel);

        void onComplaintButtonClick(String orderID, boolean whetherHasAnyComplaint, int position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final View mainView;
        private final MaterialTextView txvOrderID;
        private final MaterialTextView txvAmount;
        private final MaterialTextView txvOrderStatus;
        private final MaterialCardView cardPostComplaint;


        public ViewHolder(View view) {
            super(view);

            mainView = view.findViewById(R.id.customer_order_item_mainView);
            txvOrderID = view.findViewById(R.id.customer_order_item_orderID);
            txvAmount = view.findViewById(R.id.customer_order_item_amount);
            txvOrderStatus = view.findViewById(R.id.customer_order_item_status);
            cardPostComplaint = view.findViewById(R.id.card_report_problem);

        }

        public View getMainView() {
            return mainView;
        }

        public MaterialTextView getTxvOrderID() {
            return txvOrderID;
        }

        public MaterialTextView getTxvAmount() {
            return txvAmount;
        }

        public MaterialTextView getTxvOrderStatus() {
            return txvOrderStatus;
        }

        public MaterialCardView getCardPostComplaint() {
            return cardPostComplaint;
        }
    }


}
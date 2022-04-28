package com.sadaat.groceryapp.adapters.deliveryboy;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sadaat.groceryapp.R;
import com.sadaat.groceryapp.models.Users.UserModel;
import com.sadaat.groceryapp.models.orders.OrderModel;
import com.sadaat.groceryapp.temp.FirebaseDataKeys;
import com.sadaat.groceryapp.ui.Loaders.LoadingDialogue;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Objects;

public class OrderItemDisplayAdapterDeliveryBoy extends RecyclerView.Adapter<OrderItemDisplayAdapterDeliveryBoy.ViewHolder> {

    private final ArrayList<OrderModel> localDataSet;
    private final int LAYOUT_FILE = R.layout.delivery_item_live_delivered_order;
    private LoadingDialogue progressDialogue;

    public OrderItemDisplayAdapterDeliveryBoy(ArrayList<OrderModel> localDataSet, Context mContext) {
        this.localDataSet = localDataSet;
        this.progressDialogue = new LoadingDialogue(mContext);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(LAYOUT_FILE, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {

        // Data Setters
        viewHolder.getTxvOrderID().setText(localDataSet.get(viewHolder.getAdapterPosition()).getOrderID());
        viewHolder.getTxvAmount().setText(MessageFormat.format("{0} Rs.", localDataSet.get(viewHolder.getAdapterPosition()).getTotalOrderAmountInRetail()));
        viewHolder.getTxvMethod().setText(localDataSet.get(viewHolder.getAdapterPosition()).getPaymentThrough().getPaymentThroughMethod().toUpperCase());
        viewHolder.getTxvOrderLocation().setText(localDataSet.get(viewHolder.getAdapterPosition()).getDeliveryLocation());
        FirebaseFirestore
                .getInstance()
                .collection(new FirebaseDataKeys().getUsersRef())
                .document(localDataSet.get(viewHolder.getAdapterPosition()).getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            viewHolder.getTxvCustomerName().setText(Objects.requireNonNull(task.getResult().toObject(UserModel.class)).getFullName());
                        }
                    }
                });
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
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final MaterialTextView txvOrderID;
        private final MaterialTextView txvAmount;
        private final MaterialTextView txvCustomerName;
        private final MaterialTextView txvMethod;
        private final MaterialTextView txvOrderLocation;


        public ViewHolder(View view) {
            super(view);

            txvOrderID = view.findViewById(R.id.delivery_order_item_orderID);
            txvCustomerName = view.findViewById(R.id.delivery_order_item_customerName);
            txvAmount = view.findViewById(R.id.delivery_order_item_amount);
            txvMethod = view.findViewById(R.id.delivery_order_item_method);
            txvOrderLocation = view.findViewById(R.id.delivery_order_item_address);

        }

        public MaterialTextView getTxvOrderID() {
            return txvOrderID;
        }

        public MaterialTextView getTxvAmount() {
            return txvAmount;
        }

        public MaterialTextView getTxvCustomerName() {
            return txvCustomerName;
        }

        public MaterialTextView getTxvMethod() {
            return txvMethod;
        }

        public MaterialTextView getTxvOrderLocation() {
            return txvOrderLocation;
        }
    }

    public ArrayList<OrderModel> getLocalDataSet() {
        return localDataSet;
    }
}
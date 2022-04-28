package com.sadaat.groceryapp.adapters.admin;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.sadaat.groceryapp.R;
import com.sadaat.groceryapp.models.Users.UserModel;
import com.sadaat.groceryapp.models.orders.OrderModel;
import com.sadaat.groceryapp.temp.FirebaseDataKeys;
import com.sadaat.groceryapp.temp.UserLive;
import com.sadaat.groceryapp.temp.order_management.OrderStatus;

import java.text.MessageFormat;
import java.util.ArrayList;

public class DeliveryBoyListAdapter extends RecyclerView.Adapter<DeliveryBoyListAdapter.ViewHolder> {

    private final ArrayList<UserModel> localDataSet;
    private final OnClickListener listener;

    public DeliveryBoyListAdapter(ArrayList<UserModel> localDataSet, OnClickListener listener) {
        this.localDataSet = localDataSet;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        int LAYOUT_FILE = R.layout.admin_item_list_deliveryboy_recycler;
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(LAYOUT_FILE, viewGroup, false);

        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull DeliveryBoyListAdapter.ViewHolder viewHolder, @SuppressLint("RecyclerView") int position) {

        viewHolder.getTxvName().setText("" + localDataSet.get(viewHolder.getAdapterPosition()).getFullName());


        FirebaseFirestore
                .getInstance()
                .collection(new FirebaseDataKeys().getOrdersRef())
                .whereEqualTo("currentDeliveryBoyUID", localDataSet.get(viewHolder.getAdapterPosition()).getUID())
                .whereEqualTo("currentStatus", OrderStatus.DELIVERING)
                .whereEqualTo("releasedAppCredits",0.0)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            viewHolder.getTxvQueue().setText(MessageFormat.format("{0}", task.getResult().getDocuments().size()));
                        }
                    }
                });

        FirebaseFirestore
                .getInstance()
                .collection(new FirebaseDataKeys().getOrdersRef())
                .whereEqualTo("currentDeliveryBoyUID", localDataSet.get(viewHolder.getAdapterPosition()).getUID())
                .whereEqualTo("currentStatus", OrderStatus.DELIVERED)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            viewHolder.getTxvTotal().setText(MessageFormat.format("{0}", task.getResult().getDocuments().size()));

                        }
                    }
                });

        viewHolder.getMainView().setOnClickListener(v -> {
            listener.onClick(viewHolder.getAdapterPosition(), localDataSet.get(viewHolder.getAdapterPosition()).getUID());
        });

    }

    @Override
    public int getItemCount() {
        return localDataSet.size();
    }

    public void addItem(UserModel model) {
        localDataSet.add(model);
        notifyItemInserted(localDataSet.size() - 1);
    }

    public void deleteAll() {
        int size = localDataSet.size();
        localDataSet.clear();
        notifyItemRangeRemoved(0, size);
    }

    public interface OnClickListener {
        void onClick(int position, String deliveryBoyUID);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final MaterialTextView txvName;
        private final MaterialTextView txvQueue;
        private final MaterialTextView txvTotal;
        private final View mainView;

        public ViewHolder(View view) {
            super(view);

            this.mainView = view.findViewById(R.id.mainView);

            txvName = view.findViewById(R.id.delivery_boy_assign_name);
            txvQueue = view.findViewById(R.id.delivery_boy_assign_queue);
            txvTotal = view.findViewById(R.id.delivery_boy_assign_total);


        }

        public MaterialTextView getTxvName() {
            return txvName;
        }

        public MaterialTextView getTxvQueue() {
            return txvQueue;
        }

        public MaterialTextView getTxvTotal() {
            return txvTotal;
        }

        public View getMainView() {
            return mainView;
        }
    }
}
package com.sadaat.groceryapp.adapters.admin;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;
import com.sadaat.groceryapp.R;
import com.sadaat.groceryapp.models.Users.UserModel;

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

        viewHolder.getTxvSerial().setText(localDataSet.get(viewHolder.getAdapterPosition()).getUID());
        viewHolder.getTxvSerial().setTextSize(11);

        viewHolder.getTxvStatus().setText("" + localDataSet.get(viewHolder.getAdapterPosition()).getFullName());

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

        private final MaterialTextView txvSerial;
        private final MaterialTextView txvStatus;
        private final View mainView;

        public ViewHolder(View view) {
            super(view);

            this.mainView = view.findViewById(R.id.mainView);

            txvSerial = view.findViewById(R.id.order_item_status_sr);
            txvStatus = view.findViewById(R.id.order_item_status_status);

        }

        public MaterialTextView getTxvSerial() {
            return txvSerial;
        }

        public MaterialTextView getTxvStatus() {
            return txvStatus;
        }

        public View getMainView() {
            return mainView;
        }
    }
}
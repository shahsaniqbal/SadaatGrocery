package com.sadaat.groceryapp.adapters.generic;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;
import com.sadaat.groceryapp.R;
import com.sadaat.groceryapp.models.orders.StatusModel;
import com.sadaat.groceryapp.ui.Loaders.LoadingDialogue;

import java.util.ArrayList;
import java.util.Date;

public class DetailedOrderStatusListAdapter extends RecyclerView.Adapter<DetailedOrderStatusListAdapter.ViewHolder> {

    private final ArrayList<StatusModel> localDataSet;
    private final LoadingDialogue progressDialogue;


    public DetailedOrderStatusListAdapter(Context mContext) {
        this.progressDialogue = new LoadingDialogue(mContext);
        this.localDataSet = new ArrayList<>();
    }

    public DetailedOrderStatusListAdapter(Context mContext, ArrayList<StatusModel> localDataSet) {
        this.progressDialogue = new LoadingDialogue(mContext);
        this.localDataSet = localDataSet;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        int LAYOUT_FILE = R.layout.generic_item_orderitem_status_updates_recycler;
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(LAYOUT_FILE, viewGroup, false);

        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull DetailedOrderStatusListAdapter.ViewHolder viewHolder, @SuppressLint("RecyclerView") int position) {

        viewHolder.getTxvSerial().setText("0" + (viewHolder.getAdapterPosition() + 1));
        Date d = localDataSet.get(viewHolder.getAdapterPosition()).getTimeStamp();
        /*viewHolder.getTxvTimeStamp().setText(
                d.getDate()
                        + "-"
                        + d.getMonth()
                        + "-"
                        + d.getYear()
                        + " "
                        + d.getDay()
                        + " "
                        + d.getHours()
                        + ":"
                        + d.getMinutes()
                        + ":"
                        + d.getSeconds()
        );*/
        viewHolder.getTxvTimeStamp().setText(d.toString());
        viewHolder.getTxvTimeStamp().setTextSize(11);
        viewHolder.getTxvStatus().setText("" + localDataSet.get(viewHolder.getAdapterPosition()).getStatus());
    }

    @Override
    public int getItemCount() {
        return localDataSet.size();
    }

    public void addItem(StatusModel model) {
        localDataSet.add(model);
        notifyItemInserted(localDataSet.size() - 1);
    }

    public void deleteAll() {
        int size = localDataSet.size();
        localDataSet.clear();
        notifyItemRangeRemoved(0, size);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final MaterialTextView txvSerial;
        private final MaterialTextView txvStatus;
        private final View mainView;
        private final MaterialTextView txvTimeStamp;

        public ViewHolder(View view) {
            super(view);

            this.mainView = view;

            txvSerial = view.findViewById(R.id.order_item_status_sr);
            txvStatus = view.findViewById(R.id.order_item_status_status);
            txvTimeStamp = view.findViewById(R.id.order_item_status_timestamp);

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


        public MaterialTextView getTxvTimeStamp() {
            return txvTimeStamp;
        }

    }
}
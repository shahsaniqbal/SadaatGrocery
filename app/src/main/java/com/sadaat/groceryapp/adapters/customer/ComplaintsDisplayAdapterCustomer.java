package com.sadaat.groceryapp.adapters.customer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;
import com.sadaat.groceryapp.R;
import com.sadaat.groceryapp.models.ComplaintsModel;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;

public class ComplaintsDisplayAdapterCustomer extends RecyclerView.Adapter<ComplaintsDisplayAdapterCustomer.ViewHolder> {

    private ArrayList<ComplaintsModel> localDataSet;
    private OnClickListener clickListeners;

    public ComplaintsDisplayAdapterCustomer(ArrayList<ComplaintsModel> localDataSet, OnClickListener clickListeners) {
        this.localDataSet = localDataSet;
        this.clickListeners = clickListeners;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.customer_item_complaint, parent, false);

        return new ComplaintsDisplayAdapterCustomer.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ComplaintsDisplayAdapterCustomer.ViewHolder holder, int position) {

        if (localDataSet.get(holder.getAdapterPosition()).isResolved() && !localDataSet.get(holder.getAdapterPosition()).getReplyMessage().isEmpty()) {
            holder.getCardRowResponse().setVisibility(View.VISIBLE);
        } else {
            holder.getCardRowResponse().setVisibility(View.GONE);
        }

        holder.getTxvComplaintTitle().setText(localDataSet.get(holder.getAdapterPosition()).getComplaintTitle());
        holder.getTxvComplaintMessage().setText(localDataSet.get(holder.getAdapterPosition()).getComplaintMessage());
        holder.getTxvComplaintResponse().setText(localDataSet.get(holder.getAdapterPosition()).getReplyMessage());
        holder.getTxvPostedTime().setText("Posted at: " + localDataSet.get(holder.getAdapterPosition()).getComplaintIssueDate().toString());

        if (localDataSet.get(holder.getAdapterPosition()).getReplyResolvedDate()!=null){
            holder.getTxvReplyTime().setText("Replied at: " + localDataSet.get(holder.getAdapterPosition()).getReplyResolvedDate().toString());
        }

        holder.getCardSeeOrderDetails().setOnClickListener(v -> {
            clickListeners.onShowFullOrderDetailsButtonClick(holder.getAdapterPosition(),
                    localDataSet.get(holder.getAdapterPosition()).getOrderID()
            );
        });

    }

    @Override
    public int getItemCount() {
        return localDataSet.size();
    }

    public void addItem(ComplaintsModel model) {
        localDataSet.add(model);
        notifyItemInserted(localDataSet.size() - 1);
    }

    public void deleteAll() {
        int size = localDataSet.size();
        localDataSet.clear();
        notifyItemRangeRemoved(0, size);
    }

    public interface OnClickListener {
        void onShowFullOrderDetailsButtonClick(int position, String thatOrderID);
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final MaterialTextView txvComplaintTitle;
        private final MaterialTextView txvComplaintMessage;
        private final MaterialTextView txvComplaintResponse;

        private final MaterialTextView txvPostedTime;
        private final MaterialTextView txvReplyTime;


        private final MaterialCardView cardSeeOrderDetails;
        private final MaterialCardView cardRowResponse;

        private final View mainView;

        public ViewHolder(View view) {
            super(view);

            this.mainView = view.findViewById(R.id.mainView);

            txvComplaintTitle = view.findViewById(R.id.txv_complaint_title);
            txvComplaintMessage = view.findViewById(R.id.txv_complaint_message);
            txvComplaintResponse = view.findViewById(R.id.txv_complaint_response);

            txvPostedTime = view.findViewById(R.id.txv_complaint_posted_at);
            txvReplyTime = view.findViewById(R.id.txv_complaint_replied_at);

            cardRowResponse = view.findViewById(R.id.card_response);
            cardSeeOrderDetails = view.findViewById(R.id.card_see_order_details);

        }

        public MaterialTextView getTxvComplaintTitle() {
            return txvComplaintTitle;
        }

        public MaterialTextView getTxvComplaintMessage() {
            return txvComplaintMessage;
        }

        public MaterialTextView getTxvComplaintResponse() {
            return txvComplaintResponse;
        }

        public MaterialCardView getCardSeeOrderDetails() {
            return cardSeeOrderDetails;
        }

        public MaterialCardView getCardRowResponse() {
            return cardRowResponse;
        }

        public MaterialTextView getTxvPostedTime() {
            return txvPostedTime;
        }

        public MaterialTextView getTxvReplyTime() {
            return txvReplyTime;
        }

        public View getMainView() {
            return mainView;
        }
    }
}

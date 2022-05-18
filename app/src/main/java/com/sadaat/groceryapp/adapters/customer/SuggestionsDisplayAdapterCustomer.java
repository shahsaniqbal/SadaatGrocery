package com.sadaat.groceryapp.adapters.customer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;
import com.sadaat.groceryapp.R;
import com.sadaat.groceryapp.models.ComplaintsModel;
import com.sadaat.groceryapp.models.SuggestionModel;

import java.util.ArrayList;

public class SuggestionsDisplayAdapterCustomer extends RecyclerView.Adapter<SuggestionsDisplayAdapterCustomer.ViewHolder> {

    private ArrayList<SuggestionModel> localDataSet;

    public SuggestionsDisplayAdapterCustomer(ArrayList<SuggestionModel> localDataSet) {
        this.localDataSet = localDataSet;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.customer_item_suggestion, parent, false);

        return new SuggestionsDisplayAdapterCustomer.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SuggestionsDisplayAdapterCustomer.ViewHolder holder, int position) {

        if (!localDataSet.get(holder.getAdapterPosition()).getReplyMessage().isEmpty()) {
            holder.getCardRowResponse().setVisibility(View.VISIBLE);
        } else {
            holder.getCardRowResponse().setVisibility(View.GONE);
        }

        holder.getTxvComplaintTitle().setText(localDataSet.get(holder.getAdapterPosition()).getSuggestionTitle());
        holder.getTxvComplaintMessage().setText(localDataSet.get(holder.getAdapterPosition()).getSuggestionDetails());
        holder.getTxvComplaintResponse().setText(localDataSet.get(holder.getAdapterPosition()).getReplyMessage());
        holder.getTxvPostedTime().setText("Posted at: " + localDataSet.get(holder.getAdapterPosition()).getSuggestedDate().toString());

        if (localDataSet.get(holder.getAdapterPosition()).getReplyDate()!=null){
            holder.getTxvReplyTime().setText("Replied at: " + localDataSet.get(holder.getAdapterPosition()).getReplyDate().toString());
        }

        holder.getRatingBar().setRating((float) localDataSet.get(holder.getAdapterPosition()).getFeedback());
    }

    @Override
    public int getItemCount() {
        return localDataSet.size();
    }

    public void addItem(SuggestionModel model) {
        localDataSet.add(model);
        notifyItemInserted(localDataSet.size() - 1);
    }

    public void deleteAll() {
        int size = localDataSet.size();
        localDataSet.clear();
        notifyItemRangeRemoved(0, size);
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final MaterialTextView txvComplaintTitle;
        private final MaterialTextView txvComplaintMessage;
        private final MaterialTextView txvComplaintResponse;

        private final MaterialTextView txvPostedTime;
        private final MaterialTextView txvReplyTime;


        private final RatingBar ratingBar;
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

            ratingBar = view.findViewById(R.id.simpleRatingBar);

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

        public RatingBar getRatingBar() {
            return ratingBar;
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

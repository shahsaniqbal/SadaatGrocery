package com.sadaat.groceryapp.adapters.admin;

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
import com.sadaat.groceryapp.models.SuggestionModel;

import java.util.ArrayList;
import java.util.Date;

public class SuggestionsDisplayAdapterAdmin extends RecyclerView.Adapter<SuggestionsDisplayAdapterAdmin.ViewHolder> {

    private ArrayList<SuggestionModel> localDataSet;
    private OnClickListener clickListeners;

    public SuggestionsDisplayAdapterAdmin(ArrayList<SuggestionModel> localDataSet, OnClickListener clickListeners) {
        this.localDataSet = localDataSet;
        this.clickListeners = clickListeners;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.admin_item_complaint, parent, false);

        return new SuggestionsDisplayAdapterAdmin.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SuggestionsDisplayAdapterAdmin.ViewHolder holder, int position) {

        if (!localDataSet.get(holder.getAdapterPosition()).getReplyMessage().isEmpty()) {
            holder.getCardRowPostReply().setVisibility(View.GONE);
            holder.getCardRowResponse().setVisibility(View.VISIBLE);
        } else {
            holder.getCardRowPostReply().setVisibility(View.VISIBLE);
            holder.getCardRowResponse().setVisibility(View.GONE);
        }

        holder.getTxvComplaintTitle().setText(localDataSet.get(holder.getAdapterPosition()).getSuggestionTitle());
        holder.getTxvComplaintMessage().setText(localDataSet.get(holder.getAdapterPosition()).getSuggestionDetails());
        holder.getTxvComplaintResponse().setText(localDataSet.get(holder.getAdapterPosition()).getReplyMessage());

        holder.getTxvUserName().setText(localDataSet.get(holder.getAdapterPosition()).getUserFullName());
        holder.getTxvUserEmail().setText(localDataSet.get(holder.getAdapterPosition()).getUserEmail());
        holder.getTxvUserContact().setText(localDataSet.get(holder.getAdapterPosition()).getUserContactNumber());
        holder.getTxvRatings().setText(""+localDataSet.get(holder.getAdapterPosition()).getFeedback()+" / 5.0");

        holder.getTxvPostedTime().setText("Posted at: " + localDataSet.get(holder.getAdapterPosition()).getSuggestedDate().toString());

        if (localDataSet.get(holder.getAdapterPosition()).getReplyDate()!=null){
            holder.getTxvReplyTime().setText("Replied at: " + localDataSet.get(holder.getAdapterPosition()).getReplyDate().toString());
        }
        holder.getCardSendReply().setOnClickListener(v -> {
            if (holder.getEdxReply().getText().toString().isEmpty()) {
                holder.getEdxReply().setError("Please Enter Reply and click send");
            } else {
                holder.getEdxReply().setError(null);
                clickListeners.onPostReplyButtonClick(holder.getAdapterPosition(),
                        localDataSet.get(holder.getAdapterPosition()).getSuggestionID(),
                        holder.getEdxReply().getText().toString(),
                        new Date());
            }
        });


        holder.getCardCall().setOnClickListener(v -> {
            clickListeners.onCallButtonClick(
                    localDataSet.get(holder.getAdapterPosition()).getUserContactNumber()
            );
        });
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

    public interface OnClickListener {
        void onCallButtonClick(String mobileNumber);
        void onPostReplyButtonClick(int position, String id, String reply, Date replyDate);
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final MaterialTextView txvComplaintTitle;
        private final MaterialTextView txvComplaintMessage;
        private final MaterialTextView txvComplaintResponse;
        private final MaterialTextView txvPostedTime;
        private final MaterialTextView txvReplyTime;
        private final MaterialCardView cardCall;
        private final MaterialCardView cardRowResponse;
        private final MaterialCardView cardRowPostReply;

        private final MaterialTextView txvUserName;
        private final MaterialTextView txvUserEmail;
        private final MaterialTextView txvUserContact;
        private final MaterialTextView txvRatings;


        private final MaterialCardView cardSendReply;

        private final EditText edxReply;

        private final View mainView;

        public ViewHolder(View view) {
            super(view);

            this.mainView = view.findViewById(R.id.mainView);

            txvComplaintTitle = view.findViewById(R.id.txv_complaint_title);
            txvComplaintMessage = view.findViewById(R.id.txv_complaint_message);
            txvComplaintResponse = view.findViewById(R.id.txv_complaint_response);

            txvPostedTime = view.findViewById(R.id.txv_complaint_posted_at);
            txvReplyTime = view.findViewById(R.id.txv_complaint_replied_at);

            txvUserName = view.findViewById(R.id.txv_username);
            txvUserEmail = view.findViewById(R.id.txv_email);
            txvUserContact = view.findViewById(R.id.txv_mobile);
            txvRatings = view.findViewById(R.id.txv_rating);

            edxReply = view.findViewById(R.id.edx_complaint_reply);

            cardRowPostReply = view.findViewById(R.id.card_reply);
            cardRowResponse = view.findViewById(R.id.card_response);
            cardCall = view.findViewById(R.id.card_call_to_user);
            cardSendReply = view.findViewById(R.id.card_send_reply);

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


        public MaterialCardView getCardRowResponse() {
            return cardRowResponse;
        }

        public MaterialCardView getCardRowPostReply() {
            return cardRowPostReply;
        }

        public MaterialCardView getCardSendReply() {
            return cardSendReply;
        }

        public EditText getEdxReply() {
            return edxReply;
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

        public MaterialCardView getCardCall() {
            return cardCall;
        }

        public MaterialTextView getTxvUserName() {
            return txvUserName;
        }

        public MaterialTextView getTxvUserEmail() {
            return txvUserEmail;
        }

        public MaterialTextView getTxvUserContact() {
            return txvUserContact;
        }

        public MaterialTextView getTxvRatings() {
            return txvRatings;
        }
    }
}

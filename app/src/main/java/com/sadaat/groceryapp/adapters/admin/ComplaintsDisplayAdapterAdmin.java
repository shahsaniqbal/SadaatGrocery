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

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;

public class ComplaintsDisplayAdapterAdmin extends RecyclerView.Adapter<ComplaintsDisplayAdapterAdmin.ViewHolder> {

    private ArrayList<ComplaintsModel> localDataSet;
    private OnClickListener clickListeners;

    public ComplaintsDisplayAdapterAdmin(ArrayList<ComplaintsModel> localDataSet, OnClickListener clickListeners) {
        this.localDataSet = localDataSet;
        this.clickListeners = clickListeners;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.admin_item_complaint, parent, false);

        return new ComplaintsDisplayAdapterAdmin.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ComplaintsDisplayAdapterAdmin.ViewHolder holder, int position) {

        if (localDataSet.get(holder.getAdapterPosition()).isResolved() && !localDataSet.get(holder.getAdapterPosition()).getReplyMessage().isEmpty()) {
            holder.getCardRowPostReply().setVisibility(View.GONE);
            holder.getCardRowResponse().setVisibility(View.VISIBLE);
        } else {
            holder.getCardRowPostReply().setVisibility(View.VISIBLE);
            holder.getCardRowResponse().setVisibility(View.GONE);
        }

        holder.getTxvComplaintTitle().setText(localDataSet.get(holder.getAdapterPosition()).getComplaintTitle());
        holder.getTxvComplaintMessage().setText(localDataSet.get(holder.getAdapterPosition()).getComplaintMessage());
        holder.getTxvComplaintResponse().setText(localDataSet.get(holder.getAdapterPosition()).getReplyMessage());
        holder.getTxvPostedTime().setText("Posted at: " + localDataSet.get(holder.getAdapterPosition()).getComplaintIssueDate().toString());

        if (localDataSet.get(holder.getAdapterPosition()).getReplyResolvedDate()!=null){
            holder.getTxvReplyTime().setText("Replied at: " + localDataSet.get(holder.getAdapterPosition()).getReplyResolvedDate().toString());
        }
        holder.getCardSendReply().setOnClickListener(v -> {
            if (holder.getEdxReply().getText().toString().isEmpty()) {
                holder.getEdxReply().setError("Please Enter Reply and click send");
            } else {
                holder.getEdxReply().setError(null);
                clickListeners.postReply(holder.getAdapterPosition(),
                        localDataSet.get(holder.getAdapterPosition()).getComplaintID(),
                        holder.getEdxReply().getText().toString(),
                        true,
                        new Date());
            }
        });


        holder.getCardSeeUserDetails().setOnClickListener(v -> {
            clickListeners.onShowFullUserDetailsButtonClick(holder.getAdapterPosition(),
                    localDataSet.get(holder.getAdapterPosition()).getUid()
            );
        });

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
        void onShowFullUserDetailsButtonClick(int position, String thatUID);

        void onShowFullOrderDetailsButtonClick(int position, String thatOrderID);

        void postReply(int toUpdateThePosition, String complaintID, String reply, boolean isIssueResolved, Date resolveDate);
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final MaterialTextView txvComplaintTitle;
        private final MaterialTextView txvComplaintMessage;
        private final MaterialTextView txvComplaintResponse;

        private final MaterialTextView txvPostedTime;
        private final MaterialTextView txvReplyTime;


        private final MaterialCardView cardSeeOrderDetails;
        private final MaterialCardView cardSeeUserDetails;
        private final MaterialCardView cardRowResponse;
        private final MaterialCardView cardRowPostReply;

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

            edxReply = view.findViewById(R.id.edx_complaint_reply);

            cardRowPostReply = view.findViewById(R.id.card_reply);
            cardRowResponse = view.findViewById(R.id.card_response);
            cardSeeOrderDetails = view.findViewById(R.id.card_see_order_details);
            cardSeeUserDetails = view.findViewById(R.id.card_see_user_details);
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

        public MaterialCardView getCardSeeOrderDetails() {
            return cardSeeOrderDetails;
        }

        public MaterialCardView getCardSeeUserDetails() {
            return cardSeeUserDetails;
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
    }
}

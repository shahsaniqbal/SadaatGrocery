package com.sadaat.groceryapp.adapters.admin;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;
import com.sadaat.groceryapp.R;
import com.sadaat.groceryapp.models.Users.UserModel;

import java.text.MessageFormat;
import java.util.ArrayList;

public class DeliveryBoyAppCreditsListAdapter extends RecyclerView.Adapter<DeliveryBoyAppCreditsListAdapter.ViewHolder> {

    private final ArrayList<UserModel> localDataSet;
    private final OnClickListener listener;

    public DeliveryBoyAppCreditsListAdapter(ArrayList<UserModel> localDataSet, OnClickListener listener) {
        this.localDataSet = localDataSet;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        int LAYOUT_FILE = R.layout.admin_item_deliveryboy_appcredits;
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(LAYOUT_FILE, viewGroup, false);

        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull DeliveryBoyAppCreditsListAdapter.ViewHolder viewHolder, @SuppressLint("RecyclerView") int position) {

        viewHolder.getTxvName().setText(MessageFormat.format("{0}", localDataSet.get(viewHolder.getAdapterPosition()).getFullName()));
        viewHolder.getTxvWallet().setText(MessageFormat.format("{0} Rs.", localDataSet.get(viewHolder.getAdapterPosition()).getCredits().getOwningCredits()));

        if (localDataSet.get(viewHolder.getAdapterPosition()).getCredits().getOwningCredits()!=0){
            viewHolder.getCardReceipt().setVisibility(View.VISIBLE);
        }
        else{
            viewHolder.getCardReceipt().setVisibility(View.GONE);
        }

        viewHolder.getCardReceipt().setOnLongClickListener(view -> {
            listener.onReceiptButtonClick(
                    viewHolder.getAdapterPosition(),
                    localDataSet.get(viewHolder.getAdapterPosition()).getUID(),
                    localDataSet.get(viewHolder.getAdapterPosition()).getCredits().getOwningCredits(),
                    localDataSet.get(viewHolder.getAdapterPosition()).getFullName());
            return true;
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
        void onReceiptButtonClick(int position, String deliveryBoyUID, double appCredits, String name);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final MaterialTextView txvName;
        private final MaterialTextView txvWallet;
        private final MaterialCardView cardReceipt;
        private final View mainView;

        public ViewHolder(View view) {
            super(view);

            this.mainView = view.findViewById(R.id.mainView);

            txvName = view.findViewById(R.id.delivery_boy_assign_name);
            txvWallet = view.findViewById(R.id.delivery_boy_wallet_credits);
            cardReceipt = view.findViewById(R.id.card_receipt);


        }

        public MaterialTextView getTxvName() {
            return txvName;
        }

        public MaterialCardView getCardReceipt() {
            return cardReceipt;
        }

        public MaterialTextView getTxvWallet() {
            return txvWallet;
        }

        public View getMainView() {
            return mainView;
        }
    }
}
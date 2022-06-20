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
import com.sadaat.groceryapp.models.LeadsModel;
import com.sadaat.groceryapp.models.SuggestionModel;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;

public class LeadsDisplayAdapterAdmin extends RecyclerView.Adapter<LeadsDisplayAdapterAdmin.ViewHolder> {

    private ArrayList<LeadsModel> localDataSet;

    public LeadsDisplayAdapterAdmin(ArrayList<LeadsModel> localDataSet) {
        this.localDataSet = localDataSet;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.admin_item_leads, parent, false);

        return new LeadsDisplayAdapterAdmin.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LeadsDisplayAdapterAdmin.ViewHolder holder, int position) {
        holder.getTxvLeadAction().setText(localDataSet.get(holder.getAdapterPosition()).getAction());
        holder.getTxvLeadTimestamp().setText(localDataSet.get(holder.getAdapterPosition()).getTimeStamp().toString());
    }

    @Override
    public int getItemCount() {
        return localDataSet.size();
    }

    public void addItem(LeadsModel model) {
        localDataSet.add(0, model);
        notifyItemInserted(0);
    }

    public void deleteAll() {
        int size = localDataSet.size();
        localDataSet.clear();
        notifyItemRangeRemoved(0, size);
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final MaterialTextView txvLeadAction;
        private final MaterialTextView txvLeadTimestamp;

        public ViewHolder(View view) {
            super(view);

            txvLeadAction = view.findViewById(R.id.txv_lead_action);
            txvLeadTimestamp = view.findViewById(R.id.txv_lead_time);

        }

        public MaterialTextView getTxvLeadAction() {
            return txvLeadAction;
        }

        public MaterialTextView getTxvLeadTimestamp() {
            return txvLeadTimestamp;
        }
    }
}

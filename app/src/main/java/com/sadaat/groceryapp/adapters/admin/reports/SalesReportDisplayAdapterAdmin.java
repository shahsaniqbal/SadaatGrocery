package com.sadaat.groceryapp.adapters.admin.reports;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;
import com.sadaat.groceryapp.R;
import com.sadaat.groceryapp.models.Items.ItemDetailMiniModelForSalesReport;
import com.sadaat.groceryapp.models.Items.ItemModel;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

public class SalesReportDisplayAdapterAdmin extends RecyclerView.Adapter<SalesReportDisplayAdapterAdmin.ViewHolder> {

    private final ArrayList<ItemDetailMiniModelForSalesReport> localDataSet;


    public SalesReportDisplayAdapterAdmin(ArrayList<ItemDetailMiniModelForSalesReport> localDataSet) {
        this.localDataSet = localDataSet;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        int LAYOUT_FILE = R.layout.admin_item_report_sale;
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(LAYOUT_FILE, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, @SuppressLint("RecyclerView") int position) {
        viewHolder.getTxvName().setText(localDataSet.get(viewHolder.getAdapterPosition()).getItemName());
        viewHolder.getTxvSale().setText(MessageFormat.format("{0}", localDataSet.get(viewHolder.getAdapterPosition()).getQty()));
        viewHolder.getTxvQty().setText(localDataSet.get(viewHolder.getAdapterPosition()).getUnitModel());
    }

    @Override
    public int getItemCount() {
        return localDataSet.size();
    }

    public void addItem(ItemDetailMiniModelForSalesReport model) {
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

    public void updateItem(int index, ItemDetailMiniModelForSalesReport newModel) {
        localDataSet.remove(index);
        localDataSet.add(index, newModel);
        notifyItemChanged(index);
    }

    public void addAll(List<ItemDetailMiniModelForSalesReport> models) {
        localDataSet.clear();
        localDataSet.addAll(models);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final MaterialTextView txvName;
        private final MaterialTextView txvQty;
        private final MaterialTextView txvSale;

        private final View mainView;


        public ViewHolder(View view) {
            super(view);

            this.mainView = view;

            txvName = view.findViewById(R.id.stock_item_name);
            txvQty = view.findViewById(R.id.stock_item_qty);
            txvSale = view.findViewById(R.id.stock_item_sales);

        }

        public MaterialTextView getTxvName() {
            return txvName;
        }

        public MaterialTextView getTxvQty() {
            return txvQty;
        }

        public MaterialTextView getTxvSale() {
            return txvSale;
        }

        public View getMainView() {
            return mainView;
        }
    }

    public ArrayList<ItemDetailMiniModelForSalesReport> getLocalDataSet() {
        return localDataSet;
    }
}
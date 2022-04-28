package com.sadaat.groceryapp.adapters.admin.reports;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;
import com.sadaat.groceryapp.R;
import com.sadaat.groceryapp.models.Items.ItemModel;
import com.sadaat.groceryapp.models.categories.CategoriesModel;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

public class StocksReportDisplayAdapterAdmin extends RecyclerView.Adapter<StocksReportDisplayAdapterAdmin.ViewHolder> {

    private final ArrayList<ItemModel> localDataSet;


    public StocksReportDisplayAdapterAdmin(ArrayList<ItemModel> localDataSet) {
        this.localDataSet = localDataSet;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        int LAYOUT_FILE = R.layout.admin_item_report_stock;
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(LAYOUT_FILE, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, @SuppressLint("RecyclerView") int position) {
        viewHolder.getTxvName().setText(localDataSet.get(viewHolder.getAdapterPosition()).getName());
        viewHolder.getTxvStock().setText(MessageFormat.format("{0}", localDataSet.get(viewHolder.getAdapterPosition()).getOtherDetails().getStock()));
        viewHolder.getTxvQty().setText(localDataSet.get(viewHolder.getAdapterPosition()).getQty().toString());
    }

    @Override
    public int getItemCount() {
        return localDataSet.size();
    }

    public void addItem(ItemModel model) {
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

    public void updateItem(int index, ItemModel newModel) {
        localDataSet.remove(index);
        localDataSet.add(index, newModel);
        notifyItemChanged(index);
    }

    public void addAll(List<ItemModel> models) {
        localDataSet.clear();
        localDataSet.addAll(models);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final MaterialTextView txvName;
        private final MaterialTextView txvQty;
        private final MaterialTextView txvStock;

        private final View mainView;


        public ViewHolder(View view) {
            super(view);

            this.mainView = view;

            txvName = view.findViewById(R.id.stock_item_name);
            txvQty = view.findViewById(R.id.stock_item_qty);
            txvStock = view.findViewById(R.id.stock_item_stock);

        }

        public MaterialTextView getTxvName() {
            return txvName;
        }

        public MaterialTextView getTxvQty() {
            return txvQty;
        }

        public MaterialTextView getTxvStock() {
            return txvStock;
        }

        public View getMainView() {
            return mainView;
        }
    }


}
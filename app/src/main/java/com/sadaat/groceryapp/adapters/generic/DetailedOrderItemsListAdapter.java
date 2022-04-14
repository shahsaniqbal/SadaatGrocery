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
import com.sadaat.groceryapp.models.Items.ItemModel;
import com.sadaat.groceryapp.models.cart.CartItemModel;
import com.sadaat.groceryapp.temp.UserLive;
import com.sadaat.groceryapp.ui.Loaders.LoadingDialogue;

import java.util.ArrayList;

public class DetailedOrderItemsListAdapter extends RecyclerView.Adapter<DetailedOrderItemsListAdapter.ViewHolder> {

    private final int LAYOUT_FILE = R.layout.generic_item_orderitem_itemslist_recycler;
    private final ArrayList<CartItemModel> localDataSet;
    private final Context mContext;
    private final LoadingDialogue progressDialogue;


    public DetailedOrderItemsListAdapter(Context mContext) {
        this.mContext = mContext;
        this.progressDialogue = new LoadingDialogue(mContext);
        this.localDataSet = new ArrayList<>();
    }

    public DetailedOrderItemsListAdapter(Context mContext, ArrayList<CartItemModel> localDataSet) {
        this.progressDialogue = new LoadingDialogue(mContext);
        this.localDataSet = localDataSet;
        this.mContext = mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(LAYOUT_FILE, viewGroup, false);

        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull DetailedOrderItemsListAdapter.ViewHolder viewHolder, @SuppressLint("RecyclerView") int position) {
        viewHolder.getTxvName().setText(localDataSet.get(viewHolder.getAdapterPosition()).getModel().getName() + " - " + localDataSet.get(viewHolder.getAdapterPosition()).getModel().getQty().toString());
        viewHolder.getUnitPriceTxv().setText("" + localDataSet.get(viewHolder.getAdapterPosition()).getModel().getPrices().getSalePrice());
        viewHolder.getTotalPriceTxv().setText("" + localDataSet.get(viewHolder.getAdapterPosition()).getTotalSalePrice());
        viewHolder.getTxvQty().setText("" + localDataSet.get(viewHolder.getAdapterPosition()).getQty());
    }

    @Override
    public int getItemCount() {
        return localDataSet.size();
    }

    public void addItem(CartItemModel model) {
        localDataSet.add(model);
        notifyItemInserted(localDataSet.size() - 1);
    }

    public void deleteAll() {
        int size = localDataSet.size();
        localDataSet.clear();
        notifyItemRangeRemoved(0, size);
    }

    public void notifyChange() {

        deleteAll();

        for (String k : UserLive.currentLoggedInUser.getCart().getCartItems().keySet()) {
            addItem(UserLive.currentLoggedInUser.getCart().getCartItems().get(k));
        }
    }

    public interface ItemClickListeners {
        CartItemModel indicateItemCountChange(ItemModel item, int quantity);

        void prepareCart(CartItemModel cartItemModel, LoadingDialogue progressDialogue);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final MaterialTextView txvName;
        private final MaterialTextView txvQty;
        private final View mainView;
        private final MaterialTextView unitPriceTxv;
        private final MaterialTextView totalPriceTxv;


        public ViewHolder(View view) {
            super(view);

            this.mainView = view;
            txvName = view.findViewById(R.id.customer_item_cart_name);
            txvQty = view.findViewById(R.id.txv_item_qty_customer_items);
            unitPriceTxv = view.findViewById(R.id.customer_item_cart_unit_price);
            totalPriceTxv = view.findViewById(R.id.customer_item_cart_total);
        }

        public MaterialTextView getTxvName() {
            return txvName;
        }

        public MaterialTextView getTxvQty() {
            return txvQty;
        }

        public View getMainView() {
            return mainView;
        }

        public MaterialTextView getUnitPriceTxv() {
            return unitPriceTxv;
        }

        public MaterialTextView getTotalPriceTxv() {
            return totalPriceTxv;
        }
    }
}
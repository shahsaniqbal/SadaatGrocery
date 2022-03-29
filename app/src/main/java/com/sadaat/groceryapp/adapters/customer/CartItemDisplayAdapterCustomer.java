package com.sadaat.groceryapp.adapters.customer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;
import com.sadaat.groceryapp.R;
import com.sadaat.groceryapp.models.ItemModel;
import com.sadaat.groceryapp.models.cart.CartItemModel;
import com.sadaat.groceryapp.temp.UserLive;
import com.sadaat.groceryapp.ui.Loaders.LoadingDialogue;

import java.sql.Array;
import java.util.HashMap;
import java.util.Objects;

public class CartItemDisplayAdapterCustomer extends RecyclerView.Adapter<CartItemDisplayAdapterCustomer.ViewHolder> {

    private final int LAYOUT_FILE = R.layout.customer_item_cartitem_recycler_v3;
    public ItemClickListeners customOnClickListener;
    int maxQtyPerOrder = 25;
    private HashMap<String, CartItemModel> localDataSet = UserLive.currentLoggedInUser.getCart().getCartItems();
    private String[] keys;
    private Context mContext;
    private LoadingDialogue progressDialogue;


    public CartItemDisplayAdapterCustomer(Context mContext, ItemClickListeners customOnClickListener) {
        this.customOnClickListener = customOnClickListener;
        this.mContext = mContext;
        keys = new String[localDataSet.size()];
        this.progressDialogue = new LoadingDialogue(mContext);
        keys = localDataSet.keySet().toArray(keys);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(LAYOUT_FILE, viewGroup, false);

        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull CartItemDisplayAdapterCustomer.ViewHolder viewHolder, int position) {
        viewHolder.getTxvName().setText(localDataSet.get(keys[position]).getModel().getName());
        viewHolder.getTxvUnit().setText(localDataSet.get(keys[position]).getModel().getQty().toString());
        viewHolder.getUnitPriceTxv().setText("Rs. " + localDataSet.get(keys[position]).getModel().getPrices().getSalePrice());
        viewHolder.getTotalPriceTxv().setText("Rs. " + localDataSet.get(keys[position]).getTotalSalePrice());
        viewHolder.getTxvQty().setText("" + localDataSet.get(keys[position]).getQty());

        //viewHolder.getUnitPriceTxv().setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);

        if (UserLive.currentLoggedInUser.getCart().getCartItems().containsKey(
                localDataSet.get(keys[viewHolder.getAdapterPosition()]).getModel().getID())
        ) {
            viewHolder.getTxvQty().setText("" + Objects.requireNonNull(UserLive.currentLoggedInUser.getCart().getCartItems().get(
                    localDataSet.get(keys[viewHolder.getAdapterPosition()]).getModel().getID()
            )).getQty());
        }

        int currentCount = Integer.parseInt(viewHolder.getTxvQty().getText().toString());
        if (currentCount == 0) {
            viewHolder.getiButtonMinus().setClickable(false);
            viewHolder.getiButtonMinus().setFocusable(false);
        }

        viewHolder.getiButtonPlus().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    int currentCount = Integer.parseInt(viewHolder.getTxvQty().getText().toString());

                    viewHolder.getiButtonPlus().setClickable(true);
                    viewHolder.getiButtonPlus().setFocusable(true);
                    if (currentCount == maxQtyPerOrder || currentCount >= localDataSet.get(keys[viewHolder.getAdapterPosition()]).getModel().getOtherDetails().getStock()) {
                        viewHolder.getiButtonPlus().setClickable(false);
                        viewHolder.getiButtonPlus().setFocusable(false);
                    } else {
                        viewHolder.getiButtonMinus().setClickable(true);
                        viewHolder.getiButtonMinus().setFocusable(true);
                        viewHolder.getTxvQty().setText("" + (Integer.parseInt(viewHolder.getTxvQty().getText().toString()) + 1));
                        handleCount(localDataSet.get(keys[viewHolder.getAdapterPosition()]).getModel(), Integer.parseInt(viewHolder.getTxvQty().getText().toString()));
                    }
                }
            });
            viewHolder.getiButtonMinus().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    viewHolder.getiButtonPlus().setClickable(true);
                    viewHolder.getiButtonPlus().setFocusable(true);

                    int currentCount = Integer.parseInt(viewHolder.getTxvQty().getText().toString());
                    if (currentCount == 0) {
                        viewHolder.getiButtonMinus().setClickable(false);
                        viewHolder.getiButtonMinus().setFocusable(false);
                    } else if (currentCount > 0) {
                        viewHolder.getiButtonMinus().setClickable(true);
                        viewHolder.getiButtonMinus().setFocusable(true);
                        viewHolder.getTxvQty().setText("" + (Integer.parseInt(viewHolder.getTxvQty().getText().toString()) - 1));
                        handleCount(localDataSet.get(keys[viewHolder.getAdapterPosition()]).getModel(), Integer.parseInt(viewHolder.getTxvQty().getText().toString()));
                    }
                }
            });


    }

    private void handleCount(ItemModel itemModel, int count) {
        customOnClickListener.prepareCart(customOnClickListener.indicateItemCountChange(itemModel, count));

    }

    @Override
    public int getItemCount() {
        return keys.length;
    }


    public interface ItemClickListeners {
        public CartItemModel indicateItemCountChange(ItemModel item, int quantity);

        public void prepareCart(CartItemModel cartItemModel);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final MaterialTextView txvName;
        private final MaterialTextView txvQty;
        private final MaterialCardView iButtonPlus;
        private final MaterialCardView iButtonMinus;
        private final View mainView;
        private final MaterialTextView unitPriceTxv;
        private final MaterialTextView totalPriceTxv;
        private MaterialTextView txvUnit;


        public ViewHolder(View view) {
            super(view);

            this.mainView = view;

            txvName = (MaterialTextView) view.findViewById(R.id.customer_item_cart_name);
            txvQty = (MaterialTextView) view.findViewById(R.id.txv_item_qty_customer_items);

            txvUnit = (MaterialTextView) view.findViewById(R.id.customer_item_cart_unit);

            iButtonPlus = (MaterialCardView) view.findViewById(R.id.mcard_item_plus_customer_items);
            iButtonMinus = (MaterialCardView) view.findViewById(R.id.mcard_item_minus_customer_items);

            unitPriceTxv = (MaterialTextView) view.findViewById(R.id.customer_item_cart_unit_price);
            totalPriceTxv = (MaterialTextView) view.findViewById(R.id.customer_item_cart_total);

        }

        public MaterialTextView getTxvName() {
            return txvName;
        }

        public MaterialTextView getTxvQty() {
            return txvQty;
        }

        public MaterialCardView getiButtonPlus() {
            return iButtonPlus;
        }

        public MaterialCardView getiButtonMinus() {
            return iButtonMinus;
        }

        public View getMainView() {
            return mainView;
        }

        public MaterialTextView getTxvUnit() {
            return txvUnit;
        }

        public MaterialTextView getUnitPriceTxv() {
            return unitPriceTxv;
        }

        public MaterialTextView getTotalPriceTxv() {
            return totalPriceTxv;
        }
    }

}
package com.sadaat.groceryapp.adapters.admin;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;
import com.sadaat.groceryapp.R;
import com.sadaat.groceryapp.formatter.Price;
import com.sadaat.groceryapp.models.ItemModel;

import java.util.ArrayList;

public class ItemsDisplayAdapterAdmin extends RecyclerView.Adapter<ItemsDisplayAdapterAdmin.ViewHolder> {

    private final ArrayList<ItemModel> localDataSet;
    private final int LAYOUT_FILE = R.layout.admin_item_items_recycler;
    public ItemClickListeners customOnClickListener;
    private Context mContext;


    public ItemsDisplayAdapterAdmin(ArrayList<ItemModel> localDataSet, ItemClickListeners customOnClickListener) {
        this.localDataSet = localDataSet;
        this.customOnClickListener = (ItemClickListeners) customOnClickListener;
    }

    public ItemsDisplayAdapterAdmin(ArrayList<ItemModel> localDataSet, ItemClickListeners customOnClickListener, Context mContext) {
        this.localDataSet = localDataSet;
        this.customOnClickListener = customOnClickListener;
        this.mContext = mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(LAYOUT_FILE, viewGroup, false);

        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        viewHolder.getTxvName().setText(localDataSet.get(position).getName());
        viewHolder.getTxvDesc().setText(localDataSet.get(position).getDescription());

        viewHolder.getTxvRetailPrice().setText(Price.format(localDataSet.get(position).getPrices().getRetailPrice()));
        viewHolder.getTxvSalePrice().setText(Price.format(localDataSet.get(position).getPrices().getSalePrice()));

        viewHolder.getTxvStock().setText(String.valueOf(localDataSet.get(position).getOtherDetails().getStock()) + " Remaining");

        viewHolder.getTxvSecurityCharges().setText(Price.format(localDataSet.get(position).getOtherDetails().getSecurityCharges()));
        viewHolder.getTxvCardDiscount().setText(Price.format(localDataSet.get(position).getOtherDetails().getSpecialDiscountForCardHolder()));

        viewHolder.getTxvQty().setText(localDataSet.get(position).getQty().toString());

        viewHolder.getiButtonShowMenuForItemOptions().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customOnClickListener.setItemButtonPopupAtPosition(v, viewHolder.getAdapterPosition(), localDataSet.get(viewHolder.getAdapterPosition())).show();
            }
        });


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

    /*
    public void replaceAllData(ArrayList<UserModel> allUsers) {
        deleteAll();
        // TODO Modify this method
        for(UserModel m: allUsers){
            addUser(m);
        }
    }
    */


    public interface ItemClickListeners {

        PopupMenu setItemButtonPopupAtPosition(View v, int position, ItemModel itemModel);

        void onDeleteButtonClick(View v, int position, ItemModel modelToDelete);

        void onAddStockButtonClick(View v, int position, ItemModel model);

        void onUpdateDetailsButtonClick(View v, int position, ItemModel oldModelToUpdate);

        void onShowFullDetailsButtonClick(ItemModel modelToShow);

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final MaterialTextView txvName;
        private final MaterialTextView txvDesc;

        private final MaterialTextView txvRetailPrice;
        private final MaterialTextView txvSalePrice;
        private final MaterialTextView txvQty;
        private final MaterialTextView txvStock;

        private final MaterialTextView txvSecurityCharges;
        private final MaterialTextView txvCardDiscount;

        private final ImageView imageDisplayItemImage;

        private final ImageButton iButtonShowMenuForItemOptions;
        private final View mainView;


        public ViewHolder(View view) {
            super(view);

            this.mainView = view;

            txvName = (MaterialTextView) view.findViewById(R.id.item_item_name_admin);
            txvDesc = (MaterialTextView) view.findViewById(R.id.item_item_desc_admin);
            txvRetailPrice = (MaterialTextView) view.findViewById(R.id.item_item_retail_admin);
            txvSalePrice = (MaterialTextView) view.findViewById(R.id.item_item_sale_admin);
            txvQty = (MaterialTextView) view.findViewById(R.id.item_item_qty_admin);
            txvStock = (MaterialTextView) view.findViewById(R.id.item_item_stock_admin);
            txvSecurityCharges = (MaterialTextView) view.findViewById(R.id.item_item_security_admin);
            txvCardDiscount = (MaterialTextView) view.findViewById(R.id.item_item_cardHolderDiscount_admin);

            imageDisplayItemImage = (ImageView) view.findViewById(R.id.item_item_image_admin);

            iButtonShowMenuForItemOptions = (ImageButton) view.findViewById(R.id.item_item_button_options);

            txvRetailPrice.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);

        }

        public MaterialTextView getTxvName() {
            return txvName;
        }

        public MaterialTextView getTxvDesc() {
            return txvDesc;
        }

        public MaterialTextView getTxvRetailPrice() {
            return txvRetailPrice;
        }

        public MaterialTextView getTxvSalePrice() {
            return txvSalePrice;
        }

        public MaterialTextView getTxvQty() {
            return txvQty;
        }

        public MaterialTextView getTxvStock() {
            return txvStock;
        }

        public ImageView getImageDisplayItemImage() {
            return imageDisplayItemImage;
        }

        public ImageButton getiButtonShowMenuForItemOptions() {
            return iButtonShowMenuForItemOptions;
        }

        public View getMainView() {
            return mainView;
        }

        public MaterialTextView getTxvSecurityCharges() {
            return txvSecurityCharges;
        }

        public MaterialTextView getTxvCardDiscount() {
            return txvCardDiscount;
        }
    }



}
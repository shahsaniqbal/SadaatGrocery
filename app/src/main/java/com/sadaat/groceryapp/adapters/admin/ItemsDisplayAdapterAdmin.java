package com.sadaat.groceryapp.adapters.admin;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.BitmapFactory;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.sadaat.groceryapp.R;
import com.sadaat.groceryapp.formatter.Price;
import com.sadaat.groceryapp.models.Items.ItemModel;
import com.sadaat.groceryapp.temp.FirebaseDataKeys;
import com.sadaat.groceryapp.ui.Loaders.LoadingDialogue;

import java.util.ArrayList;

public class ItemsDisplayAdapterAdmin extends RecyclerView.Adapter<ItemsDisplayAdapterAdmin.ViewHolder> {

    private final ArrayList<ItemModel> localDataSet;
    private final LoadingDialogue progressDialogue;
    public ItemClickListeners customOnClickListener;


    public ItemsDisplayAdapterAdmin(ArrayList<ItemModel> localDataSet, ItemClickListeners customOnClickListener, Context mContext) {
        this.localDataSet = localDataSet;
        this.customOnClickListener = customOnClickListener;
        this.progressDialogue = new LoadingDialogue(mContext);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        int LAYOUT_FILE = R.layout.admin_item_items_recycler;
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(LAYOUT_FILE, viewGroup, false);

        return new ViewHolder(view);
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, @SuppressLint("RecyclerView") int position) {
        viewHolder.getTxvName().setText(localDataSet.get(viewHolder.getAdapterPosition()).getName());
        viewHolder.getTxvDesc().setText(localDataSet.get(viewHolder.getAdapterPosition()).getDescription());

        viewHolder.getTxvRetailPrice().setText(Price.format(localDataSet.get(viewHolder.getAdapterPosition()).getPrices().getRetailPrice()));
        viewHolder.getTxvSalePrice().setText(Price.format(localDataSet.get(viewHolder.getAdapterPosition()).getPrices().getSalePrice()));

        viewHolder.getTxvStock().setText(localDataSet.get(viewHolder.getAdapterPosition()).getOtherDetails().getStock() + " Remaining");

        viewHolder.getTxvSecurityCharges().setText(Price.format(localDataSet.get(viewHolder.getAdapterPosition()).getOtherDetails().getSecurityCharges()));
        viewHolder.getTxvCardDiscount().setText(Price.format(localDataSet.get(viewHolder.getAdapterPosition()).getOtherDetails().getSpecialDiscountForCardHolder()));

        viewHolder.getTxvQty().setText(localDataSet.get(viewHolder.getAdapterPosition()).getQty().toString());


        viewHolder.getiButtonShowMenuForItemOptions().setOnClickListener(v -> customOnClickListener.setItemButtonPopupAtPosition(v, viewHolder.getAdapterPosition(), localDataSet.get(viewHolder.getAdapterPosition())).show());

        viewHolder.getiButtonIsHot().setOnClickListener(view -> {
            localDataSet.get(viewHolder.getAdapterPosition()).setHot(!(localDataSet.get(viewHolder.getAdapterPosition()).isHot()));
            customOnClickListener.onIsHotButtonClick(
                    localDataSet.get(viewHolder.getAdapterPosition()).isHot(),
                    (ImageButton) view,
                    localDataSet.get(viewHolder.getAdapterPosition()).getID(),
                    viewHolder.getAdapterPosition()
            );
            notifyItemChanged(viewHolder.getAdapterPosition());
        });


        progressDialogue.show("Please Wait", "Loading Item Images");


        if (localDataSet.get(viewHolder.getAdapterPosition()).isHot()) {
            viewHolder.getiButtonIsHot().setImageResource(R.drawable.ic_fire_filled);
        } else viewHolder.getiButtonIsHot().setImageResource(R.drawable.ic_fire_outlined);


        if (!localDataSet
                .get(viewHolder.getAdapterPosition())
                .getOtherDetails()
                .getImageLink().equals("")) {

            StorageReference imgRef = FirebaseStorage
                    .getInstance(FirebaseDataKeys.STORAGE_BUCKET_ADDRESS)
                    .getReference()
                    .child(localDataSet
                            .get(viewHolder.getAdapterPosition())
                            .getOtherDetails()
                            .getImageLink());

            final long ONE_MEGABYTE = 1024 * 1024;

            imgRef.getBytes(10 * ONE_MEGABYTE).addOnSuccessListener(bytes -> {
                viewHolder.getImageDisplayItemImage().setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
                progressDialogue.dismiss();
            }).addOnFailureListener(exception -> {
                //Toast.makeText(mContext, "Image Load Failed, \n Leave it or use a new one", Toast.LENGTH_SHORT).show();
            });
        } else {
            progressDialogue.dismiss();
        }

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

        void onIsHotButtonClick(boolean isHot, ImageButton view, String itemID, int position);

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
        private final ImageButton iButtonIsHot;
        private final View mainView;


        public ViewHolder(View view) {
            super(view);

            this.mainView = view;

            txvName = view.findViewById(R.id.item_item_name_admin);
            txvDesc = view.findViewById(R.id.item_item_desc_admin);
            txvRetailPrice = view.findViewById(R.id.item_item_retail_admin);
            txvSalePrice = view.findViewById(R.id.item_item_sale_admin);
            txvQty = view.findViewById(R.id.item_item_qty_admin);
            txvStock = view.findViewById(R.id.item_item_stock_admin);
            txvSecurityCharges = view.findViewById(R.id.item_item_security_admin);
            txvCardDiscount = view.findViewById(R.id.item_item_cardHolderDiscount_admin);

            imageDisplayItemImage = view.findViewById(R.id.item_item_image_admin);

            iButtonShowMenuForItemOptions = view.findViewById(R.id.item_item_button_options);
            iButtonIsHot = view.findViewById(R.id.item_item_button_hot);

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

        public ImageButton getiButtonIsHot() {
            return iButtonIsHot;
        }
    }


}
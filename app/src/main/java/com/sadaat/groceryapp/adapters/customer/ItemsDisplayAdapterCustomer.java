package com.sadaat.groceryapp.adapters.customer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.sadaat.groceryapp.R;
import com.sadaat.groceryapp.models.Items.ItemModel;
import com.sadaat.groceryapp.models.cart.CartItemModel;
import com.sadaat.groceryapp.temp.FirebaseDataKeys;
import com.sadaat.groceryapp.temp.UserLive;
import com.sadaat.groceryapp.ui.Loaders.LoadingDialogue;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ItemsDisplayAdapterCustomer extends RecyclerView.Adapter<ItemsDisplayAdapterCustomer.ViewHolder> {

    private final ArrayList<ItemModel> localDataSet;
    //private final LoadingDialogue progressDialogue;
    public ItemClickListeners customOnClickListener;


    public ItemsDisplayAdapterCustomer(ArrayList<ItemModel> localDataSet, ItemClickListeners customOnClickListener, Context mContext) {
        this.localDataSet = localDataSet;
        this.customOnClickListener = customOnClickListener;
        //this.progressDialogue = new LoadingDialogue(mContext);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        int LAYOUT_FILE = R.layout.customer_item_items_recycler;
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(LAYOUT_FILE, viewGroup, false);

        return new ViewHolder(view);
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        viewHolder.getTxvName().setText(localDataSet.get(position).getName());
        viewHolder.getTxvQtyUnit().setText(localDataSet.get(position).getQty().toString());
        viewHolder.getRetailPriceTxv().setText("Rs. " + localDataSet.get(position).getPrices().getRetailPrice());
        viewHolder.getSalePriceTxv().setText("Rs. " + localDataSet.get(position).getPrices().getSalePrice());
        viewHolder.getRetailPriceTxv().setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);

        if (UserLive.currentLoggedInUser.getCart().getCartItems().containsKey(
                localDataSet.get(position).getID()
        )) {
            viewHolder.getTxvQty().setText("" + Objects.requireNonNull(UserLive.currentLoggedInUser.getCart().getCartItems().get(
                    localDataSet.get(position).getID()
            )).getQty());
        }

        int currentCount = Integer.parseInt(viewHolder.getTxvQty().getText().toString());
        if (currentCount == 0) {
            viewHolder.getiButtonMinus().setClickable(false);
            viewHolder.getiButtonMinus().setFocusable(false);
        }

        if (localDataSet.get(viewHolder.getAdapterPosition()).getOtherDetails().getStock() == 0) {
            viewHolder.setViewStockAvailablability(false);
        } else {
            viewHolder.setViewStockAvailablability(true);
            viewHolder.getiButtonPlus().setOnClickListener(view -> {

                int currentCount1 = Integer.parseInt(viewHolder.getTxvQty().getText().toString());

                viewHolder.getiButtonPlus().setClickable(true);
                viewHolder.getiButtonPlus().setFocusable(true);
                if (currentCount1 == localDataSet.get(viewHolder.getAdapterPosition()).getMaxQtyPerOrder() || currentCount1 >= localDataSet.get(viewHolder.getAdapterPosition()).getOtherDetails().getStock()) {
                    viewHolder.getiButtonPlus().setClickable(false);
                    viewHolder.getiButtonPlus().setFocusable(false);
                } else {
                    viewHolder.getiButtonMinus().setClickable(true);
                    viewHolder.getiButtonMinus().setFocusable(true);
                    viewHolder.getTxvQty().setText("" + (Integer.parseInt(viewHolder.getTxvQty().getText().toString()) + 1));
                    handleCount(localDataSet.get(viewHolder.getAdapterPosition()), Integer.parseInt(viewHolder.getTxvQty().getText().toString()));
                }
            });
            viewHolder.getiButtonMinus().setOnClickListener(view -> {
                viewHolder.getiButtonPlus().setClickable(true);
                viewHolder.getiButtonPlus().setFocusable(true);

                int currentCount12 = Integer.parseInt(viewHolder.getTxvQty().getText().toString());
                if (currentCount12 == 0) {
                    viewHolder.getiButtonMinus().setClickable(false);
                    viewHolder.getiButtonMinus().setFocusable(false);
                } else if (currentCount12 > 0) {
                    viewHolder.getiButtonMinus().setClickable(true);
                    viewHolder.getiButtonMinus().setFocusable(true);
                    viewHolder.getTxvQty().setText("" + (Integer.parseInt(viewHolder.getTxvQty().getText().toString()) - 1));
                    handleCount(localDataSet.get(viewHolder.getAdapterPosition()), Integer.parseInt(viewHolder.getTxvQty().getText().toString()));
                }
            });
        }

        //progressDialogue.show("Please Wait", "Loading Item Images.");
        if (!localDataSet
                .get(position)
                .getOtherDetails()
                .getImageLink().equals("")) {

            StorageReference imgRef = FirebaseStorage
                    .getInstance(FirebaseDataKeys.STORAGE_BUCKET_ADDRESS)
                    .getReference()
                    .child(localDataSet
                            .get(position)
                            .getOtherDetails()
                            .getImageLink());

            final long ONE_MEGABYTE = 1024 * 1024;

            imgRef.getBytes(10 * ONE_MEGABYTE).addOnSuccessListener(bytes -> {
                viewHolder.getImageDisplayItemImage().setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
                //progressDialogue.dismiss();
            }).addOnFailureListener(exception -> {
                //Toast.makeText(mContext, "Image Load Failed, \n Leave it or use a new one", Toast.LENGTH_SHORT).show();
            });
        } else {
            //progressDialogue.dismiss();
        }

        viewHolder.getMainView().setOnClickListener(view -> customOnClickListener.onClick(localDataSet.get(viewHolder.getAdapterPosition())));

    }

    private void handleCount(ItemModel itemModel, int count) {
        customOnClickListener.prepareCart(customOnClickListener.indicateItemCountChange(itemModel, count));

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

    public void addAll(List<ItemModel> itemModel) {
        localDataSet.clear();
        localDataSet.addAll(itemModel);
        notifyDataSetChanged();
    }


    public interface ItemClickListeners {
        CartItemModel indicateItemCountChange(ItemModel item, int quantity);

        void prepareCart(CartItemModel cartItemModel);

        void onClick(ItemModel model);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final MaterialTextView txvName;
        private final MaterialTextView txvQty;
        private final ImageView imageDisplayItemImage;
        private final MaterialCardView iButtonPlus;
        private final MaterialCardView iButtonMinus;
        private final View mainView;
        private final MaterialTextView retailPriceTxv;
        private final MaterialTextView salePriceTxv;
        private final MaterialTextView txvQtyUnit;


        public ViewHolder(View view) {
            super(view);

            this.mainView = view.findViewById(R.id.customer_subcategory_parent_card);

            txvName = view.findViewById(R.id.txv_item_title_customer_items);
            txvQty = view.findViewById(R.id.txv_item_qty_customer_items);

            txvQtyUnit = view.findViewById(R.id.txv_item_qtyunit_customer_items);

            imageDisplayItemImage = view.findViewById(R.id.imgv_item_customer_items);

            iButtonPlus = view.findViewById(R.id.mcard_item_plus_customer_items);
            iButtonMinus = view.findViewById(R.id.mcard_item_minus_customer_items);

            retailPriceTxv = view.findViewById(R.id.customer_item_retail_price);
            salePriceTxv = view.findViewById(R.id.customer_item_sale_price);


        }

        public MaterialTextView getTxvName() {
            return txvName;
        }

        public MaterialTextView getTxvQty() {
            return txvQty;
        }

        public ImageView getImageDisplayItemImage() {
            return imageDisplayItemImage;
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

        public MaterialTextView getTxvQtyUnit() {
            return txvQtyUnit;
        }

        public MaterialTextView getRetailPriceTxv() {
            return retailPriceTxv;
        }

        public MaterialTextView getSalePriceTxv() {
            return salePriceTxv;
        }

        @SuppressLint("ResourceAsColor")
        public void setViewStockAvailablability(boolean isAvailable) {
            mainView.findViewById(R.id.available_layout).setVisibility(isAvailable ? View.VISIBLE : View.GONE);
            mainView.findViewById(R.id.unavailable_layout).setVisibility(isAvailable ? View.GONE : View.VISIBLE);
            //mainView.setBackgroundColor(isAvailable? R.color.white : R.color.grey);
        }
    }


}
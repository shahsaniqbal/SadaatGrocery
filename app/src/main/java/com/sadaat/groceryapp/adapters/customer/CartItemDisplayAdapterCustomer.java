package com.sadaat.groceryapp.adapters.customer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;
import com.sadaat.groceryapp.R;
import com.sadaat.groceryapp.models.Items.ItemModel;
import com.sadaat.groceryapp.models.cart.CartItemModel;
import com.sadaat.groceryapp.temp.FirebaseDataKeys;
import com.sadaat.groceryapp.temp.UserLive;
import com.sadaat.groceryapp.ui.Loaders.LoadingDialogue;

import java.util.ArrayList;
import java.util.Objects;

public class CartItemDisplayAdapterCustomer extends RecyclerView.Adapter<CartItemDisplayAdapterCustomer.ViewHolder> {

    final String ITEM_REF = new FirebaseDataKeys().getItemsRef();
    private final int LAYOUT_FILE = R.layout.customer_item_cartitem_recycler_v3;
    public ItemClickListeners customOnClickListener;
    //int maxQtyPerOrder = 25;
    private final ArrayList<CartItemModel> localDataSet;
    private final Context mContext;
    private final LoadingDialogue progressDialogue;


    public CartItemDisplayAdapterCustomer(Context mContext, ItemClickListeners customOnClickListener) {
        this.customOnClickListener = customOnClickListener;
        this.mContext = mContext;
        //keys = new ArrayList<>(localDataSet.size());
        this.progressDialogue = new LoadingDialogue(mContext);
        this.localDataSet = new ArrayList<>();
        notifyChange();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(LAYOUT_FILE, viewGroup, false);

        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull CartItemDisplayAdapterCustomer.ViewHolder viewHolder, @SuppressLint("RecyclerView") int position) {
        viewHolder.getTxvName().setText(localDataSet.get(viewHolder.getAdapterPosition()).getModel().getName());
        viewHolder.getTxvUnit().setText(localDataSet.get(viewHolder.getAdapterPosition()).getModel().getQty().toString());
        viewHolder.getUnitPriceTxv().setText("Rs. " + localDataSet.get(viewHolder.getAdapterPosition()).getModel().getPrices().getSalePrice());
        viewHolder.getTotalPriceTxv().setText("Rs. " + localDataSet.get(viewHolder.getAdapterPosition()).getTotalSalePrice());
        viewHolder.getTxvQty().setText("" + localDataSet.get(viewHolder.getAdapterPosition()).getQty());

        //viewHolder.getUnitPriceTxv().setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);

        if (UserLive.currentLoggedInUser.getCart().getCartItems().containsKey(
                localDataSet.get(viewHolder.getAdapterPosition()).getModel().getID())
        ) {
            viewHolder.getTxvQty().setText("" + Objects.requireNonNull(UserLive.currentLoggedInUser.getCart().getCartItems().get(
                    localDataSet.get(viewHolder.getAdapterPosition()).getModel().getID()
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

                progressDialogue.show("Processing", "Cart");

                int currentCount = Integer.parseInt(viewHolder.getTxvQty().getText().toString());
                /*
                final int[] latestStock = {0};
                FirebaseFirestore.getInstance()
                        .collection(ITEM_REF)
                        .document(localDataSet.get(viewHolder.getAdapterPosition()).getModel().getID())
                        .collection("otherDetails")
                        .document("stock")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                latestStock[0] = task.getResult().toObject(Integer.class);
                            }
                        });
                        */

                viewHolder.getiButtonPlus().setClickable(true);
                viewHolder.getiButtonPlus().setFocusable(true);

                Log.e("Current Position", ""+viewHolder.getAdapterPosition());
                Log.e("Position", ""+position);
                Log.e("Size Position", ""+localDataSet.size());


                if (currentCount == localDataSet.get(position).getModel().getMaxQtyPerOrder() || currentCount >= localDataSet.get(position).getModel().getOtherDetails().getStock()) {
                    viewHolder.getiButtonPlus().setClickable(false);
                    viewHolder.getiButtonPlus().setFocusable(false);
                    progressDialogue.dismiss();
                } else {
                    viewHolder.getiButtonMinus().setClickable(true);
                    viewHolder.getiButtonMinus().setFocusable(true);
                    viewHolder.getTxvQty().setText("" + (Integer.parseInt(viewHolder.getTxvQty().getText().toString()) + 1));
                    handleCount(localDataSet.get(position).getModel(), Integer.parseInt(viewHolder.getTxvQty().getText().toString()));
                }


            }
        });
        viewHolder.getiButtonMinus().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialogue.show("Processing", "Cart");
                viewHolder.getiButtonPlus().setClickable(true);
                viewHolder.getiButtonPlus().setFocusable(true);


                int currentCount = Integer.parseInt(viewHolder.getTxvQty().getText().toString());
                if (currentCount == 0) {
                    viewHolder.getiButtonMinus().setClickable(false);
                    viewHolder.getiButtonMinus().setFocusable(false);

                    progressDialogue.dismiss();
                } else if (currentCount > 0) {
                    viewHolder.getiButtonMinus().setClickable(true);
                    viewHolder.getiButtonMinus().setFocusable(true);
                    viewHolder.getTxvQty().setText("" + (Integer.parseInt(viewHolder.getTxvQty().getText().toString()) - 1));
                    handleCount(localDataSet.get(position).getModel(), Integer.parseInt(viewHolder.getTxvQty().getText().toString()));
                }
            }
        });
    }

    private void handleCount(ItemModel itemModel, int count) {
        customOnClickListener.prepareCart(customOnClickListener.indicateItemCountChange(itemModel, count), progressDialogue);
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
        private final MaterialCardView iButtonPlus;
        private final MaterialCardView iButtonMinus;
        private final View mainView;
        private final MaterialTextView unitPriceTxv;
        private final MaterialTextView totalPriceTxv;
        private final MaterialTextView txvUnit;


        public ViewHolder(View view) {
            super(view);

            this.mainView = view;

            txvName = view.findViewById(R.id.customer_item_cart_name);
            txvQty = view.findViewById(R.id.txv_item_qty_customer_items);

            txvUnit = view.findViewById(R.id.customer_item_cart_unit);

            iButtonPlus = view.findViewById(R.id.mcard_item_plus_customer_items);
            iButtonMinus = view.findViewById(R.id.mcard_item_minus_customer_items);

            unitPriceTxv = view.findViewById(R.id.customer_item_cart_unit_price);
            totalPriceTxv = view.findViewById(R.id.customer_item_cart_total);

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
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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.sadaat.groceryapp.R;
import com.sadaat.groceryapp.formatter.Price;
import com.sadaat.groceryapp.models.Items.ItemModel;
import com.sadaat.groceryapp.models.Users.UserModel;
import com.sadaat.groceryapp.models.orders.OrderModel;
import com.sadaat.groceryapp.temp.FirebaseDataKeys;
import com.sadaat.groceryapp.ui.Loaders.LoadingDialogue;

import java.util.ArrayList;

public class OrderItemDisplayAdapterAdmin extends RecyclerView.Adapter<OrderItemDisplayAdapterAdmin.ViewHolder> {

    private final ArrayList<OrderModel> localDataSet;
    private final int LAYOUT_FILE = R.layout.admin_item_order_list;
    public ItemClickListeners customOnClickListener;
    private Context mContext;
    private LoadingDialogue progressDialogue;


    public OrderItemDisplayAdapterAdmin(ArrayList<OrderModel> localDataSet, ItemClickListeners customOnClickListener) {
        this.localDataSet = localDataSet;
        this.customOnClickListener = customOnClickListener;
    }

    public OrderItemDisplayAdapterAdmin(ArrayList<OrderModel> localDataSet, ItemClickListeners customOnClickListener, Context mContext) {
        this.localDataSet = localDataSet;
        this.customOnClickListener = customOnClickListener;
        this.mContext = mContext;
        this.progressDialogue = new LoadingDialogue(mContext);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(LAYOUT_FILE, viewGroup, false);

        return new ViewHolder(view);
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {

        // TODO Set Data and Listeners

    }

    @Override
    public int getItemCount() {
        return localDataSet.size();
    }

    public void addItem(OrderModel model) {
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

    public void updateItem(int index, OrderModel newModel) {
        localDataSet.remove(index);
        localDataSet.add(index, newModel);
        notifyItemChanged(index);
    }



    public interface ItemClickListeners {
        void onFullItemClick(OrderModel orderModel);
        void onAssignDeliveryBoyButtonClick(OrderModel orderModel, String deliveryBoyUID, String deliveryBoyName, String newStatus);
        void onReleaseAppCreditsButtonClick(OrderModel orderModel, double releasingAppCredits);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final View mainView;
        private final MaterialTextView txvOrderID;
        private final MaterialTextView txvCustomerName;
        private final MaterialTextView txvAmount;
        private final MaterialTextView txvRemaining;
        private final MaterialTextView txvMethod;
        private final MaterialTextView txvRelease;
        private final MaterialTextView txvOrderStatus;
        private final MaterialTextView txvAddress;
        private final MaterialTextView txvDeliveryBoy;
        private final MaterialCardView btnAddDeliveryBoy;
        private final MaterialCardView btnReleaseAppCredits;
        private final MaterialTextView txvTimeInitiated;


        public ViewHolder(View view) {
            super(view);

            mainView = view.findViewById(R.id.admin_order_item_mainView);
            txvOrderID = view.findViewById(R.id.admin_order_item_orderID);
            txvAmount = view.findViewById(R.id.admin_order_item_amount);
            txvRemaining = view.findViewById(R.id.admin_order_item_remaining);
            txvMethod = view.findViewById(R.id.admin_order_item_method);
            txvRelease = view.findViewById(R.id.admin_order_item_releasing);
            txvOrderStatus = view.findViewById(R.id.admin_order_item_status);
            txvAddress = view.findViewById(R.id.admin_order_item_address);
            txvDeliveryBoy = view.findViewById(R.id.admin_order_item_deliveryBoy);
            btnAddDeliveryBoy = view.findViewById(R.id.admin_order_item_assign_button);
            btnReleaseAppCredits = view.findViewById(R.id.admin_order_item_release_credits_btn);
            txvCustomerName = view.findViewById(R.id.admin_order_item_customerName);
            txvTimeInitiated = view.findViewById(R.id.admin_order_item_time);

        }

        public View getMainView() {
            return mainView;
        }

        public MaterialTextView getTxvOrderID() {
            return txvOrderID;
        }

        public MaterialTextView getTxvCustomerName() {
            return txvCustomerName;
        }

        public MaterialTextView getTxvAmount() {
            return txvAmount;
        }

        public MaterialTextView getTxvRemaining() {
            return txvRemaining;
        }

        public MaterialTextView getTxvMethod() {
            return txvMethod;
        }

        public MaterialTextView getTxvRelease() {
            return txvRelease;
        }

        public MaterialTextView getTxvOrderStatus() {
            return txvOrderStatus;
        }

        public MaterialTextView getTxvAddress() {
            return txvAddress;
        }

        public MaterialTextView getTxvDeliveryBoy() {
            return txvDeliveryBoy;
        }

        public MaterialCardView getBtnAddDeliveryBoy() {
            return btnAddDeliveryBoy;
        }

        public MaterialCardView getBtnReleaseAppCredits() {
            return btnReleaseAppCredits;
        }

        public MaterialTextView getTxvTimeInitiated() {
            return txvTimeInitiated;
        }
    }
}
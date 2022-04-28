package com.sadaat.groceryapp.adapters.deliveryboy;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.sadaat.groceryapp.R;
import com.sadaat.groceryapp.models.Users.UserModel;
import com.sadaat.groceryapp.models.cart.CartItemModel;
import com.sadaat.groceryapp.models.orders.OrderModel;
import com.sadaat.groceryapp.models.orders.StatusModel;
import com.sadaat.groceryapp.temp.FirebaseDataKeys;
import com.sadaat.groceryapp.temp.order_management.OrderStatus;
import com.sadaat.groceryapp.ui.Activities.UsersBased.DeliveryBoy.MainActivityDelivery;
import com.sadaat.groceryapp.ui.Loaders.LoadingDialogue;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

public class LiveOrderDisplayAdapterDeliveryBoy extends RecyclerView.Adapter<LiveOrderDisplayAdapterDeliveryBoy.ViewHolder> {

    private final ArrayList<OrderModel> localDataSet;
    private final int LAYOUT_FILE = R.layout.delivery_item_live_order;
    private ItemClickListeners listener;
    private LoadingDialogue progressDialogue;

    public LiveOrderDisplayAdapterDeliveryBoy(ArrayList<OrderModel> localDataSet, ItemClickListeners listener, Context context) {
        this.localDataSet = localDataSet;
        this.listener = listener;
        this.progressDialogue = new LoadingDialogue(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(LAYOUT_FILE, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {

        // Data Setters
        viewHolder.getTxvOrderID().setText(localDataSet.get(viewHolder.getAdapterPosition()).getOrderID());
        viewHolder.getTxvAmount().setText(MessageFormat.format("{0} Rs.", localDataSet.get(viewHolder.getAdapterPosition()).getRemainingPaymentToPayAtDelivery()));
        viewHolder.getTxvMethod().setText(localDataSet.get(viewHolder.getAdapterPosition()).getPaymentThrough().getPaymentThroughMethod().toUpperCase());
        viewHolder.getTxvOrderLocation().setText(localDataSet.get(viewHolder.getAdapterPosition()).getDeliveryLocation());

        FirebaseFirestore
                .getInstance()
                .collection(new FirebaseDataKeys().getUsersRef())
                .document(localDataSet.get(viewHolder.getAdapterPosition()).getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            viewHolder.getTxvCustomerName().setText(Objects.requireNonNull(task.getResult().toObject(UserModel.class)).getFullName());
                            viewHolder.getTxvMobile().setText(Objects.requireNonNull(task.getResult().toObject(UserModel.class)).getMobileNumber());

                            viewHolder.getBtnCall().setOnClickListener(v -> listener.onCallButtonClick(
                                    Objects.requireNonNull(task.getResult().toObject(UserModel.class)).getMobileNumber()
                            ));

                            FirebaseStorage
                                    .getInstance(FirebaseDataKeys.STORAGE_BUCKET_ADDRESS)
                                    .getReference()
                                    .child(Objects.requireNonNull(task.getResult().toObject(UserModel.class)).getDetails().getImageReference())
                                    .getBytes(10*1024*1024).addOnSuccessListener(bytes -> {
                                viewHolder.getImageCustomer().setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
                            }).addOnFailureListener(Throwable::printStackTrace);
                        }
                    }
                });
        if (MainActivityDelivery.userImage != null) {
            viewHolder.getImageDeliveryBoy().setImageBitmap(MainActivityDelivery.userImage);
        }

        viewHolder.getBtnReceived().setOnClickListener(v -> {
            listener.onReceivedButtonClick(
                    localDataSet.get(viewHolder.getAdapterPosition()).getOrderID(),
                    new StatusModel(OrderStatus.DELIVERED, new Date()),
                    localDataSet.get(viewHolder.getAdapterPosition()).getUid(),
                    localDataSet.get(viewHolder.getAdapterPosition()).getRemainingPaymentToPayAtDelivery()
            );
        });

        viewHolder.getBtnNotReceived().setOnClickListener(v -> {
            listener.onDidNotReceiveButtonClick(
                    localDataSet.get(viewHolder.getAdapterPosition()).getOrderID(),
                    new StatusModel(OrderStatus.NOT_RECEIVED, new Date()),
                    localDataSet.get(viewHolder.getAdapterPosition()).getUid(),
                    localDataSet.get(viewHolder.getAdapterPosition()).getRemainingPaymentToPayAtDelivery(),
                    localDataSet.get(viewHolder.getAdapterPosition()).getOrderDetails().getCartItems()
            );
        });

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
        void onCallButtonClick(String mobileNumber);
        void onReceivedButtonClick(String orderID, StatusModel newStatusModel, String uid, double creditsToAddToUserMainWallet);
        void onDidNotReceiveButtonClick(String orderID, StatusModel newStatus, String uid, double creditsToARemoveFromUserPending, HashMap<String, CartItemModel> itemsToRestock);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final MaterialTextView txvOrderID;
        private final MaterialTextView txvCustomerName;
        private final MaterialTextView txvMobile;
        private final MaterialTextView txvOrderLocation;
        private final MaterialTextView txvAmount;
        private final MaterialTextView txvMethod;

        private final ImageView imageDeliveryBoy;
        private final ImageView imageCustomer;

        private final MaterialCardView btnCall;
        private final MaterialButton btnReceived;
        private final MaterialButton btnNotReceived;


        public ViewHolder(View view) {
            super(view);

            txvOrderID = view.findViewById(R.id.orderID);
            txvCustomerName = view.findViewById(R.id.customerName);
            txvMobile = view.findViewById(R.id.mobile);
            txvOrderLocation = view.findViewById(R.id.location);

            txvAmount = view.findViewById(R.id.toReceiveAmount);
            txvMethod = view.findViewById(R.id.paymentMethod);

            imageDeliveryBoy = view.findViewById(R.id.deliveryBoy_image);
            imageCustomer = view.findViewById(R.id.customer_image);

            btnCall = view.findViewById(R.id.call);

            btnReceived = view.findViewById(R.id.received);
            btnNotReceived = view.findViewById(R.id.not_received);

        }

        public MaterialTextView getTxvOrderID() {
            return txvOrderID;
        }

        public MaterialTextView getTxvCustomerName() {
            return txvCustomerName;
        }

        public MaterialTextView getTxvMobile() {
            return txvMobile;
        }

        public MaterialTextView getTxvOrderLocation() {
            return txvOrderLocation;
        }

        public MaterialTextView getTxvAmount() {
            return txvAmount;
        }

        public MaterialTextView getTxvMethod() {
            return txvMethod;
        }

        public ImageView getImageDeliveryBoy() {
            return imageDeliveryBoy;
        }

        public ImageView getImageCustomer() {
            return imageCustomer;
        }

        public MaterialCardView getBtnCall() {
            return btnCall;
        }

        public MaterialButton getBtnReceived() {
            return btnReceived;
        }

        public MaterialButton getBtnNotReceived() {
            return btnNotReceived;
        }
    }

    public ArrayList<OrderModel> getLocalDataSet() {
        return localDataSet;
    }
}
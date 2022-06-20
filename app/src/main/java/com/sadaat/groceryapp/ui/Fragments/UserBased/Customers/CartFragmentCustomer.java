package com.sadaat.groceryapp.ui.Fragments.UserBased.Customers;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textview.MaterialTextView;
import com.sadaat.groceryapp.R;
import com.sadaat.groceryapp.adapters.customer.CartItemDisplayAdapterCustomer;
import com.sadaat.groceryapp.models.Items.ItemModel;
import com.sadaat.groceryapp.models.cart.CartItemModel;
import com.sadaat.groceryapp.models.orders.OrderModel;
import com.sadaat.groceryapp.models.orders.PaymentThrough;
import com.sadaat.groceryapp.models.orders.StatusModel;
import com.sadaat.groceryapp.syncronizer.CustomerCartSynchronizer;
import com.sadaat.groceryapp.temp.UserLive;
import com.sadaat.groceryapp.temp.order_management.OrderStatus;
import com.sadaat.groceryapp.temp.order_management.PaymentMethods;
import com.sadaat.groceryapp.ui.Fragments.UserBased.Customers.passive.OrderGatewayFragmentCustomer;
import com.sadaat.groceryapp.ui.Loaders.LoadingDialogue;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;

public class CartFragmentCustomer extends Fragment
        implements CartItemDisplayAdapterCustomer.ItemClickListeners, View.OnClickListener {

    AlertDialog.Builder dialogueBuilder;
    AlertDialog checkoutPopupDialogueBox;
    View popupView;
    PopupViewHolder holder;
    private RecyclerView recyclerView;
    private CartItemDisplayAdapterCustomer cartAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private LinearLayout layoutEmptyCart, layoutFilledCart;
    private MaterialButton emptyCartExploreButton;
    private MaterialTextView txvName;
    private MaterialTextView txvLastUpdateTimestamp;
    private MaterialTextView txvEmail;
    private MaterialTextView txvMobile;
    private MaterialTextView txvLocation;
    private MaterialTextView txvTotalRetail;
    private MaterialTextView txvTotalDiscounted;
    private MaterialTextView txvTotalCOD_SC;
    private MaterialTextView txvTotalCard_ED;
    private MaterialTextView txvYouAreSaving;
    private MaterialTextView txvByPayingThroughCard;
    private MaterialButton btnCheckoutMainFragment;
    private OrderModel orderModel;
    private boolean paymentThroughCOD = true;


    public CartFragmentCustomer() {

    }

    public static CartFragmentCustomer newInstance() {
        return new CartFragmentCustomer();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.customer_fragment_cart, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init_(view);

        UserLive.currentLoggedInUser.getCart().eliminateCartByLatestStock();

        updateFragment();
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(cartAdapter);

        emptyCartExploreButton.setOnClickListener(this);

        btnCheckoutMainFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updatePopup();
                checkoutPopupDialogueBox.show();
            }
        });

        holder.getRadioGroupPaymentType().setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (((RadioButton) holder.getRadioButtonCard()).isChecked()) {
                    paymentThroughCOD = false;

                } else if (((RadioButton) holder.getRadioButtonCOD()).isChecked()) {
                    paymentThroughCOD = true;
                }
            }
        });

        holder.getmSwitch().setOnCheckedChangeListener((compoundButton, b) -> {


            long maxCredits = (long) (Long.parseLong(holder.getTxvTotalPayable().getText().toString()) / 2);

            if (b) {
                Toast.makeText(CartFragmentCustomer.this.requireActivity(), "You are about to use your previous stored reward points from " + UserLive.currentLoggedInUser.getCredits().getOwningCredits() + " Credits. Max Use In this Order " + (Math.min(UserLive.currentLoggedInUser.getCredits().getOwningCredits(), maxCredits)) + " Rs.", Toast.LENGTH_LONG).show();
            }

            long toShowCredits;

            if (maxCredits > UserLive.currentLoggedInUser.getCredits().getOwningCredits()) {
                if (b) {
                    toShowCredits = (long) UserLive.currentLoggedInUser.getCredits().getOwningCredits();
                } else {
                    toShowCredits = 0;
                }
            } else {
                if (b) {
                    toShowCredits = (long) maxCredits;
                } else {
                    toShowCredits = 0;
                }
            }

            holder.getTxvPartialAppCredits().setText(MessageFormat.format("{0}", toShowCredits));

            updatePopup();
        });

        holder.getButtonProceed().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                orderModel.setOrderDetails(UserLive.currentLoggedInUser.getCart());
                orderModel.setTotalOrderAmountInRetail(UserLive.currentLoggedInUser.getCart().getNetTotalRetailPrice());
                orderModel.setCurrentDeliveryBoyUID("");
                orderModel.setReceivingStatus("");
                orderModel.setReleasingAppCredits(
                        (paymentThroughCOD) ? Double.parseDouble(holder.getTxvCreditsForCOD().getText().toString()) :
                                Double.parseDouble(holder.getTxvCreditsForCard().getText().toString())
                );

                orderModel.setPaymentThrough(new PaymentThrough(
                        ((paymentThroughCOD) ? PaymentMethods.COD : PaymentMethods.CARD),
                        Long.parseLong(holder.getTxvPartialAppCredits().getText().toString()),
                        ""
                ));

                orderModel.setUid(UserLive.currentLoggedInUser.getUID());

                ArrayList<StatusModel> updates = new ArrayList<StatusModel>();
                updates.add(new StatusModel(OrderStatus.INITIATED, new Date()));

                orderModel.setStatusUpdates(updates);
                orderModel.setComplaintID("");

                orderModel.setTotalOrderAmountInRetail(Double.parseDouble(holder.getTxvNetTotal().getText().toString()));
                orderModel.setRemainingPaymentToPayAtDelivery(
                        (paymentThroughCOD) ?
                                Double.parseDouble(holder.getTxvNetTotal().getText().toString()) : 0
                );

                orderModel.setDeliveryLocation(UserLive.currentLoggedInUser.getDetails().getAddress().toString());

                orderModel.setReleasedAppCredits(0.0);

                orderModel.setCurrentStatus(OrderStatus.INITIATED);

                checkoutPopupDialogueBox.dismiss();

                getParentFragmentManager()
                        .beginTransaction()
                        .replace(R.id.flFragmentCustomer, OrderGatewayFragmentCustomer.newInstance(orderModel))
                        .addToBackStack("cart")
                        .commit();

            }
        });

    }

    private void init_(View v) {
        recyclerView = v.findViewById(R.id.recycler_cart_items);
        cartAdapter = new CartItemDisplayAdapterCustomer(CartFragmentCustomer.this.requireActivity(), this);
        layoutManager = new LinearLayoutManager(CartFragmentCustomer.this.requireActivity());

        layoutEmptyCart = v.findViewById(R.id.customer_cart_layout_empty);
        layoutFilledCart = v.findViewById(R.id.customer_cart_layout_nonempty);

        emptyCartExploreButton = layoutEmptyCart.findViewById(R.id.explore_button);

        txvName = v.findViewById(R.id.txv_cart_item_name);
        txvLastUpdateTimestamp = v.findViewById(R.id.txv_cart_item_last_update_time);
        txvEmail = v.findViewById(R.id.txv_cart_item_email);
        txvMobile = v.findViewById(R.id.txv_cart_item_mobile);
        txvLocation = v.findViewById(R.id.txv_cart_item_location);
        txvTotalRetail = v.findViewById(R.id.txv_cart_item_total_net_retail);
        txvTotalDiscounted = v.findViewById(R.id.txv_cart_item_total_net_sale);
        txvTotalCOD_SC = v.findViewById(R.id.txv_cart_item_total_net_COD_SC);
        txvTotalCard_ED = v.findViewById(R.id.txv_cart_item_total_net_CD);
        txvYouAreSaving = v.findViewById(R.id.you_are_saving);
        txvByPayingThroughCard = v.findViewById(R.id.by_paying_through_card);
        btnCheckoutMainFragment = v.findViewById(R.id.checkout);

        dialogueBuilder = new AlertDialog.Builder(requireActivity());

        popupView = this.getLayoutInflater().inflate(R.layout.customer_popup_order_checkout_payment_option, null, false);
        dialogueBuilder.setView(popupView);
        holder = new PopupViewHolder(popupView);

        checkoutPopupDialogueBox = dialogueBuilder.create();
        orderModel = new OrderModel();

    }

    @Override
    public CartItemModel indicateItemCountChange(ItemModel item, int quantity) {
        return new CartItemModel(item, quantity);
    }

    public class MyRunnable implements Runnable {

        @Override
        public void run() {
            try {
                Thread.sleep(50);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        public void getCalled(LoadingDialogue d){
            d.dismiss();
            updateFragment();
        }
    }

    @Override
    public void prepareCart(CartItemModel cartItemModel, LoadingDialogue progressDialogue) {
        UserLive.currentLoggedInUser.getCart().modifyCartItem(cartItemModel);

        CustomerCartSynchronizer.synchronize(UserLive.currentLoggedInUser.getUID(),
                UserLive.currentLoggedInUser.getCart());

        cartAdapter.notifyChange();

        MyRunnable runnable = new MyRunnable();

        Thread thread = new Thread(runnable);
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        runnable.getCalled(progressDialogue);

        //Log.e("CART", UserLive.currentLoggedInUser.getCart().toString());
    }

    private void updateFragment() {
        if (UserLive.currentLoggedInUser.getCart().getCartItems().keySet().size() == 0) {
            showLayoutIfCartHasItems(false);
        } else {
            showLayoutIfCartHasItems(true);
            updateContentValues();
        }
    }

    private void updateContentValues() {
        txvName.setText(UserLive.currentLoggedInUser.getFullName());
        //TODO Update Timestamp Module
        txvEmail.setText(UserLive.currentLoggedInUser.getEmailAddress());
        txvMobile.setText(UserLive.currentLoggedInUser.getMobileNumber());
        if (UserLive.currentLoggedInUser.getDetails() != null) {
            txvLocation.setText(UserLive.currentLoggedInUser.getDetails().getAddress().toString());
        }

        double a = (Double) UserLive.currentLoggedInUser.getCart().getNetTotalRetailPrice();
        double b = (Double) UserLive.currentLoggedInUser.getCart().getNetTotalSalePrice();

        txvLastUpdateTimestamp.setText(UserLive.currentLoggedInUser.getCart().getTimeStamp().toString());

        txvTotalRetail.setText("" + a);
        txvTotalDiscounted.setText("" + b);

        txvTotalCOD_SC.setText("" + (Double) UserLive.currentLoggedInUser.getCart().getNetTotalSecurityCharges());

        txvTotalCard_ED.setText("" + (Double) UserLive.currentLoggedInUser.getCart().getNetTotalCardDiscount());

        String YOU_ARE_SAVING = "You are saving Rs. ";
        txvYouAreSaving.setText(YOU_ARE_SAVING + (a - b));
        String BY_PAYING_THROUGH_CARD = "By Paying through card, you will save extra Rs. ";
        txvByPayingThroughCard.setText(BY_PAYING_THROUGH_CARD + (Double) UserLive.currentLoggedInUser.getCart().getNetTotalCardDiscount() + " in your AppCredits after completing the order");

    }

    private void showLayoutIfCartHasItems(boolean b) {
        if (b) {
            layoutFilledCart.setVisibility(View.VISIBLE);
            layoutEmptyCart.setVisibility(View.GONE);
        } else {
            layoutFilledCart.setVisibility(View.GONE);
            layoutEmptyCart.setVisibility(View.VISIBLE);
        }
    }

    @SuppressLint("SetTextI18n")
    private void updatePopup() {
        holder.getTxvTotalPayable().setText("" + ((long) UserLive.currentLoggedInUser.getCart().getNetTotalRetailPrice() + (long) UserLive.currentLoggedInUser.getCart().getNetTotalSecurityCharges()));
        long totalPayable = Long.parseLong(holder.getTxvTotalPayable().getText().toString());
        long partialCredits = Long.parseLong(holder.getTxvPartialAppCredits().getText().toString());
        holder.getTxvNetTotal().setText("" + (totalPayable - partialCredits));

        holder.getTxvCreditsForCOD().setText(
                "" + (
                        (long) UserLive.currentLoggedInUser.getCart().getNetTotalRetailPrice() -
                                (long) UserLive.currentLoggedInUser.getCart().getNetTotalSalePrice() +
                                (long) UserLive.currentLoggedInUser.getCart().getNetTotalSecurityCharges()
                )
        );

        holder.getTxvCreditsForCard().setText(
                "" + (
                        (long) UserLive.currentLoggedInUser.getCart().getNetTotalRetailPrice() -
                                (long) UserLive.currentLoggedInUser.getCart().getNetTotalSalePrice() +
                                (long) UserLive.currentLoggedInUser.getCart().getNetTotalSecurityCharges() +
                                (long) UserLive.currentLoggedInUser.getCart().getNetTotalCardDiscount()
                )
        );

        holder.getTxvLocation().setText(UserLive.currentLoggedInUser.getDetails().getAddress().toString());

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == emptyCartExploreButton.getId()) {
            ((BottomNavigationView) requireActivity().findViewById(R.id.bottomNavigationView)).setSelectedItemId(R.id.nav_customer_categories);
            requireActivity().getSupportFragmentManager().getFragments().remove(this);
        }
    }

    private class PopupViewHolder {
        MaterialTextView txvTotalPayable;
        MaterialTextView txvPartialAppCredits;
        MaterialTextView txvNetTotal;
        MaterialTextView txvCreditsForCOD;
        MaterialTextView txvCreditsForCard;
        MaterialTextView txvLocation;

        SwitchMaterial mSwitch;
        RadioGroup radioGroupPaymentType;
        RadioButton radioButtonCard;
        RadioButton radioButtonCOD;

        MaterialButton buttonProceed;

        public PopupViewHolder(View pV) {
            //Initializations
            txvTotalPayable = pV.findViewById(R.id.txv_total_payable);
            txvPartialAppCredits = pV.findViewById(R.id.txv_app_cr);
            txvNetTotal = pV.findViewById(R.id.txv_net_total);
            txvCreditsForCOD = pV.findViewById(R.id.txv_cod_credits);
            txvCreditsForCard = pV.findViewById(R.id.txv_credits_card);
            txvLocation = pV.findViewById(R.id.txv_location);

            mSwitch = pV.findViewById(R.id.mSwitch);
            radioGroupPaymentType = pV.findViewById(R.id.rdg);
            radioButtonCard = pV.findViewById(R.id.rdb_card);
            radioButtonCOD = pV.findViewById(R.id.rdb_cod);

            buttonProceed = pV.findViewById(R.id.btn_proceed);
        }

        public MaterialTextView getTxvTotalPayable() {
            return txvTotalPayable;
        }

        public MaterialTextView getTxvPartialAppCredits() {
            return txvPartialAppCredits;
        }

        public MaterialTextView getTxvNetTotal() {
            return txvNetTotal;
        }

        public MaterialTextView getTxvCreditsForCOD() {
            return txvCreditsForCOD;
        }

        public MaterialTextView getTxvCreditsForCard() {
            return txvCreditsForCard;
        }

        public MaterialTextView getTxvLocation() {
            return txvLocation;
        }

        public SwitchMaterial getmSwitch() {
            return mSwitch;
        }

        public RadioGroup getRadioGroupPaymentType() {
            return radioGroupPaymentType;
        }

        public RadioButton getRadioButtonCard() {
            return radioButtonCard;
        }

        public RadioButton getRadioButtonCOD() {
            return radioButtonCOD;
        }

        public MaterialButton getButtonProceed() {
            return buttonProceed;
        }
    }
}
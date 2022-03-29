package com.sadaat.groceryapp.ui.Fragments.UserBased.Customers;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;
import com.sadaat.groceryapp.R;
import com.sadaat.groceryapp.adapters.customer.CartItemDisplayAdapterCustomer;
import com.sadaat.groceryapp.models.ItemModel;
import com.sadaat.groceryapp.models.cart.CartItemModel;
import com.sadaat.groceryapp.syncronizer.CustomerCartSynchronizer;
import com.sadaat.groceryapp.temp.UserLive;
import com.sadaat.groceryapp.ui.Loaders.LoadingDialogue;

public class CartFragmentCustomer extends Fragment implements CartItemDisplayAdapterCustomer.ItemClickListeners, View.OnClickListener {
    private final String YOU_ARE_SAVING = "You are saving Rs. ";
    private final String BY_PAYING_THROUGH_CARD = "By Paying through card, you will save extra Rs. ";
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
    }

    @Override
    public CartItemModel indicateItemCountChange(ItemModel item, int quantity) {
        return new CartItemModel(item, quantity);
    }

    @Override
    public void prepareCart(CartItemModel cartItemModel, LoadingDialogue progressDialogue) {
        UserLive.currentLoggedInUser.getCart().modifyCartItem(cartItemModel);

        CustomerCartSynchronizer.synchronize(UserLive.currentLoggedInUser.getUID(),
                UserLive.currentLoggedInUser.getCart());

        cartAdapter.notifyChange();


        Thread runnable = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    updateFragment();
                    Thread.sleep(800);
                    progressDialogue.dismiss();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        });
        runnable.start();

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

        double a = (Double)UserLive.currentLoggedInUser.getCart().getNetTotalRetailPrice();
        double b = (Double)UserLive.currentLoggedInUser.getCart().getNetTotalSalePrice();

        txvTotalRetail.setText(""+a);
        txvTotalDiscounted.setText(""+b);

        txvTotalCOD_SC.setText(""+(Double)UserLive.currentLoggedInUser.getCart().getNetTotalSecurityCharges());

        txvTotalCard_ED.setText(""+(Double)UserLive.currentLoggedInUser.getCart().getNetTotalCardDiscount());

        txvYouAreSaving.setText(YOU_ARE_SAVING + (a-b));
        txvByPayingThroughCard.setText(BY_PAYING_THROUGH_CARD+ (Double)UserLive.currentLoggedInUser.getCart().getNetTotalCardDiscount()+" in your AppCredits after completing the order");

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

    @Override
    public void onClick(View view) {
        if (view.getId() == emptyCartExploreButton.getId()) {
            ((BottomNavigationView) requireActivity().findViewById(R.id.bottomNavigationView)).setSelectedItemId(R.id.nav_customer_categories);
            requireActivity().getSupportFragmentManager().getFragments().remove(this);
        }
    }
}
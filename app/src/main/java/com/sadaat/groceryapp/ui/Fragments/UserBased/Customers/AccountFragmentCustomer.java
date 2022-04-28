package com.sadaat.groceryapp.ui.Fragments.UserBased.Customers;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.sadaat.groceryapp.R;
import com.sadaat.groceryapp.models.Users.UserModel;
import com.sadaat.groceryapp.temp.FirebaseDataKeys;
import com.sadaat.groceryapp.temp.UserLive;
import com.sadaat.groceryapp.ui.Activities.GenericForAll.LoginNavigatorActivity;
import com.sadaat.groceryapp.ui.Fragments.UserBased.Customers.passive.OrdersFragmentCustomer;

import java.text.MessageFormat;

public class AccountFragmentCustomer extends Fragment {

    ImageView accountDP;

    MaterialTextView txvUserAddress;
    MaterialTextView txvCreditsInWallet;
    MaterialTextView txvCreditsInPending;

    MaterialCardView cardAppCredits;
    MaterialCardView cardOrders;
    MaterialCardView cardWallet;
    MaterialCardView cardAddress;
    MaterialCardView cardSuggestionsAndComplaints;
    MaterialCardView cardHelpAndSupport;
    MaterialCardView cardSettings;
    MaterialCardView cardSignOut;
    MaterialCardView cardCallToCustomerSupport;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.customer_fragment_account, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initialize(view);

        FirebaseFirestore.getInstance()
                .collection(new FirebaseDataKeys().getUsersRef())
                .document(UserLive.currentLoggedInUser.getUID())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> t) {
                        if (t.isSuccessful()) {
                            UserLive.currentLoggedInUser = t.getResult().toObject(UserModel.class);
                            assert UserLive.currentLoggedInUser != null;
                            Log.e("USER", UserLive.currentLoggedInUser.toString());



                        }
                    }
                });

        if (UserLive.currentLoggedInUser != null) {
            setCreditsToUser();
            setImageToUser();
            setUserAddress();
        }
        listeners();

    }

    private void setCreditsToUser() {

        txvCreditsInPending.setText(MessageFormat.format("{0}", UserLive.currentLoggedInUser.getCredits().getPendingCredits()));
        txvCreditsInWallet.setText(MessageFormat.format("{0}", UserLive.currentLoggedInUser.getCredits().getOwningCredits()));
    }

    private void listeners() {

        cardSignOut.setOnClickListener(view -> new AlertDialog.Builder(AccountFragmentCustomer.this.requireActivity())
                .setTitle("Signing Out")
                .setMessage("Are you sure you want to sign out?")

                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton("Yes", (dialog, which) -> {
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(AccountFragmentCustomer.this.requireActivity(), LoginNavigatorActivity.class));
                    UserLive.currentLoggedInUser = null;
                    AccountFragmentCustomer.this.requireActivity().finish();
                })

                // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton("No", (dialogInterface, i) -> dialogInterface.dismiss())
                .setIcon(R.mipmap.logo)
                .show());

        cardOrders.setOnClickListener(v -> {
            requireActivity()
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.flFragmentCustomer, OrdersFragmentCustomer.newInstance())
                    .addToBackStack("account")
                    .commit();
        });
    }

    private void setUserAddress() {
        txvUserAddress.setText(UserLive.currentLoggedInUser
                .getDetails()
                .getAddress().toString());
    }

    private void setImageToUser() {

        if (!UserLive.currentLoggedInUser
                .getDetails()
                .getImageReference().equals("")) {

            StorageReference imgRef = FirebaseStorage
                    .getInstance(FirebaseDataKeys.STORAGE_BUCKET_ADDRESS)
                    .getReference()
                    .child(UserLive.currentLoggedInUser
                            .getDetails()
                            .getImageReference());

            final long ONE_MEGABYTE = 1024 * 1024;

            imgRef.getBytes(10 * ONE_MEGABYTE).addOnSuccessListener(bytes -> accountDP.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length))).addOnFailureListener(exception -> {
                //Toast.makeText(mContext, "Image Load Failed, \n Leave it or use a new one", Toast.LENGTH_SHORT).show();
            });
        }

    }

    void initialize(View v) {
        accountDP = v.findViewById(R.id.card_add_users_dp_imgv);
        txvUserAddress = v.findViewById(R.id.user_address);
        txvCreditsInWallet = v.findViewById(R.id.txv_appcredits_wallet);
        txvCreditsInPending = v.findViewById(R.id.txv_appcredits_pending);

        cardAppCredits = v.findViewById(R.id.card_app_credits);
        cardOrders = v.findViewById(R.id.customer_account_card_orders);
        cardWallet = v.findViewById(R.id.customer_account_card_wallet);

        cardAddress = v.findViewById(R.id.customer_account_card_address);
        cardSuggestionsAndComplaints = v.findViewById(R.id.customer_account_card_suggestions);
        cardHelpAndSupport = v.findViewById(R.id.customer_account_card_help);
        cardSettings = v.findViewById(R.id.customer_account_card_settings);
        cardSignOut = v.findViewById(R.id.customer_account_card_logout);
        cardCallToCustomerSupport = v.findViewById(R.id.call_to_customer_support);
    }


}
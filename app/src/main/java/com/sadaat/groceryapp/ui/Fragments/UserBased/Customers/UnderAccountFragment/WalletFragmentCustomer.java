package com.sadaat.groceryapp.ui.Fragments.UserBased.Customers.UnderAccountFragment;

import android.animation.ValueAnimator;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.sadaat.groceryapp.R;
import com.sadaat.groceryapp.models.orders.OrderModel;
import com.sadaat.groceryapp.temp.FirebaseDataKeys;
import com.sadaat.groceryapp.temp.UserLive;
import com.sadaat.groceryapp.temp.order_management.OrderStatus;

import java.text.MessageFormat;

public class WalletFragmentCustomer extends Fragment {


    public WalletFragmentCustomer() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static WalletFragmentCustomer newInstance() {
        return new WalletFragmentCustomer();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.customer_fragment_wallet, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FirebaseFirestore
                .getInstance()
                .collection(new FirebaseDataKeys().getOrdersRef())
                .whereEqualTo("uid", UserLive.currentLoggedInUser.getUID())
                .whereEqualTo("currentStatus", OrderStatus.DELIVERED)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            double sum = 0;
                            for (QueryDocumentSnapshot d :
                                    task.getResult()) {
                                sum += d.toObject(OrderModel.class).getTotalOrderAmountInRetail();
                            }
                            animateTextView(0, (int) sum, view.findViewById(R.id.txv_total_spent));
                            animateTextView(0, (int) UserLive.currentLoggedInUser.getCredits().getOwningCredits(), view.findViewById(R.id.txv_in_wallet_credits));
                        }
                    }
                });

    }

    public void animateTextView(int initialValue, int finalValue, final MaterialTextView textview) {

        ValueAnimator valueAnimator = ValueAnimator.ofInt(initialValue, finalValue);
        valueAnimator.setDuration(1500);

        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                textview.setText(MessageFormat.format("{0}", valueAnimator.getAnimatedValue().toString()));

            }
        });
        valueAnimator.start();

    }
}
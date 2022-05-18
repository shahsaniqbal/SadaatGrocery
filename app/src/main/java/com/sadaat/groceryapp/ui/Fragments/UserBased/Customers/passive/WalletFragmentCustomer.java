package com.sadaat.groceryapp.ui.Fragments.UserBased.Customers.passive;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sadaat.groceryapp.R;

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
}
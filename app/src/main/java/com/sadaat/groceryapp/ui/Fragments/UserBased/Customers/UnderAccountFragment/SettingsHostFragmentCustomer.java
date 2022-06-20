package com.sadaat.groceryapp.ui.Fragments.UserBased.Customers.UnderAccountFragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.card.MaterialCardView;
import com.sadaat.groceryapp.R;
import com.sadaat.groceryapp.ui.Fragments.UserBased.Customers.UnderSettingsFragment.PasswordChangeFragmentCustomer;
import com.sadaat.groceryapp.ui.Fragments.UserBased.Customers.UnderSettingsFragment.UpdateProfileFragmentCustomer;

public class SettingsHostFragmentCustomer extends Fragment {

    MaterialCardView cardChangeProfile;
    MaterialCardView cardChangePassword;


    public SettingsHostFragmentCustomer() {
        // Required empty public constructor
    }
    public static SettingsHostFragmentCustomer newInstance() {
        return new SettingsHostFragmentCustomer();

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.customer_fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        cardChangeProfile = view.findViewById(R.id.change_profile_details);
        cardChangePassword = view.findViewById(R.id.change_password);


        cardChangeProfile.setOnClickListener(v->{
            requireActivity()
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.flFragmentCustomer, UpdateProfileFragmentCustomer.newInstance())
                    .addToBackStack("settings")
                    .commit();
        });


        cardChangePassword.setOnClickListener(v->{
            requireActivity()
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.flFragmentCustomer, PasswordChangeFragmentCustomer.newInstance())
                    .addToBackStack("settings")
                    .commit();
        });

    }
}
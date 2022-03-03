package com.sadaat.groceryapp.ui.Fragments.UserBased.Admin.UnderListingFragmentChildSuper;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sadaat.groceryapp.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DealsListFragmentAdmin#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DealsListFragmentAdmin extends Fragment {

    public DealsListFragmentAdmin() {
        // Required empty public constructor
    }

    public static DealsListFragmentAdmin newInstance() {
        DealsListFragmentAdmin fragment = new DealsListFragmentAdmin();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.admin_fragment_deals_list, container, false);
    }
    @Override
    public void onPause() {
        super.onPause();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle(null);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle("Listing -> Deals");
    }

}
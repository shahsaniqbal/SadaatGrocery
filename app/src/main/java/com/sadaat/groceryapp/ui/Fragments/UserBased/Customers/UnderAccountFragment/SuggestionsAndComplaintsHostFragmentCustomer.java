package com.sadaat.groceryapp.ui.Fragments.UserBased.Customers.UnderAccountFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.card.MaterialCardView;
import com.sadaat.groceryapp.R;
import com.sadaat.groceryapp.ui.Fragments.UserBased.Customers.UnderSuggestionsAndComplaints.ComplaintsFragmentCustomer;
import com.sadaat.groceryapp.ui.Fragments.UserBased.Customers.UnderSuggestionsAndComplaints.SuggestionsFragmentCustomer;

public class SuggestionsAndComplaintsHostFragmentCustomer extends Fragment {

    MaterialCardView cardSuggestions;
    MaterialCardView cardComplaints;
    FrameLayout layoutFragmentLive;

    public SuggestionsAndComplaintsHostFragmentCustomer() {
        // Required empty public constructor
    }

    public static SuggestionsAndComplaintsHostFragmentCustomer newInstance() {
        return new SuggestionsAndComplaintsHostFragmentCustomer();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.customer_fragment_suggestions_and_complaints_host, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        cardSuggestions = view.findViewById(R.id.card_suggestions);
        cardComplaints = view.findViewById(R.id.card_complaints);

        layoutFragmentLive = view.findViewById(R.id.area_suggestions_complaints_fragment);

        cardSuggestions.setOnClickListener(view1 -> caller(SuggestionsFragmentCustomer.newInstance()));

        cardComplaints.setOnClickListener(view1 -> caller(ComplaintsFragmentCustomer.newInstance()));

    }

    private void caller(Fragment instance) {
        layoutFragmentLive.removeAllViews();

        requireActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(layoutFragmentLive.getId(), instance,null)
                .commit();
    }


}
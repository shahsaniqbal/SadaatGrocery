package com.sadaat.groceryapp.ui.Fragments.UserBased.Admin.UnderHomeFragmentSuper;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.sadaat.groceryapp.R;
import com.sadaat.groceryapp.ui.Fragments.UserBased.Admin.UnderListingFragmentChildSuper.CategoriesListFragmentAdmin;
import com.sadaat.groceryapp.ui.Fragments.UserBased.Admin.UnderListingFragmentChildSuper.SlidesListFragmentAdmin;
import com.sadaat.groceryapp.ui.Fragments.UserBased.Admin.UnderListingFragmentChildSuper.ItemsListFragmentAdmin;
import com.sadaat.groceryapp.ui.Fragments.UserBased.Admin.UnderListingFragmentChildSuper.LocationsListFragmentAdmin;

public class ListingFragmentAdmin extends Fragment implements View.OnClickListener {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    View root;
    private String mParam1;
    private String mParam2;

    /*public ListingFragmentAdmin() {
        // Required empty public constructor
    }*/

    public static ListingFragmentAdmin newInstance(String param1, String param2) {
        ListingFragmentAdmin fragment = new ListingFragmentAdmin();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.admin_fragment_listing_v2, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        root = view;

        view.findViewById(R.id.admin_card_categories).setOnClickListener(this);
/*
        view.findViewById(R.id.admin_card_subcategories).setOnClickListener(this);
*/
        view.findViewById(R.id.admin_card_items).setOnClickListener(this);
        view.findViewById(R.id.admin_card_deals).setOnClickListener(this);
        view.findViewById(R.id.admin_card_location).setOnClickListener(this);

        setActionClicksBehavior(R.id.admin_card_categories);

        //TODO Set Action for Back Buttons on Fragment // Comment By Ahsan Iqbal 23-12-2021
        /*
        getActivity().getOnBackPressedDispatcher().addCallback(getActivity(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                caller(ListingFragmentAdmin.class);
            }
        });
        */

    }

    @Override
    public void onClick(View v) {
        setActionClicksBehavior(v.getId());
    }

    private void caller(Class c) {


        if (getActivity() != null) {
            FrameLayout fl;
            fl = root.findViewById(R.id.admin_listing_frame);
            fl.removeAllViews();
        }

        getActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.admin_listing_frame, c, null)
                .setCustomAnimations(R.anim.slide_in, R.anim.slide_out)
                .commit();

    }


    @Override
    public void onPause() {
        super.onPause();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle(null);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle("Listing");
    }


    // This method is being used for Highlights and default behavior settings for FrameLayout
    void setActionClicksBehavior(int viewID) {


        switch (viewID) {
            case R.id.admin_card_categories:
                caller(CategoriesListFragmentAdmin.class);
                break;
            case R.id.admin_card_deals:
                caller(SlidesListFragmentAdmin.class);
                break;
            case R.id.admin_card_items:
                caller(ItemsListFragmentAdmin.class);
                break;
            case R.id.admin_card_location:
                caller(LocationsListFragmentAdmin.class);
                break;
        }

    }
}
package com.sadaat.groceryapp.ui.SuperFragments.Admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.sadaat.groceryapp.R;
import com.sadaat.groceryapp.ui.Fragments.UserBased.Admin.UnderHomeFragmentSuper.ListingFragmentAdmin;
import com.sadaat.groceryapp.ui.Fragments.UserBased.Admin.UnderHomeFragmentSuper.OrdersFragmentAdmin;
import com.sadaat.groceryapp.ui.Fragments.UserBased.Admin.UnderHomeFragmentSuper.TrackingFragmentAdmin;

public class HomeFragment extends Fragment {

    BottomNavigationView bottomNavigationView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.admin_fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bottomNavigationView = view.findViewById(R.id.bottomNavigationView);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                FrameLayout fl = view.findViewById(R.id.flFragment);
                fl.removeAllViews();

                switch (item.getItemId()) {
                    case R.id.nav_admin_home_orders:
                        getChildFragmentManager()
                                .beginTransaction()
                                .replace(R.id.flFragment, OrdersFragmentAdmin.class, null)
                                .commit();
                        return true;

                    case R.id.nav_admin_home_listing:
                        getChildFragmentManager()
                                .beginTransaction()
                                .replace(R.id.flFragment, ListingFragmentAdmin.class, null)
                                .commit();
                        return true;

                    case R.id.nav_admin_home_tracking:
                        getChildFragmentManager()
                                .beginTransaction()
                                .replace(R.id.flFragment, TrackingFragmentAdmin.class, null)
                                .commit();
                        return true;

                }


                return false;
            }
        });
        bottomNavigationView.setSelectedItemId(R.id.nav_admin_home_orders);

    }

}
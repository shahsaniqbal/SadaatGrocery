package com.sadaat.groceryapp.ui.Fragments.UserBased.Admin.host;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.sadaat.groceryapp.R;
import com.sadaat.groceryapp.ui.Fragments.UserBased.Admin.UnderResponsesFragmentSuper.ComplaintsFragmentAdmin;
import com.sadaat.groceryapp.ui.Fragments.UserBased.Admin.UnderResponsesFragmentSuper.QueriesFragmentAdmin;
import com.sadaat.groceryapp.ui.Fragments.UserBased.Admin.UnderResponsesFragmentSuper.SuggestionsFragmentAdmin;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ResponsesFragmentAdmin#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ResponsesFragmentAdmin extends Fragment {

    BottomNavigationView bottomNavigationView;

    public ResponsesFragmentAdmin() {
        // Required empty public constructor
    }

    public static ResponsesFragmentAdmin newInstance(String param1, String param2) {
        return new ResponsesFragmentAdmin();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.admin_fragment_responses, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ComplaintsFragmentAdmin complaintsFragmentAdmin = new ComplaintsFragmentAdmin();
        QueriesFragmentAdmin queriesFragmentAdmin = new QueriesFragmentAdmin();
        SuggestionsFragmentAdmin suggestionsFragmentAdmin = new SuggestionsFragmentAdmin();


        bottomNavigationView = view.findViewById(R.id.bottomNavigationView);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_admin_response_complaints:
                        getChildFragmentManager().beginTransaction().replace(R.id.flFragmentChild, complaintsFragmentAdmin).commit();
                        return true;

/*
                    case R.id.nav_admin_response_queries:
                        getChildFragmentManager().beginTransaction().replace(R.id.flFragment, queriesFragmentAdmin).commit();
                        return true;
*/

                    case R.id.nav_admin_response_suggestions:
                        getChildFragmentManager().beginTransaction().replace(R.id.flFragmentChild, suggestionsFragmentAdmin).commit();
                        return true;

                }
                return false;
            }
        });
        bottomNavigationView.setSelectedItemId(R.id.nav_admin_response_suggestions);

    }

}
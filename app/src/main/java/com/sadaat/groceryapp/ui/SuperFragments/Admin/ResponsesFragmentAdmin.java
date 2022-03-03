package com.sadaat.groceryapp.ui.SuperFragments.Admin;

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
import com.sadaat.groceryapp.ui.Fragments.UserBased.Admin.UnderResponsesFragmentSuper.FeedbacksFragmentAdmin;
import com.sadaat.groceryapp.ui.Fragments.UserBased.Admin.UnderResponsesFragmentSuper.QueriesFragmentAdmin;
import com.sadaat.groceryapp.ui.Fragments.UserBased.Admin.UnderResponsesFragmentSuper.SuggestionsFragmentAdmin;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ResponsesFragmentAdmin#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ResponsesFragmentAdmin extends Fragment {

    BottomNavigationView bottomNavigationView;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ResponsesFragmentAdmin() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ResponsesFragmentAdmin.
     */
    // TODO: Rename and change types and number of parameters
    public static ResponsesFragmentAdmin newInstance(String param1, String param2) {
        ResponsesFragmentAdmin fragment = new ResponsesFragmentAdmin();
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
        return inflater.inflate(R.layout.admin_fragment_responses, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FeedbacksFragmentAdmin feedbacksFragmentAdmin = new FeedbacksFragmentAdmin();
        QueriesFragmentAdmin queriesFragmentAdmin = new QueriesFragmentAdmin();
        SuggestionsFragmentAdmin suggestionsFragmentAdmin = new SuggestionsFragmentAdmin();


        bottomNavigationView = view.findViewById(R.id.bottomNavigationView);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.nav_admin_response_feedback:
                        getChildFragmentManager().beginTransaction().replace(R.id.flFragment, feedbacksFragmentAdmin).commit();
                        return true;

                    case R.id.nav_admin_response_queries:
                        getChildFragmentManager().beginTransaction().replace(R.id.flFragment, queriesFragmentAdmin).commit();
                        return true;

                    case R.id.nav_admin_response_suggestions:
                        getChildFragmentManager().beginTransaction().replace(R.id.flFragment, suggestionsFragmentAdmin).commit();
                        return true;

                }


                return false;
            }
        });
        bottomNavigationView.setSelectedItemId(R.id.nav_admin_home_orders);

    }

}
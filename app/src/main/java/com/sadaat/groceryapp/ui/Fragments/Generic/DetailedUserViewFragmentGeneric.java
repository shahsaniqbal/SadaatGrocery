package com.sadaat.groceryapp.ui.Fragments.Generic;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sadaat.groceryapp.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DetailedUserViewFragmentGeneric#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DetailedUserViewFragmentGeneric extends Fragment {

    private static final String ARG_PARAM = "uid";

    private String mUID;

    public DetailedUserViewFragmentGeneric() {
        // Required empty public constructor
    }

    public static DetailedUserViewFragmentGeneric newInstance(String mUID) {
        DetailedUserViewFragmentGeneric fragment = new DetailedUserViewFragmentGeneric();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM, mUID);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mUID = getArguments().getString(ARG_PARAM);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.generic_fragment_detailed_user_view, container, false);
    }
}
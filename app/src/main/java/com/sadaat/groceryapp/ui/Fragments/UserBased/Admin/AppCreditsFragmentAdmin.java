package com.sadaat.groceryapp.ui.Fragments.UserBased.Admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.sadaat.groceryapp.R;

public class AppCreditsFragmentAdmin extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.admin_fragment_appcredits, container, false);
    }

}
package com.sadaat.groceryapp.ui.Containers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.google.android.material.textview.MaterialTextView;
import com.sadaat.groceryapp.R;

public class CustomAccountItem extends View {
    public CustomAccountItem(Context context, int iconResId, String title) {
        super(context);
        View v = LayoutInflater.from(context).inflate(R.layout.customer_fragment_account_body, null);/*
        ((ImageView) v.findViewById(R.id.account_item_icon)).setImageResource(iconResId);
        ((MaterialTextView) v.findViewById(R.id.account_item_title)).setImageResource(iconResId);*/
    }
}

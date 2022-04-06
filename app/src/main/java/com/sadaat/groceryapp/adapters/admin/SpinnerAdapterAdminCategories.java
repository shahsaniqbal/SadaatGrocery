package com.sadaat.groceryapp.adapters.admin;

import android.content.Context;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;

import com.sadaat.groceryapp.models.categories.CategoriesModel;

import java.util.List;

public class SpinnerAdapterAdminCategories extends ArrayAdapter<CategoriesModel> {
    public SpinnerAdapterAdminCategories(@NonNull Context context, @NonNull List<CategoriesModel> objects) {

        super(context, android.R.layout.simple_spinner_item, objects);

    }
}

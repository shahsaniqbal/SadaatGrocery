package com.sadaat.groceryapp.adapters.customer;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;
import com.sadaat.groceryapp.R;
import com.sadaat.groceryapp.models.Items.ItemModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

public class SearchAdapter extends ArrayAdapter<ItemModel> {

    private ArrayList<ItemModel> items;
    private OnClickListeners listeners;

    public interface  OnClickListeners{
        public void onItemClick(int i, ItemModel m);
    }

    public SearchAdapter(@NonNull Context context, ArrayList<ItemModel> items, OnClickListeners listeners) {
        super(context, 0, items);
        this.items = items;
        this.listeners = listeners;
    }

    public Filter getItemFilter() {
        return itemFilter;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if (convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.customer_item_searchbar, parent, false);
        }

        MaterialTextView txvName = convertView.findViewById(R.id.item_name);
        MaterialTextView txvInStock = convertView.findViewById(R.id.item_instock);
        MaterialCardView card = convertView.findViewById(R.id.customer_search_item_mainView);

        card.setOnClickListener(v->{
            listeners.onItemClick(position, getItem(position));
        });

        txvName.setText(getItem(position).toFullString());

        if (getItem(position).getOtherDetails().getStock() > 0){
            txvInStock.setText(R.string.in_stock);
            txvInStock.setTextColor(Color.parseColor("#99CC00"));
        } else{
            txvInStock.setText(R.string.out_of_stock);
            txvInStock.setTextColor(Color.parseColor("#F8424C"));
        }

        return convertView;
    }

    private Filter itemFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            FilterResults results = new FilterResults();
            ArrayList<ItemModel> mItems = new ArrayList<>();

            Log.e("CHARS", charSequence.toString());

            if (null != charSequence) {

                CharSequence filterPattern = charSequence.toString().toLowerCase();
                for (ItemModel i : items) {
                    if (i.getName().toLowerCase().contains(filterPattern)) {
                        mItems.add(i);
                    }
                    if (i.toFullString().toLowerCase().contains(filterPattern)) {
                        mItems.add(i);
                    }
                }

                results.count = mItems.size();
                results.values = mItems;
            }

            return results;

        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            clear();
            addAll((ArrayList<ItemModel>) filterResults.values);
            notifyDataSetChanged();
        }

        @Override
        public CharSequence convertResultToString(Object resultValue) {
            return ((ItemModel) resultValue).getID();
        }
    };

    @Override
    public void add(@Nullable ItemModel object) {
        super.add(object);
    }
}

package com.sadaat.groceryapp.adapters.admin.locations;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;
import com.sadaat.groceryapp.R;
import com.sadaat.groceryapp.models.locations.AreaModel;

import java.util.ArrayList;

public class AreasItemAdapterAdmin extends RecyclerView.Adapter<AreasItemAdapterAdmin.ViewHolder> {

    String mainDocID;
    private final ArrayList<AreaModel> localDataSet;
    public AreasItemAdapterListener customListenerForSubCategories;

    public AreasItemAdapterAdmin(String mainDocID, ArrayList<AreaModel> localDataSet, AreasItemAdapterListener customListenerForSubCategories) {
        this.mainDocID = mainDocID;
        this.localDataSet = localDataSet;
        this.customListenerForSubCategories = customListenerForSubCategories;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.admin_item_location_recycler, viewGroup, false);

        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, @SuppressLint("RecyclerView") int position) {

        viewHolder.getTxvTitle().setText(localDataSet.get(position).getName());

        viewHolder.getBtnDelete().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customListenerForSubCategories.onDeleteAreaItemClick(
                        v,
                        position,
                        mainDocID,
                        viewHolder.getAdapterPosition(),
                        localDataSet.get(viewHolder.getAdapterPosition()));
            }
        });
        viewHolder.getBtnUpdate().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customListenerForSubCategories.onUpdateAreaItemClick(
                        v,
                        position,
                        mainDocID,
                        viewHolder.getAdapterPosition(),
                        localDataSet.get(viewHolder.getAdapterPosition()));
            }
        });

    }

    public void addArea(AreaModel model) {
        localDataSet.add(model);
        notifyItemInserted(localDataSet.size() - 1);
    }

    public void deleteArea(int index) {
        localDataSet.remove(index);
        notifyItemRemoved(index);
    }

    public void deleteAll() {
        int size = localDataSet.size();
        localDataSet.clear();
        notifyItemRangeRemoved(0, size);
    }


    @Override
    public int getItemCount() {
        return localDataSet.size();
    }


    public interface AreasItemAdapterListener {
        void onUpdateAreaItemClick(View v, int position, String mainDocID, int subDocIndex, AreaModel areaModel);

        void onDeleteAreaItemClick(View v, int position, String mainDocID, int subDocIndex, AreaModel areaToDelete);
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final MaterialTextView txvTitle;
        private final ImageButton btnUpdate;
        private final ImageButton btnDelete;
        private final MaterialButton addAreaButton;

        public ViewHolder(View view) {
            super(view);
            txvTitle = (MaterialTextView) view.findViewById(R.id.category_list_title);

            btnUpdate = (ImageButton) view.findViewById(R.id.modify);
            btnDelete = (ImageButton) view.findViewById(R.id.delete);
            addAreaButton = (MaterialButton) view.findViewById(R.id.add_subcate);

            addAreaButton.setVisibility(View.GONE);
        }

        public MaterialTextView getTxvTitle() {
            return txvTitle;
        }

        public ImageButton getBtnUpdate() {
            return btnUpdate;
        }

        public ImageButton getBtnDelete() {
            return btnDelete;
        }


    }
}

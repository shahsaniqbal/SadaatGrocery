package com.sadaat.groceryapp.adapters.admin.locations;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;
import com.sadaat.groceryapp.R;
import com.sadaat.groceryapp.models.CategoriesModel;
import com.sadaat.groceryapp.models.locations.AreaModel;
import com.sadaat.groceryapp.models.locations.CityModel;

import java.util.ArrayList;
import java.util.List;

public class CitiesItemAdapterAdmin extends RecyclerView.Adapter<CitiesItemAdapterAdmin.ViewHolder> {

    private Context refContext;
    private final ArrayList<CityModel> localDataSet;
    public CitiesItemAdapterListener customOnClickListener;
    public AreasItemAdapterAdmin.AreasItemAdapterListener customListenerForSubCategories;

    public CitiesItemAdapterAdmin(Context refContext, ArrayList<CityModel> localDataSet, CitiesItemAdapterListener customOnClickListener, AreasItemAdapterAdmin.AreasItemAdapterListener customListenerForAreas) {
        this.refContext = refContext;
        this.localDataSet = localDataSet;
        this.customOnClickListener = customOnClickListener;
        this.customListenerForSubCategories = customListenerForAreas;
        //this.layoutManager = new LinearLayoutManager(refContext);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.admin_item_location_recycler, viewGroup, false);

        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {

        viewHolder.getTxvTitle().setText(localDataSet.get(position).getName());

        if (localDataSet.get(position).getAreas().size() > 0) {

            viewHolder.getRecyclerViewAreas().setVisibility(View.VISIBLE);
            viewHolder.getRecyclerViewAreas().setLayoutManager(
                    new LinearLayoutManager(refContext, LinearLayoutManager.VERTICAL, false)
            );

            viewHolder.getRecyclerViewAreas().setAdapter(
                    new AreasItemAdapterAdmin(
                            localDataSet.get(viewHolder.getAdapterPosition()).getId(),
                            localDataSet.get(viewHolder.getAdapterPosition()).getAreas(),
                            customListenerForSubCategories
                    )
            );
        }
        else{
            viewHolder.getRecyclerViewAreas().setVisibility(View.GONE);
        }

        viewHolder.getBtnDelete().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customOnClickListener.onDeleteCityItemClick(v,
                        viewHolder.getAdapterPosition(),
                        localDataSet.get(viewHolder.getAdapterPosition()).getId(),
                        localDataSet.get(viewHolder.getAdapterPosition()).getName());
            }
        });
        viewHolder.getBtnUpdate().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customOnClickListener.onUpdateCityItemClick(v, viewHolder.getAdapterPosition(), localDataSet.get(viewHolder.getAdapterPosition()));
            }
        });
        viewHolder.getAddAreaButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customOnClickListener.onAddAreaItemClickOverCity(v, viewHolder.getAdapterPosition(), localDataSet.get(viewHolder.getAdapterPosition()).getId());
            }
        });


    }

    public void addCategory(CityModel model) {
        localDataSet.add(model);
        notifyItemInserted(localDataSet.size() - 1);
    }

    public void deleteCategory(int index) {
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

    public void addAll(List<CityModel> models) {
        localDataSet.clear();
        localDataSet.addAll(models);
        notifyDataSetChanged();
    }

    public interface CitiesItemAdapterListener {

        void onAddAreaItemClickOverCity(View v, int position, String docID);

        void onUpdateCityItemClick(View v, int position, CityModel cityModel);

        //Document ID that needs to be deleted
        void onDeleteCityItemClick(View v, int position, String docID, String title);
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final MaterialTextView txvTitle;
        private final ImageButton btnUpdate;
        private final ImageButton btnDelete;
        private final MaterialButton addAreaButton;
        private final RecyclerView recyclerViewAreas;

        public ViewHolder(View view) {
            super(view);
            txvTitle = (MaterialTextView) view.findViewById(R.id.category_list_title);

            btnUpdate = (ImageButton) view.findViewById(R.id.modify);
            btnDelete = (ImageButton) view.findViewById(R.id.delete);
            addAreaButton = (MaterialButton) view.findViewById(R.id.add_subcate);
            recyclerViewAreas = (RecyclerView) view.findViewById(R.id.recycler_subcategories);
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

        public MaterialButton getAddAreaButton() {
            return addAreaButton;
        }

        public RecyclerView getRecyclerViewAreas() {
            return recyclerViewAreas;
        }
    }
}

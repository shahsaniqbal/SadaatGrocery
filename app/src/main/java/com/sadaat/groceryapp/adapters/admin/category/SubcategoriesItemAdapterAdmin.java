package com.sadaat.groceryapp.adapters.admin.category;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.sadaat.groceryapp.R;
import com.sadaat.groceryapp.models.CategoriesModel;
import com.sadaat.groceryapp.models.SubCategoriesModel;

import java.util.ArrayList;

public class SubcategoriesItemAdapterAdmin extends RecyclerView.Adapter<SubcategoriesItemAdapterAdmin.ViewHolder> {

    String mainDocID;
    private final ArrayList<SubCategoriesModel> localDataSet;
    public SubCategoriesItemAdapterListener customListenerForSubCategories;

    public SubcategoriesItemAdapterAdmin(String mainDocID, ArrayList<SubCategoriesModel> localDataSet, SubCategoriesItemAdapterListener customListenerForSubCategories) {
        this.mainDocID = mainDocID;
        this.localDataSet = localDataSet;
        this.customListenerForSubCategories = customListenerForSubCategories;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.admin_item_category_recycler, viewGroup, false);

        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, @SuppressLint("RecyclerView") int position) {

        viewHolder.getTxvTitle().setText(localDataSet.get(position).getTitle());
        if (localDataSet.get(position).getDescription().isEmpty()){
            viewHolder.getTxvDesc().setText("");
            viewHolder.getTxvDesc().setVisibility(View.GONE);
        }
        else{
            viewHolder.getTxvDesc().setText(localDataSet.get(position).getDescription());
            viewHolder.getTxvDesc().setVisibility(View.VISIBLE);
        }
        viewHolder.getBtnDelete().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customListenerForSubCategories.onDeleteSubCategoryItemClick(
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
                customListenerForSubCategories.onUpdateSubCategoryItemClick(
                        v,
                        position,
                        mainDocID,
                        viewHolder.getAdapterPosition(),
                        localDataSet.get(viewHolder.getAdapterPosition()));
            }
        });

    }

    public void addSubCategory(SubCategoriesModel model) {
        localDataSet.add(model);
        notifyItemInserted(localDataSet.size() - 1);
    }

    public void deleteSubcategory(int index) {
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


    public interface SubCategoriesItemAdapterListener {
        void onUpdateSubCategoryItemClick(View v, int position, String mainDocID, int subDocIndex, SubCategoriesModel categoriesModel);

        void onDeleteSubCategoryItemClick(View v, int position, String mainDocID, int subDocIndex, SubCategoriesModel subCategoryToDelete);
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView txvTitle;
        private final TextView txvDesc;
        private final ImageButton btnUpdate;
        private final ImageButton btnDelete;
        private final ImageButton addSubCategoryButton;

        public ViewHolder(View view) {
            super(view);
            txvTitle = (TextView) view.findViewById(R.id.category_list_title);
            txvDesc = (TextView) view.findViewById(R.id.category_list_desc);

            btnUpdate = (ImageButton) view.findViewById(R.id.modify);
            btnDelete = (ImageButton) view.findViewById(R.id.delete);
            addSubCategoryButton = (ImageButton) view.findViewById(R.id.add_subcate);

            addSubCategoryButton.setVisibility(View.GONE);
        }

        public TextView getTxvTitle() {
            return txvTitle;
        }

        public TextView getTxvDesc() {
            return txvDesc;
        }

        public ImageButton getBtnUpdate() {
            return btnUpdate;
        }

        public ImageButton getBtnDelete() {
            return btnDelete;
        }


    }
}

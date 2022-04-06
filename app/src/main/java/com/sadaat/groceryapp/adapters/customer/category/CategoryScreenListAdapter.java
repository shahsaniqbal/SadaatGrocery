package com.sadaat.groceryapp.adapters.customer.category;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;
import com.sadaat.groceryapp.R;
import com.sadaat.groceryapp.models.categories.CategoriesModel;

import java.util.ArrayList;
import java.util.List;

public class CategoryScreenListAdapter extends RecyclerView.Adapter<CategoryScreenListAdapter.ViewHolder> {

    private final Context mContext;
    private final ArrayList<CategoriesModel> localDataSet;
    private final SubcategoryHorizontalAdapter.OnSubcategoryItemCustomClickListener itemClickListener;

    public CategoryScreenListAdapter(Context mContext, ArrayList<CategoriesModel> localDataSet, SubcategoryHorizontalAdapter.OnSubcategoryItemCustomClickListener itemClickListener) {
        this.mContext = mContext;
        this.localDataSet = localDataSet;
        this.itemClickListener = itemClickListener;
    }

    public void addCategory(CategoriesModel model) {
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

    @NonNull
    @Override
    public CategoryScreenListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.customer_item_category_recycler, parent, false);

        return new CategoryScreenListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryScreenListAdapter.ViewHolder holder, int position) {
        holder.getTxvCategoryTitle().setText(localDataSet.get(holder.getAdapterPosition()).getTitle());
        holder.getTxvCategoryDescription().setText(localDataSet.get(holder.getAdapterPosition()).getDescription());

        if (localDataSet.get(position).getSubCategories()!=null){
            if (localDataSet.get(position).getSubCategories().size()>0){
                SubcategoryHorizontalAdapter subCategoryAdapter;
                subCategoryAdapter = new SubcategoryHorizontalAdapter(
                        mContext,
                        localDataSet.get(position).getDocID(),
                        localDataSet.get(position).getSubCategories(),
                        itemClickListener
                );

                holder.getSubCategoriesRecyclerView().setVisibility(View.VISIBLE);
                holder.getSubCategoriesRecyclerView().setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
                holder.getSubCategoriesRecyclerView().setAdapter(subCategoryAdapter);

            }
            else{
                holder.getSubCategoriesRecyclerView().setVisibility(View.GONE);
            }
        }
        else{
            holder.getSubCategoriesRecyclerView().setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return localDataSet.size();
    }

    public void addAll(List<CategoriesModel> categoriesModels) {
        localDataSet.clear();
        localDataSet.addAll(categoriesModels);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final MaterialTextView txvCategoryTitle;
        private final MaterialTextView txvCategoryDescription;
        private final RecyclerView subCategoriesRecyclerView;

        //  TODO Modify the ViewHolder
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txvCategoryTitle = itemView.findViewById(R.id.txv_category_title_customer_category);
            subCategoriesRecyclerView = itemView.findViewById(R.id.recycler_subcategories_customer_category);
            txvCategoryDescription = itemView.findViewById(R.id.txv_category_description_customer_category);
        }

        public MaterialTextView getTxvCategoryTitle() {
            return txvCategoryTitle;
        }

        public RecyclerView getSubCategoriesRecyclerView() {
            return subCategoriesRecyclerView;
        }

        public MaterialTextView getTxvCategoryDescription() {
            return txvCategoryDescription;
        }
    }

}

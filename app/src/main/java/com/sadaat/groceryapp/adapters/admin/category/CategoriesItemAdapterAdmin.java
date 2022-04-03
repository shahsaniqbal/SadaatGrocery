package com.sadaat.groceryapp.adapters.admin.category;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sadaat.groceryapp.R;
import com.sadaat.groceryapp.models.CategoriesModel;

import java.util.ArrayList;
import java.util.List;

public class CategoriesItemAdapterAdmin extends RecyclerView.Adapter<CategoriesItemAdapterAdmin.ViewHolder> {

    private Context refContext;
    private final ArrayList<CategoriesModel> localDataSet;
    public CategoriesItemAdapterListener customOnClickListener;
    public SubcategoriesItemAdapterAdmin.SubCategoriesItemAdapterListener customListenerForSubCategories;

    public CategoriesItemAdapterAdmin(Context refContext, ArrayList<CategoriesModel> localDataSet, CategoriesItemAdapterListener customOnClickListener, SubcategoriesItemAdapterAdmin.SubCategoriesItemAdapterListener customListenerForSubCategories) {
        this.refContext = refContext;
        this.localDataSet = localDataSet;
        this.customOnClickListener = customOnClickListener;
        this.customListenerForSubCategories = customListenerForSubCategories;
        //this.layoutManager = new LinearLayoutManager(refContext);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.admin_item_category_recycler, viewGroup, false);

        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {

        viewHolder.getTxvTitle().setText(localDataSet.get(position).getTitle());

        if (localDataSet.get(position).getDescription().isEmpty()){
            viewHolder.getTxvDesc().setText("");
            viewHolder.getTxvDesc().setVisibility(View.GONE);
        }
        else{
            viewHolder.getTxvDesc().setText(localDataSet.get(position).getDescription());
            viewHolder.getTxvDesc().setVisibility(View.VISIBLE);
        }

        if (localDataSet.get(position).getSubCategories().size() > 0) {

            viewHolder.getRecyclerViewSubCategories().setVisibility(View.VISIBLE);
            viewHolder.getRecyclerViewSubCategories().setLayoutManager(
                    new LinearLayoutManager(refContext, LinearLayoutManager.VERTICAL, false)
            );

            viewHolder.getRecyclerViewSubCategories().setAdapter(
                    new SubcategoriesItemAdapterAdmin(
                            localDataSet.get(viewHolder.getAdapterPosition()).getDocID(),
                            localDataSet.get(viewHolder.getAdapterPosition()).getSubCategories(),
                            customListenerForSubCategories
                    )
            );
        }
        else{
            viewHolder.getRecyclerViewSubCategories().setVisibility(View.GONE);
        }

        viewHolder.getBtnDelete().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customOnClickListener.onDeleteCategoryItemClick(v,
                        viewHolder.getAdapterPosition(),
                        localDataSet.get(viewHolder.getAdapterPosition()).getDocID(),
                        localDataSet.get(viewHolder.getAdapterPosition()).getTitle());
            }
        });
        viewHolder.getBtnUpdate().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customOnClickListener.onUpdateCategoryItemClick(v, viewHolder.getAdapterPosition(), localDataSet.get(viewHolder.getAdapterPosition()));
            }
        });
        viewHolder.getAddSubCategoryButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customOnClickListener.onAddSubCategoryItemClickOverCategory(v, viewHolder.getAdapterPosition(), localDataSet.get(viewHolder.getAdapterPosition()).getDocID());
            }
        });


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


    @Override
    public int getItemCount() {
        return localDataSet.size();
    }

    public void addAll(List<CategoriesModel> categoriesModels) {
        localDataSet.clear();
        localDataSet.addAll(categoriesModels);
        notifyDataSetChanged();
    }

    public interface CategoriesItemAdapterListener {

        void onAddSubCategoryItemClickOverCategory(View v, int position, String docID);

        void onUpdateCategoryItemClick(View v, int position, CategoriesModel categoriesModel);

        //Document ID that needs to be deleted
        void onDeleteCategoryItemClick(View v, int position, String docID, String title);
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView txvTitle;
        private final TextView txvDesc;
        private final ImageButton btnUpdate;
        private final ImageButton btnDelete;
        private final ImageButton addSubCategoryButton;
        private final RecyclerView recyclerViewSubCategories;

        public ViewHolder(View view) {
            super(view);
            txvTitle = (TextView) view.findViewById(R.id.category_list_title);
            txvDesc = (TextView) view.findViewById(R.id.category_list_desc);

            btnUpdate = (ImageButton) view.findViewById(R.id.modify);
            btnDelete = (ImageButton) view.findViewById(R.id.delete);
            addSubCategoryButton = (ImageButton) view.findViewById(R.id.add_subcate);
            recyclerViewSubCategories = (RecyclerView) view.findViewById(R.id.recycler_subcategories);
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

        public ImageButton getAddSubCategoryButton() {
            return addSubCategoryButton;
        }

        public RecyclerView getRecyclerViewSubCategories() {
            return recyclerViewSubCategories;
        }
    }
}

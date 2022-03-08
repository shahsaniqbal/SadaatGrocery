package com.sadaat.groceryapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.sadaat.groceryapp.R;
import com.sadaat.groceryapp.models.CategoriesModel;

import java.util.ArrayList;

public class CategoriesItemAdapterAdmin extends RecyclerView.Adapter<CategoriesItemAdapterAdmin.ViewHolder> {

    public CategoriesItemAdapterListener customOnClickListener;
    private final ArrayList<CategoriesModel> localDataSet;


    public CategoriesItemAdapterAdmin(ArrayList<CategoriesModel> localDataSet, CategoriesItemAdapterListener customOnClickListener) {
        this.localDataSet = localDataSet;
        this.customOnClickListener = customOnClickListener;
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
        viewHolder.getTxvDesc().setText(localDataSet.get(position).getDescription());

        viewHolder.getBtnDelete().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customOnClickListener.onDeleteSubCategoryItemClick(v,
                        viewHolder.getAdapterPosition(),
                        localDataSet.get(viewHolder.getAdapterPosition()).getDocID(),
                        localDataSet.get(viewHolder.getAdapterPosition()).getTitle());
            }
        });

        viewHolder.getBtnUpdate().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customOnClickListener.onUpdateSubCategoryItemClick(v, viewHolder.getAdapterPosition(), localDataSet.get(viewHolder.getAdapterPosition()));
            }
        });

        viewHolder.getAddSubCategoryButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customOnClickListener.onAddSubCategoryItemClick(v, viewHolder.getAdapterPosition());
            }
        });


    }

    public void addCategory(CategoriesModel model) {
        localDataSet.add(model);
        notifyItemInserted(localDataSet.size() - 1);
    }

    public void deleteCategory(int index){
        localDataSet.remove(index);
        notifyItemRemoved(index);
    }

    public void deleteAll(){
        int size = localDataSet.size();
        localDataSet.clear();
        notifyItemRangeRemoved(0, size);
    }


    @Override
    public int getItemCount() {
        return localDataSet.size();
    }

    public interface CategoriesItemAdapterListener {

        void onAddSubCategoryItemClick(View v, int position);

        void onUpdateSubCategoryItemClick(View v, int position, CategoriesModel categoriesModel);

        //Document ID that needs to be deleted
        void onDeleteSubCategoryItemClick(View v, int position, String docID, String title);
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

    }
}

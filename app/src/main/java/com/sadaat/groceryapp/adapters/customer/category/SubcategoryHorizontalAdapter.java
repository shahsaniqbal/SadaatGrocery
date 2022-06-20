package com.sadaat.groceryapp.adapters.customer.category;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.sadaat.groceryapp.R;
import com.sadaat.groceryapp.models.categories.SubCategoriesModel;
import com.sadaat.groceryapp.temp.FirebaseDataKeys;
import com.sadaat.groceryapp.ui.Loaders.LoadingDialogue;

import java.util.ArrayList;
import java.util.List;

public class SubcategoryHorizontalAdapter extends RecyclerView.Adapter<SubcategoryHorizontalAdapter.ViewHolder> {

    private final String parentCategoryDocID;
    private final ArrayList<SubCategoriesModel> localDataSet;
    private final OnSubcategoryItemCustomClickListener itemClickListener;
    //LoadingDialogue loadingDialogue;


    public SubcategoryHorizontalAdapter(Context mSubContext, String docID, ArrayList<SubCategoriesModel> localDataSet, OnSubcategoryItemCustomClickListener itemClickListener) {
        this.localDataSet = localDataSet;
        this.itemClickListener = itemClickListener;
        this.parentCategoryDocID = docID;
        //loadingDialogue = new LoadingDialogue(mSubContext);
    }

    public void addSubCategory(SubCategoriesModel model) {
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
    public SubcategoryHorizontalAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.customer_item_subcategory_recycler, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SubcategoryHorizontalAdapter.ViewHolder holder, int position) {

        holder.getTxvSubcategoryTitle().setText(localDataSet.get(holder.getAdapterPosition()).getTitle());
        holder.getParentView().setOnClickListener(view -> itemClickListener.onClickItemSubcategory(
                parentCategoryDocID,
                localDataSet.get(holder.getAdapterPosition()).getDocID(),
                localDataSet.get(holder.getAdapterPosition()).getTitle(),
                localDataSet.get(holder.getAdapterPosition()).getDescription()
        ));

        if (!localDataSet.get(holder.getAdapterPosition()).getImageRef().equals("")) {
            //loadingDialogue.show("Please wait", "While Loading Images");
            StorageReference imgRef = FirebaseStorage
                    .getInstance(FirebaseDataKeys.STORAGE_BUCKET_ADDRESS)
                    .getReference()
                    .child(localDataSet
                            .get(position)
                            .getImageRef());

            final long ONE_MEGABYTE = 1024 * 1024;
            imgRef.getBytes(10 * ONE_MEGABYTE).addOnSuccessListener(bytes -> {
                holder.getImgViewSubcategoryImage().setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
                //loadingDialogue.dismiss();
                //progressDialogue.dismiss();
            }).addOnFailureListener(exception -> {
                //loadingDialogue.dismiss();
                //Toast.makeText(mContext, "Image Load Failed, \n Leave it or use a new one", Toast.LENGTH_SHORT).show();
            });
        }
    }

    @Override
    public int getItemCount() {
        return localDataSet.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void addAll(List<SubCategoriesModel> subCategoriesModels) {
        localDataSet.clear();
        localDataSet.addAll(subCategoriesModels);
        notifyDataSetChanged();
    }

    public interface OnSubcategoryItemCustomClickListener {
        void onClickItemSubcategory(String mainCategoryID, String subcategoryID, String categoryTitle, String categoryDescription);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final MaterialTextView txvSubcategoryTitle;
        private final ImageView imgViewSubcategoryImage;

        private final MaterialCardView parentView;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            parentView = itemView.findViewById(R.id.customer_subcategory_parent_card);
            txvSubcategoryTitle = itemView.findViewById(R.id.txv_subcategory_title_customer_category);
            imgViewSubcategoryImage = itemView.findViewById(R.id.imgv_subcategory_image_customer_category);
        }

        public MaterialTextView getTxvSubcategoryTitle() {
            return txvSubcategoryTitle;
        }

        public ImageView getImgViewSubcategoryImage() {
            return imgViewSubcategoryImage;
        }

        public MaterialCardView getParentView() {
            return parentView;
        }
    }

}

package com.sadaat.groceryapp.adapters.customer.category;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.sadaat.groceryapp.R;
import com.sadaat.groceryapp.models.CategoriesModel;
import com.sadaat.groceryapp.temp.FirebaseDataKeys;
import com.sadaat.groceryapp.ui.Loaders.LoadingDialogue;

import java.util.ArrayList;
import java.util.List;

public class SubcategoryHorizontalAdapter extends RecyclerView.Adapter<SubcategoryHorizontalAdapter.ViewHolder> {

    private Context mSubContext;
    private String parentCategoryDocID;

    private ArrayList<CategoriesModel> localDataSet;
    private OnSubcategoryItemCustomClickListener itemClickListener;

    LoadingDialogue loadingDialogue;


    public interface OnSubcategoryItemCustomClickListener{
        void onClickItemSubcategory(String mainCategoryID, String subcategoryID, String categoryTitle, String categoryDescription);
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

    public SubcategoryHorizontalAdapter(Context mSubContext, String docID, ArrayList<CategoriesModel> localDataSet, OnSubcategoryItemCustomClickListener itemClickListener) {
        this.mSubContext = mSubContext;
        this.localDataSet = localDataSet;
        this.itemClickListener = itemClickListener;
        this.parentCategoryDocID = docID;
        loadingDialogue = new LoadingDialogue(mSubContext);
    }

    @NonNull
    @Override
    public SubcategoryHorizontalAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.customer_item_subcategory_recycler, parent, false);

        return new SubcategoryHorizontalAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SubcategoryHorizontalAdapter.ViewHolder holder, int position) {

        holder.getTxvSubcategoryTitle().setText(localDataSet.get(holder.getAdapterPosition()).getTitle());
        holder.getParentView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemClickListener.onClickItemSubcategory(
                        parentCategoryDocID,
                        localDataSet.get(holder.getAdapterPosition()).getDocID(),
                        localDataSet.get(holder.getAdapterPosition()).getTitle(),
                        localDataSet.get(holder.getAdapterPosition()).getDescription()
                );
            }
        });


        if (!localDataSet.get(holder.getAdapterPosition()).getImageRef().equals("")){
            loadingDialogue.show("Please wait", "While Loading Images");
            StorageReference imgRef = FirebaseStorage
                    .getInstance(FirebaseDataKeys.STORAGE_BUCKET_ADDRESS)
                    .getReference()
                    .child(localDataSet
                            .get(position)
                            .getImageRef());

            final long ONE_MEGABYTE = 1024 * 1024;
            imgRef.getBytes(10 * ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    holder.getImgvSubcategoryImage().setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
                    loadingDialogue.dismiss();
                    //progressDialogue.dismiss();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    loadingDialogue.dismiss();
                    //Toast.makeText(mContext, "Image Load Failed, \n Leave it or use a new one", Toast.LENGTH_SHORT).show();
                }
            });
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

    public class ViewHolder extends RecyclerView.ViewHolder{

        private MaterialTextView txvSubcategoryTitle;
        private ImageView imgvSubcategoryImage;

        private MaterialCardView parentView;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            parentView = itemView.findViewById(R.id.customer_subcategory_parent_card);
            txvSubcategoryTitle = itemView.findViewById(R.id.txv_subcategory_title_customer_category);
            imgvSubcategoryImage = itemView.findViewById(R.id.imgv_subcategory_image_customer_category);
        }

        public MaterialTextView getTxvSubcategoryTitle() {
            return txvSubcategoryTitle;
        }

        public ImageView getImgvSubcategoryImage() {
            return imgvSubcategoryImage;
        }

        public MaterialCardView getParentView() {
            return parentView;
        }
    }

}

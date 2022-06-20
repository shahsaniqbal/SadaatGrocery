package com.sadaat.groceryapp.adapters.generic;

import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.islamkhsh.CardSliderAdapter;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.sadaat.groceryapp.R;
import com.sadaat.groceryapp.models.SliderModel;
import com.sadaat.groceryapp.temp.FirebaseDataKeys;

import java.util.ArrayList;
import java.util.List;

public class SliderAdapter extends CardSliderAdapter<SliderAdapter.ViewHolder> {

    ArrayList<SliderModel> slides;
    OnClickListener onClickListener;

    public SliderAdapter(ArrayList<SliderModel> slides) {
        this.slides = slides;
    }

    public SliderAdapter(ArrayList<SliderModel> slides, OnClickListener onClickListener) {
        this.slides = slides;
        this.onClickListener = onClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.generic_item_slider, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return slides.size();
    }

    @Override
    public void bindVH(@NonNull ViewHolder viewHolder, int i) {

        if (onClickListener != null) {
            viewHolder.getCardDelete().setVisibility(View.VISIBLE);
            viewHolder.getLayout().setVisibility(View.VISIBLE);

        } else {
            viewHolder.getCardDelete().setVisibility(View.GONE);
            viewHolder.getLayout().setVisibility(View.GONE);
        }

        viewHolder.getTxvTitle().setText(slides.get(i).getTitle());
        viewHolder.getTxvDesc().setText(slides.get(i).getDescription());

        viewHolder.getCardDelete().setOnClickListener(v -> {
            onClickListener.onDelete(slides.get(i).getId(), slides.get(i).getImageLink());
        });

        if (!slides
                .get(i)
                .getImageLink().equals("")) {

            StorageReference imgRef = FirebaseStorage
                    .getInstance(FirebaseDataKeys.STORAGE_BUCKET_ADDRESS)
                    .getReference()
                    .child(slides
                            .get(i)
                            .getImageLink());

            final long ONE_MEGABYTE = 1024 * 1024;

            imgRef.getBytes(10 * ONE_MEGABYTE).addOnSuccessListener(bytes -> {
                viewHolder.getIvSlide().setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
            }).addOnFailureListener(exception -> {
                //Toast.makeText(mContext, "Image Load Failed, \n Leave it or use a new one", Toast.LENGTH_SHORT).show();
            });
        }

    }

    public void addSlide(SliderModel model) {
        slides.add(model);
        notifyItemInserted(slides.size() - 1);
    }

    public void deleteItem(int index) {
        slides.remove(index);
        notifyItemRemoved(index);
    }

    public void deleteAll() {
        int size = slides.size();
        slides.clear();
        notifyItemRangeRemoved(0, size);
    }

    public void addAll(List<SliderModel> model) {
        slides.clear();
        slides.addAll(model);
        notifyDataSetChanged();
    }

    public interface OnClickListener {
        void onDelete(String slideID, String imageRef);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        MaterialTextView txvTitle, txvDesc;
        ImageView ivSlide;
        MaterialCardView cardDelete;
        LinearLayout layout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txvTitle = itemView.findViewById(R.id.txv_title);
            txvDesc = itemView.findViewById(R.id.txv_description);
            ivSlide = itemView.findViewById(R.id.iv_slide);
            cardDelete = itemView.findViewById(R.id.card_delete);
            layout = itemView.findViewById(R.id.layout_delete_button);
        }

        public MaterialTextView getTxvTitle() {
            return txvTitle;
        }

        public MaterialTextView getTxvDesc() {
            return txvDesc;
        }

        public ImageView getIvSlide() {
            return ivSlide;
        }

        public MaterialCardView getCardDelete() {
            return cardDelete;
        }

        public LinearLayout getLayout() {
            return layout;
        }
    }


}
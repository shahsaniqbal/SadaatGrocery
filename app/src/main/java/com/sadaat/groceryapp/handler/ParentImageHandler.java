package com.sadaat.groceryapp.handler;

import android.graphics.BitmapFactory;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.sadaat.groceryapp.temp.FirebaseDataKeys;
import com.sadaat.groceryapp.ui.Loaders.LoadingDialogue;

public class ParentImageHandler {
    private FragmentActivity activity;
    private LoadingDialogue loadingDialogue;

    public ParentImageHandler(FragmentActivity activity, LoadingDialogue loadingDialogue) {
        this.activity = activity;
        this.loadingDialogue = loadingDialogue;
    }

    public FragmentActivity getActivity() {
        return activity;
    }

    public void setActivity(FragmentActivity activity) {
        this.activity = activity;
    }

    public LoadingDialogue getLoadingDialogue() {
        return loadingDialogue;
    }

    public void setLoadingDialogue(LoadingDialogue loadingDialogue) {
        this.loadingDialogue = loadingDialogue;
    }

    public void downloadLoadImage(@NonNull String imageReference, int maxBytesToLoad, @NonNull ImageView loadImageView) {
        getLoadingDialogue().show("Showing", "Item Image");
        if (imageReference.charAt(0) == 'f') {
            downloadLoadFirebaseImage(imageReference, maxBytesToLoad, loadImageView);
        } else if (imageReference.charAt(0) == 'h') {
            downloadLoadLinkImage(imageReference, maxBytesToLoad, loadImageView);
        }
    }

    private void downloadLoadLinkImage(String imageReference, int maxBytesToLoad, ImageView loadImageView) {
        getLoadingDialogue().dismiss();
        //for testing I am setting the method also to firebase
        downloadLoadFirebaseImage(imageReference, maxBytesToLoad, loadImageView);
    }

    private boolean downloadLoadFirebaseImage(String imageReference, int maxBytesToLoad, ImageView loadImageView) {
        final Boolean[] result = new Boolean[1];
        result[0] = false;
        StorageReference imgRef = FirebaseStorage.getInstance(FirebaseDataKeys.STORAGE_BUCKET_ADDRESS).getReference().child(imageReference);
        imgRef.getBytes(maxBytesToLoad).addOnSuccessListener(bytes -> {
            loadImageView.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
            result[0] = true;
        }).addOnFailureListener(exception -> result[0] = false);

        getLoadingDialogue().dismiss();
        return result[0];

    }


}

package com.sadaat.groceryapp.handler;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.ActivityResultRegistry;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.sadaat.groceryapp.temp.FirebaseDataKeys;
import com.sadaat.groceryapp.ui.Loaders.LoadingDialogue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.UUID;

public class ImageHandlerUploader extends ParentImageHandler implements DefaultLifecycleObserver {

    public static String ImageResource = "";
    private final int requestCode;
    private final ActivityResultRegistry registry;
    String pathPrefix;
    int compressQuality;
    ImageView loadImageView;
    private ActivityResultLauncher<String> mGetContent;

    public ImageHandlerUploader(FragmentActivity activity, int requestCode, String pathPrefix, int compressQuality, ImageView loadImageView) {
        super(activity, new LoadingDialogue(activity));
        this.requestCode = requestCode;
        this.registry = activity.getActivityResultRegistry();
        this.pathPrefix = pathPrefix;
        this.compressQuality = compressQuality;
        this.loadImageView = loadImageView;
    }

    @Override
    public void onCreate(@NonNull LifecycleOwner owner) {
        DefaultLifecycleObserver.super.onCreate(owner);

        mGetContent = registry.register("" + requestCode, owner, new ActivityResultContracts.GetContent(),
                uri -> {
                    try {
                        ImageResource = loadUploadImage(uri, pathPrefix, compressQuality, loadImageView);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
    }

    public void chooseAndUploadImage() {

        mGetContent.launch("image/*");
    }

    public String loadUploadImage(Uri imageURI, String pathPrefix, int compressQuality, ImageView loadImageView) throws IOException {


        try {

            // Setting image on image view using Bitmap
            Bitmap bitmap = MediaStore
                    .Images
                    .Media
                    .getBitmap(
                            super.getActivity().getContentResolver(),
                            imageURI);
            //Setting Image Bitmap to my desired ImageView

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, outputStream);

            loadImageView.setImageBitmap(bitmap);

            if (imageURI != null) {

                // Code for showing progressDialog while uploading
                super.getLoadingDialogue().show("Uploading", "Image");

                // Defining the child of storageReference
                ImageResource = "f/" + pathPrefix + UUID.randomUUID().toString();

                StorageReference ref = FirebaseStorage.getInstance(FirebaseDataKeys.STORAGE_BUCKET_ADDRESS).getReference().child(ImageResource);

                ref.putBytes(outputStream.toByteArray())
                        .addOnSuccessListener(
                                taskSnapshot -> {
                                    super.getLoadingDialogue().dismiss();
                                    Toast.makeText(
                                            super.getActivity(),
                                            "Image Uploaded!!",
                                            Toast.LENGTH_SHORT
                                    ).show();
                                })

                        .addOnFailureListener(e -> {
                            super.getLoadingDialogue().dismiss();
                            Log.e(e.getCause() + "\n", e.getMessage());
                            Toast.makeText(super.getActivity(),
                                    "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        })
                        .addOnProgressListener(
                                taskSnapshot -> {
                                    double progress
                                            = (100.0
                                            * taskSnapshot.getBytesTransferred()
                                            / taskSnapshot.getTotalByteCount());
                                    super.getLoadingDialogue().show(
                                            "Uploaded ", ""
                                                    + (int) progress + "%");
                                });
            }


        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }

        return ImageResource;
    }

    public void downloadLoadImage(@NonNull String imageReference, int maxBytesToLoad, @NonNull ImageView loadImageView) {
        super.getLoadingDialogue().show("SHowing", "Item Image");
        if (imageReference.charAt(0) == 'f') {
            downloadLoadFirebaseImage(imageReference, maxBytesToLoad, loadImageView);
        } else if (imageReference.charAt(0) == 'h') {
            downloadLoadLinkImage(imageReference, maxBytesToLoad, loadImageView);
        }
    }

    private void downloadLoadLinkImage(String imageReference, int maxBytesToLoad, ImageView loadImageView) {
        //TODO Load Link Image

        super.getLoadingDialogue().dismiss();
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

        super.getLoadingDialogue().dismiss();
        return result[0];

    }

    public String deleteImageFromFirebase(String firebaseImageRef) {
        return "";
    }

    public void chooseAndUpdate(String imageRef) {

    }
}

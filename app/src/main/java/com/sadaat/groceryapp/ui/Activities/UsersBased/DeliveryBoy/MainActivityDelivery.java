package com.sadaat.groceryapp.ui.Activities.UsersBased.DeliveryBoy;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.sadaat.groceryapp.R;
import com.sadaat.groceryapp.temp.FirebaseDataKeys;
import com.sadaat.groceryapp.temp.UserLive;
import com.sadaat.groceryapp.ui.Activities.GenericForAll.LoginNavigatorActivity;
import com.sadaat.groceryapp.ui.Loaders.LoadingDialogue;

import java.util.Objects;

public class MainActivityDelivery extends AppCompatActivity {

    public static Bitmap userImage;
    private LoadingDialogue dialogue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.delivery_activity_main);

        Objects.requireNonNull(getSupportActionBar()).hide();
        dialogue = new LoadingDialogue(this);

        ((MaterialTextView) findViewById(R.id.delivery_main_username)).setText(UserLive.currentLoggedInUser.getFullName());

        if (!UserLive.currentLoggedInUser.getDetails()
                .getImageReference().isEmpty()) {

            StorageReference imgRef = FirebaseStorage
                    .getInstance(FirebaseDataKeys.STORAGE_BUCKET_ADDRESS)
                    .getReference()
                    .child(UserLive.currentLoggedInUser.getDetails()
                            .getImageReference());

            final long ONE_MEGABYTE = 1024 * 1024;

            dialogue = new LoadingDialogue(this);
            dialogue.show("Please Wait", "Loading User Data");

            imgRef.getBytes(10 * ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    userImage = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    dialogue.dismiss();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Toast.makeText(MainActivityDelivery.this, "User Image Load Failed, \n Leave it ", Toast.LENGTH_SHORT).show();
                    dialogue.dismiss();
                }
            });
        } else {
            dialogue.dismiss();
        }

    }

    public void onLiveOrderMenuClick(View view) {
        startActivity(new Intent(this, LiveOrderActivityDelivery.class));
    }

    public void onOrdersMenuClick(View view) {
        startActivity(new Intent(this, OrdersActivityDelivery.class));
    }

    public void onProfileMenuClick(View view) {
        startActivity(new Intent(this, ProfileActivityDelivery.class));
    }

    public void onLogoutMenuClick(View view) {

        new AlertDialog.Builder(MainActivityDelivery.this)
                .setTitle("Signing Out")
                .setMessage("Are you sure you want to sign out?")

                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton("Yes", (dialog, which) -> {
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(MainActivityDelivery.this, LoginNavigatorActivity.class));
                    UserLive.currentLoggedInUser = null;
                    finish();
                })
                .setCancelable(false)

                // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton("No", (dialogInterface, i) -> dialogInterface.dismiss())
                .setIcon(R.mipmap.logo)
                .show();
    }

}
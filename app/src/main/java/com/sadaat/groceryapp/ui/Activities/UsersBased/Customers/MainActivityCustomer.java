package com.sadaat.groceryapp.ui.Activities.UsersBased.Customers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.sadaat.groceryapp.R;
import com.sadaat.groceryapp.temp.FirebaseDataKeys;
import com.sadaat.groceryapp.temp.UserLive;
import com.sadaat.groceryapp.ui.Activities.GenericForAll.LoginNavigatorActivity;
import com.sadaat.groceryapp.ui.Activities.UsersBased.DeliveryBoy.MainActivityDelivery;
import com.sadaat.groceryapp.ui.Fragments.UserBased.Customers.AccountFragmentCustomer;
import com.sadaat.groceryapp.ui.Fragments.UserBased.Customers.CartFragmentCustomer;
import com.sadaat.groceryapp.ui.Fragments.UserBased.Customers.CategoriesFragmentCustomer;
import com.sadaat.groceryapp.ui.Fragments.UserBased.Customers.HomeFragmentCustomer;
import com.sadaat.groceryapp.ui.Fragments.UserBased.Customers.SearchFragmentCustomer;
import com.sadaat.groceryapp.ui.Loaders.LoadingDialogue;

import java.util.Objects;

public class MainActivityCustomer extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    public static Bitmap userImage;
    private LoadingDialogue dialogue;
    ImageView backButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.customer_activity_main);
        dialogue = new LoadingDialogue(this);

        Objects.requireNonNull(getSupportActionBar()).hide();

        ((TextView) findViewById(R.id.actionbar_subtitle)).setText(UserLive.currentLoggedInUser.getFullName());

        initialize();

        backButton = findViewById(R.id.back_button);
        findViewById(R.id.logo_sadaat).setVisibility(View.VISIBLE);

        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                    backButton.setVisibility(View.VISIBLE);
                    findViewById(R.id.logo_sadaat).setVisibility(View.GONE);
                } else {
                    backButton.setVisibility(View.GONE);
                    findViewById(R.id.logo_sadaat).setVisibility(View.VISIBLE);
                }
            }
        });

        backButton.setOnClickListener(v->{
            getSupportFragmentManager().popBackStack();
        });

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


                    if (MainActivityCustomer.userImage!=null){
                        ((ImageView) findViewById(R.id.userDP)).setImageBitmap(userImage);
                    }
                    else{

                    }
                    dialogue.dismiss();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Toast.makeText(MainActivityCustomer.this, "User Image Load Failed, \n Leave it ", Toast.LENGTH_SHORT).show();
                    dialogue.dismiss();
                }
            });
        } else {
            dialogue.dismiss();
        }


    }

    private void initialize() {

        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.nav_customer_home:
                        getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.flFragmentCustomer, HomeFragmentCustomer.class, null)
                                .commit();
                        return true;

                    case R.id.nav_customer_categories:
                        getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.flFragmentCustomer, CategoriesFragmentCustomer.class, null)
                                .commit();
                        return true;

                    case R.id.nav_customer_cart:
                        getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.flFragmentCustomer, CartFragmentCustomer.class, null)
                                .commit();
                        return true;

                    case R.id.nav_customer_search:
                        getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.flFragmentCustomer, SearchFragmentCustomer.class, null)
                                .commit();
                        return true;

                    case R.id.nav_customer_account:
                        getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.flFragmentCustomer, AccountFragmentCustomer.class, null)
                                .commit();
                        return true;
                }


                return false;
            }
        });
        bottomNavigationView.setSelectedItemId(R.id.nav_customer_home);


    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            backButton.setVisibility(View.VISIBLE);
            findViewById(R.id.logo_sadaat).setVisibility(View.GONE);
            getSupportFragmentManager().popBackStack();
        } else {
            backButton.setVisibility(View.GONE);
            findViewById(R.id.logo_sadaat).setVisibility(View.VISIBLE);
            new AlertDialog.Builder(MainActivityCustomer.this)
                    .setTitle("Exit")
                    .setMessage("Are you sure you want to exit the app?")

                    // Specifying a listener allows you to take an action before dismissing the dialog.
                    // The dialog is automatically dismissed when a dialog button is clicked.
                    .setPositiveButton("Yes", (dialog, which) -> {
                        super.onBackPressed();
                    })

                    // A null listener allows the button to dismiss the dialog and take no further action.
                    .setNegativeButton("No", (dialogInterface, i) -> dialogInterface.dismiss())
                    .setIcon(R.mipmap.logo)
                    .show();
        }
    }

}
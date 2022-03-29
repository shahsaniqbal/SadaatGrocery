package com.sadaat.groceryapp.ui.Activities.UsersBased.Customers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toolbar;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.sadaat.groceryapp.R;
import com.sadaat.groceryapp.temp.UserLive;
import com.sadaat.groceryapp.ui.Fragments.UserBased.Admin.UnderHomeFragmentSuper.OrdersFragmentAdmin;
import com.sadaat.groceryapp.ui.Fragments.UserBased.Customers.AccountFragmentCustomer;
import com.sadaat.groceryapp.ui.Fragments.UserBased.Customers.CartFragmentCustomer;
import com.sadaat.groceryapp.ui.Fragments.UserBased.Customers.CategoriesFragmentCustomer;
import com.sadaat.groceryapp.ui.Fragments.UserBased.Customers.HomeFragmentCustomer;
import com.sadaat.groceryapp.ui.Fragments.UserBased.Customers.SearchFragmentCustomer;

import java.util.Objects;

public class MainActivityCustomer extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.customer_activity_main);

        Objects.requireNonNull(getSupportActionBar()).hide();

        ((TextView) findViewById(R.id.actionbar_title)).setText("Hello"+"!");
        ((TextView) findViewById(R.id.actionbar_subtitle)).setText(UserLive.currentLoggedInUser.getFullName());

        initialize();

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
}
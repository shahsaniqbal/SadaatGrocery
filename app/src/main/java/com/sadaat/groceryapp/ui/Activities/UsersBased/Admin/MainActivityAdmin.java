package com.sadaat.groceryapp.ui.Activities.UsersBased.Admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.sadaat.groceryapp.R;
import com.sadaat.groceryapp.databinding.AdminActivityMainBinding;
import com.sadaat.groceryapp.temp.UserLive;
import com.sadaat.groceryapp.ui.Activities.GenericForAll.LoginNavigatorActivity;

public class MainActivityAdmin extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private AdminActivityMainBinding binding;

    private final String TYPE = "Admin - Sadaat";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = AdminActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMainActivityAdmin.toolbar);
        binding.appBarMainActivityAdmin.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.

        ((TextView) navigationView.getHeaderView(0).findViewById(R.id.admin_header_name_txv)).setText(UserLive.currentLoggedInUser.getFullName());
        ((TextView) navigationView.getHeaderView(0).findViewById(R.id.admin_header_title_txv)).setText(TYPE);

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_app_credits_admin, R.id.nav_user_mgmt,
                R.id.nav_responses, R.id.nav_returns_admin, R.id.nav_reports_admin)
                .setOpenableLayout(drawer)
                .build();


        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main_activity_admin);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.admin_main_activity_option_menu, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main_activity_admin);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.action_logout){
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(this, LoginNavigatorActivity.class));
            UserLive.currentLoggedInUser = null;
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
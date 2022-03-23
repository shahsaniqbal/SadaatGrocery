package com.sadaat.groceryapp.ui.Activities.GenericForAll;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.sadaat.groceryapp.R;
import com.sadaat.groceryapp.ui.Fragments.Generic.LoginFragment;

import java.util.Objects;

public class LoginNavigatorActivity extends AppCompatActivity {

    FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.generic_activity_login_navigator);

        Objects.requireNonNull(getSupportActionBar()).hide();

        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_manager_at_login, LoginFragment.class, null)
                .setReorderingAllowed(true)
                .addToBackStack("login_frg") // name can be null
                .commit();
    }
}
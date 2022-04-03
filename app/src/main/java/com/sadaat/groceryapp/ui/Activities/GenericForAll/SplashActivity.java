package com.sadaat.groceryapp.ui.Activities.GenericForAll;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sadaat.groceryapp.R;
import com.sadaat.groceryapp.handler.LoginIntentHandler;
import com.sadaat.groceryapp.models.Users.UserModel;
import com.sadaat.groceryapp.temp.FirebaseDataKeys;
import com.sadaat.groceryapp.temp.UserLive;

import java.util.Objects;

public class SplashActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.generic_activity_splash);

        Objects.requireNonNull(getSupportActionBar()).hide();

        String uid = FirebaseAuth.getInstance().getUid();


        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();

        if (uid == null || uid.isEmpty()) {
            callToActionOnUserDataNotFound();
        } else {
            performSplashOnUserdataFound(uid);
        }

   }

    private void performSplashOnUserdataFound(String uid) {

        FirebaseFirestore
                .getInstance()
                .collection(new FirebaseDataKeys().getUsersRef())
                .document(uid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @SuppressLint("StaticFieldLeak")
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            UserLive.currentLoggedInUser = task.getResult().toObject(UserModel.class);
                            startActivity(new LoginIntentHandler(SplashActivity.this, UserLive.currentLoggedInUser.getUserType()));
                            finish();
                        }
                    }
                });


    }

    private void callToActionOnUserDataNotFound() {
        startActivity(new Intent(SplashActivity.this, LoginNavigatorActivity.class));
        finish();
    }

}
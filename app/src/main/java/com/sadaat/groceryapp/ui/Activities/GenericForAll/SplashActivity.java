package com.sadaat.groceryapp.ui.Activities.GenericForAll;

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
import com.sadaat.groceryapp.models.UserModel;
import com.sadaat.groceryapp.syncronizer.BackgroundExecutor;
import com.sadaat.groceryapp.temp.FirebaseDataKeys;
import com.sadaat.groceryapp.temp.UserLive;

import java.util.Objects;

public class SplashActivity extends AppCompatActivity {

    BackgroundExecutor backgroundExecutor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.generic_activity_splash);

        Objects.requireNonNull(getSupportActionBar()).hide();

        String uid = FirebaseAuth.getInstance().getUid();

        if (uid == null || uid.isEmpty()) {
            callToActionOnUserDataNotFound();
        } else {
            performSplashOnUserdataFound(uid);
        }

        backgroundExecutor.startExecution();
    }

    private void performSplashOnUserdataFound(String uid) {

        backgroundExecutor = new BackgroundExecutor() {
            @Override
            protected Object backgroundTaskOnThread(Object... objects) {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void beforeTask() {

            }

            @Override
            protected void afterExecutionOnMainThread(Object o) {
                FirebaseFirestore
                        .getInstance()
                        .collection(new FirebaseDataKeys().getUsersRef())
                        .document(uid)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
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
        };
    }

    private void callToActionOnUserDataNotFound() {
        backgroundExecutor = new BackgroundExecutor() {
            @Override
            protected Object backgroundTaskOnThread(Object... objects) {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void beforeTask() {

            }

            @Override
            protected void afterExecutionOnMainThread(Object o) {
                startActivity(new Intent(SplashActivity.this, LoginNavigatorActivity.class));
                finish();
            }
        };
    }

}
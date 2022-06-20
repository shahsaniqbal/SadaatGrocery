package com.sadaat.groceryapp.handler;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sadaat.groceryapp.models.LeadsModel;
import com.sadaat.groceryapp.temp.FirebaseDataKeys;

import java.util.Date;

public abstract class LeadsActionHandler {
    private DocumentReference reference;

    public LeadsActionHandler() {
        this.reference = FirebaseFirestore
                .getInstance()
                .collection(new FirebaseDataKeys().getLeadsRef())
                .document("main-leads-for-admin");
    }

    public abstract  void onSuccessCompleteAction();
    public abstract  void onCancelledAction();

    public void addAction(final String ACTION){
        reference
                .update("leads", FieldValue.arrayUnion(
                        new LeadsModel( new Date(), ACTION)
                ))
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) onSuccessCompleteAction();
                    else if (task.isCanceled()) onCancelledAction();
                });
    }
}

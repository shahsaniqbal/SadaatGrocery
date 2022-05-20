package com.sadaat.groceryapp.ui.Fragments.UserBased.Admin;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.sadaat.groceryapp.R;
import com.sadaat.groceryapp.adapters.admin.DeliveryBoyAppCreditsListAdapter;
import com.sadaat.groceryapp.models.SuggestionModel;
import com.sadaat.groceryapp.models.Users.UserModel;
import com.sadaat.groceryapp.temp.FirebaseDataKeys;
import com.sadaat.groceryapp.temp.UserTypes;

import java.util.ArrayList;

public class AppCreditsFragmentAdmin extends Fragment implements DeliveryBoyAppCreditsListAdapter.OnClickListener {

    RecyclerView recyclerDeliveryBoys;
    RecyclerView.LayoutManager manager;
    DeliveryBoyAppCreditsListAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.admin_fragment_appcredits, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerDeliveryBoys = view.findViewById(R.id.recycler_d_boys_credits);
        manager = new LinearLayoutManager(this.requireActivity());
        adapter = new DeliveryBoyAppCreditsListAdapter(new ArrayList<>(), this);

        recyclerDeliveryBoys.setLayoutManager(manager);
        recyclerDeliveryBoys.setAdapter(adapter);

        refreshData();

        Toast.makeText(AppCreditsFragmentAdmin.this.requireActivity(), "Hold the \"Add Receeipt\" button in case you have received the payment (in negative) from the Delivery Boy", Toast.LENGTH_LONG).show();
    }

    private void refreshData() {
        adapter.deleteAll();
        FirebaseFirestore
                .getInstance()
                .collection(new FirebaseDataKeys().getUsersRef())
                .whereEqualTo("userType", UserTypes.DeliveryBoy)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {

                            return;
                        }

                        if (value != null && value.getDocuments().size() > 0) {

                            adapter.deleteAll();
                            for (DocumentSnapshot d :
                                    value.getDocuments()) {
                                adapter.addItem(d.toObject(UserModel.class));
                            }

                        } else {

                        }
                    }
                });
    }

    @Override
    public void onReceiptButtonClick(int position, String deliveryBoyUID, double appCredits, String name) {
        FirebaseFirestore
                .getInstance()
                .collection(new FirebaseDataKeys().getUsersRef())
                .document(deliveryBoyUID)
                .update("credits.owningCredits", FieldValue.increment(((-1) * appCredits)));
    }
}
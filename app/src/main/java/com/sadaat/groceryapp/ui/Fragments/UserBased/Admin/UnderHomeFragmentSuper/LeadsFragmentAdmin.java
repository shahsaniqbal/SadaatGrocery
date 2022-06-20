package com.sadaat.groceryapp.ui.Fragments.UserBased.Admin.UnderHomeFragmentSuper;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.sadaat.groceryapp.R;
import com.sadaat.groceryapp.adapters.admin.LeadsDisplayAdapterAdmin;
import com.sadaat.groceryapp.models.LeadsHolder;
import com.sadaat.groceryapp.models.LeadsModel;
import com.sadaat.groceryapp.models.SuggestionModel;
import com.sadaat.groceryapp.temp.FirebaseDataKeys;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Objects;

public class LeadsFragmentAdmin extends Fragment {

    RecyclerView leadsRecycler;
    RecyclerView.LayoutManager manager;
    LeadsDisplayAdapterAdmin leadsDisplayAdapterAdmin;

    MaterialTextView txvSession;
    MaterialCardView cardResetSession;


    public LeadsFragmentAdmin() {
        // Required empty public constructor
    }

    public static LeadsFragmentAdmin newInstance(String param1, String param2) {
        return new LeadsFragmentAdmin();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.admin_fragment_leads, container, false);
    }


    @Override
    public void onPause() {
        super.onPause();
        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).setSubtitle(null);
    }

    @Override
    public void onResume() {
        super.onResume();
        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).setSubtitle("Leads");
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        leadsRecycler = view.findViewById(R.id.recycler_leads);
        manager = new LinearLayoutManager(LeadsFragmentAdmin.this.requireActivity());
        leadsDisplayAdapterAdmin = new LeadsDisplayAdapterAdmin(new ArrayList<>());

        txvSession = view.findViewById(R.id.txv_session_number);
        cardResetSession = view.findViewById(R.id.addSession);

        leadsRecycler.setLayoutManager(manager);
        leadsRecycler.setAdapter(leadsDisplayAdapterAdmin);

        cardResetSession.setOnClickListener(v->{
            FirebaseFirestore
                    .getInstance()
                    .collection(new FirebaseDataKeys().getLeadsRef())
                    .document("main-leads-for-admin")
                    .update("leads", new ArrayList<LeadsModel>(),
                            "session", FieldValue.increment(1));
        });

        FirebaseFirestore
                .getInstance()
                .collection(new FirebaseDataKeys().getLeadsRef())
                .document("main-leads-for-admin")
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Log.w("TAG", "Listen failed. SuggestionFragmentAdmin", error);
                            return;
                        }

                        if (value != null && value.exists()) {

                            LeadsHolder holder = value.toObject(LeadsHolder.class);

                            leadsDisplayAdapterAdmin.deleteAll();

                            if (holder != null) {
                                txvSession.setText(MessageFormat.format("{0}", holder.getSession()));
                                for (LeadsModel m:
                                     holder.getLeads()) {
                                    leadsDisplayAdapterAdmin.addItem(m);
                                }
                            }

                        } else {
                            Log.d("TAG", "source file SuggestionFragmentAdmin" + " data: null");
                        }
                    }
                });

        FirebaseFirestore
                .getInstance()
                .collection(new FirebaseDataKeys().getLeadsRef())

                .get()
                .addOnCompleteListener(task->{
                    if (task.isSuccessful()){

                    }
                });
    }
}
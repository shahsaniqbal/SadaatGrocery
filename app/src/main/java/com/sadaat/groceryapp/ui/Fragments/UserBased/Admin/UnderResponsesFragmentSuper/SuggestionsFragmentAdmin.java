package com.sadaat.groceryapp.ui.Fragments.UserBased.Admin.UnderResponsesFragmentSuper;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.sadaat.groceryapp.R;
import com.sadaat.groceryapp.adapters.admin.SuggestionsDisplayAdapterAdmin;
import com.sadaat.groceryapp.models.SuggestionModel;
import com.sadaat.groceryapp.temp.FirebaseDataKeys;

import java.util.ArrayList;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SuggestionsFragmentAdmin#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SuggestionsFragmentAdmin extends Fragment implements SuggestionsDisplayAdapterAdmin.OnClickListener {
    RecyclerView recyclerView;
    RecyclerView.LayoutManager manager;
    SuggestionsDisplayAdapterAdmin adapter;

    public SuggestionsFragmentAdmin() {
        // Required empty public constructor
    }


    public static SuggestionsFragmentAdmin newInstance(String param1, String param2) {
        return new SuggestionsFragmentAdmin();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.admin_fragment_suggestions, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recycler);
        manager = new LinearLayoutManager(SuggestionsFragmentAdmin.this.requireActivity());
        adapter = new SuggestionsDisplayAdapterAdmin(new ArrayList<>(), this);


        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);

        FirebaseFirestore
                .getInstance()
                .collection(new FirebaseDataKeys().getSuggestionsRef())
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Log.w("TAG", "Listen failed. SuggestionFragmentAdmin", error);
                            return;
                        }

                        if (value != null && value.getDocuments().size() > 0) {

                            adapter.deleteAll();
                            for (DocumentSnapshot d :
                                    value.getDocuments()) {
                                adapter.addItem(d.toObject(SuggestionModel.class));
                            }

                        } else {
                            Log.d("TAG", "source file SuggestionFragmentAdmin" + " data: null");
                        }
                    }
                });
    }

    @Override
    public void onPause() {
        super.onPause();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle(null);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle("Suggestions");
    }

    @Override
    public void onCallButtonClick(String mobileNumber) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + mobileNumber));
        startActivity(intent);
    }

    @Override
    public void onPostReplyButtonClick(int position, String id, String reply, Date replyDate) {
        FirebaseFirestore
                .getInstance()
                .collection(new FirebaseDataKeys().getComplaintsRef())
                .document(id)
                .update(
                        "replyMessage", reply,
                        "replyDate", replyDate
                );
    }
}
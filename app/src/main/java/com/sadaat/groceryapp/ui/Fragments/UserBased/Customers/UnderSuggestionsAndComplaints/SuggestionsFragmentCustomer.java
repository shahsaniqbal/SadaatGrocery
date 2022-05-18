package com.sadaat.groceryapp.ui.Fragments.UserBased.Customers.UnderSuggestionsAndComplaints;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.sadaat.groceryapp.R;
import com.sadaat.groceryapp.adapters.customer.SuggestionsDisplayAdapterCustomer;
import com.sadaat.groceryapp.models.SuggestionModel;
import com.sadaat.groceryapp.temp.FirebaseDataKeys;
import com.sadaat.groceryapp.temp.UserLive;

import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SuggestionsFragmentCustomer#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SuggestionsFragmentCustomer extends Fragment {

    MaterialCardView fabAddSuggestions;
    CustomPopupViewHolder viewHolder;

    RecyclerView recyclerView;
    RecyclerView.LayoutManager manager;
    SuggestionsDisplayAdapterCustomer adapter;

    AlertDialog.Builder dialogueBuilder;
    AlertDialog dialogueBox;
    View popupView;


    public SuggestionsFragmentCustomer() {
        // Required empty public constructor
    }

    public static SuggestionsFragmentCustomer newInstance() {

        return new SuggestionsFragmentCustomer();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.customer_fragment_suggestions, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init_(view);

        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);

        FirebaseFirestore
                .getInstance()
                .collection(new FirebaseDataKeys().getSuggestionsRef())
                .whereEqualTo("uid", UserLive.currentLoggedInUser.getUID())
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Log.w("TAG", "Listen failed.", error);
                            return;
                        }

                        if (value != null && value.getDocuments().size()>0) {

                            adapter.deleteAll();
                            for (DocumentSnapshot d :
                                    value.getDocuments()) {
                                adapter.addItem(d.toObject(SuggestionModel.class));
                            }

                        } else {
                            Log.d("TAG", "source file SuggestionFragmentCustomer" + " data: null");
                        }
                    }
                });

        fabAddSuggestions.setOnClickListener(v -> {
            dialogueBox.show();
        });
        viewHolder.getBtnPostSuggestion().setOnClickListener(v -> {
            if (viewHolder.analyzeInputs(true)){

                String new_id = "S_"+ UserLive.currentLoggedInUser.getUID().substring(0,5)+"_"+new Date().getDate()+"_"+new Date().getTime();
                FirebaseFirestore.getInstance()
                        .collection(new FirebaseDataKeys().getSuggestionsRef())
                        .document(new_id)
                        .set(new SuggestionModel(
                                new_id,
                                UserLive.currentLoggedInUser.getUID(),
                                UserLive.currentLoggedInUser.getFullName(),
                                UserLive.currentLoggedInUser.getMobileNumber(),
                                UserLive.currentLoggedInUser.getEmailAddress(),
                                Objects.requireNonNull(viewHolder.getTxvTitle().getText()).toString(),
                                Objects.requireNonNull(viewHolder.getTxvDescription().getText()).toString(),
                                new Date(),
                                (double) viewHolder.getRatingBar().getRating(),
                                "",
                                null
                        ))
                        .addOnCompleteListener(t->{
                            if (t.isSuccessful()){
                                dialogueBox.dismiss();
                            }
                            else if (t.isCanceled()){
                                Toast.makeText(SuggestionsFragmentCustomer.this.requireActivity(), "Suggestion/Feedback POST Error", Toast.LENGTH_SHORT).show();
                                dialogueBox.dismiss();
                            }
                        });
            }
        });
    }

    private void init_(View view) {
        fabAddSuggestions = view.findViewById(R.id.fab);
        recyclerView = view.findViewById(R.id.recycler);
        manager = new LinearLayoutManager(SuggestionsFragmentCustomer.this.requireActivity());
        dialogueBuilder = new AlertDialog.Builder(SuggestionsFragmentCustomer.this.requireActivity());
        popupView = this.getLayoutInflater().inflate(R.layout.customer_popup_add_suggestion, null, false);
        dialogueBuilder.setView(popupView);
        dialogueBox = dialogueBuilder.create();
        adapter = new SuggestionsDisplayAdapterCustomer(new ArrayList<>());

        viewHolder = new CustomPopupViewHolder(popupView);
    }

    private class CustomPopupViewHolder {

        private final TextInputEditText txvTitle;
        private final TextInputEditText txvDescription;
        private final MaterialButton btnPostSuggestion;
        private final RatingBar ratingBar;

        public CustomPopupViewHolder(View v) {
            txvTitle = v.findViewById(R.id.edx_suggestion_title);
            txvDescription = v.findViewById(R.id.edx_suggestion_desc);
            btnPostSuggestion = v.findViewById(R.id.addSuggestion);
            ratingBar = v.findViewById(R.id.simpleRatingBar);
        }

        public TextInputEditText getTxvTitle() {
            return txvTitle;
        }

        public TextInputEditText getTxvDescription() {
            return txvDescription;
        }

        public MaterialButton getBtnPostSuggestion() {
            return btnPostSuggestion;
        }

        public RatingBar getRatingBar() {
            return ratingBar;
        }

        public boolean analyzeInputs(boolean b) {
            if (txvTitle.getText().toString().isEmpty()){
                txvTitle.setError("Title can't be Empty");
                return false;
            }

            else if (txvDescription.getText().toString().isEmpty()){
                txvDescription.setError("Description can't be Empty");
                return false;
            }

            else{
                txvTitle.setError(null);
                txvDescription.setError(null);
                return true;
            }
        }
    }
}
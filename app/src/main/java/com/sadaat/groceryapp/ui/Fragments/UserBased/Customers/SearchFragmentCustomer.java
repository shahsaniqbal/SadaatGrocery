package com.sadaat.groceryapp.ui.Fragments.UserBased.Customers;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.sadaat.groceryapp.R;
import com.sadaat.groceryapp.adapters.customer.SearchAdapter;
import com.sadaat.groceryapp.models.Items.ItemModel;
import com.sadaat.groceryapp.temp.FirebaseDataKeys;
import com.sadaat.groceryapp.ui.Fragments.Generic.DetailedItemFragmentGeneric;
import com.sadaat.groceryapp.ui.Loaders.LoadingDialogue;

import java.util.ArrayList;

public class SearchFragmentCustomer extends Fragment implements SearchAdapter.OnClickListeners {

    private LoadingDialogue dialogue;
    private ArrayList<ItemModel> models;
    private SearchAdapter adapter;
    private AutoCompleteTextView textView;

    public SearchFragmentCustomer() {
        // Required empty public constructor
    }

    public static SearchFragmentCustomer newInstance() {
        return new SearchFragmentCustomer();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.customer_fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        textView = view.findViewById(R.id.search);

        models = new ArrayList<>();
        adapter = new SearchAdapter(SearchFragmentCustomer.this.requireActivity(), models, this);


        textView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.e("ITEM AT POSITION", "" + i);
                textView.setText(((ItemModel) adapter.getItem(i)).toFullString());
            }
        });


        dialogue = new LoadingDialogue(this.requireActivity());

        dialogue.show("", "");

        FirebaseFirestore.getInstance()
                .collection(new FirebaseDataKeys().getItemsRef())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        dialogue.dismiss();
                        if (queryDocumentSnapshots.getDocuments().size() > 0) {
                            for (DocumentSnapshot d :
                                    queryDocumentSnapshots) {
                                adapter.add(d.toObject(ItemModel.class));
                            }

                            textView.setAdapter(adapter);

                        }
                    }
                });

    }

    @Override
    public void onItemClick(int i, ItemModel m) {
        SearchFragmentCustomer
                .this
                .requireActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .addToBackStack("search")
                .replace(R.id.flFragmentCustomer, DetailedItemFragmentGeneric.newInstance(m), null)
                .commit();
    }
}
package com.sadaat.groceryapp.ui.Fragments.UserBased.Customers.UnderSuggestionsAndComplaints;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.sadaat.groceryapp.R;
import com.sadaat.groceryapp.adapters.customer.ComplaintsDisplayAdapterCustomer;
import com.sadaat.groceryapp.models.ComplaintsModel;
import com.sadaat.groceryapp.models.orders.OrderModel;
import com.sadaat.groceryapp.temp.FirebaseDataKeys;
import com.sadaat.groceryapp.ui.Fragments.Generic.DetailedOrderViewFragmentGeneric;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ComplaintsFragmentCustomer#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ComplaintsFragmentCustomer extends Fragment implements ComplaintsDisplayAdapterCustomer.OnClickListener {

    private RecyclerView recyclerView;
    private LinearLayoutManager manager;
    private ComplaintsDisplayAdapterCustomer adapter;

    public ComplaintsFragmentCustomer() {
        // Required empty public constructor
    }

    public static ComplaintsFragmentCustomer newInstance() {

        return new ComplaintsFragmentCustomer();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.customer_fragment_complaints, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recycler);
        adapter = new ComplaintsDisplayAdapterCustomer(new ArrayList<>(), this);
        manager = new LinearLayoutManager(this.requireActivity());

        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);

        FirebaseFirestore
                .getInstance()
                .collection(new FirebaseDataKeys().getComplaintsRef())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot q :
                                    task.getResult()) {

                                adapter.addItem(q.toObject(ComplaintsModel.class));
                            }
                        }
                    }
                });

    }

    @Override
    public void onShowFullOrderDetailsButtonClick(int position, String thatOrderID) {

        FirebaseFirestore
                .getInstance()
                .collection(new FirebaseDataKeys().getOrdersRef())
                .document(thatOrderID)
                .get()
                .addOnCompleteListener(t -> {
                    if (t.isSuccessful()) {
                        ComplaintsFragmentCustomer
                                .this
                                .requireActivity()
                                .getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.area_suggestions_complaints_fragment, DetailedOrderViewFragmentGeneric.newInstance(t.getResult().toObject(OrderModel.class)))
                                .addToBackStack("complaints")
                                .commit();
                    }
                });


    }
}
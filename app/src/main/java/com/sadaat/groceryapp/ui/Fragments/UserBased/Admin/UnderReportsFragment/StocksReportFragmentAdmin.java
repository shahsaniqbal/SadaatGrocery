package com.sadaat.groceryapp.ui.Fragments.UserBased.Admin.UnderReportsFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.sadaat.groceryapp.R;
import com.sadaat.groceryapp.adapters.admin.reports.StocksReportDisplayAdapterAdmin;
import com.sadaat.groceryapp.models.Items.ItemModel;
import com.sadaat.groceryapp.temp.FirebaseDataKeys;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Objects;

/**
 * Stocks Report ->
 * Item Name, Stocks (Current Stock) <- List
 * Total Stock Retail Price
 * Total Stock Discounted Price
 */
public class StocksReportFragmentAdmin extends Fragment {

    RecyclerView recyclerView;
    MaterialTextView txvTotalRetail;
    MaterialTextView txvTotalDiscounted;

    RecyclerView.LayoutManager manager;
    StocksReportDisplayAdapterAdmin adapter;

    double totalRetail = 0.0;
    double totalDiscounted = 0.0;

    public StocksReportFragmentAdmin() {
    }

    public static StocksReportFragmentAdmin newInstance() {
        return new StocksReportFragmentAdmin();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.admin_fragment_reports_stocks_report, container, false);
    }

    @Override
    public void onPause() {
        super.onPause();
        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).setSubtitle(null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).setSubtitle("Stocks");

        recyclerView = view.findViewById(R.id.recycler);
        txvTotalRetail = view.findViewById(R.id.total_retail);
        txvTotalDiscounted = view.findViewById(R.id.total_discounted);

        manager = new LinearLayoutManager(this.requireActivity());
        adapter = new StocksReportDisplayAdapterAdmin(new ArrayList<>());
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);

        totalRetail = 0.0;
        totalDiscounted = 0.0;

        FirebaseFirestore.getInstance()
                .collection(new FirebaseDataKeys().getItemsRef())
                .orderBy("otherDetails.stock", Query.Direction.ASCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot d : task.getResult()) {
                                ItemModel m = d.toObject(ItemModel.class);
                                adapter.addItem(m);
                                totalRetail += (m.getPrices().getRetailPrice() * m.getOtherDetails().getStock());
                                totalDiscounted += (m.getPrices().getDiscountedPrice() * m.getOtherDetails().getStock());
                            }
                            computeAndUpdateTotalsData();
                        }
                    }
                });

    }

    private void computeAndUpdateTotalsData() {
        txvTotalRetail.setText(MessageFormat.format("{0} Rs.", totalRetail));
        txvTotalDiscounted.setText(MessageFormat.format("{0} Rs.", totalDiscounted));
    }
/*

    @Override
    public void onPause() {
        super.onPause();

        requireActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .remove(this)
                .commit();
    }

    @Override
    public void onStop() {
        super.onStop();
        requireActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .remove(this)
                .commit();
    }
*/

}
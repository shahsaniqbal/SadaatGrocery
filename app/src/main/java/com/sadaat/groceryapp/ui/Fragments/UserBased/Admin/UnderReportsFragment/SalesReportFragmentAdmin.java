package com.sadaat.groceryapp.ui.Fragments.UserBased.Admin.UnderReportsFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.sadaat.groceryapp.R;
import com.sadaat.groceryapp.adapters.admin.reports.SalesReportDisplayAdapterAdmin;
import com.sadaat.groceryapp.models.ItemIDListModel;
import com.sadaat.groceryapp.models.Items.ItemDetailMiniModelForSalesReport;
import com.sadaat.groceryapp.models.Items.ItemModel;
import com.sadaat.groceryapp.models.cart.CartItemModel;
import com.sadaat.groceryapp.models.orders.OrderModel;
import com.sadaat.groceryapp.temp.FirebaseDataKeys;
import com.sadaat.groceryapp.temp.order_management.OrderStatus;
import com.sadaat.groceryapp.temp.order_management.PaymentMethods;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;

import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class SalesReportFragmentAdmin extends Fragment {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager manager;
    SalesReportDisplayAdapterAdmin adapter;

    double totalRetailSale = 0.0;
    double totalDiscountedSale = 0.0;

    MaterialTextView txvTotalSales;
    MaterialTextView txvTotalDiscounts;
    MaterialTextView txvTotalDiscountPercentage;
    MaterialTextView txvCardPaymentsPercentage;
    MaterialCardView cardIndicateTopSelling;
    PieChart chart;

    long cashPayments = 0;
    long cardPayments = 0;

    public SalesReportFragmentAdmin() {
        // Required empty public constructor
    }

    public static SalesReportFragmentAdmin newInstance() {
        return new SalesReportFragmentAdmin();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.admin_fragment_reports_sales, container, false);
    }

    @Override
    public void onPause() {
        super.onPause();
        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).setSubtitle(null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).setSubtitle("Sales");

        final Map<String, Long>[] saleItems = new HashMap[1];
        recyclerView = view.findViewById(R.id.recycler);

        txvTotalSales = view.findViewById(R.id.total_retail_sale);
        txvTotalDiscounts = view.findViewById(R.id.total_retail_discounts);
        txvTotalDiscountPercentage = view.findViewById(R.id.total_discounted_percentage);
        txvCardPaymentsPercentage = view.findViewById(R.id.admin_report_card_percentage);
        cardIndicateTopSelling = view.findViewById(R.id.cardIndiate);
        chart = view.findViewById(R.id.pie_chart_orders_report);

        manager = new LinearLayoutManager(this.requireActivity());
        adapter = new SalesReportDisplayAdapterAdmin(new ArrayList<>());

        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);

        FirebaseFirestore
                .getInstance()
                .collection(new FirebaseDataKeys().getOrdersRef())
                .whereEqualTo("currentStatus", OrderStatus.DELIVERED)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot d : task.getResult().getDocuments()) {
                                for (String k : Objects.requireNonNull(d.toObject(OrderModel.class)).getOrderDetails().getCartItems().keySet()) {
                                    saleItems[0] = saleItemCalculator(saleItems[0], d.toObject(OrderModel.class).getOrderDetails().getCartItems().get(k));
                                }

                                totalRetailSale += Objects.requireNonNull(d.toObject(OrderModel.class)).getTotalOrderAmountInRetail();
                                totalDiscountedSale += (Objects.requireNonNull(d.toObject(OrderModel.class)).getTotalOrderAmountInRetail() - Objects.requireNonNull(d.toObject(OrderModel.class)).getReleasingAppCredits());
                                String method = Objects.requireNonNull(d.toObject(OrderModel.class)).getPaymentThrough().getPaymentThroughMethod();
                                if (method.equalsIgnoreCase(PaymentMethods.COD)){
                                    cashPayments++;
                                }
                                else{
                                    cardPayments++;
                                }
                            }
                        }
                        updateDataToViews(saleItems[0]);
                    }
                });

        cardIndicateTopSelling.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (adapter.getLocalDataSet().size() > 0 && adapter.getLocalDataSet().size() == saleItems[0].size()) {
                    ArrayList<String> newList = new ArrayList<>();
                    int startIndex = 0;
                    int endIndex;

                    if (adapter.getLocalDataSet().size() < 5) {
                        endIndex = adapter.getLocalDataSet().size()-1;
                    } else {
                        endIndex=4;
                    }

                    for (int i = startIndex; i <= endIndex; i++) {
                        newList.add(adapter.getLocalDataSet().get(i).getItemID());
                    }

                    FirebaseFirestore.getInstance()
                            .collection(new FirebaseDataKeys().getTopSellingRef())
                            .document("Items")
                            .set(new ItemIDListModel(newList))
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(SalesReportFragmentAdmin.this.requireActivity(), "Indicated", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }

            }
        });
    }

    private void updateDataToViews(Map<String, Long> saleItem) {

        txvCardPaymentsPercentage.setText(
                new DecimalFormat("0.00").format(((double) (cardPayments * 100) / (double) (cashPayments+cardPayments)))
        );

        chart.addPieSlice(
                new PieModel(
                        "CASH",
                        cashPayments,
                        getResources().getColor(android.R.color.black)));
        chart.addPieSlice(
                new PieModel(
                        "CARD",
                        cardPayments,
                        getResources().getColor(R.color.grey)));

        chart.startAnimation();

        Comparator<Map.Entry<String, Long>> valueComparator = new Comparator<Map.Entry<String, Long>>() {
            @Override
            public int compare(Map.Entry<String, Long> e1, Map.Entry<String, Long> e2) {

                Long v1 = e1.getValue();
                Long v2 = e2.getValue();

                if ((long) v1 == (long) v2) {
                    return 0;
                } else if ((long) v1 < (long) v2) {
                    return -10;
                } else {
                    return +10;
                }

            }


        };

        List<Map.Entry<String, Long>> listOfEntries = new ArrayList<Map.Entry<String, Long>>(saleItem.entrySet());
        Collections.sort(listOfEntries, valueComparator);
        LinkedHashMap<String, Long> newItems
                = new LinkedHashMap<String, Long>(listOfEntries.size());

        for (Map.Entry<String, Long> entry : listOfEntries) {
            newItems.put(entry.getKey(), entry.getValue());
        }


        adapter.deleteAll();
        for (String k : newItems.keySet()) {
            FirebaseFirestore
                    .getInstance()
                    .collection(new FirebaseDataKeys().getItemsRef())
                    .document(k)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                ItemModel m = Objects.requireNonNull(task.getResult().toObject(ItemModel.class));
                                adapter.addItem(new ItemDetailMiniModelForSalesReport(k,
                                        m.getName(),
                                        m.getQty().toString(),
                                        newItems.get(k)));
                            }
                        }
                    });
        }

        txvTotalSales.setText(MessageFormat.format("{0} Rs.", totalRetailSale));
        txvTotalDiscounts.setText(MessageFormat.format("{0} Rs.", totalDiscountedSale));

        txvTotalDiscountPercentage.setText(
                new DecimalFormat("0.00 %").format((double) ((totalRetailSale - totalDiscountedSale)) / (double) totalRetailSale)
        );

        /*
        for (String payment : paymentMethods) {
            if (payment.equalsIgnoreCase(PaymentMethods.COD)) {
                cashPayments++;
            } else if (payment.equalsIgnoreCase(PaymentMethods.CARD)) {
                cardPayments++;
            }
        }
        */


    }

    Map<String, Long> saleItemCalculator(Map<String, Long> saleItems, CartItemModel model) {
        if (saleItems == null) {
            saleItems = new HashMap<>();
        }

        if (saleItems.containsKey(model.getModel().getID())) {
            long newValue;

            if (saleItems.get(model.getModel().getID()) > 0) {
                newValue = saleItems.get(model.getModel().getID()) + model.getQty();
            } else {
                newValue = model.getQty();
            }
            saleItems.put(model.getModel().getID(), newValue);
        } else {
            saleItems.put(model.getModel().getID(), (long) model.getQty());
        }
        return saleItems;
    }

}
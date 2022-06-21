package com.sadaat.groceryapp.ui.Fragments.UserBased.Admin.UnderReportsFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.sadaat.groceryapp.R;
import com.sadaat.groceryapp.models.orders.OrderModel;
import com.sadaat.groceryapp.temp.FirebaseDataKeys;
import com.sadaat.groceryapp.temp.order_management.OrderStatus;
import com.sadaat.groceryapp.ui.Loaders.LoadingDialogue;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;

import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.util.Objects;

public class OCRReportFragmentAdmin extends Fragment {

    LoadingDialogue dialogue;
    int initiatedOrders;
    int packingOrders;
    int deliveringOrders;
    int deliveredOrders;
    int cancelledOrders;
    int didNotReceiveOrders;

    PieChart chart;
    MaterialTextView txvInitiated;
    MaterialTextView txvPacking;
    MaterialTextView txvDelivering;
    MaterialTextView txvDelivered;
    MaterialTextView txvCancelled;
    MaterialTextView txvNotReceived;
    MaterialTextView txvOCRPercentage;

    public OCRReportFragmentAdmin() {
        // Required empty public constructor
    }

    public static OCRReportFragmentAdmin newInstance() {
        return new OCRReportFragmentAdmin();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.admin_fragment_reports_ocr, container, false);
    }

    @Override
    public void onPause() {
        super.onPause();
        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).setSubtitle(null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).setSubtitle("Order Cancellation Rate");

        ini_ordersCountToZero();

        dialogue = new LoadingDialogue(this.requireActivity());
        dialogue.show("Please Wait", "While We are fetching Order Details");

        chart = view.findViewById(R.id.pie_chart_orders_ocr_report);

        txvInitiated = view.findViewById(R.id.initiated_orders_count);
        txvPacking = view.findViewById(R.id.packing_orders_count);
        txvDelivering = view.findViewById(R.id.delivering_orders_count);
        txvDelivered = view.findViewById(R.id.delivered_orders_count);
        txvCancelled = view.findViewById(R.id.cancelled_orders_count);
        txvNotReceived = view.findViewById(R.id.not_received_orders_count);
        txvOCRPercentage = view.findViewById(R.id.admin_report_ocr_ocr_percentage);


        CollectionReference co = FirebaseFirestore
                .getInstance()
                .collection(new FirebaseDataKeys().getOrdersRef());

        co.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                ini_ordersCountToZero();
                for (QueryDocumentSnapshot d :
                        task.getResult()) {

                    OrderModel m = d.toObject(OrderModel.class);
                    if (m.getCurrentStatus().equals(OrderStatus.NOT_RECEIVED)) {
                        didNotReceiveOrders += 1;
                    }

                    else if (m.getCurrentStatus().equalsIgnoreCase(OrderStatus.INITIATED))
                        ++initiatedOrders;
                    else if (m.getCurrentStatus().equalsIgnoreCase(OrderStatus.PACKING))
                        ++packingOrders;
                    else if (m.getCurrentStatus().equalsIgnoreCase(OrderStatus.DELIVERING))
                        ++deliveringOrders;
                    else if (m.getCurrentStatus().equalsIgnoreCase(OrderStatus.DELIVERED))
                        ++deliveredOrders;
                    else if (m.getCurrentStatus().equalsIgnoreCase(OrderStatus.CANCELLED))
                        ++cancelledOrders;
                }
                dialogue.dismiss();
                updateDataIntoViews();
            }

        });

    }

    private void ini_ordersCountToZero() {
        initiatedOrders = 0;
        packingOrders= 0 ;
        deliveringOrders = 0;
        deliveredOrders = 0;
        cancelledOrders = 0;
        didNotReceiveOrders = 0;
    }

    private void updateDataIntoViews() {
        chart.clearChart();



        txvInitiated.setText(MessageFormat.format("{0}", initiatedOrders));
        txvPacking.setText(MessageFormat.format("{0}", packingOrders));
        txvDelivering.setText(MessageFormat.format("{0}", deliveringOrders));
        txvDelivered.setText(MessageFormat.format("{0}", deliveredOrders));
        txvCancelled.setText(MessageFormat.format("{0}", cancelledOrders));
        txvNotReceived.setText(MessageFormat.format("{0}", didNotReceiveOrders));


        chart.addPieSlice(
                new PieModel(
                        "Fulfilled",
                        (initiatedOrders + packingOrders + deliveredOrders + deliveringOrders),
                        getResources().getColor(android.R.color.holo_green_light)));
        chart.addPieSlice(
                new PieModel(
                        "Cancelled",
                        (cancelledOrders + didNotReceiveOrders),
                        getResources().getColor(R.color.scarlet_red_500)));

        double percentage = ((double) (cancelledOrders + didNotReceiveOrders) / (double) (initiatedOrders + packingOrders + deliveredOrders + deliveringOrders+ cancelledOrders + didNotReceiveOrders )) * 100;

        txvOCRPercentage.setText(new DecimalFormat("00.00").format(percentage));
        chart.startAnimation();
    }

}
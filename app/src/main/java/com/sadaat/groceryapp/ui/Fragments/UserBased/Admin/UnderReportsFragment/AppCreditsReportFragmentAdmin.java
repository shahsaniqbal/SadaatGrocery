package com.sadaat.groceryapp.ui.Fragments.UserBased.Admin.UnderReportsFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.sadaat.groceryapp.R;
import com.sadaat.groceryapp.models.Users.UserModel;
import com.sadaat.groceryapp.models.orders.OrderModel;
import com.sadaat.groceryapp.temp.FirebaseDataKeys;
import com.sadaat.groceryapp.temp.UserTypes;
import com.sadaat.groceryapp.ui.Loaders.LoadingDialogue;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;

import java.text.MessageFormat;
import java.util.Objects;

public class AppCreditsReportFragmentAdmin extends Fragment {

    LoadingDialogue dialogue;
    long customerPendingCredits = 0;
    long customerOwningCredits = 0;
    long deliveryBoyPendingCredits = 0;
    long deliveryBoyOwningCredits = 0;
    long total = 0;

    PieChart chart;

    MaterialTextView txvCustomerPending;
    MaterialTextView txvCustomerOwning;
    MaterialTextView txvDeliveryPending;
    MaterialTextView txvDeliveryOwning;
    MaterialTextView txvTotal;
    MaterialTextView txvPRPercentage;


    public AppCreditsReportFragmentAdmin() {
        // Required empty public constructor
    }

    public static AppCreditsReportFragmentAdmin newInstance() {

        return new AppCreditsReportFragmentAdmin();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.admin_fragment_reports_app_credits, container, false);


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        dialogue = new LoadingDialogue(AppCreditsReportFragmentAdmin.this.requireActivity());
        dialogue.show("Please Wait", "While We are generating App Credits Net Report");

        chart = view.findViewById(R.id.pie_chart_orders_ocr_report);

        txvCustomerPending = view.findViewById(R.id.customer_pending_credits);
        txvCustomerOwning = view.findViewById(R.id.customer_wallet_credits);
        txvDeliveryPending = view.findViewById(R.id.delivery_pending_credits);
        txvDeliveryOwning = view.findViewById(R.id.delivery_owning_credits);
        txvTotal = view.findViewById(R.id.total_credits);
        txvPRPercentage = view.findViewById(R.id.admin_report_p_r_percentage);

        CollectionReference oCo = FirebaseFirestore
                .getInstance()
                .collection(new FirebaseDataKeys().getOrdersRef());

        CollectionReference uCo = FirebaseFirestore
                .getInstance()
                .collection(new FirebaseDataKeys().getUsersRef());

        uCo.whereEqualTo("userType", UserTypes.Customer)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult().size() > 0) {
                                for (DocumentSnapshot d : task.getResult().getDocuments()) {
                                    customerPendingCredits += Objects.requireNonNull(d.toObject(UserModel.class)).getCredits().getPendingCredits();
                                    customerOwningCredits += Objects.requireNonNull(d.toObject(UserModel.class)).getCredits().getOwningCredits();
                                }
                            }
                        }
                        uCo.whereEqualTo("userType", UserTypes.DeliveryBoy)
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            if (task.getResult().size() > 0) {
                                                for (DocumentSnapshot d : task.getResult().getDocuments()) {
                                                    deliveryBoyPendingCredits += Objects.requireNonNull(d.toObject(UserModel.class)).getCredits().getPendingCredits();
                                                    deliveryBoyOwningCredits += Objects.requireNonNull(d.toObject(UserModel.class)).getCredits().getOwningCredits();
                                                }

                                                deliveryBoyPendingCredits = Math.abs(deliveryBoyPendingCredits);
                                                deliveryBoyOwningCredits = Math.abs(deliveryBoyOwningCredits);

                                            }
                                        }
                                        oCo.whereGreaterThan("releasedAppCredits", 0.0)
                                                .get()
                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                        if (task.isSuccessful()) {
                                                            if (task.getResult().size() > 0) {
                                                                for (DocumentSnapshot d : task.getResult().getDocuments()) {
                                                                    total += Objects.requireNonNull(d.toObject(OrderModel.class)).getReleasedAppCredits();
                                                                }

                                                            }
                                                        }

                                                        updateData();
                                                    }
                                                });
                                    }
                                });
                    }
                });
    }

    private void updateData() {
        chart.clearChart();
        this.dialogue.dismiss();

        txvCustomerOwning.setText(MessageFormat.format("{0} Rs.", customerOwningCredits));
        txvCustomerPending.setText(MessageFormat.format("{0} Rs.", customerPendingCredits));
        txvDeliveryOwning.setText(MessageFormat.format("{0} Rs.", deliveryBoyOwningCredits));
        txvDeliveryPending.setText(MessageFormat.format("{0} Rs.", deliveryBoyPendingCredits));
        txvTotal.setText(MessageFormat.format("{0} Rs.", total));

        txvPRPercentage.setText(MessageFormat.format("{0}", (((double) (customerOwningCredits + customerPendingCredits) / (double) (deliveryBoyOwningCredits + deliveryBoyPendingCredits))) * 100));

        chart.addPieSlice(
                new PieModel(
                        "ToReceive",
                        (float) (deliveryBoyOwningCredits + deliveryBoyPendingCredits),
                        getResources().getColor(android.R.color.holo_green_light)));
        chart.addPieSlice(
                new PieModel(
                        "ToPay",
                        (float) (customerOwningCredits + customerPendingCredits),
                        getResources().getColor(R.color.scarlet_red_500)));
    }
}
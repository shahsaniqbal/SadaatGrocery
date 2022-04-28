package com.sadaat.groceryapp.ui.Fragments.UserBased.Admin;

import static android.graphics.Typeface.BOLD;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.datatransport.runtime.scheduling.jobscheduling.SchedulerConfig;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.sadaat.groceryapp.R;
import com.sadaat.groceryapp.adapters.admin.DeliveryBoyListAdapter;
import com.sadaat.groceryapp.models.Users.UserModel;
import com.sadaat.groceryapp.models.orders.StatusModel;
import com.sadaat.groceryapp.temp.FirebaseDataKeys;
import com.sadaat.groceryapp.temp.UserTypes;
import com.sadaat.groceryapp.temp.order_management.OrderStatus;
import com.sadaat.groceryapp.ui.Fragments.UserBased.Admin.UnderHomeFragmentSuper.OrdersFragmentAdmin;

import java.util.ArrayList;
import java.util.Date;

public class DeliveryBoysListToSetForOrder extends Fragment implements DeliveryBoyListAdapter.OnClickListener {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private final CollectionReference usersRef = FirebaseFirestore.getInstance().collection(new FirebaseDataKeys().getUsersRef());

    private String mOrderID;
    private double mAppCreditsForOrder; //Actually Not App credits but remaining Order Payment to use as App Credits for D.Boy
    private RecyclerView recyclerView;
    private DeliveryBoyListAdapter adapter;
    private RecyclerView.LayoutManager manager;

    public DeliveryBoysListToSetForOrder() {
        // Required empty public constructor
    }

    public static DeliveryBoysListToSetForOrder newInstance(String orderID, double remainingPaymentAsAppCreditsForDeliveryBoy) {
        DeliveryBoysListToSetForOrder fragment = new DeliveryBoysListToSetForOrder();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, orderID);
        args.putDouble(ARG_PARAM2, remainingPaymentAsAppCreditsForDeliveryBoy);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mOrderID = getArguments().getString(ARG_PARAM1);
            mAppCreditsForOrder = getArguments().getDouble(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.admin_fragment_delivery_boys_list_to_set_for_order, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.recycler);
        manager = new LinearLayoutManager(this.requireActivity());
        adapter = new DeliveryBoyListAdapter(new ArrayList<>(), this);

        ((MaterialTextView)view.findViewById(R.id.delivery_boy_assign_total)).setTypeface(null, BOLD);
        ((MaterialTextView)view.findViewById(R.id.delivery_boy_assign_name)).setTypeface(null, BOLD);
        ((MaterialTextView)view.findViewById(R.id.delivery_boy_assign_queue)).setTypeface(null, BOLD);


        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);

        FirebaseFirestore.getInstance().collection(new FirebaseDataKeys().getUsersRef())
                .whereEqualTo("userType", UserTypes.DeliveryBoy)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot q : task.getResult()) {
                                adapter.addItem(q.toObject(UserModel.class));
                            }
                        }
                    }
                });
    }


    @Override
    public void onClick(int position, String deliveryBoyUID) {

        FirebaseFirestore
                .getInstance()
                .collection(new FirebaseDataKeys().getUsersRef())
                .document(deliveryBoyUID)
                .update(
                        "credits.pendingCredits", FieldValue.increment(((-1)* mAppCreditsForOrder))
                )
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> t) {
                        FirebaseFirestore
                                .getInstance()
                                .collection(new FirebaseDataKeys().getOrdersRef())
                                .document(mOrderID)
                                .update(
                                        "currentStatus", OrderStatus.DELIVERING,
                                        "currentDeliveryBoyUID", deliveryBoyUID,
                                        "statusUpdates", FieldValue.arrayUnion(new StatusModel(OrderStatus.DELIVERING, new Date()))
                                )
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            requireActivity().
                                                    getSupportFragmentManager()
                                                    .beginTransaction()
                                                    .replace(R.id.nav_host_fragment_content_main_activity_admin, OrdersFragmentAdmin.newInstance("", ""))
                                                    .commit();
                                        }
                                    }
                                });
                    }
                });

    }
}
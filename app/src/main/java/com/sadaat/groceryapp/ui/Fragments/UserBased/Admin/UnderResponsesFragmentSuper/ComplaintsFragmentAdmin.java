package com.sadaat.groceryapp.ui.Fragments.UserBased.Admin.UnderResponsesFragmentSuper;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.sadaat.groceryapp.R;
import com.sadaat.groceryapp.adapters.admin.ComplaintsDisplayAdapterAdmin;
import com.sadaat.groceryapp.models.ComplaintsModel;
import com.sadaat.groceryapp.models.orders.OrderModel;
import com.sadaat.groceryapp.temp.FirebaseDataKeys;
import com.sadaat.groceryapp.ui.Fragments.Generic.DetailedOrderViewFragmentGeneric;
import com.sadaat.groceryapp.ui.Fragments.Generic.DetailedUserViewFragmentGeneric;

import java.util.ArrayList;
import java.util.Date;

public class ComplaintsFragmentAdmin extends Fragment implements ComplaintsDisplayAdapterAdmin.OnClickListener {

    private RecyclerView recyclerView;
    private LinearLayoutManager manager;
    private ComplaintsDisplayAdapterAdmin adapter;

    public ComplaintsFragmentAdmin() {
        // Required empty public constructor
    }

    public static ComplaintsFragmentAdmin newInstance(String param1, String param2) {
        return new ComplaintsFragmentAdmin();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.admin_fragment_complaints, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recycler);
        adapter = new ComplaintsDisplayAdapterAdmin(new ArrayList<>(), this);
        manager  = new LinearLayoutManager(this.requireActivity());

        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);

        refetch();
    }

    private void refetch() {
        adapter.deleteAll();
        FirebaseFirestore
                .getInstance()
                .collection(new FirebaseDataKeys().getComplaintsRef())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot q :
                                    task.getResult()) {

                                adapter.addItem(q.toObject(ComplaintsModel.class));
                            }
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
        ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle("Complaints");
    }

    @Override
    public void onShowFullUserDetailsButtonClick(int position, String thatUID) {
        requireActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.flFragmentChild, DetailedUserViewFragmentGeneric.newInstance(thatUID))
                .addToBackStack("complaints")
                .commit();
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
                        ComplaintsFragmentAdmin
                                .this
                                .requireActivity()
                                .getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.flFragmentChild, DetailedOrderViewFragmentGeneric.newInstance(t.getResult().toObject(OrderModel.class)))
                                .addToBackStack("complaints")
                                .commit();
                    }
                });
    }

    @Override
    public void postReply(int toUpdateThePosition, String complaintID, String reply, boolean isIssueResolved, Date resolveDate) {
        FirebaseFirestore
                .getInstance()
                .collection(new FirebaseDataKeys().getComplaintsRef())
                .document(complaintID)
                .update(
                        "replyMessage", reply,
                        "resolved", isIssueResolved,
                        "replyResolvedDate", resolveDate
                )
                .addOnCompleteListener(t->{
                    if (t.isSuccessful()){
                        refetch();
                    }
                });
    }
}
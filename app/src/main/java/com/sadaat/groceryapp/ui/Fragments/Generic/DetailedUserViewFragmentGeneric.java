package com.sadaat.groceryapp.ui.Fragments.Generic;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.sadaat.groceryapp.R;
import com.sadaat.groceryapp.models.Users.UserModel;
import com.sadaat.groceryapp.models.orders.OrderModel;
import com.sadaat.groceryapp.temp.FirebaseDataKeys;
import com.sadaat.groceryapp.ui.Loaders.LoadingDialogue;

import java.text.MessageFormat;
import java.util.Objects;

public class DetailedUserViewFragmentGeneric extends Fragment {

    private static final String ARG_PARAM = "uid";

    MaterialTextView tName;
    MaterialTextView tEmail;
    MaterialTextView tMobile;
    MaterialTextView tPending;
    MaterialTextView tOwning;
    MaterialTextView tTotalSuggestions;
    MaterialTextView tTotalComplaints;
    MaterialTextView tTotalOrders;
    MaterialTextView tDeliveredOrders;
    MaterialTextView tMemberDate;
    MaterialTextView tAmountOrdered;
    MaterialTextView tCMI;
    ImageView iUserImage;

    LoadingDialogue dialogue;

    private String mUID;

    public DetailedUserViewFragmentGeneric() {
        // Required empty public constructor
    }

    public static DetailedUserViewFragmentGeneric newInstance(String mUID) {
        DetailedUserViewFragmentGeneric fragment = new DetailedUserViewFragmentGeneric();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM, mUID);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mUID = getArguments().getString(ARG_PARAM);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.generic_fragment_detailed_user_view, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init_(view);
        FirebaseFirestore.getInstance()
                .collection(new FirebaseDataKeys().getUsersRef())
                .document(mUID)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        dialogue.show("Please Wait", "Loading User Details");
                        setData(Objects.requireNonNull(documentSnapshot.toObject(UserModel.class)));
                    }
                });
    }

    private void setData(UserModel m) {

        tName.setText(m.getFullName());
        tEmail.setText(m.getEmailAddress());
        tMobile.setText(m.getMobileNumber());
        tPending.setText(MessageFormat.format("{0}", m.getCredits().getPendingCredits()));
        tOwning.setText(MessageFormat.format("{0}", m.getCredits().getOwningCredits()));
        tTotalSuggestions.setText(MessageFormat.format("{0}", m.getSuggestions().size()));
        tTotalComplaints.setText(MessageFormat.format("{0}", m.getComplaints().size()));
        tTotalOrders.setText(MessageFormat.format("{0}", m.getOrders().size()));
        String dateData = (m.getUserSignupDate()!=null)?m.getUserSignupDate().toString() : "No Data";
        tMemberDate.setText(dateData);


        FirebaseFirestore.getInstance()
                .collection(new FirebaseDataKeys().getOrdersRef())
                .whereEqualTo("uid", m.getUID())
                .get()
                .addOnSuccessListener(doc -> {
                    double amountOrdered = 0;
                    for (DocumentSnapshot s : doc.getDocuments()) {
                        amountOrdered += s.toObject(OrderModel.class).getTotalOrderAmountInRetail();
                    }

                    tDeliveredOrders.setText(MessageFormat.format("{0}", doc.size()));
                    tAmountOrdered.setText(MessageFormat.format("{0} Rs.", amountOrdered));

                    double finalAmountOrdered = amountOrdered;
                    FirebaseFirestore.getInstance()
                            .collection(new FirebaseDataKeys().getOrdersRef())
                            .get()
                            .addOnSuccessListener(document -> {

                                double totalOrdersAmountPublic = 0.0;
                                for (DocumentSnapshot ds : document.getDocuments()) {
                                    totalOrdersAmountPublic += ds.toObject(OrderModel.class).getTotalOrderAmountInRetail();
                                }

                                double cmi = 0.0;

                                cmi = (finalAmountOrdered / totalOrdersAmountPublic);

                                tCMI.setText(MessageFormat.format("{0} %", cmi*100));

                                dialogue.dismiss();

                            });

                })
                .addOnCanceledListener(new OnCanceledListener() {
                    @Override
                    public void onCanceled() {
                        dialogue.dismiss();
                        Toast.makeText(DetailedUserViewFragmentGeneric.this.requireActivity(), "Full Details Fetched Error", Toast.LENGTH_SHORT).show();
                    }
                });

        if (!m.getDetails()
                .getImageReference().equals("")) {

            StorageReference imgRef = FirebaseStorage
                    .getInstance(FirebaseDataKeys.STORAGE_BUCKET_ADDRESS)
                    .getReference()
                    .child(m.getDetails()
                            .getImageReference());

            final long ONE_MEGABYTE = 1024 * 1024;

            imgRef.getBytes(10 * ONE_MEGABYTE).addOnSuccessListener(bytes -> {
                iUserImage.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
            }).addOnFailureListener(exception -> {
                //Toast.makeText(mContext, "Image Load Failed, \n Leave it or use a new one", Toast.LENGTH_SHORT).show();
            });
        }


    }

    private void init_(View v) {

        dialogue = new LoadingDialogue(DetailedUserViewFragmentGeneric.this.requireActivity());

        tName = v.findViewById(R.id.txv_name);
        tEmail = v.findViewById(R.id.txv_email);
        tMobile = v.findViewById(R.id.txv_mobile);
        tPending = v.findViewById(R.id.txv_pCredits);
        tOwning = v.findViewById(R.id.txv_oCredits);
        tTotalSuggestions = v.findViewById(R.id.txv_tSuggestions);
        tTotalComplaints = v.findViewById(R.id.txv_tComplaints);
        tTotalOrders = v.findViewById(R.id.txv_tOrders);
        tDeliveredOrders = v.findViewById(R.id.txv_dOrders);
        tMemberDate = v.findViewById(R.id.txv_mDate);
        tAmountOrdered = v.findViewById(R.id.txv_amountOrdered);
        tCMI = v.findViewById(R.id.txv_cmi);

        iUserImage = v.findViewById(R.id.iUserImage);

    }
}
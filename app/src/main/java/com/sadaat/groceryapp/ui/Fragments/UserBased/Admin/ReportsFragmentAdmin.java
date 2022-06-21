package com.sadaat.groceryapp.ui.Fragments.UserBased.Admin;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sadaat.groceryapp.R;
import com.sadaat.groceryapp.ui.Fragments.UserBased.Admin.UnderReportsFragment.AppCreditsReportFragmentAdmin;
import com.sadaat.groceryapp.ui.Fragments.UserBased.Admin.UnderReportsFragment.OCRReportFragmentAdmin;
import com.sadaat.groceryapp.ui.Fragments.UserBased.Admin.UnderReportsFragment.SalesReportFragmentAdmin;
import com.sadaat.groceryapp.ui.Fragments.UserBased.Admin.UnderReportsFragment.StocksReportFragmentAdmin;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ReportsFragmentAdmin#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ReportsFragmentAdmin extends Fragment implements View.OnClickListener{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ReportsFragmentAdmin() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ReportsFragmentAdmin.
     */
    // TODO: Rename and change types and number of parameters
    public static ReportsFragmentAdmin newInstance(String param1, String param2) {
        ReportsFragmentAdmin fragment = new ReportsFragmentAdmin();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.admin_fragment_reports, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).setSubtitle(null);
        view.findViewById(R.id.admin_report_ocr).setOnClickListener(this);
        view.findViewById(R.id.admin_report_stocks).setOnClickListener(this);
        view.findViewById(R.id.admin_report_credits).setOnClickListener(this);
        view.findViewById(R.id.admin_report_sales).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.admin_report_ocr) {
            getChildFragmentManager()
                     .beginTransaction()
                     .replace(R.id.fl_reports, OCRReportFragmentAdmin.newInstance())
                     .addToBackStack("reports")
                     .commit();
        }
        else if (view.getId() == R.id.admin_report_stocks) {
            getChildFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fl_reports, StocksReportFragmentAdmin.newInstance())
                    .addToBackStack("reports")
                    .commit();
        }
        else if (view.getId() == R.id.admin_report_credits) {
            getChildFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fl_reports, AppCreditsReportFragmentAdmin.newInstance())
                    .addToBackStack("reports")
                    .commit();
        }
        else if (view.getId() == R.id.admin_report_sales) {
            getChildFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fl_reports, SalesReportFragmentAdmin.newInstance())
                    .addToBackStack("reports")
                    .commit();
        }
    }

}
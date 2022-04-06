package com.sadaat.groceryapp.ui.Fragments.UserBased.Customers;

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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.sadaat.groceryapp.R;
import com.sadaat.groceryapp.adapters.customer.category.CategoryScreenListAdapter;
import com.sadaat.groceryapp.adapters.customer.category.SubcategoryHorizontalAdapter;
import com.sadaat.groceryapp.models.categories.CategoriesModel;
import com.sadaat.groceryapp.temp.FirebaseDataKeys;
import com.sadaat.groceryapp.ui.Fragments.UserBased.Customers.passive.ItemsFragmentCustomer;

import java.util.ArrayList;

public class CategoriesFragmentCustomer extends Fragment implements SubcategoryHorizontalAdapter.OnSubcategoryItemCustomClickListener {

    final CollectionReference MENU_COLLECTION_REFERENCE = FirebaseFirestore.getInstance().collection(new FirebaseDataKeys().getMenuRef());

    private ArrayList<CategoriesModel> categoryList;
    private CategoryScreenListAdapter categoryAdapterCustomer;
    private RecyclerView recyclerViewCategories;
    private RecyclerView.LayoutManager layoutManager;

    public CategoriesFragmentCustomer() {
    }


    public static CategoriesFragmentCustomer newInstance(String param1, String param2) {
        CategoriesFragmentCustomer fragment = new CategoriesFragmentCustomer();
        Bundle args = new Bundle();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.customer_fragment_categories, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializations(view);

        recyclerViewCategories.setLayoutManager(layoutManager);
        recyclerViewCategories.setAdapter(categoryAdapterCustomer);

        MENU_COLLECTION_REFERENCE
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            categoryList = (ArrayList<CategoriesModel>) task.getResult().toObjects(CategoriesModel.class);
                            categoryAdapterCustomer.addAll(categoryList);
                        }
                    }
                });

    }

    private void initializations(View v) {
        categoryList = new ArrayList<>();
        categoryAdapterCustomer = new CategoryScreenListAdapter(CategoriesFragmentCustomer.this.requireActivity(),
                categoryList, this);
        recyclerViewCategories = v.findViewById(R.id.customer_recycler_categories);
        layoutManager = new LinearLayoutManager(CategoriesFragmentCustomer.this.requireActivity());
    }


    @Override
    public void onClickItemSubcategory(String mainCategoryID, String subcategoryID, String categoryTitle, String categoryDescription) {

        //Toast.makeText(this.requireActivity(), "Click Event is happening", Toast.LENGTH_SHORT).show();

        getParentFragmentManager()
                .beginTransaction()
                .replace(R.id.flFragmentCustomer, ItemsFragmentCustomer.newInstance(
                        mainCategoryID,
                        subcategoryID,
                        categoryTitle,
                        categoryDescription
                        )
                )
                .addToBackStack("categories")
                .commit();
    }
}
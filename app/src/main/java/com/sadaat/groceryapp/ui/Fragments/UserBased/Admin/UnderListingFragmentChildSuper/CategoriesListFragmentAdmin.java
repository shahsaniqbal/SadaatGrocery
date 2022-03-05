package com.sadaat.groceryapp.ui.Fragments.UserBased.Admin.UnderListingFragmentChildSuper;

import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.sadaat.groceryapp.R;
import com.sadaat.groceryapp.adapters.CategoriesItemAdapterAdmin;
import com.sadaat.groceryapp.models.CategoriesModel;
import com.sadaat.groceryapp.temp.FirebaseDataKeys;
import com.sadaat.groceryapp.ui.Loaders.LoadingDialogue;

import java.util.ArrayList;

public class CategoriesListFragmentAdmin extends Fragment {

    FloatingActionButton addCategoriesButton;

    PopupWindow popupWindow;
    /*FrameLayout frameLayout;
     */
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    RecyclerView recyclerView;
    RecyclerView.LayoutManager manager;

    CategoriesItemAdapterAdmin adapterAdmin;

    ArrayList<CategoriesModel> list;

    LoadingDialogue progressDialog;

    //   LayoutInflater layoutInflater;


    public CategoriesListFragmentAdmin() {
        // Required empty public constructor
    }

    public static CategoriesListFragmentAdmin newInstance() {
        CategoriesListFragmentAdmin fragment = new CategoriesListFragmentAdmin();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.admin_fragment_categories_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //     layoutInflater = (LayoutInflater) getActivity().getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);


        addCategoriesButton = view.findViewById(R.id.fab);
        recyclerView = view.findViewById(R.id.recycler_categories);
        manager = new LinearLayoutManager(CategoriesListFragmentAdmin.this.requireActivity());

        list = new ArrayList<>();
        progressDialog = new LoadingDialogue(CategoriesListFragmentAdmin.this.requireActivity());
        progressDialog.show("Please Wait", "While We Are Fetching Categories for you");

        adapterAdmin = new CategoriesItemAdapterAdmin(list, new CategoriesItemAdapterAdmin.CategoriesItemAdapterListener() {
            @Override
            public void onAddSubCategoryItemClick(View v, int position) {

            }

            @Override
            public void onUpdateSubCategoryItemClick(View v, int position) {

            }

            @Override
            public void onDeleteSubCategoryItemClick(View v, int position, String docID, String title) {

                firebaseFirestore
                        .collection(new FirebaseDataKeys().getMenuRef())
                        .document(docID)
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                adapterAdmin.deleteCategory(position);
                            }
                        });


            }
        });
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapterAdmin);


        backgroundExecutorForShowingData(view);

        //Popup Display
        addCategoriesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle("Listing -> Categories -> Add");

                view.setVisibility(View.INVISIBLE);
                View customView = getLayoutInflater().inflate(R.layout.admin_popup_add_categories, null, false);

                popupWindow = new PopupWindow(customView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                popupWindow.setFocusable(true);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    popupWindow.setTouchModal(true);
                }
                popupWindow.setElevation(5.0f);
                popupWindow.setAnimationStyle(R.anim.slide_in);

                //display the popup window
                popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

                Button addCategory = popupWindow.getContentView().findViewById(R.id.addCategory);

                EditText edxCate = popupWindow.getContentView().findViewById(R.id.cateTitle);
                EditText edxCateDescription = popupWindow.getContentView().findViewById(R.id.cateDesc);
                addCategory.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (edxCate.getText().toString().isEmpty()) {
                            edxCate.setError("Category or SubCategory Title Cannot be Empty");

                        } else {
                            String title = edxCate.getText().toString();
                            String desc = edxCateDescription.getText().toString();

                            firebaseFirestore
                                    .collection(new FirebaseDataKeys().getMenuRef())
                                    .add(new CategoriesModel(title, desc))
                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {

                                            ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle("Listing -> Categories");

                                            backgroundExecutorForShowingData(view);

                                            popupWindow.dismiss();
                                            view.setVisibility(View.VISIBLE);

                                        }
                                    });
                        }
                    }
                });
            }
        });

    }

    private void backgroundExecutorForShowingData(View view) {

        firebaseFirestore
                .collection(new FirebaseDataKeys().getMenuRef())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            adapterAdmin.deleteAll();
                            list.clear();
                            list = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                adapterAdmin.addCategory(
                                        new CategoriesModel(
                                                document.getId(),
                                                (String) document.get("title"),
                                                (String) document.get("description"),
                                                (Boolean) document.get("hasSubcategories"),
                                                (ArrayList<CategoriesModel>) document.get("subCategories")
                                        )

                                );
                            }

                            view.setVisibility(View.VISIBLE);

                            Toast.makeText(CategoriesListFragmentAdmin.this.requireActivity(), "" + list.size(), Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();

                        } else {

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
        ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle("Listing -> Categories");
    }
/*

    class CustomSync extends AsyncTask<View, Object, Object>{


        View mainView;

        @Override
        protected Object doInBackground(View... views) {

            mainView = (View) views[0];


            return null;
        }

        @Override
        protected void onPreExecute() {

            super.onPreExecute();


        }

        @Override
        protected void onPostExecute(Object o) {

            super.onPostExecute(o);

            adapterAdmin.notifyDataSetChanged();


        }
    }
*/

}
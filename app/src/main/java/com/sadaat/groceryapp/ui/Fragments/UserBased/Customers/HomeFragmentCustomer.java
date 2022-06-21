package com.sadaat.groceryapp.ui.Fragments.UserBased.Customers;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.islamkhsh.CardSliderViewPager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.sadaat.groceryapp.R;
import com.sadaat.groceryapp.adapters.customer.ItemsDisplayAdapterCustomer;
import com.sadaat.groceryapp.adapters.generic.SliderAdapter;
import com.sadaat.groceryapp.models.ItemIDListModel;
import com.sadaat.groceryapp.models.Items.ItemModel;
import com.sadaat.groceryapp.models.SliderModel;
import com.sadaat.groceryapp.models.cart.CartItemModel;
import com.sadaat.groceryapp.syncronizer.CustomerCartSynchronizer;
import com.sadaat.groceryapp.temp.FirebaseDataKeys;
import com.sadaat.groceryapp.temp.UserLive;
import com.sadaat.groceryapp.ui.Fragments.Generic.DetailedItemFragmentGeneric;
import com.sadaat.groceryapp.ui.Fragments.UserBased.Customers.UnderAccountFragment.OrdersFragmentCustomer;
import com.sadaat.groceryapp.ui.Loaders.LoadingDialogue;

import java.util.ArrayList;

public class HomeFragmentCustomer extends Fragment implements ItemsDisplayAdapterCustomer.ItemClickListeners {

    private final CollectionReference ITEMS_COLLECTION_REF = FirebaseFirestore.getInstance().collection(new FirebaseDataKeys().getItemsRef());

    private RecyclerView recyclerViewHotItems;
    private RecyclerView.LayoutManager layoutManagerForHotItems;
    private ItemsDisplayAdapterCustomer displayAdapterHotItems;

    private RecyclerView recyclerViewMostSelling;
    private RecyclerView.LayoutManager layoutManagerForMostSelling;
    private ItemsDisplayAdapterCustomer displayAdapterMostSelling;

    private LoadingDialogue progressDialogue;

    private MaterialCardView cardOpenOrders;

    ArrayList<SliderModel> movies;
    CardSliderViewPager cardSliderViewPager;
    SliderAdapter adapter;

    public HomeFragmentCustomer() {
        // Required empty public constructor
    }

    public static HomeFragmentCustomer newInstance(String param1, String param2) {

        return new HomeFragmentCustomer();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.customer_fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializations(view);
        cardSliderViewPager.setAdapter(adapter);
        Log.e("WIDTH: ",""+cardSliderViewPager.getOtherPagesWidth());
        Log.e("WIDTH: ",""+cardSliderViewPager.getWidth());

        cardSliderViewPager.setAutoSlideTime(8);

        showHotProducts(view);

        FirebaseFirestore
                .getInstance()
                .collection(new FirebaseDataKeys().getSlidesRef())
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.w("TAG", "Listen failed.", error);
                        return;
                    }

                    if (value != null && value.getDocuments().size()>0) {

                        adapter.deleteAll();
                        for (DocumentSnapshot d :
                                value.getDocuments()) {
                            adapter.addSlide(d.toObject(SliderModel.class));
                        }

                    } else {
                        Log.d("TAG", "source file HomeFragmentCustomer" + " data: null");
                    }
                });

        showMostSelling();

        cardOpenOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requireActivity()
                        .getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.flFragmentCustomer, OrdersFragmentCustomer.newInstance())
                        .addToBackStack("home")
                        .commit();
            }
        });

    }

    private void showMostSelling() {
        UserLive.currentLoggedInUser.getCart().eliminateCartByLatestStock();

        recyclerViewMostSelling.setLayoutManager(layoutManagerForMostSelling);
        recyclerViewMostSelling.setAdapter(displayAdapterMostSelling);

        progressDialogue.show("Please Wait", "Loading Most Selling Items");


        FirebaseFirestore
                .getInstance()
                .collection(new FirebaseDataKeys().getTopSellingRef())
                .document("Items")
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {

                            ItemIDListModel list = task.getResult().toObject(ItemIDListModel.class);

                            if (list != null) {
                                for (String s : list.getItems()) {

                                    ITEMS_COLLECTION_REF
                                            .document(s)
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    if (task.isSuccessful()){
                                                        displayAdapterMostSelling.addItem(task.getResult().toObject(ItemModel.class));
                                                    }
                                                }
                                            });

                                }
                            }

                        }
                        progressDialogue.dismiss();
                    }
                });


    }

    private void showHotProducts(View view) {

        UserLive.currentLoggedInUser.getCart().eliminateCartByLatestStock();

        recyclerViewHotItems.setLayoutManager(layoutManagerForHotItems);
        recyclerViewHotItems.setAdapter(displayAdapterHotItems);

        progressDialogue.show("Please Wait", "Loading Hot Items");


        ITEMS_COLLECTION_REF
                .whereEqualTo("hot", true)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            displayAdapterHotItems.addAll(task.getResult().toObjects(ItemModel.class));
                        } else {
                            Toast.makeText(HomeFragmentCustomer.this.requireActivity(), "Error Loading Items", Toast.LENGTH_SHORT).show();
                        }
                        progressDialogue.dismiss();
                    }
                });
    }


    private void initializations(View vParent) {
        recyclerViewHotItems = vParent.findViewById(R.id.recycler_customer_items);
        layoutManagerForHotItems = new LinearLayoutManager(HomeFragmentCustomer.this.requireActivity(), LinearLayoutManager.HORIZONTAL, false);
        displayAdapterHotItems = new ItemsDisplayAdapterCustomer(new ArrayList<>(), this, HomeFragmentCustomer.this.requireActivity());
        progressDialogue = new LoadingDialogue(HomeFragmentCustomer.this.requireActivity());

        recyclerViewMostSelling = vParent.findViewById(R.id.recycler_customer_items_most_selling);
        layoutManagerForMostSelling = new LinearLayoutManager(HomeFragmentCustomer.this.requireActivity(), LinearLayoutManager.HORIZONTAL, false);
        displayAdapterMostSelling = new ItemsDisplayAdapterCustomer(new ArrayList<>(), this, HomeFragmentCustomer.this.requireActivity());
        cardOpenOrders = vParent.findViewById(R.id.card_view_orders);
        cardSliderViewPager = (CardSliderViewPager) vParent.findViewById(R.id.viewPager);
        adapter = new SliderAdapter(new ArrayList<>());

    }


    /*
     * Hot Items Override
     */
    @Override
    public CartItemModel indicateItemCountChange(ItemModel item, int quantity) {
        return new CartItemModel(item, quantity);
    }

    @Override
    public void prepareCart(CartItemModel cartItemModel) {
        UserLive.currentLoggedInUser.getCart().modifyCartItem(cartItemModel);

        CustomerCartSynchronizer.synchronize(UserLive.currentLoggedInUser.getUID(),
                UserLive.currentLoggedInUser.getCart());


        Log.e("CART", UserLive.currentLoggedInUser.getCart().toString());
    }

    @Override
    public void onClick(ItemModel model) {
        getParentFragmentManager()
                .beginTransaction()
                .replace(R.id.flFragmentCustomer, DetailedItemFragmentGeneric.newInstance(model))
                .addToBackStack("home")
                .commit();
    }
}
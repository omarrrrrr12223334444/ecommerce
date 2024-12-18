package com.example.e_commerceapplication.ui.fragments;

import static com.example.e_commerceapplication.general.Constants.ADD_TO_CART;
import static com.example.e_commerceapplication.general.Constants.USER;
import static com.example.e_commerceapplication.general.Constants.USERS;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.e_commerceapplication.adapter.MyCartAdapter;
import com.example.e_commerceapplication.ui.address.AddressActivity;
import com.example.e_commerceapplication.databinding.FragmentCartBinding;
import com.example.e_commerceapplication.database.DataLayer;
import com.example.e_commerceapplication.classes.product.MyCart;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CartFragment extends Fragment {
    List<MyCart> list;
    MyCartAdapter adapter;
    DataLayer dataLayer;

    FragmentCartBinding binding;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCartBinding.inflate(inflater);
        dataLayer = new DataLayer(USERS);

        binding.historyRec.setLayoutManager(new LinearLayoutManager(getActivity()));
        list = new ArrayList<>();
        adapter = new MyCartAdapter(getContext(), list);
        binding.historyRec.setAdapter(adapter);


        dataLayer.cartDatabase(ADD_TO_CART, USER, adapter, list, binding.myCartTotalPrice, binding.cartLayout, binding.emptyCart);


        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                MyCart removeCart = list.get(position);
                dataLayer.removeCartDatabase(ADD_TO_CART,
                        USER,
                        list,
                        binding.myCartTotalPrice,
                        removeCart,
                        CartFragment.this);
            }
        }).attachToRecyclerView(binding.historyRec);

        binding.finalBuy.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), AddressActivity.class);
            double finalPaymentAmount = 0.0;
            for (MyCart model : list) finalPaymentAmount += model.getTotalPrice();
            intent.putExtra("finalPaymentAmount", finalPaymentAmount);
            intent.putExtra("listOfCart", (Serializable) list);
            intent.putExtra("quantity", list.size());
            startActivity(intent);
        });
        return binding.getRoot();
    }
}
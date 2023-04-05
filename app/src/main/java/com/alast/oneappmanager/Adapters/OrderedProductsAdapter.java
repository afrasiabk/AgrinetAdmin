package com.alast.oneappmanager.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alast.oneappmanager.Model.Product;
import com.alast.oneappmanager.R;
import com.alast.oneappmanager.ViewHolders.OrderedProductsVh;

import java.util.ArrayList;


public class OrderedProductsAdapter extends RecyclerView.Adapter <OrderedProductsVh> {

    private ArrayList<Product> products;

    public OrderedProductsAdapter(ArrayList<Product> products) {
        this.products = products;
    }

    @NonNull
    @Override
    public OrderedProductsVh onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View orderView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.ordered_products_layout,viewGroup,false);
        return new OrderedProductsVh(orderView);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderedProductsVh holder, int i) {

        holder.name.setText(products.get(i).getName());
        holder.price.setText("Price: Rs "+products.get(i).getPrice());
        holder.id.setText("Pid: "+products.get(i).getId());
        holder.quantity.setText("Items: "+products.get(i).getQuantity());
    }

    @Override
    public int getItemCount() {
        return products.size();
    }
}

package com.alast.oneappmanager.Adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alast.oneappmanager.Activities.OrderDetailsActivity;
import com.alast.oneappmanager.Model.Orders;
import com.alast.oneappmanager.R;
import com.alast.oneappmanager.ViewHolders.OrdersVH;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersVH> {

    private String type;
    private ArrayList<Orders> orders;

    public OrdersAdapter(ArrayList<Orders> orders, String type) {
        this.orders = orders;
        this.type = type;
    }

    @NonNull
    @Override
    public OrdersVH onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View orderView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.orders_layout,viewGroup,false);
        return new OrdersVH(orderView);
    }

    @Override
    public void onBindViewHolder(@NonNull final OrdersVH holder, int i) {
        final String id = orders.get(i).getId();
        final Long time;

        if (type.equals("Pending")){
            time = orders.get(i).getPlaced_time();
        }
        else {
            time = orders.get(i).getDelivered_time();
        }

        holder.id.setText("Order Id " + id);
        holder.bill.setText("Rs " + orders.get(i).getBill());

        DateFormat formatter = new SimpleDateFormat("dd/MM/yy", Locale.getDefault());
        formatter.setTimeZone(TimeZone.getTimeZone("GMT+5"));

        holder.time.setText(formatter.format(new Date(time)));

        holder.details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(holder.itemView.getContext(), OrderDetailsActivity.class);
                intent.putExtra("id",id);
                intent.putExtra("type",type);
                holder.itemView.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }
}

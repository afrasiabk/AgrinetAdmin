package com.alast.oneappmanager.ViewHolders;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alast.oneappmanager.R;


public class OrdersVH extends RecyclerView.ViewHolder {
    public TextView id, bill, time, details;

    public OrdersVH(@NonNull View itemView) {
        super(itemView);
        id = itemView.findViewById(R.id.user_orders_id);
        bill = itemView.findViewById(R.id.user_orders_bill);
        time = itemView.findViewById(R.id.user_orders_time);
        details = itemView.findViewById(R.id.user_orders_details);
    }
}

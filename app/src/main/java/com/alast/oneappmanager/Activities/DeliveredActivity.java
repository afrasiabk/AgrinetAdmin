package com.alast.oneappmanager.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.alast.oneappmanager.Adapters.OrdersAdapter;
import com.alast.oneappmanager.Model.Orders;
import com.alast.oneappmanager.R;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DeliveredActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView empty_label;
    private DatabaseReference ordersRef;
    private LinearLayoutManager layoutManager;
    private ArrayList<Orders> orders;
    private OrdersAdapter adapter;
    private SwipeRefreshLayout refreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivered);
        backBtn();
        assignViews();
    }

    private void assignViews() {
        recyclerView = (RecyclerView) findViewById(R.id.del_orders_rv);
        empty_label = (TextView) findViewById(R.id.del_orders_empty);
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.del_orders_swipe);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        orders = new ArrayList<>();
        adapter = new OrdersAdapter(orders, "Delivered");
        recyclerView.setAdapter(adapter);

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                setData();
            }
        });

    }

    private void setData() {
        recyclerView.setLayoutFrozen(true);
        orders.clear();
        refreshLayout.setRefreshing(true);
        ordersRef = FirebaseDatabase.getInstance().getReference("Agrinet/Orders").child("Delivered");
        ordersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int count = (int) dataSnapshot.getChildrenCount();
                if (count == 0){
                    empty_label.setVisibility(View.VISIBLE);
                }
                else {
                    empty_label.setVisibility(View.INVISIBLE);
                    for (DataSnapshot order : dataSnapshot.getChildren()) {
                        orders.add(0, order.getValue(Orders.class));
                    }
                }
                adapter.notifyDataSetChanged();
                refreshLayout.setRefreshing(false);
                recyclerView.setLayoutFrozen(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        setData();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void backBtn() {
        if (getSupportActionBar()!=null) {
            getSupportActionBar().setTitle("Delivered Orders");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setElevation(0);
        }
    }
}

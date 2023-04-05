package com.alast.oneappmanager.Activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import com.alast.oneappmanager.Adapters.OrdersAdapter;
import com.alast.oneappmanager.Model.Orders;
import com.alast.oneappmanager.R;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

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
        setContentView(R.layout.activity_main);
        if (internet()) {
            check_login();
        }
    }

    private boolean internet() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) {
            return true;
        }
        else {
            Toast.makeText(this, "Internet Not Connected", Toast.LENGTH_SHORT).show();
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

            alertDialogBuilder.setTitle("No Internet");
            alertDialogBuilder
                    .setCancelable(false)
                    .setNegativeButton("Retry", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            recreate();
                        }
                    });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
            return false;
        }
    }

    private void check_login() {

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Intent intent = new Intent(MainActivity.this, PhoneVerifyActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finishAffinity();
        }
        else {

            final String phone = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (!dataSnapshot.child("Managers").child(phone).exists()) {
                        FirebaseAuth.getInstance().signOut();
                        recreate();
                    }
                    proceed();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    private void proceed() {
        updateToken();
        assignViews();
        setData();
    }

    private static void updateToken(){
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            String phone = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Managers").child(phone);
            String token = FirebaseInstanceId.getInstance().getToken();
            ref.child("token").setValue(token);
        }
    }

    private void assignViews() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        recyclerView = (RecyclerView) findViewById(R.id.main_orders_rv);
        empty_label = (TextView) findViewById(R.id.main_orders_empty);
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.main_orders_swipe);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        orders = new ArrayList<>();
        adapter = new OrdersAdapter(orders, "Pending");
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
        ordersRef = FirebaseDatabase.getInstance().getReference("Agrinet/Orders").child("Pending");
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
                recyclerView.setLayoutFrozen(false);
                adapter.notifyDataSetChanged();
                refreshLayout.setRefreshing(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_delivered) {
            startActivity(new Intent(MainActivity.this,DeliveredActivity.class));
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}

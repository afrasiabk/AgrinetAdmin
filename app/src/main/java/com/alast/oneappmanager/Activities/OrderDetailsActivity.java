package com.alast.oneappmanager.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alast.oneappmanager.Adapters.OrderedProductsAdapter;
import com.alast.oneappmanager.Model.Orders;
import com.alast.oneappmanager.Model.Product;
import com.alast.oneappmanager.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;


public class OrderDetailsActivity extends AppCompatActivity {

    private String order_id, type;
    private DatabaseReference orderRef;
    private DatabaseReference cartRef;
    private TextView address_txt, id_txt, bill_txt, placed_txt, status_txt, ordered_label;

    private RecyclerView recyclerView;
    private ArrayList<Product> products;
    private LinearLayoutManager layoutManager;
    private OrderedProductsAdapter adapter;
    private ProgressBar progressBar;

    private Orders o;

    private TextView dispatch_btn, deliver_btn, navigate_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);
        fromIntent();
        backBtn();
        assignViews();
        setData();
        listeners();
    }

    private void fromIntent() {
        order_id = getIntent().getStringExtra("id");
        type = getIntent().getStringExtra("type");
    }

    private void assignViews() {
        address_txt = (TextView) findViewById(R.id.orders_details_address);
        id_txt = (TextView) findViewById(R.id.orders_details_id);
        bill_txt = (TextView) findViewById(R.id.orders_details_bill);
        placed_txt = (TextView) findViewById(R.id.orders_details_time);
        status_txt = (TextView) findViewById(R.id.orders_delivery_status);
        recyclerView  = (RecyclerView) findViewById(R.id.orders_details_rv);
        progressBar = (ProgressBar) findViewById(R.id.orders_details_pb);
        ordered_label =(TextView) findViewById(R.id.order_det_olabel);
        dispatch_btn = (TextView) findViewById(R.id.orders_det_disp_btn);
        deliver_btn = (TextView) findViewById(R.id.orders_det_del_btn);
        navigate_btn = (TextView) findViewById(R.id.orders_det_nav_btn);
        products = new ArrayList<>();
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new OrderedProductsAdapter(products);
        recyclerView.setAdapter(adapter);
        if (type.equals("Delivered")){
            dispatch_btn.setVisibility(View.GONE);
            deliver_btn.setText("UnDeliver");
        }
    }

    private void listeners() {
        dispatch_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                if (dispatch_btn.getText().equals("Dispatch")) {
                    FirebaseDatabase.getInstance().getReference("Agrinet/Orders").child("Pending")
                            .child(order_id).child("status").setValue("Dispatched").addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(OrderDetailsActivity.this, "Done", Toast.LENGTH_LONG).show();
                            dispatch_btn.setText("Dispatched");
                            setData();
                        }
                    });
                }
                else {
                    FirebaseDatabase.getInstance().getReference("Agrinet/Orders").child("Pending")
                            .child(order_id).child("status").setValue("Pending").addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(OrderDetailsActivity.this, "Done", Toast.LENGTH_LONG).show();
                            dispatch_btn.setText("Dispatch");
                            setData();
                        }
                    });
                }
            }
        });

        deliver_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (products.size() > 0) { //products loaded or not
                    if (type.equals("Delivered")) {
                        undeliver();
                    } else {
                        deliver();
                    }
                }
            }
        });

        navigate_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uri = "http://maps.google.com/maps?daddr="+o.getLati()+","+o.getLongi();
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                        Uri.parse(uri));
                startActivity(intent);
            }
        });
    }

    private void undeliver() {
        progressBar.setVisibility(View.VISIBLE);
        HashMap<String, Object> ordermap = new HashMap<>();
        ordermap.put("bill", o.getBill());
        ordermap.put("cart", products);
        ordermap.put("id", o.getId());
        ordermap.put("placed_time", o.getPlaced_time());
        ordermap.put("user_phone", o.getUser_phone());
        ordermap.put("user_name", o.getUser_name());
        ordermap.put("lati", o.getLati());
        ordermap.put("longi", o.getLongi());
        ordermap.put("address", o.getAddress());
        ordermap.put("status", o.getStatus());

        FirebaseDatabase.getInstance().getReference("Agrinet/Orders").child("Pending")
                .child(order_id).setValue(ordermap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                FirebaseDatabase.getInstance().getReference("Agrinet/Orders").child("Delivered")
                        .child(order_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(OrderDetailsActivity.this, "UnDelivered", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(OrderDetailsActivity.this,MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        finish();
                    }
                });
            }
        });
    }

    private void deliver() {
        progressBar.setVisibility(View.VISIBLE);
        HashMap<String, Object> ordermap = new HashMap<>();
        ordermap.put("bill", o.getBill());
        ordermap.put("cart", products);
        ordermap.put("id", o.getId());
        ordermap.put("placed_time", o.getPlaced_time());
        ordermap.put("user_phone", o.getUser_phone());
        ordermap.put("user_name", o.getUser_name());
        ordermap.put("lati", o.getLati());
        ordermap.put("longi", o.getLongi());
        ordermap.put("address", o.getAddress());
        ordermap.put("status", o.getStatus());
        ordermap.put("delivered_time", ServerValue.TIMESTAMP);

        FirebaseDatabase.getInstance().getReference("Agrinet/Orders").child("Delivered")
                .child(order_id).setValue(ordermap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                FirebaseDatabase.getInstance().getReference("Agrinet/Orders").child("Pending")
                        .child(order_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(OrderDetailsActivity.this, "Delivered", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(OrderDetailsActivity.this,MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        finish();
                    }
                });
            }
        });
    }

    private void setData() {
        orderRef = FirebaseDatabase.getInstance().getReference("Agrinet/Orders").child(type).child(order_id);
        orderRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                o = dataSnapshot.getValue(Orders.class);
                setFields();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }

    private void setFields() {
        address_txt.setText(o.getAddress());
        id_txt.setText("Order Id: "+o.getId());
        if (o.getCharges().equals("yes")){
            bill_txt.setText("Rs "+o.getBill()+"(Rs.50 delivery charges)");
        }
        else{
            bill_txt.setText("Rs "+o.getBill());
        }
        DateFormat formatter = new SimpleDateFormat("hh:mm a  dd/MM/yy", Locale.getDefault());
        formatter.setTimeZone(TimeZone.getTimeZone("GMT+5"));
        if (type.equals("Delivered")) {
            placed_txt.setVisibility(View.GONE);
            ordered_label.setVisibility(View.GONE);
            String del_time = "Delivered (" + formatter.format(new Date(o.getDelivered_time()))+")";
            status_txt.setText(del_time);
        }
        else  {
            placed_txt.setText(formatter.format(new Date(o.getPlaced_time())));
            status_txt.setText(o.getStatus());
            if (o.getStatus().equals("Dispatched")) {
                dispatch_btn.setText("Dispatched");
            }

            else{
                dispatch_btn.setText("Dispatch");
            }
        }
        progressBar.setVisibility(View.INVISIBLE);

        cartRef = FirebaseDatabase.getInstance().getReference().child("Agrinet/Orders").child(type).child(order_id).child("cart");
        cartRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot p : dataSnapshot.getChildren()){
                    products.add(p.getValue(Product.class));
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void backBtn() {
        if (getSupportActionBar()!=null) {
            getSupportActionBar().setTitle("Order "+order_id);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setElevation(0);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}

package com.example.ecart;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.ecart.Model.Products;
import com.example.ecart.Prevalent.Prevalent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ProductInfoActivity extends AppCompatActivity {
//    private FloatingActionButton fabAddToCart;
    private Button btnAddToCart;
    private ImageView imgProductInfo;
    private ElegantNumberButton btnQuantity;
    private TextView txtProductIName, txtProductIDes, txtProductIPrice;

    private String productID = "", status = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_info);

        productID = getIntent().getStringExtra("p_id");

        initView();

        getProductInfo(productID);

        btnAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                addToCartList();
                if (status.equals("Đơn hàng đang chuẩn bị") || status.equals("Đơn hàng được gửi")) {
                    Toast.makeText(ProductInfoActivity.this, "Tạo đơn hàng mới khi đã nhận được đơn hàng", Toast.LENGTH_SHORT).show();
                }
                else {
                    addToCartList();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        CheckOrderStatus();
    }

    private void addToCartList() {
        String saveCurrentDate, saveCurrentTime;

        Calendar calDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MM-yyyy");
        saveCurrentDate = currentDate.format(calDate.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calDate.getTime());

        final DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference().child("Cart List");

        final HashMap<String, Object> cartMap = new HashMap<>();
        cartMap.put("p_id", productID);
        cartMap.put("p_name", txtProductIName.getText().toString());
        cartMap.put("price", txtProductIPrice.getText().toString());
        cartMap.put("date", saveCurrentDate);
        cartMap.put("time", saveCurrentTime);
        cartMap.put("quantity", btnQuantity.getNumber());
        cartMap.put("discount", "");

        cartRef.child("User Cart").child(Prevalent.currentOnlineUser.getPhone()).child("Products")
                .child(productID).updateChildren(cartMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            cartRef.child("Admin Cart").child(Prevalent.currentOnlineUser.getPhone()).child("Products")
                                    .child(productID).updateChildren(cartMap)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull @NotNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(ProductInfoActivity.this, "Đã thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();

                                                Intent intent = new Intent(ProductInfoActivity.this, HomePageActivity.class);
                                                startActivity(intent);
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    private void getProductInfo(String productID) {
        DatabaseReference productRef = FirebaseDatabase.getInstance().getReference().child("Products");
        productRef.child(productID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Products products = snapshot.getValue(Products.class);
                    txtProductIName.setText(products.getP_name());
                    txtProductIDes.setText(products.getDescription());
                    txtProductIPrice.setText(products.getPrice() + " VND");
                    Picasso.get().load(products.getImage()).into(imgProductInfo);
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    private void CheckOrderStatus() {
        DatabaseReference orderRef;
        orderRef = FirebaseDatabase.getInstance().getReference().child("Orders")
                .child(Prevalent.currentOnlineUser.getPhone());
        orderRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String shipStatus = snapshot.child("status").getValue().toString();

                    if (shipStatus.equals("Đã giao")) {
                        status = "Đơn hàng được gửi";
                    }
                    else if (shipStatus.equals("Chưa được giao")) {
                        status = "Đơn hàng đang chuẩn bị";
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void initView() {
//        fabAddToCart = findViewById(R.id.fabAddToCart);
        btnAddToCart = findViewById(R.id.btnAddToCart);
        imgProductInfo = findViewById(R.id.imgProductInfo);
        btnQuantity = findViewById(R.id.btnQuantity);
        txtProductIName = findViewById(R.id.txtProductIName);
        txtProductIDes = findViewById(R.id.txtProductIDes);
        txtProductIPrice = findViewById(R.id.txtProductIPrice);
    }
}
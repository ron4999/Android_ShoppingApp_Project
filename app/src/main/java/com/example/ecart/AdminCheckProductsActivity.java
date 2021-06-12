package com.example.ecart;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ecart.Model.Cart;
import com.example.ecart.ViewHolder.CartViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;

public class AdminCheckProductsActivity extends AppCompatActivity {
    private RecyclerView recyclerProducts;
    RecyclerView.LayoutManager layoutManager;

    private String userID = "";

    private DatabaseReference orderProductsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_check_products);

        userID = getIntent().getStringExtra("userID");

        recyclerProducts = findViewById(R.id.recyclerProducts);
        recyclerProducts.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerProducts.setLayoutManager(layoutManager);

        orderProductsRef = FirebaseDatabase.getInstance().getReference().child("Cart List")
                .child("Admin Cart").child(userID)
                .child("Products");
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Cart> options =
                new FirebaseRecyclerOptions.Builder<Cart>()
                .setQuery(orderProductsRef, Cart.class).build();

        FirebaseRecyclerAdapter<Cart, CartViewHolder> adapter =
                new FirebaseRecyclerAdapter<Cart, CartViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull CartViewHolder cartViewHolder, int i, @NonNull Cart cart) {
                        cartViewHolder.txtCartPQuantity.setText("Số lượng: " + cart.getQuantity());
                        cartViewHolder.txtCartPPrice.setText("Giá: " + cart.getPrice());
                        cartViewHolder.txtCartPName.setText(cart.getP_name());

                    }

                    @NonNull
                    @Override
                    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_items, parent, false);
                        CartViewHolder holder = new CartViewHolder(view);
                        return holder;
                    }
                };
        recyclerProducts.setAdapter(adapter);
        adapter.startListening();
    }
}
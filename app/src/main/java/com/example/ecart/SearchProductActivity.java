package com.example.ecart;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.ecart.Model.Products;
import com.example.ecart.ViewHolder.ProductViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

public class SearchProductActivity extends AppCompatActivity {
    private Button btnProductSearch;
    private EditText edtProductNameSearch;
    private RecyclerView rvSearch;
    private String keySearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_product);

        initView();

        rvSearch.setLayoutManager(new LinearLayoutManager(SearchProductActivity.this));

        btnProductSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                keySearch = edtProductNameSearch.getText().toString();
                onStart();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        DatabaseReference searchRef = FirebaseDatabase.getInstance().getReference().child("Products");

        FirebaseRecyclerOptions<Products> options =
                new FirebaseRecyclerOptions.Builder<Products>()
                .setQuery(searchRef.orderByChild("p_name").startAt(keySearch).endAt("\uf8ff"), Products.class)
                .build();

        FirebaseRecyclerAdapter<Products, ProductViewHolder> adapter =
                new FirebaseRecyclerAdapter<Products, ProductViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull ProductViewHolder holder, int i, @NonNull Products products) {
                        holder.txtProductName.setText(products.getP_name());
                        holder.txtProductDes.setText(products.getDescription());
                        holder.txtProductPrice.setText("Price = " + products.getPrice() + "VND");
                        Picasso.get().load(products.getImage()).into(holder.imgProductImg);

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(SearchProductActivity.this, ProductInfoActivity.class);
                                intent.putExtra("p_id", products.getP_id());
                                startActivity(intent);
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_items, parent, false);
                        ProductViewHolder holder = new ProductViewHolder(view);
                        return holder;
                    }
                };
        rvSearch.setAdapter(adapter);
        adapter.startListening();
    }

    private void initView() {
        btnProductSearch = findViewById(R.id.btnProductSearch);
        edtProductNameSearch = findViewById(R.id.edtProductNameSearch);
        rvSearch = findViewById(R.id.rvSearch);
    }


}
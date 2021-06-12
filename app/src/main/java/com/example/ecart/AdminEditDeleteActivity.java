package com.example.ecart;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class AdminEditDeleteActivity extends AppCompatActivity {
    private Button btnEdit, btnDelete;
    private EditText edtName, edtDescription, edtPrice;
    private ImageView imgProduct;

    private String productID = "";
    private DatabaseReference productRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_edit_delete);

        productID = getIntent().getStringExtra("p_id");
        productRef = FirebaseDatabase.getInstance().getReference().child("Products").child(productID);

        initView();

        displayProductInfo();

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                applyEdit();
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteProduct();
            }
        });
    }

    private void deleteProduct() {
        productRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Intent intent = new Intent(AdminEditDeleteActivity.this, AdminCategoryActivity.class);
                startActivity(intent);
                finish();
                Toast.makeText(AdminEditDeleteActivity.this, "Xóa sản phẩm thành công", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void applyEdit() {
        String name = edtName.getText().toString();
        String price = edtPrice.getText().toString();
        String description = edtDescription.getText().toString();

        if (name.equals("")) {
            Toast.makeText(this, "Không để trống tên sản phẩm", Toast.LENGTH_SHORT).show();
        }
        else if (price.equals("")) {
            Toast.makeText(this, "Không để trống giá sản phẩm", Toast.LENGTH_SHORT).show();
        }
        else if (description.equals("")) {
            Toast.makeText(this, "Không để trống mô tả sản phẩm", Toast.LENGTH_SHORT).show();
        }
        else {
            HashMap<String, Object> productChangeMap = new HashMap<>();
            productChangeMap.put("p_id", productID);
            productChangeMap.put("p_name", name);
            productChangeMap.put("price", price);
            productChangeMap.put("description", description);

            productRef.updateChildren(productChangeMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(AdminEditDeleteActivity.this, "Sửa thành công", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(AdminEditDeleteActivity.this, AdminCategoryActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            });
        }
    }

    private void displayProductInfo() {
        productRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String name = snapshot.child("p_name").getValue().toString();
                    String price = snapshot.child("price").getValue().toString();
                    String description = snapshot.child("description").getValue().toString();
                    String image = snapshot.child("image").getValue().toString();

                    edtName.setText(name);
                    edtPrice.setText(price);
                    edtDescription.setText(description);
                    Picasso.get().load(image).into(imgProduct);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void initView() {
        btnEdit = findViewById(R.id.btnEdit);
        edtName = findViewById(R.id.edtName);
        edtDescription = findViewById(R.id.edtDescription);
        edtPrice = findViewById(R.id.edtPrice);
        imgProduct = findViewById(R.id.imgProduct);
        btnDelete = findViewById(R.id.btnDelete);
    }
}
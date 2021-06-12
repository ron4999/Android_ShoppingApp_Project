package com.example.ecart;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.ecart.Model.AdminOrders;
import com.example.ecart.Prevalent.Prevalent;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;

public class AdminCheckOrdersActivity extends AppCompatActivity {
    private RecyclerView recyclerOrders;
    private DatabaseReference orderRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_check_orders);

        orderRef = FirebaseDatabase.getInstance().getReference().child("Orders");

        recyclerOrders = findViewById(R.id.recyclerOrders);
        recyclerOrders.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<AdminOrders> options =
                new FirebaseRecyclerOptions.Builder<AdminOrders>()
                .setQuery(orderRef, AdminOrders.class).build();

        FirebaseRecyclerAdapter<AdminOrders, AdminOrdersViewHolder> adapter =
                new FirebaseRecyclerAdapter<AdminOrders, AdminOrdersViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull AdminOrdersViewHolder adminOrdersViewHolder, int i, @NonNull AdminOrders adminOrders) {
                        adminOrdersViewHolder.txtOrderUserName.setText("Tên: " + adminOrders.getName());
                        adminOrdersViewHolder.txtOrderPhoneNum.setText("Số: " + adminOrders.getPhone());
                        adminOrdersViewHolder.txtOrderTotalPrice.setText("Tổng tiền: " + adminOrders.getTotalPrice() + " VND");
                        adminOrdersViewHolder.txtOrderAddress.setText("Địa chỉ: " + adminOrders.getAddress() + ", " + adminOrders.getCity());
                        adminOrdersViewHolder.txtOrderDateTime.setText("Thời gian: " + adminOrders.getDate() + ", " + adminOrders.getTime());

                        adminOrdersViewHolder.btnShowProducts.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String userId = getRef(i).getKey();

                                Intent intent = new Intent(AdminCheckOrdersActivity.this, AdminCheckProductsActivity.class);
                                intent.putExtra("userID", userId);
                                startActivity(intent);
                            }
                        });

                        adminOrdersViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                CharSequence options[] = new CharSequence[]{
                                        "Giao hàng",
                                        "Không"
                                };
                                AlertDialog.Builder builder = new AlertDialog.Builder(AdminCheckOrdersActivity.this);
                                builder.setTitle("Bạn có muốn giao đơn hàng này không?");
                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (which == 0) {
                                            String userID = getRef(i).getKey();
                                            orderRef.child(userID).removeValue();
                                            FirebaseDatabase.getInstance().getReference().child("Cart List")
                                                    .child("Admin Cart")
                                                    .child(userID)
                                                    .child("Products")
                                                    .removeValue();
                                        }
                                        else if (which == 1) {
                                            finish();
                                        }
                                    }
                                });
                                builder.show();
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public AdminOrdersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.orders_item, parent, false);
                        return new AdminOrdersViewHolder(view);
                    }
                };
        recyclerOrders.setAdapter(adapter);
        adapter.startListening();
    }

    public static class AdminOrdersViewHolder extends RecyclerView.ViewHolder {
        public TextView txtOrderUserName, txtOrderPhoneNum, txtOrderTotalPrice, txtOrderAddress, txtOrderDateTime;
        public Button btnShowProducts;

        public AdminOrdersViewHolder(@NonNull View itemView) {
            super(itemView);
            txtOrderUserName = itemView.findViewById(R.id.txtOrderUserName);
            txtOrderPhoneNum = itemView.findViewById(R.id.txtOrderPhoneNum);
            txtOrderTotalPrice = itemView.findViewById(R.id.txtOrderTotalPrice);
            txtOrderAddress = itemView.findViewById(R.id.txtOrderAddress);
            txtOrderDateTime = itemView.findViewById(R.id.txtOrderDateTime);
            btnShowProducts = itemView.findViewById(R.id.btnShowProducts);
        }
    }
}
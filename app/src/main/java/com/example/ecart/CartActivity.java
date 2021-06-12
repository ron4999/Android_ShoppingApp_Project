package com.example.ecart;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ecart.Model.AdminOrders;
import com.example.ecart.Model.Cart;
import com.example.ecart.Model.Users;
import com.example.ecart.Prevalent.Prevalent;
import com.example.ecart.ViewHolder.BillViewHolder;
import com.example.ecart.ViewHolder.CartViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

public class CartActivity extends AppCompatActivity {
    private RecyclerView recyclerCart;
    private RecyclerView.LayoutManager layoutManager;

    private TextView txtBillUserName, txtBillPhoneNum, txtBillTotalPrice;
    private TextView txtBillAddress, txtBillDateTime;
    private Button btnShowProducts;

    private Button btnNext;
    private TextView txtTotalPrice, txtMessage;
    private RelativeLayout relativeLayout;

    private int calTotalPrice = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        initView();

        recyclerCart.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerCart.setLayoutManager(layoutManager);

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                txtTotalPrice.setText("Tổng tiền = " + String.valueOf(calTotalPrice) + " VND");

                Intent intent = new Intent(CartActivity.this, ConfirmOrderActivity.class);
                intent.putExtra("totalPrice", String.valueOf(calTotalPrice));
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        checkOrderStatus();

        final DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference().child("Cart List");

        FirebaseRecyclerOptions<Cart> options = new FirebaseRecyclerOptions.Builder<Cart>()
                .setQuery(cartRef.child("User Cart").child(Prevalent.currentOnlineUser.getPhone())
                .child("Products"), Cart.class).build();

        FirebaseRecyclerAdapter<Cart, CartViewHolder> adapter =
                new FirebaseRecyclerAdapter<Cart, CartViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull CartViewHolder cartViewHolder, int i, @NonNull Cart cart) {
                        cartViewHolder.txtCartPQuantity.setText("Số lượng: " + cart.getQuantity());
                        cartViewHolder.txtCartPPrice.setText("Giá: " + cart.getPrice());
                        cartViewHolder.txtCartPName.setText(cart.getP_name());

                        // tính tổng tiền
//                        int oneProductTotalPrice = Integer.parseInt(cart.getPrice()) * Integer.parseInt(cart.getQuantity());
                        int oneProductTotalPrice = (Integer.parseInt(cart.getPrice().replaceAll("\\D+",""))) * Integer.parseInt(cart.getQuantity());
                        calTotalPrice = calTotalPrice + oneProductTotalPrice;
                        txtTotalPrice.setText("Tổng tiền = " + String.valueOf(calTotalPrice) + " VND");

                        cartViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                CharSequence options[] = new CharSequence[] {
                                  "Sửa",
                                  "Xóa"
                                };
                                AlertDialog.Builder builder = new AlertDialog.Builder(CartActivity.this);
                                builder.setTitle("Vui lòng chọn: ");

                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (which == 0) {
                                            Intent intent = new Intent(CartActivity.this, ProductInfoActivity.class);
                                            intent.putExtra("p_id", cart.getP_id());
                                            startActivity(intent);
                                        }
                                        if (which == 1) {
                                            cartRef.child("User Cart")
                                                    .child(Prevalent.currentOnlineUser.getPhone())
                                                    .child("Products").child(cart.getP_id())
                                                    .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(CartActivity.this, "Xóa sản phẩm thành công", Toast.LENGTH_SHORT).show();
                                                        Intent intent = new Intent(CartActivity.this, HomePageActivity.class);
                                                        startActivity(intent);
                                                    }
                                                }
                                            });
                                        }
                                    }
                                });
                                builder.show();
                            }
                        });
                    }

                    @NonNull
                    @NotNull
                    @Override
                    public CartViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_items, parent, false);
                        CartViewHolder holder = new CartViewHolder(view);
                        return holder;
                    }
                };
        recyclerCart.setAdapter(adapter);
        adapter.startListening();;
    }

    private void checkOrderStatus() {
        DatabaseReference orderRef;
        orderRef = FirebaseDatabase.getInstance().getReference().child("Orders")
                .child(Prevalent.currentOnlineUser.getPhone());
        orderRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String shipStatus = snapshot.child("status").getValue().toString();
                    String userName = snapshot.child("name").getValue().toString();

                    if (shipStatus.equals("Đã giao")) {
                        txtTotalPrice.setText("Đơn của " + userName + " được giao thành công");
                        recyclerCart.setVisibility(View.GONE);
                        txtMessage.setVisibility(View.VISIBLE);
                        txtMessage.setText("Dơn hàng dược tạo thành công");
                        btnNext.setVisibility(View.GONE);

                        Toast.makeText(CartActivity.this, "Cảm ơn!", Toast.LENGTH_SHORT).show();
//                        DatabaseReference cartDeleteRef = FirebaseDatabase.getInstance().getReference().child("Cart List");
//
//                        FirebaseRecyclerOptions<Cart> options = new FirebaseRecyclerOptions.Builder<Cart>()
//                                .setQuery(cartDeleteRef.child("User Cart").child(Prevalent.currentOnlineUser.getPhone())
//                                        .child("Products"), Cart.class).build();
//
//                        FirebaseRecyclerAdapter<Cart, CartViewHolder> adapter =
//                                new FirebaseRecyclerAdapter<Cart, CartViewHolder>(options) {
//                                    @Override
//                                    protected void onBindViewHolder(@NonNull CartViewHolder cartViewHolder, int i, @NonNull Cart cart) {
//
//                                    }
//                                };
                    }
                    else if (shipStatus.equals("Chưa được giao")) {
                        txtTotalPrice.setText("Đơn của " + userName + " chưa được giao");
//                        txtTotalPrice.setVisibility(View.GONE);
                        relativeLayout.setVisibility(View.VISIBLE);
                        recyclerCart.setVisibility(View.VISIBLE);
                        txtMessage.setVisibility(View.VISIBLE);
                        btnNext.setVisibility(View.GONE);
                        txtBillUserName.setText("Tên: " + snapshot.child("name").getValue().toString());
                        txtBillPhoneNum.setText("Số: " + snapshot.child("phone").getValue().toString());
                        txtBillTotalPrice.setText("Tổng tiền: " + snapshot.child("totalPrice").getValue().toString());
                        txtBillDateTime.setText("Thời gian: " + snapshot.child("date").getValue().toString() + ", " + snapshot.child("time").getValue().toString());
                        txtBillAddress.setText("Địa chỉ: " + snapshot.child("address").getValue().toString() + ", " + snapshot.child("city").getValue().toString());
                        btnShowProducts.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(CartActivity.this, AdminCheckProductsActivity.class);
                                intent.putExtra("userID", Prevalent.currentOnlineUser.getPhone());
                                startActivity(intent);
                            }
                        });

//
                        Toast.makeText(CartActivity.this, "Bạn có thể mua thêm khi đơn hàng được xác minh", Toast.LENGTH_SHORT).show();
//                        FirebaseRecyclerOptions<AdminOrders> options =
//                                new FirebaseRecyclerOptions.Builder<AdminOrders>()
//                                        .setQuery(orderRef.child(Prevalent.currentOnlineUser.getPhone()), AdminOrders.class).build();
//
//                        FirebaseRecyclerAdapter<AdminOrders, BillViewHolder> adapter =
//                                new FirebaseRecyclerAdapter<AdminOrders, BillViewHolder>(options) {
//                                    @Override
//                                    protected void onBindViewHolder(@NonNull BillViewHolder adminOrdersViewHolder, int i, @NonNull AdminOrders adminOrders) {
//                                        adminOrdersViewHolder.txtOrderUserName.setText("Tên: " + adminOrders.getName());
//                                        adminOrdersViewHolder.txtOrderPhoneNum.setText("Số: " + adminOrders.getPhone());
//                                        adminOrdersViewHolder.txtOrderTotalPrice.setText("Tổng tiền: " + adminOrders.getTotalPrice() + " VND");
//                                        adminOrdersViewHolder.txtOrderAddress.setText("Địa chỉ: " + adminOrders.getAddress() + ", " + adminOrders.getCity());
//                                        adminOrdersViewHolder.txtOrderDateTime.setText("Thời gian: " + adminOrders.getDate() + ", " + adminOrders.getTime());
//
//                                        adminOrdersViewHolder.btnShowProducts.setOnClickListener(new View.OnClickListener() {
//                                            @Override
//                                            public void onClick(View v) {
//                                                String userId = getRef(i).getKey();
//
//                                                Intent intent = new Intent(CartActivity.this, AdminCheckProductsActivity.class);
//                                                intent.putExtra("userID", userId);
//                                                startActivity(intent);
//                                            }
//                                        });
//                                    }
//
//                                    @NonNull
//                                    @Override
//                                    public BillViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//                                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.orders_item, parent, false);
//                                        return new BillViewHolder(view);
//                                    }
//                                };
//                        recyclerCart.setAdapter(adapter);
//                        adapter.startListening();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void initView() {
        recyclerCart = findViewById(R.id.recyclerCart);
        btnNext = findViewById(R.id.btnNext);
        txtTotalPrice = findViewById(R.id.txtTotalPrice);
        txtMessage = findViewById(R.id.txtMessage);
        relativeLayout = findViewById(R.id.rlBill);
        txtBillUserName = findViewById(R.id.txtBillUserName);
        txtBillAddress = findViewById(R.id.txtBillAddress);
        txtBillDateTime = findViewById(R.id.txtBillDateTime);
        txtBillPhoneNum = findViewById(R.id.txtBillPhoneNum);
        txtBillTotalPrice = findViewById(R.id.txtBillTotalPrice);
        btnShowProducts = findViewById(R.id.btnShowBillProducts);
    }

//    public static class UserOrdersViewHolder extends RecyclerView.ViewHolder {
//        public TextView txtOrderUserName, txtOrderPhoneNum, txtOrderTotalPrice, txtOrderAddress, txtOrderDateTime;
//        public Button btnShowProducts;
//
//        public UserOrdersViewHolder(@NonNull View itemView) {
//            super(itemView);
//            txtOrderUserName = itemView.findViewById(R.id.txtOrderUserName);
//            txtOrderPhoneNum = itemView.findViewById(R.id.txtOrderPhoneNum);
//            txtOrderTotalPrice = itemView.findViewById(R.id.txtOrderTotalPrice);
//            txtOrderAddress = itemView.findViewById(R.id.txtOrderAddress);
//            txtOrderDateTime = itemView.findViewById(R.id.txtOrderDateTime);
//            btnShowProducts = itemView.findViewById(R.id.btnShowProducts);
//        }
//    }
}
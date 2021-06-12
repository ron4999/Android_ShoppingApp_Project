package com.example.ecart;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ecart.Prevalent.Prevalent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ConfirmOrderActivity extends AppCompatActivity {
    private EditText edtUserShipName, edtUserShipPhone, edtUserShipAddress, edtUserShipCity;
    private Button btnConfirmOrder;

    private String totalPrice = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_order);

        totalPrice = getIntent().getStringExtra("totalPrice");
        Toast.makeText(this, "Tổng tiền = " + totalPrice, Toast.LENGTH_SHORT).show();

        initView();

        btnConfirmOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckConfirm();
            }
        });
    }

    private void CheckConfirm() {
        if (TextUtils.isEmpty(edtUserShipName.getText().toString())) {
            Toast.makeText(this, "Vui lòng điền đầy đủ họ tên", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(edtUserShipPhone.getText().toString())) {
            Toast.makeText(this, "Vui lòng điền đầy đủ số điện thoại", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(edtUserShipAddress.getText().toString())) {
            Toast.makeText(this, "Vui lòng điền đầy đủ địa chỉ", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(edtUserShipCity.getText().toString())) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thành phố", Toast.LENGTH_SHORT).show();
        }
        else {
            ConfirmCartOrder();
        }
    }

    private void ConfirmCartOrder() {
        final String saveCurrentDate, saveCurrentTime;

        Calendar calDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MM-yyyy");
        saveCurrentDate = currentDate.format(calDate.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calDate.getTime());

        final DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference().child("Orders")
                .child(Prevalent.currentOnlineUser.getPhone());

        final HashMap<String, Object> orderMap = new HashMap<>();
        orderMap.put("totalPrice", totalPrice);
        orderMap.put("name", edtUserShipName.getText().toString());
        orderMap.put("phone", edtUserShipPhone.getText().toString());
        orderMap.put("address", edtUserShipAddress.getText().toString());
        orderMap.put("city", edtUserShipCity.getText().toString());
        orderMap.put("date", saveCurrentDate);
        orderMap.put("time", saveCurrentTime);
        orderMap.put("status", "Chưa được giao");

        orderRef.updateChildren(orderMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull  Task<Void> task) {
                FirebaseDatabase.getInstance().getReference().child("Cart List")
                        .child("User Cart")
                        .child(Prevalent.currentOnlineUser.getPhone())
                        .child("Products")
                        .removeValue()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(ConfirmOrderActivity.this, "Đơn hàng đã được tạo thành công", Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(ConfirmOrderActivity.this, HomePageActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            }
                        });
            }
        });
    }

    private void initView() {
        edtUserShipName = findViewById(R.id.edtUserShipName);
        edtUserShipPhone = findViewById(R.id.edtUserShipPhone);
        edtUserShipAddress = findViewById(R.id.edtUserShipAddress);
        edtUserShipCity = findViewById(R.id.edtUserShipCity);
        btnConfirmOrder = findViewById(R.id.btnConfirmOrder);
    }
}
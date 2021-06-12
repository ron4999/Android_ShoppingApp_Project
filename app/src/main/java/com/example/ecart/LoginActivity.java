package com.example.ecart;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ecart.Model.Users;
import com.example.ecart.Prevalent.Prevalent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rey.material.widget.CheckBox;

import io.paperdb.Paper;

public class LoginActivity extends AppCompatActivity {
    private EditText edtLoginPhoneNum, edtLoginPassword;
    private Button btnLogin;
    private ProgressDialog loadingBar;
    private TextView txtAdminLogin, txtNotAdminLogin; 

    private String parendDB = "Users";

    private CheckBox ckb_remember;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initView();

        Paper.init(this);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });

        txtAdminLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnLogin.setText("Đăng nhập Admin");
                txtAdminLogin.setVisibility(View.INVISIBLE);
                txtNotAdminLogin.setVisibility(View.VISIBLE);
                parendDB = "Admins";
            }
        });

        txtNotAdminLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnLogin.setText("Đăng nhập");
                txtAdminLogin.setVisibility(View.VISIBLE);
                txtNotAdminLogin.setVisibility(View.INVISIBLE);
                parendDB = "Users";
            }
        });
    }

    private void loginUser() {
        String phone = edtLoginPhoneNum.getText().toString();
        String password = edtLoginPassword.getText().toString();

        if (TextUtils.isEmpty(phone)) {
            Toast.makeText(this, "Vui lòng nhập số điện thoại", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Vui lòng nhập mật khẩu", Toast.LENGTH_SHORT).show();
        }
        else {
            loadingBar.setTitle("Đăng nhập tài khoản");
            loadingBar.setMessage("Đang kiểm tra thông tin, vui lòng chờ");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            allowAccessToAccount(phone, password);
        }
    }

    private void allowAccessToAccount(String phone, String password) {
        if (ckb_remember.isChecked()) {
            Paper.book().write(Prevalent.UserPhoneKey, phone);
            Paper.book().write(Prevalent.UserPasswordKey, password);
        }

        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // nếu sđt có tồn tại
                if (snapshot.child(parendDB).child(phone).exists()) {
                    Users usersData = snapshot.child(parendDB).child(phone).getValue(Users.class);
                    if (usersData.getPhone().equals(phone)) {
                        if (usersData.getPassword().equals(password)) {
                            if (parendDB.equals("Admins")) {
                                Toast.makeText(LoginActivity.this, "Admin đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();

                                Intent intent = new Intent(LoginActivity.this, AdminCategoryActivity.class);
                                startActivity(intent);
                            }
                            else if (parendDB.equals("Users")) {
                                Toast.makeText(LoginActivity.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();

                                Intent intent = new Intent(LoginActivity.this, HomePageActivity.class);
                                Prevalent.currentOnlineUser = usersData;
                                startActivity(intent);
                            }
                        }
                        else {
                            loadingBar.dismiss();
                            Toast.makeText(LoginActivity.this, "Sai mật khẩu, thử lại!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                else {
                    Toast.makeText(LoginActivity.this, "Tài khoản với số " + phone + " không tồn tại!", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                    Toast.makeText(LoginActivity.this, "Bạn cần tạo tài khoản mới", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void initView() {
        btnLogin = findViewById(R.id.btnLogin);
        edtLoginPhoneNum = findViewById(R.id.edtLoginPhoneNum);
        edtLoginPassword = findViewById(R.id.edtLoginPassword);
        txtAdminLogin = findViewById(R.id.txtAdminLogin);
        txtNotAdminLogin = findViewById(R.id.txtNotAdminLogin);
        loadingBar = new ProgressDialog(this);
        ckb_remember = findViewById(R.id.ckb_remember);
    }
}

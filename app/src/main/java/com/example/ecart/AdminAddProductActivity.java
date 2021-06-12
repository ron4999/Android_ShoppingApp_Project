package com.example.ecart;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class AdminAddProductActivity extends AppCompatActivity {
    private String categoryName, productDes, productPrice, productName;
    private String saveCurrentDate, saveCurrentTime;
    private Button btnAddProduct;
    private ImageView imgSelectImgProduct;
    private EditText edtProductName, edtProductDes, edtProductPrice;
    private static final int galleryPick = 1;
    private Uri imageUri;
    private String productRandomKey, downloadImgUrl;
    private StorageReference productImgRef;
    private DatabaseReference productRef;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_product);

        initView();

        // Toast.makeText(this, "Xin chào Admin", Toast.LENGTH_SHORT).show();
        // gọi tên các category
        categoryName = getIntent().getExtras().get("category").toString();
        // Toast.makeText(this, categoryName, Toast.LENGTH_SHORT).show();

        productImgRef = FirebaseStorage.getInstance().getReference().child("Product Images");
        productRef = FirebaseDatabase.getInstance().getReference().child("Products");

        imgSelectImgProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // mở thư mục để chọn ảnh
                OpenGallery();
            }
        });

        btnAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // xác nhận thêm sản phẩm
                ValidateProductData();
            }
        });
    }

    private void ValidateProductData() {
        productDes = edtProductDes.getText().toString();
        productName = edtProductName.getText().toString();
        productPrice = edtProductPrice.getText().toString();

        if (imageUri == null) {
            Toast.makeText(this, "Cần chọn ảnh sản phẩm", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(productDes)) {
            Toast.makeText(this, "Vui lòng nhập mô tả sản phẩm", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(productName)) {
            Toast.makeText(this, "Vui lòng nhập tên sản phẩm", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(productPrice)) {
            Toast.makeText(this, "Vui lòng nhập giá sản phẩm", Toast.LENGTH_SHORT).show();
        }
        else {
            // lưu trữ thông tin mặt hàng
            StoreProductInfo();
        }
    }

    private void StoreProductInfo() {
        loadingBar.setTitle("Thêm sản phẩm");
        loadingBar.setMessage("Vui lòng đợi, đang thêm sản phẩm!");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MM-yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calendar.getTime());

        // thêm id ngẫu nhiên cho sản phẩm
        productRandomKey = saveCurrentDate + "_" + saveCurrentTime;

        StorageReference filePath = productImgRef.child(imageUri.getLastPathSegment() + productRandomKey + ".jpg");

        final UploadTask uploadTask = filePath.putFile(imageUri);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                String message = e.toString();
                Toast.makeText(AdminAddProductActivity.this, "Lỗi: " + message, Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(AdminAddProductActivity.this, "Upload ảnh thành công!", Toast.LENGTH_SHORT).show();
                Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }
                        downloadImgUrl = filePath.getDownloadUrl().toString();
                        return  filePath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        downloadImgUrl = task.getResult().toString();
                        Toast.makeText(AdminAddProductActivity.this, "Lưu Url ảnh thành công", Toast.LENGTH_SHORT).show();
                        SaveProductInfoIntoDB();
                    }
                });
            }
        });
    }

    private void SaveProductInfoIntoDB() {
        HashMap<String, Object> productMap = new HashMap<>();
        productMap.put("p_id", productRandomKey);
        productMap.put("date", saveCurrentDate);
        productMap.put("time", saveCurrentTime);
        productMap.put("description", productDes);
        productMap.put("image", downloadImgUrl);
        productMap.put("category", categoryName);
        productMap.put("price", productPrice);
        productMap.put("p_name", productName);

        productRef.child(productRandomKey).updateChildren(productMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Intent intent = new Intent(AdminAddProductActivity.this, AdminCategoryActivity.class);
                            startActivity(intent);

                            loadingBar.dismiss();
                            Toast.makeText(AdminAddProductActivity.this, "Sản phẩm được thêm thành công", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            loadingBar.dismiss();
                            String message = task.getException().toString();
                            Toast.makeText(AdminAddProductActivity.this, "Lỗi: " + message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void OpenGallery() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, galleryPick);
    }

    private void initView() {
        btnAddProduct = findViewById(R.id.btnAddProduct);
        imgSelectImgProduct = findViewById(R.id.imgSelectImgProduct);
        edtProductName = findViewById(R.id.edtProductName);
        edtProductDes = findViewById(R.id.edtProductDes);
        edtProductPrice = findViewById(R.id.edtProductPrice);
        loadingBar = new ProgressDialog(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == galleryPick && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            imgSelectImgProduct.setImageURI(imageUri);
        }
    }
}

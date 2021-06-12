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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ecart.Prevalent.Prevalent;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {
    private CircleImageView civSettingsProfileImg;
    private EditText edtChangeName, edtChangePhoneNum, edtChangeAddress;
    private TextView txtCloseSettings, txtUpdateSettings, txtChangeImage;

    private Uri imageUri;
    private String myUrl = "";
    private StorageTask uploadTask;
    private StorageReference storageReference;
    private String checker = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        storageReference = FirebaseStorage.getInstance().getReference().child("User Images");

        initView();

        userInfoDisplay(civSettingsProfileImg, edtChangeName, edtChangePhoneNum, edtChangeAddress);

        txtCloseSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        txtUpdateSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checker.equals("clicked")) {
                    userInfoSave();
                }
                else {
                    userInfoUpdateOnly();
                }
            }
        });

        txtChangeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checker = "clicked";

                CropImage.activity(imageUri)
                        .setAspectRatio(1, 1)
                        .start(SettingsActivity.this);
            }
        });
    }

    private void userInfoUpdateOnly() {
        if (TextUtils.isEmpty(edtChangeName.getText().toString())) {
            Toast.makeText(this, "Đây là trường bắt buộc", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(edtChangePhoneNum.getText().toString())) {
            Toast.makeText(this, "Đây là trường bắt buộc", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(edtChangeAddress.getText().toString())) {
            Toast.makeText(this, "Đây là trường bắt buộc", Toast.LENGTH_SHORT).show();
        }
       else {
            DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("Users");

            HashMap<String, Object> userMap = new HashMap<>();
            userMap.put("name", edtChangeName.getText().toString());
            userMap.put("phoneOrder", edtChangePhoneNum.getText().toString());
            userMap.put("address", edtChangeAddress.getText().toString());

            dbRef.child(Prevalent.currentOnlineUser.getPhone()).updateChildren(userMap);

            startActivity(new Intent(SettingsActivity.this, HomePageActivity.class));
            Toast.makeText(SettingsActivity.this, "Cập nhật thông tin thành công", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE
                && resultCode == RESULT_OK
                && data != null) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            imageUri = result.getUri();

            civSettingsProfileImg.setImageURI(imageUri);
        }
        else {
            Toast.makeText(this, "Lỗi, hãy thử lại", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(SettingsActivity.this, SettingsActivity.class));
            finish();
        }
    }

    private void userInfoSave() {
        if (TextUtils.isEmpty(edtChangeName.getText().toString())) {
            Toast.makeText(this, "Đây là trường bắt buộc", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(edtChangePhoneNum.getText().toString())) {
            Toast.makeText(this, "Đây là trường bắt buộc", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(edtChangeAddress.getText().toString())) {
            Toast.makeText(this, "Đây là trường bắt buộc", Toast.LENGTH_SHORT).show();
        }
        else if (checker.equals("clicked")) {
            uploadImage();
        }
    }

    private void uploadImage() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Cập nhật thông tin");
        progressDialog.setMessage("Vui lòng chờ");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        if (imageUri != null) {
            //lưu dẫn ảnh = số điện thoại + ".jpg"
            final StorageReference fileRef = storageReference
                    .child(Prevalent.currentOnlineUser.getPhone()
                    + ".jpg");

            uploadTask = fileRef.putFile(imageUri);

            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    return fileRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUrl = task.getResult();
                        myUrl = downloadUrl.toString();

                        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("Users");
                        HashMap<String, Object> userMap = new HashMap<>();
                        userMap.put("name", edtChangeName.getText().toString());
                        userMap.put("phoneOrder", edtChangePhoneNum.getText().toString());
                        userMap.put("address", edtChangeAddress.getText().toString());
                        userMap.put("image", myUrl);

                        dbRef.child(Prevalent.currentOnlineUser.getPhone()).updateChildren(userMap);

                        progressDialog.dismiss();

                        startActivity(new Intent(SettingsActivity.this, HomePageActivity.class));
                        Toast.makeText(SettingsActivity.this, "Cập nhật thông tin thành công", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                    else {
                        progressDialog.dismiss();
                        Toast.makeText(SettingsActivity.this, "Có lỗi xảy ra", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        else {
            Toast.makeText(this, "Chưa chọn hình ảnh", Toast.LENGTH_SHORT).show();
        }
    }

    private void userInfoDisplay(final CircleImageView civSettingsProfileImg, final EditText edtChangeName, final EditText edtChangePhoneNum, final EditText edtChangeAddress) {
        DatabaseReference UserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(Prevalent.currentOnlineUser.getPhone());

        UserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    if (snapshot.child("image").exists()) {
                        String image = snapshot.child("image").getValue().toString();
                        String name = snapshot.child("name").getValue().toString();
                        String phone = snapshot.child("phone").getValue().toString();
                        String address = snapshot.child("address").getValue().toString();

                        Picasso.get().load(image).into(civSettingsProfileImg);
                        edtChangeName.setText(name);
                        edtChangePhoneNum.setText(phone);
                        edtChangeAddress.setText(address);
                    }
                    if (!snapshot.child("image").exists()) {
                        if (snapshot.child("address").exists()) {
                            String name = snapshot.child("name").getValue().toString();
                            String phone = snapshot.child("phone").getValue().toString();


                            edtChangeName.setText(name);
                            edtChangePhoneNum.setText(phone);
                        }
                        else if (!snapshot.child("address").exists()) {
                            String name = snapshot.child("name").getValue().toString();
                            String phone = snapshot.child("phone").getValue().toString();
                            String address = snapshot.child("address").getValue().toString();

                            edtChangeName.setText(name);
                            edtChangePhoneNum.setText(phone);
                            edtChangeAddress.setText(address);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });
    }

    private void initView() {
        civSettingsProfileImg = findViewById(R.id.civSettingsProfileImg);
        edtChangeName = findViewById(R.id.edtChangeName);
        edtChangePhoneNum = findViewById(R.id.edtChangePhoneNum);
        edtChangeAddress = findViewById(R.id.edtChangeAddress);
        txtCloseSettings = findViewById(R.id.txtCloseSettings);
        txtUpdateSettings = findViewById(R.id.txtUpdateSettings);
        txtChangeImage = findViewById(R.id.txtChangeImage);
    }
}

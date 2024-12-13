package com.example.ticketbooking;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.json.JSONObject;

import services.AccountHomeService;

public class ChangeInfoUserActivity extends Activity {
    private EditText edt_Name, edt_Address,edt_FileChangeImage;
    private ImageButton btn_back;
    private Button button_edit;
    private ImageView img_User,img_Chosseimage;
    private AccountHomeService accountHomeService;
    private static final int PICK_IMGAE_REQUEST = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_info_user_activity);

        edt_Name = findViewById(R.id.edt_Name);
        edt_Address = findViewById(R.id.edt_Address);
        img_User = findViewById(R.id.img_User);
        img_Chosseimage = findViewById(R.id.img_Chosseimage);
        btn_back = findViewById(R.id.btn_back);
        edt_FileChangeImage = findViewById(R.id.edt_filechangeimage);
        button_edit = findViewById(R.id.button_edit);

        if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
        }

        accountHomeService = new AccountHomeService(this);

        btn_back.setOnClickListener(v -> onBackPressed());

        img_Chosseimage.setOnClickListener(v -> {
            // Mở trình chọn ảnh
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, PICK_IMGAE_REQUEST);
        });



        // Lấy thông tin tài khoản hiện tại
        fetchAccountInfo();

        // Gắn sự kiện cho nút "Lưu"
        button_edit.setOnClickListener(v -> {
            String name = edt_Name.getText().toString().trim();
            String address = edt_Address.getText().toString().trim();
            updateAccountInfo(name,address);
        });

    }

    private void fetchAccountInfo() {
        accountHomeService.getAccountInfo(new AccountHomeService.ResponseCallback() {
            @Override
            public void onSuccess(String success) {
                try {
                    // Parse the response and update the UI
                    JSONObject jsonResponse = new JSONObject(success);
                    JSONObject data = jsonResponse.optJSONObject("data");
                    if (data != null) {
                        String name = data.optString("name", "");
                        String address = data.optString("address", "");
                        String image = "https://ticket-system-l5j0.onrender.com/public/account/" + data.optString("_id") + "/" + data.optString("image");

                        // Update the UI on the main thread
                        runOnUiThread(() -> {
                            edt_Name.setText(name);
                            edt_Address.setText(address);

                            // Load the profile image
                            if (!image.isEmpty()) {
                                Glide.with(ChangeInfoUserActivity.this)
                                        .load(image)
                                        .override(350,350)
                                        .into(img_User);
                            }
                        });
                    }
                } catch (Exception e) {
                    Log.e("ChangeInfoUserActivity", "Error parsing account info", e);
                }
            }

            @Override
            public void onFailure(String error) {
                // Handle the error
                runOnUiThread(() ->
                        Toast.makeText(ChangeInfoUserActivity.this, "Failed to load account info: " + error, Toast.LENGTH_SHORT).show()
                );
                Log.e("ChangeInfoUserActivity", "Error fetching account info: " + error);
            }
        });
    }
    private void updateAccountInfo(String name, String address){
        if (name.isEmpty() || address.isEmpty()) {
            Toast.makeText(ChangeInfoUserActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Gọi API cập nhật thông tin
        accountHomeService.updateAccount(name, address, new AccountHomeService.ResponseCallback() {
            @Override
            public void onSuccess(String success) {
                // Quay về AccountUserFragment
                runOnUiThread(() -> {
                    Toast.makeText(ChangeInfoUserActivity.this, "Update successful", Toast.LENGTH_SHORT).show();
                    finish(); // Kết thúc activity
                });
            }

            @Override
            public void onFailure(String error) {
                // Xử lý lỗi
                runOnUiThread(() ->
                        Toast.makeText(ChangeInfoUserActivity.this, "Failed to update: " + error, Toast.LENGTH_SHORT).show()
                );
            }
        });
    }

}
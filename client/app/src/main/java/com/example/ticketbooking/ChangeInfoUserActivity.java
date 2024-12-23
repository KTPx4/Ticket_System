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
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.json.JSONObject;

import services.AccountHomeService;

public class ChangeInfoUserActivity extends Activity {
    private EditText edt_Name, edt_Address,edt_FileChangeImage,edt_Email;
    private ImageButton btn_back;
    private Button button_edit;
    private TextView tv_Email;
    private ImageView img_User,img_Chosseimage;
    private AccountHomeService accountHomeService;
    private static final int PICK_IMGAE_REQUEST = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_info_user_activity);

        edt_Name = findViewById(R.id.edt_Name);
        tv_Email = findViewById(R.id.tv_Email);
        edt_Address = findViewById(R.id.edt_Address);
        img_User = findViewById(R.id.img_User);
        btn_back = findViewById(R.id.btn_back);
        button_edit = findViewById(R.id.button_edit);

        if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
        }

        accountHomeService = new AccountHomeService(this);

        btn_back.setOnClickListener(v -> onBackPressed());

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
        String serverUrl = getString(R.string.server_url);
        accountHomeService.getAccountInfo(new AccountHomeService.ResponseCallback() {
            @Override
            public void onSuccess(String success) {
                try {
                    // Parse the response and update the UI
                    JSONObject jsonResponse = new JSONObject(success);
                    JSONObject data = jsonResponse.optJSONObject("data");
                    if (data != null) {
                        String name = data.optString("name", "");
                        String email = data.optString("email", "");
                        String address = data.optString("address", "");
                        String image = serverUrl + "/public/account/" + data.optString("_id") + "/" + data.optString("image");

                        // Update the UI on the main thread
                        runOnUiThread(() -> {
                            edt_Name.setText(name);
                            edt_Address.setText(address);
                            tv_Email.setText(email);

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
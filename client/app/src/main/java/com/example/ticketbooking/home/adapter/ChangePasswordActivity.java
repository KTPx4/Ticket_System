package com.example.ticketbooking.home.adapter;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.ticketbooking.R;

import services.AccountHomeService;

public class ChangePasswordActivity extends Activity {
    private EditText edt_old_password, edt_new_password, edt_new_password1;
    private ImageButton btn_back;
    private Button button_edit;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_activity_change_password);
        edt_old_password = findViewById(R.id.edt_old_password);
        edt_new_password = findViewById(R.id.edt_new_password);
        edt_new_password1 = findViewById(R.id.edt_new_password1);
        btn_back = findViewById(R.id.btn_back);
        button_edit = findViewById(R.id.button_edit);

        btn_back.setOnClickListener(v -> onBackPressed());

        button_edit.setOnClickListener(v -> {
            String oldPassword = edt_old_password.getText().toString().trim();
            String newPassword = edt_new_password.getText().toString().trim();
            String confirmPassword = edt_new_password1.getText().toString().trim();

            if (newPassword.isEmpty() || confirmPassword.isEmpty() || oldPassword.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!newPassword.equals(confirmPassword)) {
                Toast.makeText(this, "New passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            AccountHomeService accountService = new AccountHomeService(this);
            accountService.updatePassword(oldPassword, newPassword, new AccountHomeService.ResponseCallback() {
                @Override
                public void onSuccess(String success) {
                    runOnUiThread(() -> Toast.makeText(ChangePasswordActivity.this, "Password updated successfully", Toast.LENGTH_SHORT).show());
                }

                @Override
                public void onFailure(String error) {
                    runOnUiThread(() -> Toast.makeText(ChangePasswordActivity.this, "Failed to update password: " + error, Toast.LENGTH_SHORT).show());
                }
            });
        });

    }

}

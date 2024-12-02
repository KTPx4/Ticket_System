package com.example.ticketbooking;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import modules.LocalStorageManager;
import services.AccountService;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText txtEmail, txtPass;
    private Button btnRegister;
    private ImageButton btnBack;
    LocalStorageManager localStorageManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        txtEmail = findViewById(R.id.register_email);
        txtPass = findViewById(R.id.register_password);
        btnRegister = findViewById(R.id.register_button);
        btnBack = findViewById(R.id.back_button);
        localStorageManager = new LocalStorageManager(this);
        btnRegister.setOnClickListener(this);
        btnBack.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id == R.id.register_button)
        {
            String email = txtEmail.getText().toString();
            String pass = txtPass.getText().toString();

            if(email.isEmpty() || pass.isEmpty())
            {
                Toast.makeText(RegisterActivity.this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                if(email.isEmpty())
                {
                    txtEmail.requestFocus();
                }
                else{
                    txtPass.requestFocus();
                }
                return;
            }
            AccountService accountService = new AccountService(getApplicationContext());
            accountService.registerAccount(email, pass, new AccountService.ResponseCallback() {
                @Override
                public void onSuccess(String message) {
                    runOnUiThread(() -> {
                        Toast.makeText(RegisterActivity.this, "Tạo tài khoản thành công", Toast.LENGTH_SHORT).show();
                        localStorageManager.saveLoginToken(message);
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        RegisterActivity.this.finish();
                    });

                }

                @Override
                public void onFailure(String error) {
                    runOnUiThread(() -> {
                        Toast.makeText(RegisterActivity.this, error, Toast.LENGTH_SHORT).show();
                    });

                }
            });
        }
        else if(id == R.id.back_button)
        {
            this.finish();
        }

    }
}

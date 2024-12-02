package com.example.ticketbooking;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import modules.LocalStorageManager;
import services.AccountService;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{
    private boolean isUser = true;
    private EditText loginEmail, loginPassword;
    private String loginToken;
    AccountService accountService ;
    LocalStorageManager localStorageManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        accountService = new AccountService(this);
        localStorageManager = new LocalStorageManager(this);
        loginEmail = findViewById(R.id.login_email);
        loginPassword = findViewById(R.id.login_password);
        findViewById(R.id.login_button).setOnClickListener(this);
        findViewById(R.id.login_btn_register).setOnClickListener(this);
        getFromIntent();
        checkIsLogin();
    }

    void getFromIntent()
    {
        Intent itent = getIntent();
        String loginOption = itent.getStringExtra("login-option");
        loginToken = itent.getStringExtra("login-token");

        if(loginOption.equals("user"))
        {
            isUser = true;
        }
        else {
            isUser = false;
        }
    }

    void checkIsLogin()
    {
        if(loginToken != null && !loginToken.isEmpty())
        {
            // check verify token . if true -> main screen
            // else clear token -> login screen

            if (isUser) {
                accountService.verifyUser(loginToken, new AccountService.OnVerifyCallback() {
                    @Override
                    public void onSuccess() {
                        // Token hợp lệ cho người dùng, chuyển sang màn hình chính
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.putExtra("login-token", loginToken);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onFailure(String error) {
                        // Token không hợp lệ, hiển thị lỗi
                        runOnUiThread(() -> {
                            Toast.makeText(LoginActivity.this, error, Toast.LENGTH_LONG).show();
                        });
                    }
                });
            }

            else{
                accountService.verifyStaff(loginToken, new AccountService.OnRegisterCallback() {

                    @Override
                    public void onSuccess(String message) {

                    }

                    @Override
                    public void onFailure(String error) {

                    }
                });
            }
        }
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.login_button) {
            String email = loginEmail.getText().toString().trim();
            String password = loginPassword.getText().toString().trim();

            // Gọi API login user
            accountService.loginUser(email, password, new AccountService.OnRegisterCallback() {
                @Override
                public void onSuccess(String token) {
                    // Lưu token vào SharedPreferences thông qua LocalStorageManager
                    localStorageManager.saveLoginToken(token); // Lưu token
                    loginToken = token;

                    // Chuyển sang MainActivity và hiển thị token
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra("login-token", token);
                    startActivity(intent);
                    finish(); // Đóng LoginActivity
                }

                @Override
                public void onFailure(String error) {
                    // Hiển thị lỗi đăng nhập (có thể dùng Toast hoặc Dialog)
                    runOnUiThread(() -> {
                        Toast.makeText(LoginActivity.this, error, Toast.LENGTH_LONG).show();
                    });
                }
            });
        } else if (id == R.id.login_btn_register) {
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
            finish();
        }
    }

}

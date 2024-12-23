package com.example.ticketbooking;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import modules.LocalStorageManager;
import services.AccountService;
import staffactivity.StaffMainActivity;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{
    private boolean isUser = true;
    private EditText loginEmail, loginPassword;
    private TextView txtLogin;
    private String loginToken;
    private ImageButton btnBack;
    private LinearLayout layoutRegister;
    private Button forgot_password_button, btnDev;

    AccountService accountService ;
    // lưu token vào SharedPreferences thông qua LocalStorageManager
    LocalStorageManager localStorageManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        accountService = new AccountService(this);
        localStorageManager = new LocalStorageManager(this);
        loginEmail = findViewById(R.id.login_email);
        forgot_password_button = findViewById(R.id.forgot_password_button);
        loginPassword = findViewById(R.id.login_password);
        layoutRegister = findViewById(R.id.login_layout_resigter);
        txtLogin = findViewById(R.id.txtLogin);
        btnDev = findViewById(R.id.btnDev);
        btnDev.setOnClickListener(this);
        findViewById(R.id.back_button).setOnClickListener(this);
        findViewById(R.id.login_button).setOnClickListener(this);
        findViewById(R.id.login_btn_register).setOnClickListener(this);
        getFromIntent();
        checkIsLogin();

        forgot_password_button.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
        });
    }

    void getFromIntent()
    {
        Intent itent = getIntent();
        String loginOption = itent.getStringExtra("login-option");
        loginToken = itent.getStringExtra("login-token");

        if(loginOption.equals("user"))
        {
            isUser = true;
            layoutRegister.setVisibility(View.VISIBLE);
            txtLogin.setText("Login");
        }
        else if(loginOption.equals("staff")){
            isUser = false;
            layoutRegister.setVisibility(View.GONE);
            txtLogin.setText("Welcom Staff");

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
//                        localStorageManager.clearLoginToken();
                    }
                });
            }

            else{
                accountService.verifyStaff(loginToken, new AccountService.ResponseCallback() {

                    @Override
                    public void onSuccess(String message) {
                        // Chuyển sang screen cho staff
                        runOnUiThread(()->{
                            Intent intent = new Intent(getApplicationContext(), StaffMainActivity.class);
                            startActivity(intent);
                            finish();
                        });
                    }

                    @Override
                    public void onFailure(String error) {
                        // Token không hợp lệ, hiển thị lỗi
                        runOnUiThread(() -> {
                            Toast.makeText(LoginActivity.this, error, Toast.LENGTH_LONG).show();
                        });
//                        localStorageManager.clearLoginToken();
                    }
                });
            }
        }
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id == R.id.btnDev)
        {
            if(isUser)
            {
                // Gọi API login user
                accountService.loginUser("kieuthanhphat.work@gmail.com", "12345", new AccountService.ResponseCallback() {
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
            }
            else{
                accountService.loginStaff("px4", "px4.vnd@gmail.com", new AccountService.ResponseCallback() {
                    @Override
                    public void onSuccess(String token) {
                        // Lưu token vào SharedPreferences thông qua LocalStorageManager
                        localStorageManager.saveLoginToken(token); // Lưu token
                        loginToken = token;
                        runOnUiThread(()->{
                            Intent intent = new Intent(getApplicationContext(), StaffMainActivity.class);
                            startActivity(intent);
                            finish();
                        });
                        // Chuyển sang Screen cho staff
//                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//                        intent.putExtra("login-token", token);
//                        startActivity(intent);
//                        finish(); // Đóng LoginActivity
                    }

                    @Override
                    public void onFailure(String error) {
                        // Hiển thị lỗi đăng nhập (có thể dùng Toast hoặc Dialog)
                        runOnUiThread(() -> {
                            Toast.makeText(LoginActivity.this, error, Toast.LENGTH_LONG).show();
                        });
                    }
                });
            }
        }
        else if (id == R.id.login_button) {
            String email = loginEmail.getText().toString().trim();
            String password = loginPassword.getText().toString().trim();
            if(email.isEmpty() || password.isEmpty())
            {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                if(email.isEmpty())
                {
                    loginEmail.requestFocus();
                }
                else{
                    loginPassword.requestFocus();
                }
                return;
            }

            if(isUser)
            {
                // Gọi API login user
                accountService.loginUser(email, password, new AccountService.ResponseCallback() {
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
            }
            else{
                accountService.loginStaff(email, password, new AccountService.ResponseCallback() {
                    @Override
                    public void onSuccess(String token) {
                        // Lưu token vào SharedPreferences thông qua LocalStorageManager
                        localStorageManager.saveLoginToken(token); // Lưu token
                        loginToken = token;
                        runOnUiThread(()->{
                            Intent intent = new Intent(getApplicationContext(), StaffMainActivity.class);
                            startActivity(intent);
                            finish();
                        });
                        // Chuyển sang Screen cho staff
//                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//                        intent.putExtra("login-token", token);
//                        startActivity(intent);
//                        finish(); // Đóng LoginActivity
                    }

                    @Override
                    public void onFailure(String error) {
                        // Hiển thị lỗi đăng nhập (có thể dùng Toast hoặc Dialog)
                        runOnUiThread(() -> {
                            Toast.makeText(LoginActivity.this, error, Toast.LENGTH_LONG).show();
                        });
                    }
                });
            }



        }
        else if (id == R.id.login_btn_register) {
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
//            finish();
        }
        else if(id == R.id.back_button)
        {
            localStorageManager.clearLoginOption();
            Intent intent = new Intent(this, IntroActivity.class);
            startActivity(intent);
            finish();
        }
    }

}

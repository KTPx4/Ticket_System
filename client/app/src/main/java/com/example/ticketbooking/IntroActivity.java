package com.example.ticketbooking;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import modules.InternetConnection;
import modules.LocalStorageManager;
import services.AccountService;
import services.InternetCheckService;

public class IntroActivity extends AppCompatActivity implements View.OnClickListener{
    Button btnUser, btnStaff;
    LocalStorageManager localStorageManager ;
    private static String loginToken;
    private static String loginOption;
    AccountService accountService ;
    private static Boolean isUser = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);


        localStorageManager = new LocalStorageManager(this);
        accountService = new AccountService(this);
        initValue();
        btnUser = findViewById(R.id.user);
        btnStaff = findViewById(R.id.staff);
        btnUser.setOnClickListener(this);
        btnStaff.setOnClickListener(this);

    }
    void initValue() // check if has token -> login screen
    {

        loginOption = localStorageManager.getLoginOption();
        loginToken = localStorageManager.getLoginToken();
//        if(!loginOption.isEmpty() && !loginToken.isEmpty())
//        {
//            if(loginOption.equals("staff")) isUser = false;
//            checkIsLogin();
//        }
        if(!loginOption.isEmpty())
        {
            startLogin();
        }
    }
    void startLogin()
    {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra("login-option", loginOption);
        intent.putExtra("login-token", loginToken);
        startActivity(intent);
        this.finish();
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
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.putExtra("login-token", loginToken);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onFailure(String error) {
                        // Token không hợp lệ, hiển thị lỗi
                        runOnUiThread(() -> {
                            Toast.makeText(getApplicationContext(), error, Toast.LENGTH_LONG).show();
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

                    }

                    @Override
                    public void onFailure(String error) {
                        // Token không hợp lệ, hiển thị lỗi
                        runOnUiThread(() -> {
                            Toast.makeText(getApplicationContext(), error, Toast.LENGTH_LONG).show();
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

        if(id == R.id.user)
        {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.putExtra("login-option", "user");
            localStorageManager.saveLoginOption("user");
            startActivity(intent);
        }
        else if(id == R.id.staff)
        {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.putExtra("login-option", "staff");
            localStorageManager.saveLoginOption("staff");
            startActivity(intent);
        }
        this.finish();
    }
}

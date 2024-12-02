package com.example.ticketbooking;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import services.AccountService;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{
    private boolean isUser = true;
    private EditText loginEmail, loginPassword;
    private String loginToken;
    AccountService accountService ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        accountService = new AccountService(this);
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

            if(isUser)
            {
                accountService.verifyUser(loginToken, new AccountService.OnRegisterCallback() {
                    @Override
                    public void onSuccess(String message) {

                    }

                    @Override
                    public void onFailure(String error) {

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
        if(id == R.id.login_button)
        {

        }
        else if(id == R.id.login_btn_register){
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
            this.finish();
        }
    }
}

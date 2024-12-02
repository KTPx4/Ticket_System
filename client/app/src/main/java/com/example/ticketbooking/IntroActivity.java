package com.example.ticketbooking;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import modules.LocalStorageManager;

public class IntroActivity extends AppCompatActivity implements View.OnClickListener{
    Button btnUser, btnStaff;
    LocalStorageManager localStorageManager ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        localStorageManager = new LocalStorageManager(this);
        initValue();
        btnUser = findViewById(R.id.user);
        btnStaff = findViewById(R.id.staff);
        btnUser.setOnClickListener(this);
        btnStaff.setOnClickListener(this);

    }
    void initValue() // check if has token -> login screen
    {
        String loginOption = localStorageManager.getLoginOption();
        String loginToken = localStorageManager.getLoginToken();

        if(!loginOption.isEmpty())
        {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.putExtra("login-option", loginOption);
            intent.putExtra("login-token", loginToken);
            startActivity(intent);
            this.finish();
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

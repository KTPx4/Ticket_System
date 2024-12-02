package com.example.ticketbooking;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import modules.LocalStorageManager;

public class MainActivity extends AppCompatActivity {

    private LocalStorageManager localStorageManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_user);

        // Khởi tạo LocalStorageManager
        localStorageManager = new LocalStorageManager(this);

        // Lấy token từ Intent
        String token = getIntent().getStringExtra("login-token");
        TextView tokenView = findViewById(R.id.token_view);
        tokenView.setText("Token của bạn: " + token);

        // Đặt sự kiện cho nút logout
        Button btnLogout = findViewById(R.id.btn_logout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });

        // Đặt trang chính là HomeUserFragment
        if (savedInstanceState == null) {
            showFragment(new HomeUserFragment());
        }
    }

    // Hàm logout
    private void logout() {
        // Xoá token và login option đã lưu
        localStorageManager.clearLoginOption();
        localStorageManager.clearLoginToken();

        // Quay lại IntroActivity
        Intent intent = new Intent(MainActivity.this, IntroActivity.class);
        startActivity(intent);
        finish();
    }



    private void showFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_fragment, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bottom_nav_menu, menu);
        return true;
    }
}

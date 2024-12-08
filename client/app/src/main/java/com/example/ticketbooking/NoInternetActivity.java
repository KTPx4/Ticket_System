package com.example.ticketbooking;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import modules.InternetConnection;

public class NoInternetActivity extends AppCompatActivity {
    Button btnReload;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_no_internet);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        btnReload = findViewById(R.id.btnReload);
        btnReload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            // Kiểm tra kết nối Internet
                if (InternetConnection.isConnected(NoInternetActivity.this)) {
                    // Có kết nối, chuyển sang IntroActivity
                    Intent intent = new Intent(NoInternetActivity.this, IntroActivity.class);
                    startActivity(intent);
                    finish(); // Đóng NoInternetActivity
                } else {
                    // Không có kết nối, hiển thị thông báo
                    Toast.makeText(NoInternetActivity.this, "Không có kết nối mạng!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
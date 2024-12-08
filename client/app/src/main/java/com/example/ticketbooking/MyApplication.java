package com.example.ticketbooking;

import android.app.Application;
import android.content.Intent;

import services.InternetCheckService;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // Khởi động Service một lần duy nhất
        Intent serviceIntent = new Intent(this, InternetCheckService.class);
        startService(serviceIntent);
    }
}

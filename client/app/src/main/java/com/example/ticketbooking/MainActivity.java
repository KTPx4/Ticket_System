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
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.ticketbooking.databinding.ActivityMainUserBinding;

import modules.LocalStorageManager;

public class MainActivity extends AppCompatActivity {
    ActivityMainUserBinding binding;
    private LocalStorageManager localStorageManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Khởi tạo LocalStorageManager
        localStorageManager = new LocalStorageManager(this);

        String token = localStorageManager.getLoginToken();

        // Đặt trang chính là HomeUserFragment
//        if (savedInstanceState == null) {
//            showFragment(new HomeUserFragment());
//        }
        showFragment(new HomeUserFragment());

        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.home) {
                showFragment(new HomeUserFragment());
            } else if (id == R.id.tickets) {
                showFragment(new TicketsUserFragment());
            } else if (id == R.id.account) {
                showFragment(new AccountUserFragment());
            }
            return true;
        });
    }

    private void showFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_fragment, fragment);
        fragmentTransaction.commit();
    }
}

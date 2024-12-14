package com.example.ticketbooking.coupon;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager2.widget.ViewPager2;

import com.example.ticketbooking.R;
import com.example.ticketbooking.coupon.adapter.ViewPagerAdapter;
import com.example.ticketbooking.coupon.fragment.ExchangeFragment;
import com.example.ticketbooking.coupon.fragment.MyCouponFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class CouponActivity extends AppCompatActivity {
    TabLayout tabLayout;
    ViewPager2 viewPager;
    private SwipeRefreshLayout swipeRefreshLayout;
    ViewPagerAdapter viewPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.coupon_activity_main);

        // Ánh xạ các view
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);

        // Khởi tạo adapter cho ViewPager2
        viewPagerAdapter = new ViewPagerAdapter(this);
        viewPager.setAdapter(viewPagerAdapter);

        // Liên kết TabLayout với ViewPager2
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            if (position == 0) {
                tab.setText("Mã giảm giá");
            } else if (position == 1) {
                tab.setText("Đổi mã");
            }
        }).attach();

        // Cấu hình SwipeRefreshLayout
        swipeRefreshLayout.setOnRefreshListener(() -> {
            // Reload data logic
            reloadCurrentFragment();
        });
    }

    private void reloadCurrentFragment() {
        // Lấy fragment hiện tại dựa trên vị trí ViewPager
        int currentPosition = viewPager.getCurrentItem();
        if (currentPosition == 0) {
            // Reload fragment MyCouponFragment
            MyCouponFragment fragment = (MyCouponFragment) getSupportFragmentManager().findFragmentByTag("f" + currentPosition);
            if (fragment != null) {
                fragment.reloadData();
            }
        } else if (currentPosition == 1) {
            // Reload fragment ExchangeFragment
            ExchangeFragment fragment = (ExchangeFragment) getSupportFragmentManager().findFragmentByTag("f" + currentPosition);
            if (fragment != null) {
                fragment.reloadData();
            }
        }
        // Tắt trạng thái làm mới
        swipeRefreshLayout.setRefreshing(false);
    }
}

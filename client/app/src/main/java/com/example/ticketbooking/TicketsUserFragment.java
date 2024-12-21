package com.example.ticketbooking;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager2.widget.ViewPager2;

import com.example.ticketbooking.R;
import com.example.ticketbooking.coupon.CouponActivity;
import com.example.ticketbooking.coupon.fragment.ExchangeFragment;
import com.example.ticketbooking.coupon.fragment.MyCouponFragment;
import com.example.ticketbooking.ticket.adapter.ViewPagerAdapter;
import com.example.ticketbooking.ticket.fragment.PendingFragment;
import com.example.ticketbooking.ticket.fragment.TicketFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class TicketsUserFragment extends Fragment {
    TabLayout tabLayout;
    ViewPager2 viewPager;
    ViewPagerAdapter viewPagerAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    FloatingActionButton btnDiscount;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_tickets_user, container, false);

        // Khởi tạo ViewPager2 và TabLayout
        swipeRefreshLayout = rootView.findViewById(R.id.swipeRefreshLayout);
        tabLayout = rootView.findViewById(R.id.tabLayout);
        viewPager = rootView.findViewById(R.id.viewPager);
        btnDiscount = rootView.findViewById(R.id.btnDiscount);

        btnDiscount.setOnClickListener((v)->{
            Intent intent = new Intent(getContext(), CouponActivity.class);
            startActivity(intent);
        });
        // Khởi tạo adapter và gán vào ViewPager2
        viewPagerAdapter = new ViewPagerAdapter(requireActivity());
        viewPager.setAdapter(viewPagerAdapter);

        // Liên kết TabLayout với ViewPager2
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            if (position == 0) {
                tab.setText("Vé đã mua");
            } else if (position == 1) {
                tab.setText("Chờ thanh toán");
            }
        }).attach();

        // Cấu hình SwipeRefreshLayout
        swipeRefreshLayout.setOnRefreshListener(() -> {
            // Reload data logic
            reloadCurrentFragment();
        });


        return rootView;
    }

    private void reloadCurrentFragment() {
        // Lấy fragment hiện tại dựa trên vị trí ViewPager
        int currentPosition = viewPager.getCurrentItem();
        if (currentPosition == 0) {
            // Reload fragment MyCouponFragment
            TicketFragment fragment = (TicketFragment) getParentFragmentManager().findFragmentByTag("f" + currentPosition);
            if (fragment != null) {
                fragment.reloadData(0);
            }
        } else if (currentPosition == 1) {
            // Reload fragment ExchangeFragment
            TicketFragment fragment = (TicketFragment) getParentFragmentManager().findFragmentByTag("f" + currentPosition);
            if (fragment != null) {
                fragment.reloadData(1);
            }
        }
        // Tắt trạng thái làm mới
        swipeRefreshLayout.setRefreshing(false);
    }

}

package com.example.ticketbooking.coupon.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.ticketbooking.coupon.fragment.ExchangeFragment;
import com.example.ticketbooking.coupon.fragment.MyCouponFragment;
import com.example.ticketbooking.ticket.fragment.TicketFragment;

public class ViewPagerAdapter extends FragmentStateAdapter {

    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return MyCouponFragment.newInstance(); // Tab "Vé đã mua"
            case 1:
                return ExchangeFragment.newInstance(); // Tab "Chờ thanh toán"
            default:
                return null;
        }
    }

    @Override
    public int getItemCount() {
        return 2; // Số lượng tab
    }
}

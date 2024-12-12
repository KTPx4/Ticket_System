package com.example.ticketbooking.ticket.adapter;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.ticketbooking.ticket.fragment.PendingFragment;
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
                return TicketFragment.newInstance(0); // Tab "Vé đã mua"
            case 1:
                return TicketFragment.newInstance(1); // Tab "Chờ thanh toán"
            default:
                return null;
        }
    }

    @Override
    public int getItemCount() {
        return 2; // Số lượng tab
    }
}

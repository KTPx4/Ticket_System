package com.example.ticketbooking.ticket.adapter;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.ticketbooking.ticket.fragment.PendingFragment;
import com.example.ticketbooking.ticket.fragment.TicketFragment;


public class ViewPagerAdapter
        extends FragmentPagerAdapter {

    public ViewPagerAdapter(
            @NonNull FragmentManager fm)
    {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position)
    {
        Fragment fragment = null;
        if (position == 0)
            fragment = new TicketFragment();
        else if (position == 1)
            fragment = new PendingFragment();


        return fragment;
    }

    @Override
    public int getCount()
    {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position)
    {
        String title = null;
        if (position == 0)
            title = "Vé đã mua";
        else if (position == 1)
            title = "Chờ thanh toán";

        return title;
    }
}
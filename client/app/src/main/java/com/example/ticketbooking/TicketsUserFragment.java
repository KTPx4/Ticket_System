package com.example.ticketbooking;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.ticketbooking.ticket.adapter.ViewPagerAdapter;
import com.google.android.material.tabs.TabLayout;

public class TicketsUserFragment extends Fragment {
    TabLayout tabLayout;
    ViewPager viewPager;
    ViewPagerAdapter viewPagerAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflating layout của TicketsUserFragment (thay đổi tên layout nếu cần)
        View rootView = inflater.inflate(R.layout.fragment_tickets_user, container, false);

        // Khởi tạo các view (TabLayout, ViewPager)
        tabLayout = rootView.findViewById(R.id.tabLayout);
        viewPager = rootView.findViewById(R.id.viewPager);

        // Khởi tạo ViewPagerAdapter và gán cho ViewPager
        viewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager());
        viewPager.setAdapter(viewPagerAdapter);

        // Liên kết TabLayout và ViewPager
        tabLayout.setupWithViewPager(viewPager);

        return rootView;
    }
}

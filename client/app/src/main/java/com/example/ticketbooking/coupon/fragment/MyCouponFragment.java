package com.example.ticketbooking.coupon.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ticketbooking.R;
import com.example.ticketbooking.coupon.adapter.CouponAdapter;

import java.util.ArrayList;
import java.util.List;

import model.Coupon;
import services.CouponService;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MyCouponFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyCouponFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private RecyclerView listMyCoupon;
    private List<Coupon> listCoupon = new ArrayList<>();
    private CouponAdapter couponAdapter;
    private CouponService couponService;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public MyCouponFragment() {
        // Required empty public constructor
    }


    public static MyCouponFragment newInstance( ) {
        MyCouponFragment fragment = new MyCouponFragment();
        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        couponService = new CouponService(getContext());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.coupon_fragment_my_coupon, container, false);
        listMyCoupon = view.findViewById(R.id.listMyCoupon);

        listMyCoupon.setLayoutManager(new LinearLayoutManager(getContext()));
        couponAdapter = new CouponAdapter(getContext(), listCoupon);
        listMyCoupon.setAdapter(couponAdapter);

        initData();

        return view;
    }

    public void reloadData() {
        initData();
    }
    void initData() {
        couponService.GetMyCoupon(new CouponService.CouponCallBack() {
            @Override
            public void onSuccess(List<Coupon> coupon, long point) {
                getActivity().runOnUiThread(() -> {

                    listMyCoupon.setVisibility(View.VISIBLE);
                    listCoupon.clear(); // Xóa dữ liệu cũ
                    listCoupon.addAll(coupon); // Thêm dữ liệu mới
                    couponAdapter.notifyDataSetChanged(); // Thông báo dữ liệu đã thay đổi
                });
            }

            @Override
            public void onFailure(String error) {
                getActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
}
package com.example.ticketbooking.coupon.fragment;

import android.app.AlertDialog;
import android.graphics.Color;
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
import android.widget.ProgressBar;
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
 * Use the {@link ExchangeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ExchangeFragment extends Fragment implements View.OnClickListener{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private List<Coupon> listCoupon = new ArrayList<>();
    private CouponService couponService;
    TextView tvPoint;
    Button btn2, btn3, btn4, btn5, btn10;
    RecyclerView lvPubCoupon;
    private long myPoint;
    private CouponAdapter couponAdapter;

    boolean isLoading = true;
    ProgressBar loading;

    public ExchangeFragment() {
        // Required empty public constructor
    }



    public static ExchangeFragment newInstance( ) {
        ExchangeFragment fragment = new ExchangeFragment();
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
    public void reloadData() {
        initData();
    }
    void initData() {
        couponService.GetPublicCoupon(new CouponService.CouponCallBack() {
            @Override
            public void onSuccess(List<Coupon> coupon, long point) {
                getActivity().runOnUiThread(() -> {
                    isLoading = false;
                    loading.setVisibility(View.GONE);
                    lvPubCoupon.setVisibility(View.VISIBLE);
                    Log.d("onSuccess", "onSuccess: " + coupon.stream().count());
                    listCoupon.clear(); // Xóa dữ liệu cũ
                    listCoupon.addAll(coupon); // Thêm dữ liệu mới
                    myPoint = point;
                    tvPoint.setText("Điểm của bạn: " + point +" pts");
                    couponAdapter.notifyDataSetChanged(); // Thông báo dữ liệu đã thay đổi
                });
            }

            @Override
            public void onFailure(String error) {
                getActivity().runOnUiThread(() -> {
                    isLoading = false;
                    loading.setVisibility(View.GONE);
                    lvPubCoupon.setVisibility(View.VISIBLE);
                    Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.coupon_fragment_exchange, container, false);

        tvPoint = view.findViewById(R.id.tvMyPoint);
        btn2 = view.findViewById(R.id.btn2);
        btn3 = view.findViewById(R.id.btn3);
        btn4 = view.findViewById(R.id.btn4);
        btn5 = view.findViewById(R.id.btn5);
        btn10 = view.findViewById(R.id.btn10);
        lvPubCoupon = view.findViewById(R.id.listPublicCoupon);
        loading = view.findViewById(R.id.loading);

        lvPubCoupon.setLayoutManager(new LinearLayoutManager(getContext()));
        couponAdapter = new CouponAdapter(getContext(), listCoupon);
        lvPubCoupon.setAdapter(couponAdapter);

        btn2.setOnClickListener(this);
        btn3.setOnClickListener(this);
        btn4.setOnClickListener(this);
        btn5.setOnClickListener(this);
        btn10.setOnClickListener(this);



        initData();
        return view;
    }

    @Override
    public void onClick(View v) {
        if(isLoading)
        {
            return;
        }

        int id  = v.getId();
        int point;
        if(id == R.id.btn2) {point = 2000;}
        else if(id == R.id.btn3){ point = 3000;}
        else if(id == R.id.btn4){ point = 4000;}
        else if(id == R.id.btn5){ point = 5000;}
        else if(id == R.id.btn10){ point = 10000;}
        else { point = 0;}

        if(point == 0) return;
        if(point > myPoint)
        {
            Toast.makeText(getContext(), "Điểm bạn không đủ đổi", Toast.LENGTH_SHORT).show();
            return;
        }
        else{
            AlertDialog dialog = new AlertDialog.Builder(getContext())
                    .setTitle("Đổi mã giảm giá")
                    .setMessage("Bạn có muốn đổi mã giảm giá " + point +" điểm")
                    .setPositiveButton("Hủy", (dia, which)->{
                        dia.dismiss();
                    })
                    .setNegativeButton("Đổi", (dialog2, which) -> {
                        couponService.ExchangeCoupon(point, new CouponService.CouponCallBack() {
                            @Override
                            public void onSuccess(List<Coupon> coupon, long point) {
                                getActivity().runOnUiThread(()->{
                                    myPoint = point;
                                    tvPoint.setText("Điểm của bạn: " + point +" pts");
                                    Toast.makeText(getContext(), "Đổi mã giảm giá thành công!", Toast.LENGTH_SHORT).show();
                                });
                            }

                            @Override
                            public void onFailure(String error) {
                                getActivity().runOnUiThread(()->{
                                    Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                                });
                            }
                        });
                        dialog2.dismiss();
                    }).create();

            dialog.show();
            // Tùy chỉnh màu văn bản của các nút
            dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK); // Màu cho nút "Hủy"
            dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_NEGATIVE).setTextColor(getContext().getColor(R.color.inValid_Ticket));  // Màu cho nút "Xóa"


        }

    }
}
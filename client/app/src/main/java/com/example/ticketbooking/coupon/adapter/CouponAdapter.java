package com.example.ticketbooking.coupon.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ticketbooking.R;

import java.util.List;

import model.Coupon;

public class CouponAdapter extends RecyclerView.Adapter<CouponAdapter.CouponViewHolder> {

    private Context context;
    private List<Coupon> coupons;

    public CouponAdapter(Context context, List<Coupon> coupons) {
        this.context = context;
        this.coupons = coupons;
    }

    @NonNull
    @Override
    public CouponViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate layout cho mỗi item trong RecyclerView
        View view = LayoutInflater.from(context).inflate(R.layout.coupon_item_coupon, parent, false);
        return new CouponViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CouponViewHolder holder, int position) {
        // Bind dữ liệu vào ViewHolder
        Coupon coupon = coupons.get(position);
        holder.tvCode.setText(coupon.getCode());
        holder.tvDiscount.setText(coupon.getPercentDiscount() + "%");
        holder.tvMaxDiscount.setText(coupon.getMaxDiscount() != -1 ? coupon.getMaxDiscount() + "đ" : "Không giới hạn");
        holder.tvCount.setText(String.valueOf(coupon.getCount()));
    }

    @Override
    public int getItemCount() {
        return coupons.size(); // Trả về số lượng item
    }

    // ViewHolder class cho RecyclerView
    public static class CouponViewHolder extends RecyclerView.ViewHolder {
        TextView tvCode, tvDiscount, tvMaxDiscount, tvCount;

        public CouponViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCode = itemView.findViewById(R.id.text1);
            tvDiscount = itemView.findViewById(R.id.text2);
            tvMaxDiscount = itemView.findViewById(R.id.text3);
            tvCount = itemView.findViewById(R.id.text4);
        }
    }
}

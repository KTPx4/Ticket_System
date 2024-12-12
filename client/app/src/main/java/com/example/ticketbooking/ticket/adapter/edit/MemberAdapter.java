package com.example.ticketbooking.ticket.adapter.edit;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ticketbooking.R;

import java.util.List;

import model.account.Account;

public class MemberAdapter extends RecyclerView.Adapter<MemberAdapter.MemberViewHolder> {
    private List<Account> listMember;
    private Context context;

    public MemberAdapter(List<Account> listMember, Context context) {
        this.listMember = listMember;
        this.context = context;
    }

    @NonNull
    @Override
    public MemberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.ticket_item_edit_member, parent, false);
        return new MemberViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MemberViewHolder holder, int position) {
        Account account = listMember.get(position);
        holder.tvName.setText(account.getName());
        holder.tvEmail.setText(account.getEmail());

        holder.btnDel.setOnClickListener(v -> {
            int adapterPosition = holder.getBindingAdapterPosition();
            if (adapterPosition == RecyclerView.NO_POSITION) {
                return; // Vị trí không hợp lệ
            }

            AlertDialog dglog = new AlertDialog.Builder(context)
                    .setTitle("Xác nhận")
                    .setMessage("Bạn có chắc chắn muốn xóa thành viên?")
                    .setPositiveButton("Hủy", (dialog, which) -> dialog.dismiss())
                    .setNegativeButton("Xóa", (dialog, which) -> {
                        listMember.remove(adapterPosition); // Xóa phần tử từ danh sách
                        notifyItemRemoved(adapterPosition); // Cập nhật RecyclerView
                        notifyItemRangeChanged(adapterPosition, listMember.size()); // Đảm bảo vị trí các phần tử được cập nhật
                        Toast.makeText(context, "Đã xóa: " + account.getName(), Toast.LENGTH_SHORT).show();
                    })
                    .create();

            dglog.show();
            dglog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK); // Màu cho nút "Hủy"
            dglog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(context.getColor(R.color.inValid_Ticket)); // Màu cho nút "Xóa"
        });

    }

    @Override
    public int getItemCount() {
        return listMember.size();
    }
    public List<Account> getListMember() {
        return listMember;
    }

    public static class MemberViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvEmail;
        ImageButton btnDel;

        public MemberViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvEmail = itemView.findViewById(R.id.tvEmail);
            btnDel = itemView.findViewById(R.id.btnDel);
        }
    }

    // Phương thức thêm thành viên mới
    public void addMember(Account account) {
        listMember.add(account);
        notifyItemInserted(listMember.size() - 1);
    }
}

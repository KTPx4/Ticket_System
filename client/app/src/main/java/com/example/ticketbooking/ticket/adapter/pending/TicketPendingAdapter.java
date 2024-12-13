package com.example.ticketbooking.ticket.adapter.pending;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ticketbooking.R;
import com.example.ticketbooking.ticket.viewcustom.TicketView;

import java.util.ArrayList;
import java.util.List;

import model.ticket.MyPending;
import model.ticket.Ticket;
import model.ticket.TicketInfo;
import modules.MoneyFormatter;

public class TicketPendingAdapter extends RecyclerView.Adapter<TicketPendingAdapter.TicketViewHolder> {
    public interface OnTicketClickListener {
        void onTicketClick(String ticketId);
        void onDelTicket(String infoId);
    }


    private final Context context;
    private final List<TicketInfo> listInfo;

    private final OnTicketClickListener listener; // Tham chiếu interface

    public TicketPendingAdapter(Context context, List<TicketInfo> ticketInfos, OnTicketClickListener listener) {
        this.context = context;
        this.listInfo = ticketInfos;
        this.listener = listener; // Gán giá trị

    }
    @NonNull
    @Override
    public TicketPendingAdapter.TicketViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.ticket_item_ticket, parent, false);
        return new TicketPendingAdapter.TicketViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TicketPendingAdapter.TicketViewHolder holder, int position) {

        TicketInfo info = listInfo.get(position);
        Log.d("onBindViewHolder", "Position: "+ position + " \nInfo: "+ info.toString());

        holder.bind(context, info, listener);

    }

    @Override
    public int getItemCount() {
        return listInfo.size();
    }

    public static class TicketViewHolder extends RecyclerView.ViewHolder {
        TextView tvId, tvType, tvLocation, tvPosition, tvPrice;
        TicketView ticketView;
        ImageButton btnDel;
        boolean isChecked = false;

        public TicketViewHolder(View itemView) {
            super(itemView);
            tvId = itemView.findViewById(R.id.tvId);
            tvType = itemView.findViewById(R.id.tvType);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            tvPosition = itemView.findViewById(R.id.tvPosition);
            ticketView = itemView.findViewById(R.id.item_ticket);
            btnDel = itemView.findViewById(R.id.btnDelTicket);
            btnDel.setVisibility(View.VISIBLE);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvPrice.setVisibility(View.VISIBLE);
        }
        void setColorText(int color)
        {
            tvId.setTextColor(color);
            tvType.setTextColor(color);
            tvLocation.setTextColor(color);
            tvPosition.setTextColor(color);

        }

        public void bind(Context context, TicketInfo info, OnTicketClickListener listener) {

            Ticket ticket = info.getTicket();
            // Gắn dữ liệu vào View
            tvId.setText(ticket.get_id());
            tvType.setText("Loại vé: " + ticket.getInfo().getTypeTicket());
            tvLocation.setText("Khu vực: " + ticket.getInfo().getLocation());
            tvPosition.setText("Ghế: " + ticket.getPosition());
            tvPrice.setText("Giá vé: " + MoneyFormatter.formatCurrency(ticket.getInfo().getPrice()));

            if(ticket.getAccBuy() != null && !ticket.getAccBuy().isEmpty())
            {
                ticketView.setBackgroundColor(context.getColor(R.color.inValid_Ticket));
            }


            // Set sự kiện click ticket
            ticketView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(ticket.getAccBuy() != null && !ticket.getAccBuy().isEmpty())
                    {
                        Toast.makeText(context, "Vé này đã được mua", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Notify parent via callback
                    if (listener != null) {
                        listener.onTicketClick(info.get_id());
                    }

                    if(isChecked)
                    {
                        // REMOVE from list of callback
                        setColorText(context.getColor(R.color.text_Ticket));
                        ticketView.setFullBackgroundColor(context.getColor(R.color.back_Ticket));
                        tvId.setTextColor(context.getColor(R.color.text_hint));
                    }
                    else{
                        setColorText(context.getColor(R.color.selected_Ticket_text));
                        // add to list of callback
                        ticketView.setFullBackgroundColor(context.getColor(R.color.selected_Ticket));
                    }
                    isChecked = !isChecked;

                }
            });

            // event delete ticket
            btnDel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Notify parent via callback
                    if (listener != null) {
                        listener.onDelTicket(info.get_id());
                    }
                }
            });
        }
    }

}

package com.example.ticketbooking.ticket.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.ticketbooking.R;
import com.example.ticketbooking.ticket.TicketInfoActivity;
import com.example.ticketbooking.ticket.viewcustom.TicketView;

import model.ticket.*;
import java.util.List;

public class TicketItemAdapter extends RecyclerView.Adapter<TicketItemAdapter.TicketViewHolder> {
    private final Context context;
    private final List<Ticket> ticketList;

    public TicketItemAdapter(Context context, List<Ticket> ticketList) {
        this.context = context;
        this.ticketList = ticketList;
    }

    @NonNull
    @Override
    public TicketViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.ticket_item_ticket, parent, false);
        return new TicketViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TicketViewHolder holder, int position) {
        Ticket ticket = ticketList.get(position);
        holder.tvId.setText(ticket.get_id());
        holder.tvType.setText("Loại vé: "+ticket.getTicketInfo().getTypeTicket());
        holder.tvLocation.setText("Vị trí: "+ticket.getTicketInfo().getLocation());
        holder.tvPosition.setText("Số ghế: "+ ticket.getPosition());
        holder.bind(context, ticket);
    }

    @Override
    public int getItemCount() {
        return ticketList.size();
    }

    public static class TicketViewHolder extends RecyclerView.ViewHolder {
        TextView tvId, tvType, tvLocation, tvPosition;
        TicketView ticketView;

        public TicketViewHolder(View itemView) {
            super(itemView);
            tvId = itemView.findViewById(R.id.tvId);
            tvType = itemView.findViewById(R.id.tvType);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            tvPosition = itemView.findViewById(R.id.tvPosition);
            ticketView = itemView.findViewById(R.id.item_ticket);
        }

        public void bind(Context context, Ticket ticket) {
            // Gắn dữ liệu vào View
            tvId.setText(ticket.get_id());
            tvType.setText("Loại vé: " + ticket.getTicketInfo().getTypeTicket());
            tvLocation.setText("Vị trí: " + ticket.getTicketInfo().getLocation());
            tvPosition.setText("Số ghế: " + ticket.getPosition());

            // Set sự kiện click
            ticketView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, TicketInfoActivity.class);

                    // Truyền dữ liệu qua Intent
                    intent.putExtra("ticketId", ticket.get_id());
                    intent.putExtra("type", ticket.getTicketInfo().getTypeTicket());
                    intent.putExtra("location", ticket.getTicketInfo().getLocation());
                    intent.putExtra("position", ticket.getPosition() + "");

                    // Khởi chạy Activity
                    context.startActivity(intent);
                }
            });
        }
    }

}

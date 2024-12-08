package com.example.ticketbooking.ticket.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.ticketbooking.R;
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
        holder.tvType.setText(ticket.getInfo().getTypeTicket());
        holder.tvLocation.setText(ticket.getInfo().getLocation());
        holder.tvPosition.setText(ticket.getPosition());
    }

    @Override
    public int getItemCount() {
        return ticketList.size();
    }

    public static class TicketViewHolder extends RecyclerView.ViewHolder {
        TextView tvId, tvType, tvLocation, tvPosition;

        public TicketViewHolder(View itemView) {
            super(itemView);
            tvId = itemView.findViewById(R.id.tvId);
            tvType = itemView.findViewById(R.id.tvType);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            tvPosition = itemView.findViewById(R.id.tvPosition);
        }
    }
}

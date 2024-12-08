package com.example.ticketbooking.ticket.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ticketbooking.R;

import java.util.List;

import model.ticket.MyTicket;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {
    private final Context context;
    private final List<MyTicket> myTicketList;

    public EventAdapter(Context context, List<MyTicket> myTicketList) {
        this.context = context;
        this.myTicketList = myTicketList;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.ticket_item_event, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        MyTicket myTicket = myTicketList.get(position);
        holder.tvEventName.setText(myTicket.getEvent().getName());
        holder.tvEventDate.setText(myTicket.getEvent().getDate().getStart());

        // Set the TicketItemAdapter for the nested RecyclerView (listTicket)
        TicketItemAdapter ticketItemAdapter = new TicketItemAdapter(context, myTicket.getTickets());
        holder.listTicket.setAdapter(ticketItemAdapter);
    }

    @Override
    public int getItemCount() {
        return myTicketList.size();
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView tvEventName, tvEventDate;
        RecyclerView listTicket;

        public EventViewHolder(View itemView) {
            super(itemView);
            tvEventName = itemView.findViewById(R.id.tvName);
            tvEventDate = itemView.findViewById(R.id.tvDate);
            listTicket = itemView.findViewById(R.id.listTicket);
        }
    }
}

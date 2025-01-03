package com.example.ticketbooking.ticket.adapter.myticket;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

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
//    public TicketItemAdapter(Context context, List<TicketInfo> ticketInfos, int option) {
//        this.context = context;
//        List<Ticket> tickets = new ArrayList<>();
//        for(TicketInfo ticketInfo : ticketInfos)
//        {
//            tickets.add(ticketInfo.getTicket());
//        }
//        this.ticketList = tickets;
//    }

    @NonNull
    @Override
    public TicketViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.ticket_item_ticket, parent, false);
        return new TicketViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TicketViewHolder holder, int position) {
        Ticket ticket = ticketList.get(position);
//        holder.tvId.setText(ticket.get_id());
//        holder.tvType.setText("Loại vé: "+ticket.getInfo().getTypeTicket());
//        holder.tvLocation.setText("Vị trí: "+ticket.getInfo().getLocation());
//        holder.tvPosition.setText("Số ghế: "+ ticket.getPosition());
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
            tvType.setText("Loại vé: " + ticket.getInfo().getTypeTicket());
            tvLocation.setText("Khu vực: " + ticket.getInfo().getLocation());
            tvPosition.setText("Ghế: " + ticket.getPosition());

            if(!ticket.isValid())
            {
                ticketView.setBackgroundColor(context.getColor(R.color.inValid_Ticket));
            }
            // Set sự kiện click
            ticketView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(!ticket.isValid())
                    {
                        Toast.makeText(context, "Vé này đã được sử dụng", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Intent intent = new Intent(context, TicketInfoActivity.class);

                    try{
                        String name =  "";
                        if(ticket.getEventDetails() != null && ticket.getEventDetails().getName() != null)
                        {
                            name = ticket.getEventDetails().getName();
                        }

                        // Truyền dữ liệu qua Intent
                        intent.putExtra("ticketId", ticket.get_id());
                        intent.putExtra("type", ticket.getInfo().getTypeTicket());
                        intent.putExtra("location", ticket.getInfo().getLocation());
                        intent.putExtra("position", ticket.getPosition() + "");
                        intent.putExtra("desc", ticket.getDesc());
                        intent.putExtra("name", name);

                        // Khởi chạy Activity
                        context.startActivity(intent);
                    } catch (Exception e) {
                        Log.d("ERROR", "  " + e.getMessage());
                        Toast.makeText(context, "Lỗi! Hãy thử lại sau", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

}

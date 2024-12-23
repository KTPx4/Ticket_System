package com.example.ticketbooking.home.adapter;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.ticketbooking.Event;
import com.example.ticketbooking.R;

import java.util.List;

public class FollowEventAdapter extends RecyclerView.Adapter<FollowEventAdapter.FollowEventViewHolder>{
    private List<Event> events;

    public FollowEventAdapter(List<Event> events) {
        this.events = events;
    }

    @Override
    public FollowEventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_follow_event, parent, false);
        return new FollowEventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FollowEventViewHolder holder, int position) {
        Event event = events.get(position);
        holder.tvEventName.setText(event.getName());
        holder.start_date.setText(event.getStartDate());
        holder.itemView.setOnClickListener(v -> {
            Log.d("EventAdapter", "Event ID: " + event.getId());
            Intent intent = new Intent(v.getContext(), DetailsEventActivity.class);
            intent.putExtra("eventId", event.getId());  // Truyền eventId qua Intent
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public static class FollowEventViewHolder extends RecyclerView.ViewHolder {
        TextView tvEventName,start_date;

        public FollowEventViewHolder(View itemView) {
            super(itemView);
            tvEventName = itemView.findViewById(R.id.event_name);
            start_date = itemView.findViewById(R.id.start_date);
        }
    }
}

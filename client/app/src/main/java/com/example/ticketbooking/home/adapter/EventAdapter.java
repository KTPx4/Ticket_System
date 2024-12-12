package com.example.ticketbooking.home.adapter;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ticketbooking.DetailsEventActivity;
import com.example.ticketbooking.Event;
import com.example.ticketbooking.R;

import java.util.ArrayList;
import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {
    private List<Event> eventList;
    private int nameTextColor = Color.BLACK;

    public EventAdapter(List<Event> eventList) {
        this.eventList = eventList;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_item_home, parent, false);
        return new EventViewHolder(view);
    }

    public void setNameTextColor(int color) {
        this.nameTextColor = color;
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = eventList.get(position);
        holder.eventName.setText(event.getName());
        String formattedPrice = String.format("%,d", event.getMinPrice());
        holder.eventPriceRange.setText("Từ " + formattedPrice + " đ ");
        holder.eventDate.setText(event.getStartDate());

        holder.eventName.setTextColor(nameTextColor);

        String imageUrl = "https://ticket-system-l5j0.onrender.com/public/event/" + event.getId() + "/" + event.getImage();

        // Load the image using Glide
        Glide.with(holder.eventImage.getContext())
                .load(imageUrl)
                .into(holder.eventImage);

        holder.itemView.setOnClickListener(v -> {
            // Pass event details to the DetailsEventActivity
            Intent intent = new Intent(holder.itemView.getContext(), DetailsEventActivity.class);
            intent.putExtra("event_id", event.getId());
            intent.putExtra("event_name", event.getName());
            intent.putExtra("event_location", event.getLocation());
            intent.putExtra("event_desc", event.getDescription());
            intent.putExtra("event_start_time", event.getStartDate());
            intent.putExtra("event_image", imageUrl);
            intent.putExtra("event_price", event.getMinPrice());

            holder.itemView.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {
        ImageView eventImage;
        TextView eventName, eventPriceRange, eventDate;

        public EventViewHolder(View itemView) {
            super(itemView);
            eventImage = itemView.findViewById(R.id.imageView2);
            eventName = itemView.findViewById(R.id.event_name);
            eventPriceRange = itemView.findViewById(R.id.event_price_range);
            eventDate = itemView.findViewById(R.id.event_date);
        }
    }
}
package com.example.ticketbooking;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {
    private List<Event> eventList;

    public EventAdapter(List<Event> eventList) {
        this.eventList = eventList;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_item_home, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = eventList.get(position);
        holder.eventName.setText(event.getName());
        String formattedPrice = String.format("%,d", event.getMinPrice());
        holder.eventPriceRange.setText("Từ " + formattedPrice + " đ ");
        holder.eventDate.setText(event.getStartDate());

        String imageUrl = "https://ticket-system-l5j0.onrender.com/public/event/" + event.getId() + "/" + event.getImage();

        // Load the image using Glide
        Glide.with(holder.eventImage.getContext())
                .load(imageUrl)
                .into(holder.eventImage);
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


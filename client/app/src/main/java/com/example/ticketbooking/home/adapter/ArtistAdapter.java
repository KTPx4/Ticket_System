package com.example.ticketbooking.home.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.ticketbooking.R;

import java.util.List;

public class ArtistAdapter extends RecyclerView.Adapter<ArtistAdapter.ArtistViewHolder> {

    private List<Artist> artists;  // Update this to hold Artist objects

    // Constructor to accept the list of Artist objects
    public ArtistAdapter(List<Artist> artists) {
        this.artists = artists;
    }

    @Override
    public ArtistViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.artist_item_detailsevent, parent, false);
        return new ArtistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ArtistViewHolder holder, int position) {
        Artist artist = artists.get(position);  // Get the Artist object
        holder.tvArtistName.setText(artist.getArtistName());  // Set artist name

        // Set a click listener to pass the artist ID through Intent
        holder.itemView.setOnClickListener(v -> {
            // Assuming you're in an activity, use the correct context to launch the next activity
            Intent intent = new Intent(v.getContext(), InfoArtistActivity.class);
            intent.putExtra("artistId", artist.getArtistId());  // Pass the artist ID as an extra
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return artists.size();
    }

    public static class ArtistViewHolder extends RecyclerView.ViewHolder {

        TextView tvArtistName;

        public ArtistViewHolder(View itemView) {
            super(itemView);
            tvArtistName = itemView.findViewById(R.id.artist_name);  // Replace with your actual view ID
        }
    }
}

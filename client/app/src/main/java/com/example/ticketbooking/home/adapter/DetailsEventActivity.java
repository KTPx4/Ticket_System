package com.example.ticketbooking.home.adapter;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ticketbooking.R;
import com.example.ticketbooking.ticket.ticket_activity_booking_ticket;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import services.AccountHomeService;

public class DetailsEventActivity extends AppCompatActivity {

    private ImageView imgBack, img_banner;
    private TextView tv_event_name, tv_event_time, tv_event_location, tv_description, tv_price,tv_trailer;
    private RecyclerView recycler_artist;
    private ArtistAdapter artistAdapter;
    private AccountHomeService accountHomeService;
    private Button btnBookingTicket;
    private String EventID = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_event);

        // Initialize views
        imgBack = findViewById(R.id.img_back);
        tv_event_name = findViewById(R.id.tv_event_name);
        tv_description = findViewById(R.id.tv_description);
        tv_event_location = findViewById(R.id.tv_event_location);
        tv_event_time = findViewById(R.id.tv_event_time);
        img_banner = findViewById(R.id.img_banner);
        tv_trailer = findViewById(R.id.tv_trailer);
        tv_price = findViewById(R.id.tv_price);
        recycler_artist = findViewById(R.id.recycler_artist);
        btnBookingTicket = findViewById(R.id.btn_select_schedule);
        // Handle back button click
        imgBack.setOnClickListener(v -> onBackPressed());
        String eventId = getIntent().getStringExtra("eventId");
        Log.d("DetailsEventActivity", "Event ID: " + eventId);


        accountHomeService = new AccountHomeService(this);

        fetchEvenByID();

        tv_trailer.setOnClickListener((v)->{
            if(EventID == null || EventID.isEmpty())
            {
                Toast.makeText(this, "Video chưa được tải thành công", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(this, TrailerEventActivity.class);
            intent.putExtra("idEvent", EventID);
            startActivity(intent);
        });

        btnBookingTicket.setOnClickListener((v)->{
            if(EventID == null || EventID.isEmpty())
            {
                Toast.makeText(this, "Sự kiện chưa được tải thành công", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(this, ticket_activity_booking_ticket.class);
            intent.putExtra("idEvent", EventID);
            startActivity(intent);
        });

    }

    private void fetchEvenByID(){
        String eventId = getIntent().getStringExtra("eventId");
        EventID= eventId;
        accountHomeService.getEventById(eventId, new AccountHomeService.ResponseCallback() {
            @Override
            public void onSuccess(String response) {
                // Xử lý dữ liệu sự kiện
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    JSONObject data = jsonResponse.optJSONObject("data");
                    if(data != null){
                        String name = data.optString("name");
                        String description = data.optString("desc");
                        String location = data.optString("location");
                        String timeStart = data.optJSONObject("date").optString("start");
                        String timeEnd = data.optJSONObject("date").optString("end");
                        String priceMin = data.optJSONObject("priceRange").optString("min");

                        String formattedTime = timeStart + " Đến " + timeEnd;
                        String formattedPrice = "Từ " + formatPrice(priceMin) + "đ";

                        List<String> artistIds = new ArrayList<>();
                        for (int i = 0; i < data.optJSONArray("artists").length(); i++) {
                            artistIds.add(data.optJSONArray("artists").optString(i));
                        }

                        fetchArtists(artistIds);

                        runOnUiThread(() -> {
                            tv_event_name.setText(name);
                            tv_description.setText(description);
                            tv_event_location.setText(location);
                            tv_event_time.setText(formattedTime);
                            tv_price.setText(formattedPrice);
                            String imageUrl = "https://ticket-system-l5j0.onrender.com/public/event/" + eventId + "/" + data.optString("image");
                            String trailerUrl = "https://ticket-system-l5j0.onrender.com/public/event/" + eventId + "/trailer" + "/" + data.optString("trailer");
                            Glide.with(DetailsEventActivity.this)
                                    .load(imageUrl)
                                    .into(img_banner);
                        });

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(String message) {
                // Xử lý khi có lỗi
                Log.e("DetailsEventActivity", "Error: " + message);
            }
        });
    }


    private void fetchArtists(List<String> artistIds) {
        List<Artist> artistList = new ArrayList<>(); // List to hold artist data

        for (String artistId : artistIds) {
            accountHomeService.getArtistById(artistId, new AccountHomeService.ResponseCallback() {
                @Override
                public void onSuccess(String response) {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        JSONObject artistData = jsonResponse.optJSONObject("data");
                        if (artistData != null) {
                            // Get the 'desc' JSONObject
                            JSONObject desc = artistData.optJSONObject("desc");
                            if (desc != null) {
                                String artistName = desc.optString("artistName"); // Fetch artistName

                                // Add artist to the list with both artistName and artistId
                                artistList.add(new Artist(artistName, artistId));  // Pass both artistName and artistId

                                // After all artists have been fetched, set the adapter
                                if (artistList.size() == artistIds.size()) {
                                    runOnUiThread(() -> {
                                        artistAdapter = new ArtistAdapter(artistList); // Set the adapter
                                        recycler_artist.setLayoutManager(new LinearLayoutManager(DetailsEventActivity.this));
                                        recycler_artist.setAdapter(artistAdapter); // Set adapter to RecyclerView
                                    });
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(String message) {
                    Log.e("DetailsEventActivity", "Error: " + message);
                }
            });
        }
    }


    private String formatPrice(String price) {
        try {
            int priceInt = Integer.parseInt(price);
            return String.format("%,d", priceInt); // Format price with commas
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return price;
        }
    }
}


package com.example.ticketbooking;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class DetailsEventActivity extends AppCompatActivity {

    private ImageView imgBack,img_banner;
    private TextView tv_event_name,tv_event_time, tv_event_location,tv_description,tv_price;
    private RecyclerView recycler_artist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_event);

        // Ánh xạ các view
        imgBack = findViewById(R.id.img_back);
        tv_event_name = findViewById(R.id.tv_event_name);
        tv_description = findViewById(R.id.tv_description);
        tv_event_location = findViewById(R.id.tv_event_location);
        tv_event_time = findViewById(R.id.tv_event_time);
        img_banner = findViewById(R.id.img_banner);
        tv_price = findViewById(R.id.tv_price);
        recycler_artist = findViewById(R.id.recycler_artist);
        recycler_artist.setLayoutManager(new LinearLayoutManager(this));

        // Get data passed from the previous activity
        Intent intent = getIntent();
        String eventId = intent.getStringExtra("event_id");
        String eventName = intent.getStringExtra("event_name");
        String eventLocation = intent.getStringExtra("event_location");
        String eventDesc = intent.getStringExtra("event_desc");
        String eventStartTime = intent.getStringExtra("event_start_time");
        String eventImage = intent.getStringExtra("event_image");
        int eventPrice = intent.getIntExtra("event_price", 0);

        // Set the data to the views
        tv_event_name.setText(eventName);
        tv_event_location.setText(eventLocation);
        tv_description.setText(eventDesc);
        tv_event_time.setText(eventStartTime);

        String formattedPrice = String.format("%,d", eventPrice);  // Format the price
        tv_price.setText("Từ " + formattedPrice + " đ");

        // Load the banner image using Glide
        Glide.with(this)
                .load(eventImage)
                .into(img_banner);

        // Xử lý sự kiện cho nút back
        imgBack.setOnClickListener(v -> onBackPressed());

    }

}

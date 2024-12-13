package com.example.ticketbooking.home.adapter;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.util.Log;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.ticketbooking.R;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import services.AccountHomeService;

public class TrailerEventActivity extends AppCompatActivity {
    private VideoView videoView;
    private MediaController mediaController;
    private AccountHomeService accountHomeService;
    private String EventID = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ativity_video_event);
        String eventId = getIntent().getStringExtra("idEvent");

        accountHomeService = new AccountHomeService(this);
        videoView = findViewById(R.id.videoViewTrailer);
        ImageButton btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> onBackPressed());
        mediaController = new MediaController(this);
        // Log the event ID to check if it's passed correctly
        Log.d("TrailerEventActivity", "Event ID: " + eventId);
        fetchEvenByID();
    }

    private void fetchEvenByID(){
        String eventId = getIntent().getStringExtra("idEvent");
        EventID= eventId;
        accountHomeService.getEventById(eventId, new AccountHomeService.ResponseCallback() {
            @Override
            public void onSuccess(String response) {
                // Xử lý dữ liệu sự kiện
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    JSONObject data = jsonResponse.optJSONObject("data");
                    if(data != null){
                        runOnUiThread(() -> {
                            String trailerUrl = "https://ticket-system-l5j0.onrender.com/public/event/" + eventId + "/trailer" + "/" + data.optString("trailer");
                            videoView.setVideoPath(trailerUrl);
                            mediaController.setAnchorView(videoView);
                            videoView.setMediaController(mediaController);
                            videoView.start();
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
}

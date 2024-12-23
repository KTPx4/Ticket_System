package com.example.ticketbooking.home.adapter;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ticketbooking.R;
import com.example.ticketbooking.history.HistoryActivity;
import com.example.ticketbooking.news.NewsActivity;
import com.example.ticketbooking.news.NewsDetailsActivity;
import com.example.ticketbooking.ticket.ticket_activity_booking_ticket;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import model.event.News;
import modules.LocalStorageManager;
import services.AccountHomeService;
import services.EventService;

public class DetailsEventActivity extends AppCompatActivity {

    private ImageView imgBack, img_banner;
    private TextView tv_event_name, tv_event_time, tv_event_location, tv_description, tv_price,tv_trailer;
    private RecyclerView recycler_artist;
    private ArtistAdapter artistAdapter;
    private AccountHomeService accountHomeService;
    private Button btnBookingTicket, btnAfterEvent;
    private String EventID = "";
    private TextView btnMore;
    private HorizontalScrollView listNews;
    private LinearLayout linearListNews;
    private String eventId = "";
    private String eventName = "";
    private String userId = "";
    private boolean isFollowing = false;
    private LocalStorageManager localStorageManager;
    private ImageButton img_follow;

    private EventService eventService;
    private List<News> dataListNews = new ArrayList<>();

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
        img_follow = findViewById(R.id.img_follow);
        img_banner = findViewById(R.id.img_banner);
        tv_trailer = findViewById(R.id.tv_trailer);
        tv_price = findViewById(R.id.tv_price);
        recycler_artist = findViewById(R.id.recycler_artist);
        btnBookingTicket = findViewById(R.id.btn_select_schedule);

        // Handle back button click
        imgBack.setOnClickListener(v -> onBackPressed());
        localStorageManager = new LocalStorageManager(this);
        userId = localStorageManager.getIdUser();
        Log.d("InfoArtistActivity", "User ID: " + userId);
        eventId = getIntent().getStringExtra("eventId");
        Log.d("DetailsEventActivity", "Event ID: " + eventId);
        btnAfterEvent = findViewById(R.id.btnAfterEvent);
        btnMore = findViewById(R.id.btnMore);
        listNews = findViewById(R.id.listNews);
        linearListNews = findViewById(R.id.linearListNews);

        accountHomeService = new AccountHomeService(this);

        eventService = new EventService(this);
        fetchEvenByID();
        loadNews();

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

        btnMore.setOnClickListener((v)->{
            Intent intent = new Intent(this, NewsActivity.class);
            intent.putExtra("idEvent", eventId);
            intent.putExtra("name", eventName);
            startActivity(intent);
        });

        btnAfterEvent.setOnClickListener((v)->{
            Intent intent = new Intent(this, HistoryActivity.class);
            intent.putExtra("eventId", eventId);
            startActivity(intent);
        });

        img_follow.setOnClickListener(v -> {
            if (isFollowing) {
                UnFollowEvent(eventId);
            } else {
                FollowEvent(eventId);
            }
        });
    }

    private void loadNews(){

        eventService.GetNews(eventId, 4, new EventService.CallBackGetAllNews() {
            @Override
            public void onSuccess(List<News> listNews) {
                if (listNews == null || listNews.isEmpty()) {
                    return;
                }
                try{
                    runOnUiThread(()->{
//                    dataListNews.clear();
//                    dataListNews.addAll(listNews);
                        linearListNews.removeAllViews();
                        LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
                        for (News news : listNews) {

                            View itemView = inflater.inflate(R.layout.event_item_news, linearListNews, false);

                            // Thiết lập dữ liệu cho item
                            ImageView btnImage = itemView.findViewById(R.id.btnImage);
                            TextView textView = itemView.findViewById(R.id.textView);
                            TextView textViewDate = itemView.findViewById(R.id.tvDate);

                            textView.setText(news.getTitle());
                            textViewDate.setText(news.getCreatedAt());
                            btnImage.setImageResource(R.drawable.img_news);
                            // Nếu bạn cần load ảnh
//                        Glide.with(this).load(news.getImageUrl()).into(btnImage);

                            // to activity news
                            textView.setOnClickListener(v -> {
                                Intent intent = new Intent(getApplicationContext(), NewsDetailsActivity.class);
                                intent.putExtra("id",news.get_id() );
                                startActivity(intent);
                            });

                            btnImage.setOnClickListener(v -> {
                                Intent intent = new Intent(getApplicationContext(), NewsDetailsActivity.class);
                                intent.putExtra("id",news.get_id() );
                                startActivity(intent);
                            });

                            // Thêm view vào LinearLayout
                            linearListNews.addView(itemView);
                        }
                    });

                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

            }

            @Override
            public void onFailure(String error) {
                runOnUiThread(()->{
                    Toast.makeText(getApplicationContext(), error, Toast.LENGTH_SHORT).show();
                });
            }
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

                        List<String> followersList = new ArrayList<>();
                        for (int i = 0; i < data.optJSONArray("followers").length(); i++) {
                            followersList.add(data.optJSONArray("followers").optString(i));
                        }

                        isFollowing = followersList.contains(userId); // Update follow status

                        Log.d("InfoArtistActivity", "isFollowing " + isFollowing);


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

                            if (isFollowing) {
                                img_follow.setColorFilter(ContextCompat.getColor(DetailsEventActivity.this, R.color.Blush_Pink));
                            } else {
                                img_follow.setColorFilter(ContextCompat.getColor(DetailsEventActivity.this, R.color.white));
                            }
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

    private void FollowEvent(String eventId){
        accountHomeService.FollowEvent(eventId, new EventService.ResponseCallback() {
            @Override
            public void onSuccess(String success) {
                Log.d("DetailsEventActivity", "Success: " + success);
                runOnUiThread(() -> {
                    img_follow.setColorFilter(ContextCompat.getColor(DetailsEventActivity.this, R.color.Blush_Pink));
                    Toast.makeText(DetailsEventActivity.this, "Đã theo dõi sự kiện!", Toast.LENGTH_SHORT).show();  // Show toast
                });
                fetchEvenByID();
            }

            @Override
            public void onFailure(String error) {
                Log.d("DetailsEventActivity", "Error: " + error);
            }
        });
    }


    private void UnFollowEvent(String eventId){
        accountHomeService.UnFollowEvent(eventId, new EventService.ResponseCallback() {
            @Override
            public void onSuccess(String success) {
                Log.d("DetailsEventActivity", "Success: " + success);
                runOnUiThread(() -> {
                    img_follow.setColorFilter(ContextCompat.getColor(DetailsEventActivity.this, R.color.white));
                    Toast.makeText(DetailsEventActivity.this, "Đã bỏ theo dõi sự kiện!", Toast.LENGTH_SHORT).show();  // Show toast
                });
                fetchEvenByID();
            }

            @Override
            public void onFailure(String error) {
                Log.d("DetailsEventActivity", "Error: " + error);
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


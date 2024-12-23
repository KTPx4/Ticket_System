package com.example.ticketbooking.home.adapter;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ticketbooking.Event;
import com.example.ticketbooking.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import modules.LocalStorageManager;
import services.AccountHomeService;

public class FollowListActivity extends Activity {
    private ImageButton btnBack;
    private LinearLayout btnArtist;
    private LinearLayout btnEvent;
    private RecyclerView recyclerFollow;
    private LinearLayoutManager linearLayoutManager;
    private AccountHomeService accountHomeService;
    private String userId = "";
    private LocalStorageManager localStorageManager;
    private ArtistAdapter artistAdapter;
    private FollowEventAdapter followEventAdapter;
    private List<String> artistIds = new ArrayList<>();
    private List<String> eventIds = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_activity_followlist);

        btnBack = findViewById(R.id.btn_back);
        btnArtist = findViewById(R.id.btn_artist);
        btnEvent = findViewById(R.id.btn_event);
        recyclerFollow = findViewById(R.id.recyclerview_follow);

        linearLayoutManager = new LinearLayoutManager(this);
        recyclerFollow.setLayoutManager(linearLayoutManager);

        accountHomeService = new AccountHomeService(this);

        localStorageManager = new LocalStorageManager(this);
        userId = localStorageManager.getIdUser();
        Log.d("InfoArtistActivity", "User ID: " + userId);

        // Xử lý sự kiện cho các nút
        btnBack.setOnClickListener(view -> finish()); // Quay lại màn hình trước đó

        btnArtist.setOnClickListener(view -> {
            // Call fetchArtists when btnArtist is clicked
            fetchArtists(artistIds);
        });

        btnEvent.setOnClickListener(view -> {
            fetchEvents(eventIds);
        });

        fetchAccountInfo();
    }

    private void fetchAccountInfo() {
        accountHomeService.getAccountInfo(new AccountHomeService.ResponseCallback() {
            @Override
            public void onSuccess(String success) {
                try {
                    // Parse the response and extract the data
                    JSONObject jsonResponse = new JSONObject(success);
                    JSONObject data = jsonResponse.optJSONObject("data");
                    if (data != null) {
                        JSONObject follows = data.optJSONObject("follows");

                        // Extract event and artist lists
                        JSONArray eventArray = follows.optJSONArray("event");
                        JSONArray artistArray = follows.optJSONArray("artist");

                        // Get event IDs
                        if (eventArray != null) {
                            for (int i = 0; i < eventArray.length(); i++) {
                                JSONObject event = eventArray.optJSONObject(i);
                                if (event != null) {
                                    String eventId = event.optString("_id");
                                    eventIds.add(eventId);
                                }
                            }
                        }

                        // Get artist IDs
                        if (artistArray != null) {
                            for (int i = 0; i < artistArray.length(); i++) {
                                JSONObject artist = artistArray.optJSONObject(i);
                                if (artist != null) {
                                    String artistId = artist.optString("_id");
                                    artistIds.add(artistId);
                                }
                            }
                        }

                        runOnUiThread(() -> {
                            Log.d("FollowListActivity", "Event IDs: " + eventIds);
                            Log.d("FollowListActivity", "Artist IDs: " + artistIds);
                        });
                    }
                } catch (Exception e) {
                    Log.e("FollowListActivity", "Error parsing account info", e);
                }
            }

            @Override
            public void onFailure(String error) {
                // Handle the error
                runOnUiThread(() ->
                        Toast.makeText(FollowListActivity.this, "Failed to load account info: " + error, Toast.LENGTH_SHORT).show()
                );
                Log.e("FollowListActivity", "Error fetching account info: " + error);
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
                                String artistName = desc.optString("artistName");

                                // Add artist to the list with both artistName and artistId
                                artistList.add(new Artist(artistName, artistId));  // Pass both artistName and artistId

                                // After all artists have been fetched, set the adapter
                                if (artistList.size() == artistIds.size()) {
                                    runOnUiThread(() -> {
                                        artistAdapter = new ArtistAdapter(artistList); // Set the adapter
                                        recyclerFollow.setLayoutManager(new LinearLayoutManager(FollowListActivity.this));
                                        recyclerFollow.setAdapter(artistAdapter); // Set adapter to RecyclerView
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

    private void fetchEvents(List<String> eventIds) {
        List<Event> eventList = new ArrayList<>();

        for (String eventId : eventIds) {
            accountHomeService.getEventById(eventId, new AccountHomeService.ResponseCallback() {
                @Override
                public void onSuccess(String response) {
                    try {
                        // Parse the response into a JSONObject
                        JSONObject jsonResponse = new JSONObject(response);
                        JSONObject eventData = jsonResponse.optJSONObject("data");

                        if (eventData != null) {
                            // Extract event details
                            String eventId = eventData.optString("_id");
                            String eventName = eventData.optString("name");
                            String eventDescription = eventData.optString("desc");
                            String eventLocation = eventData.optString("location");

                            // Extract price range
                            JSONObject priceRange = eventData.optJSONObject("priceRange");
                            int minPrice = priceRange != null ? priceRange.optInt("min", 0) : 0;
                            int maxPrice = priceRange != null ? priceRange.optInt("max", 0) : 0;

                            // Extract date range
                            JSONObject dateRange = eventData.optJSONObject("date");
                            String startDate = dateRange != null ? dateRange.optString("start") : "";
                            String endDate = dateRange != null ? dateRange.optString("end") : "";

                            // Extract artists (if any)
                            List<String> artists = new ArrayList<>();
                            JSONArray artistsArray = eventData.optJSONArray("artists");
                            if (artistsArray != null) {
                                for (int i = 0; i < artistsArray.length(); i++) {
                                    artists.add(artistsArray.optString(i));
                                }
                            }

                            // Extract event image
                            String eventImage = eventData.optString("image");

                            // Create an Event object
                            Event event = new Event(eventId, eventName, eventDescription, eventLocation, startDate, endDate, eventImage, minPrice, maxPrice, artists);

                            // Add event to the list
                            eventList.add(event);

                            // After all events have been fetched, set the adapter
                            if (eventList.size() == eventIds.size()) {
                                runOnUiThread(() -> {
                                    FollowEventAdapter followEventAdapter = new FollowEventAdapter(eventList);
                                    recyclerFollow.setLayoutManager(new LinearLayoutManager(FollowListActivity.this));
                                    recyclerFollow.setAdapter(followEventAdapter); // Set adapter to RecyclerView
                                });
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(String message) {
                    Log.e("FollowListActivity", "Error: " + message);
                }
            });
        }
    }

}

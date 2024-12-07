package com.example.ticketbooking;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class HomeUserFragment extends Fragment {
    private RecyclerView recyclerSpecialEvents, recyclerMusicEvents, recyclerArtEvents, recyclerComedyEvents;
    private EventAdapter specialEventAdapter, musicEventAdapter, artEventAdapter, comedyEventAdapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home_user, container, false);

        // Set up the toolbar
        Toolbar toolbar = rootView.findViewById(R.id.toolbar);
        if (getActivity() instanceof AppCompatActivity) {
            ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        }

        // Enable the fragment to handle menu items
        setHasOptionsMenu(true);

        recyclerSpecialEvents = rootView.findViewById(R.id.recycler_special_events);
        recyclerMusicEvents = rootView.findViewById(R.id.recycler_music_events);
        recyclerArtEvents = rootView.findViewById(R.id.recycler_art_events);
        recyclerComedyEvents = rootView.findViewById(R.id.recycler_comedy_events);

        // Set up RecyclerViews
        recyclerSpecialEvents.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        recyclerMusicEvents.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        recyclerArtEvents.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        recyclerComedyEvents.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));

        // Load data from the API
        loadEvents();

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, android.view.MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.toolbar_home, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.search) {
            // Perform search action (e.g., open search bar or fragment)
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadEvents() {
        String url = "https://ticket-system-l5j0.onrender.com/api/v1/event";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray events = response.getJSONArray("data");
                    List<Event> specialEvents = new ArrayList<>();
                    List<Event> musicEvents = new ArrayList<>();
                    List<Event> artEvents = new ArrayList<>();
                    List<Event> comedyEvents = new ArrayList<>();

                    for (int i = 0; i < events.length(); i++) {
                        JSONObject eventObj = events.getJSONObject(i);
                        String name = eventObj.getString("name");
                        String location = eventObj.getString("location");
                        String description = eventObj.getString("desc");
                        String startDate = eventObj.getJSONObject("date").getString("start");
                        String endDate = eventObj.getJSONObject("date").getString("end");
                        String image = eventObj.getString("image");
                        int minPrice = eventObj.getJSONObject("priceRange").getInt("min");
                        int maxPrice = eventObj.getJSONObject("priceRange").getInt("max");

                        Event event = new Event(eventObj.getString("_id"),name, description, location, startDate, endDate, image, minPrice, maxPrice);

                        // Categorize the events based on type
                        if (eventObj.getJSONArray("type").toString().contains("âm nhạc")) {
                            musicEvents.add(event);
                        } else if (eventObj.getJSONArray("type").toString().contains("hài kịch")) {
                            comedyEvents.add(event);
                        } else if (eventObj.getJSONArray("type").toString().contains("nghệ thuật")) {
                            artEvents.add(event);
                        } else {
                            specialEvents.add(event);  // This can be adjusted for other types of events
                        }

                        // Repeat for other categories like "sự kiện đặc biệt", "nghệ thuật", etc.

                    }

                    // Set data in the RecyclerViews
                    musicEventAdapter = new EventAdapter(musicEvents);
                    recyclerMusicEvents.setAdapter(musicEventAdapter);

                    comedyEventAdapter = new EventAdapter(comedyEvents);
                    recyclerComedyEvents.setAdapter(comedyEventAdapter);

                    artEventAdapter = new EventAdapter(artEvents);
                    recyclerArtEvents.setAdapter(artEventAdapter);

                    specialEventAdapter = new EventAdapter(specialEvents);
                    recyclerSpecialEvents.setAdapter(specialEventAdapter);

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "Error loading events", Toast.LENGTH_SHORT).show();
                }
            }
        }, error -> Toast.makeText(getActivity(), "Failed to load events", Toast.LENGTH_SHORT).show());

        // Add the request to the RequestQueue
        Volley.newRequestQueue(getActivity()).add(request);
    }
}

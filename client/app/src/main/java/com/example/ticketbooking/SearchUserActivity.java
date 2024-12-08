package com.example.ticketbooking;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.ticketbooking.home.adapter.EventAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SearchUserActivity extends AppCompatActivity {

    private EditText edtSearch;
    private RecyclerView recyclerSearchResults;
    private EventAdapter eventAdapter;
    private Button btnFilters;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_user);

        // Initializing views
        edtSearch = findViewById(R.id.edtSearch);
        recyclerSearchResults = findViewById(R.id.recycler_search_results);

        // Set up RecyclerView for search results with GridLayoutManager
        recyclerSearchResults.setLayoutManager(new GridLayoutManager(this, 2)); // Set 2 columns per row

        // Set up text change listener on the search input field
        edtSearch.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                String query = charSequence.toString().trim();
                if (!query.isEmpty()) {
                    searchEvents(query); // Call the search API
                } else {
                    recyclerSearchResults.setVisibility(View.GONE); // Hide RecyclerView if query is empty
                }
            }


            @Override
            public void afterTextChanged(android.text.Editable editable) {}
        });

        btnFilters = findViewById(R.id.btnFilters);
        btnFilters.setOnClickListener(v -> {
            // Show the BottomSheetDialogFragment when the "Bộ lọc" button is clicked
            FilterBottomSheetFragment filterBottomSheetFragment = new FilterBottomSheetFragment();
            filterBottomSheetFragment.show(getSupportFragmentManager(), filterBottomSheetFragment.getTag());
        });
    }

    private void searchEvents(String query) {
        String url = "https://ticket-system-l5j0.onrender.com/api/v1/event?name=" + query;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray events = response.getJSONArray("data");
                    List<Event> eventList = new ArrayList<>();

                    // Parse the response and add events to the list
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

                        Event event = new Event(eventObj.getString("_id"), name, description, location, startDate, endDate, image, minPrice, maxPrice);
                        eventList.add(event);
                    }

                    // Set the adapter with the result list
                    eventAdapter = new EventAdapter(eventList);
                    eventAdapter.setNameTextColor(Color.WHITE);
                    recyclerSearchResults.setAdapter(eventAdapter);
                    recyclerSearchResults.setVisibility(View.VISIBLE); // Show RecyclerView when results are available

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(SearchUserActivity.this, "Error loading search results", Toast.LENGTH_SHORT).show();
                }
            }
        }, error -> {
            Toast.makeText(SearchUserActivity.this, "Failed to load search results", Toast.LENGTH_SHORT).show();
        });

        // Add the request to the RequestQueue
        Volley.newRequestQueue(this).add(request);
    }

    private void FiltersEvents(String query){

    }
}

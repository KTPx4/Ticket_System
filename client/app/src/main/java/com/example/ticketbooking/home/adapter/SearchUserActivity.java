package com.example.ticketbooking.home.adapter;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ticketbooking.Event;
import com.example.ticketbooking.R;

import java.util.Calendar;
import java.util.List;

import modules.LocalStorageManager;
import services.EventService;
import services.HomeService;

import android.util.Log;

public class SearchUserActivity extends AppCompatActivity {

    private EditText edtSearch;
    private RecyclerView recyclerSearchResults;
    private EventAdapter eventAdapter;
    private Button btnFilters,btnAllDates;
    private HomeService homeService;
    private ImageView imgBack, imgSearch;

    private LocalStorageManager localStorageManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_user);

        // Initializing views
        edtSearch = findViewById(R.id.edtSearch);
        recyclerSearchResults = findViewById(R.id.recycler_search_results);
        btnFilters = findViewById(R.id.btnFilters);
        btnAllDates = findViewById(R.id.btnAllDates);
        imgBack = findViewById(R.id.img_back);
        imgSearch = findViewById(R.id.img_search);

        localStorageManager = new LocalStorageManager(this);
        homeService = new HomeService(this);

        // Set up RecyclerView for search results with GridLayoutManager
        recyclerSearchResults.setLayoutManager(new GridLayoutManager(this, 2)); // Set 2 columns per row

//        String loginToken = localStorageManager.getLoginToken();
//        if (!loginToken.isEmpty()) {
//            // Log the token to Logcat for debugging
//            Log.d("SearchUserActivity", "Login Token: " + loginToken);
//            Toast.makeText(SearchUserActivity.this, "Login Token: " + loginToken, Toast.LENGTH_SHORT).show();
//        } else {
//            Log.d("SearchUserActivity", "No login token found");
//            Toast.makeText(SearchUserActivity.this, "No login token found", Toast.LENGTH_SHORT).show();
//        }

        btnAllDates.setOnClickListener(v -> showDatePicker());

        imgSearch.setOnClickListener(v -> {
            String query = edtSearch.getText().toString().trim();
            if (!query.isEmpty()) {
                addSearchHistory(query);
                searchEvents(query); // Call searchEvents with the query
            } else {
                Toast.makeText(SearchUserActivity.this, "Vui lòng nhập từ khóa tìm kiếm", Toast.LENGTH_SHORT).show();
            }
        });

        btnFilters.setOnClickListener(v -> {
            FilterBottomSheetFragment filterBottomSheetFragment = new FilterBottomSheetFragment();
            filterBottomSheetFragment.show(getSupportFragmentManager(), filterBottomSheetFragment.getTag());
        });

        imgBack.setOnClickListener(v -> {
            finish();
        });
    }

    public void filterEvents(String location, String type) {
        // Call the new filter method from HomeService
        homeService.filterEvents(location, type, new HomeService.EventCallback() {
            @Override
            public void onEventsLoaded(List<Event> specialEvents, List<Event> musicEvents, List<Event> artEvents, List<Event> comedyEvents) {
                // If specialEvents is not empty, update the adapter with the filtered events
                if (specialEvents != null && !specialEvents.isEmpty()) {
                    eventAdapter = new EventAdapter(specialEvents, SearchUserActivity.this);
                    recyclerSearchResults.setAdapter(eventAdapter);
                    eventAdapter.setNameTextColor(Color.WHITE);
                    recyclerSearchResults.setVisibility(View.VISIBLE); // Show RecyclerView if results exist
                } else {
                    Toast.makeText(SearchUserActivity.this, "Không có sự kiện phù hợp", Toast.LENGTH_SHORT).show();
                    recyclerSearchResults.setVisibility(View.GONE); // Hide RecyclerView if no events are found
                }
            }
        });
    }



    private void searchEvents(String query) {
        // Gọi phương thức searchEvents từ HomeService
        homeService.searchEvents(query, new HomeService.EventCallback() {
            @Override
            public void onEventsLoaded(List<Event> specialEvents, List<Event> musicEvents, List<Event> artEvents, List<Event> comedyEvents) {
                // Nếu tìm thấy kết quả, hiển thị lên RecyclerView
                if (specialEvents != null && !specialEvents.isEmpty()) {
                    eventAdapter = new EventAdapter(specialEvents, SearchUserActivity.this);
                    recyclerSearchResults.setAdapter(eventAdapter);
                    eventAdapter.setNameTextColor(Color.WHITE);
                    recyclerSearchResults.setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(SearchUserActivity.this, "Không tìm thấy sự kiện nào", Toast.LENGTH_SHORT).show();
                    recyclerSearchResults.setVisibility(View.GONE);
                }
            }
        });
    }

    private void addSearchHistory(String query) {
        // Use the existing homeService instance
        homeService.addHistory(query, new EventService.ResponseCallback() {
            @Override
            public void onSuccess(String success) {
                // Search history has been saved
                Log.d("SearchUserActivity", "Lịch sử tìm kiếm đã được lưu: " + success);
            }

            @Override
            public void onFailure(String error) {
                // Error when saving search history
                Log.d("SearchUserActivity", "Lỗi khi lưu lịch sử tìm kiếm: " + error);
            }
        });
    }


    private void showDatePicker() {
        // Get current date
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Create and show the DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
                // Format the selected date as dd/MM/yyyy
                String selectedDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                btnAllDates.setText(selectedDate);
                searchEventsDate(selectedDate); // Call the search method with the selected date
            }
        }, year, month, day);

        // Show the DatePickerDialog
        datePickerDialog.show();
    }

    private void searchEventsDate(String date) {
        // Call the searchEventsDate method from HomeService with the selected date
        homeService.searchEventsDate(date, new HomeService.EventCallback() {
            @Override
            public void onEventsLoaded(List<Event> specialEvents, List<Event> musicEvents, List<Event> artEvents, List<Event> comedyEvents) {
                // Set the adapter with the result list filtered by date
                eventAdapter = new EventAdapter(specialEvents, SearchUserActivity.this);
                recyclerSearchResults.setAdapter(eventAdapter);
                eventAdapter.setNameTextColor(Color.WHITE);
                recyclerSearchResults.setVisibility(View.VISIBLE); // Show RecyclerView with date-filtered results
            }
        });
    }

}

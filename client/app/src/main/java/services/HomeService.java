package services;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.ticketbooking.R;
import com.example.ticketbooking.home.adapter.EventAdapter;
import com.example.ticketbooking.Event;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import modules.LocalStorageManager;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

public class HomeService {
    private Context context;
    private static String SERVER;
    private final OkHttpClient client;
    private final LocalStorageManager localStorageManager;

    private String token;

    public HomeService(Context context) {
        this.context = context;
        client = new OkHttpClient();
        this.SERVER = context.getString(R.string.server_url);
        this.token = new LocalStorageManager(context).getLoginToken();
        localStorageManager = new LocalStorageManager(context);
    }

    // Method to load events
    public void loadEvents(final EventCallback callback) {
        String url = SERVER + "/api/v1/event";

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

                        JSONArray artistArray = eventObj.getJSONArray("artists");
                        List<String> artists = new ArrayList<>();
                        for (int j = 0; j < artistArray.length(); j++) {
                            artists.add(artistArray.getString(j));
                        }

                        Event event = new Event(eventObj.getString("_id"), name, description, location, startDate, endDate, image, minPrice, maxPrice,artists);
                        // Categorize the events based on type
                        if (eventObj.getJSONArray("type").toString().contains("âm nhạc")) {
                            musicEvents.add(event);
                        } else if (eventObj.getJSONArray("type").toString().contains("hài kịch")) {
                            comedyEvents.add(event);
                        } else if (eventObj.getJSONArray("type").toString().contains("nghệ thuật")) {
                            artEvents.add(event);
                        } else {
                            specialEvents.add(event);
                        }
                    }

                    // Callback with the categorized events
                    callback.onEventsLoaded(specialEvents, musicEvents, artEvents, comedyEvents);

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(context, "Error loading events", Toast.LENGTH_SHORT).show();
                }
            }
        }, error -> Toast.makeText(context, "Failed to load events", Toast.LENGTH_SHORT).show());

        // Add the request to the RequestQueue
        Volley.newRequestQueue(context).add(request);
    }


    public void searchEvents(String query, final EventCallback callback) {
        String url = SERVER + "/api/v1/event?name=" + query;

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

                        JSONArray artistArray = eventObj.getJSONArray("artists");
                        List<String> artists = new ArrayList<>();
                        for (int j = 0; j < artistArray.length(); j++) {
                            artists.add(artistArray.getString(j));
                        }

                        Event event = new Event(eventObj.getString("_id"), name, description, location, startDate, endDate, image, minPrice, maxPrice,artists);
                        eventList.add(event);
                    }

                    // Callback with the result list
                    callback.onEventsLoaded(eventList, new ArrayList<>(), new ArrayList<>(), new ArrayList<>());

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(context, "Error loading search results", Toast.LENGTH_SHORT).show();
                }
            }
        }, error -> {
            Toast.makeText(context, "Failed to load search results", Toast.LENGTH_SHORT).show();
        });

        // Add the request to the RequestQueue
        Volley.newRequestQueue(context).add(request);
    }

    public void searchEventsDate(String date, final EventCallback callback) {
        String url = SERVER + "/api/v1/event?date=" + date;

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

                        JSONArray artistArray = eventObj.getJSONArray("artists");
                        List<String> artists = new ArrayList<>();
                        for (int j = 0; j < artistArray.length(); j++) {
                            artists.add(artistArray.getString(j));
                        }

                        Event event = new Event(eventObj.getString("_id"), name, description, location, startDate, endDate, image, minPrice, maxPrice,artists);
                        eventList.add(event);
                    }

                    // Callback with the result list
                    callback.onEventsLoaded(eventList, new ArrayList<>(), new ArrayList<>(), new ArrayList<>());

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(context, "Error loading search results by date", Toast.LENGTH_SHORT).show();
                }
            }
        }, error -> {
            Toast.makeText(context, "Failed to load search results by date", Toast.LENGTH_SHORT).show();
        });

        // Add the request to the RequestQueue
        Volley.newRequestQueue(context).add(request);
    }

    public void deleteHistory(EventService.ResponseCallback callback) {
        new Thread(() -> {
            try {
                String url = SERVER + "/api/v1/account/history";

                // Tạo yêu cầu DELETE với Bearer token
                okhttp3.Request request = new okhttp3.Request.Builder()
                        .url(url)
                        .header("Authorization", "Bearer " + token)
                        .delete()  // Xóa toàn bộ
                        .build();

                // Gửi yêu cầu và nhận phản hồi
                okhttp3.Response response = client.newCall(request).execute();

                try {
                    String responseBody = response.body() != null ? response.body().string() : "";
                    Log.d("deleteHistory", "Response Body: " + responseBody);

                    // Xử lý phản hồi
                    JSONObject jsonResponse = new JSONObject(responseBody);
                    String message = jsonResponse.optString("message", "Request failed!");

                    if (response.isSuccessful()) {
                        callback.onSuccess(message);  // Thành công
                    } else {
                        callback.onFailure("Failed: " + message);  // Thất bại
                    }
                } finally {
                    response.close();  // Đảm bảo đóng phản hồi
                }
            } catch (Exception e) {
                e.printStackTrace();
                callback.onFailure("Delete history failed: " + e.getMessage());
            }
        }).start();
    }



    public void addHistory(String searchQuery, EventService.ResponseCallback callback) {
        new Thread(() -> {
            try {
                String url = SERVER + "/api/v1/account/history";

                // Create the request body with search query
                RequestBody formBody = new FormBody.Builder()
                        .add("search", searchQuery)
                        .build();

                // Create the request with Bearer token
                okhttp3.Request request = new okhttp3.Request.Builder()
                        .url(url)
                        .header("Authorization", "Bearer " + token)
                        .post(formBody)
                        .build();

                // Send the request and get the response
                okhttp3.Response response = client.newCall(request).execute();

                try {
                    String responseBody = response.body() != null ? response.body().string() : "";
                    Log.d("searchHistory", "Response Body: " + responseBody);

                    // Process the response
                    JSONObject jsonResponse = new JSONObject(responseBody);
                    String message = jsonResponse.optString("message", "Request failed!");

                    if (response.isSuccessful()) {
                        callback.onSuccess(message);
                    } else {
                        callback.onFailure("Failed: " + message);
                    }
                } finally {
                    // Ensure response is closed
                    response.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
                callback.onFailure("Search history failed: " + e.getMessage());
            }
        }).start();
    }


    public void filterEvents(String location, String type, final EventCallback callback) {
        // Tạo URL dựa trên các tiêu chí lọc
        StringBuilder urlBuilder = new StringBuilder(SERVER + "/api/v1/event?");

        // Thêm các tham số lọc vào URL
        if (!location.isEmpty()) {
            urlBuilder.append("location=").append(location).append("&");
        }
        if (!type.isEmpty()) {
            urlBuilder.append("type=").append(type).append("&");
        }

        // Loại bỏ dấu "&" thừa cuối cùng nếu có
        String url = urlBuilder.toString();
        if (url.endsWith("&")) {
            url = url.substring(0, url.length() - 1);
        }

        // Tạo yêu cầu JSON
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray events = response.getJSONArray("data");
                    List<Event> eventList = new ArrayList<>();

                    // Phân tích phản hồi và thêm sự kiện vào danh sách
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

                        JSONArray artistArray = eventObj.getJSONArray("artists");
                        List<String> artists = new ArrayList<>();
                        for (int j = 0; j < artistArray.length(); j++) {
                            artists.add(artistArray.getString(j));
                        }

                        Event event = new Event(eventObj.getString("_id"), name, description, location, startDate, endDate, image, minPrice, maxPrice,artists);
                        eventList.add(event);
                    }

                    // Callback với danh sách sự kiện đã lọc
                    callback.onEventsLoaded(eventList, new ArrayList<>(), new ArrayList<>(), new ArrayList<>());

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(context, "Error loading filtered events", Toast.LENGTH_SHORT).show();
                }
            }
        }, error -> {
            Toast.makeText(context, "Failed to load filtered events", Toast.LENGTH_SHORT).show();
        });

        // Thêm yêu cầu vào RequestQueue
        Volley.newRequestQueue(context).add(request);
    }


    // Interface for the callback
    public interface EventCallback {
        void onEventsLoaded(List<Event> specialEvents, List<Event> musicEvents, List<Event> artEvents, List<Event> comedyEvents);
    }
}

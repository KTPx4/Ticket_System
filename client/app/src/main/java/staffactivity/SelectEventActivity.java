package staffactivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.ticketbooking.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import model.event.Event;
import services.EventService;
import services.response.event.RSGetAllEvent;

public class SelectEventActivity extends AppCompatActivity {
    FloatingActionButton btnClose;
    ListView listEvent;
    private EventService eventService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.staff_activity_select_event);
        eventService = new EventService(this);

        btnClose = findViewById(R.id.btnClose);
        listEvent = findViewById(R.id.listEvent);

        btnClose.setOnClickListener(v -> finish());

        getData();
    }

    private void getData() {
        eventService.GetAllEvent(new EventService.CallBackGetAllEvent() {
            @Override
            public void onSuccess(List<RSGetAllEvent.REvent> events) {
                runOnUiThread(() -> {
                    // Chuyển đổi danh sách sự kiện thành danh sách HashMap (Key: Title, Value: SubText)
                    List<HashMap<String, String>> eventList = new ArrayList<>();
                    for (RSGetAllEvent.REvent event : events) {
                        HashMap<String, String> map = new HashMap<>();
                        map.put("line1", "ID: " + event.get_id());
                        map.put("line2", "Name: " + event.getName());
                        map.put("line3", "Location: " + event.getLocation() + " - " + event.getDate().getStart());
                        eventList.add(map);
                    }

                    // Sử dụng SimpleAdapter để hiển thị ba dòng
                    SimpleAdapter adapter = new SimpleAdapter(
                            SelectEventActivity.this,
                            eventList,
                            R.layout.three_line_list_item, // File layout tùy chỉnh
                            new String[]{"line1", "line2", "line3"},
                            new int[]{R.id.text1, R.id.text2, R.id.text3}
                    );

                    listEvent.setAdapter(adapter);

                    // Bắt sự kiện click vào item
                    listEvent.setOnItemClickListener((parent, view, position, id) -> {
                        // Lấy ra HashMap tương ứng với item được click
                        HashMap<String, String> selectedEvent = (HashMap<String, String>) parent.getItemAtPosition(position);

                        // Lấy ID từ HashMap (từ giá trị "line1")
                        String eventId = selectedEvent.get("line1").replace("ID: ", ""); // Loại bỏ "ID: " nếu cần
                        String name = selectedEvent.get("line2").replace("Name: ", ""); // Loại bỏ "ID: " nếu cần
                        String location = selectedEvent.get("line3"); // Loại bỏ "ID: " nếu cần
                        Intent intent = new Intent(getApplicationContext(), ScanTicketActivity.class);
                        Log.d("ID ", "onSuccess: " + eventId);
                        Log.d("name ", "onSuccess: " + name);
                        intent.putExtra("idEvent", eventId);
                        intent.putExtra("name", name);
                        intent.putExtra("location", location);

                        startActivity(intent);
                        // TODO: Xử lý logic với eventId
                    });
                });
            }

            @Override
            public void onFailure(String error) {
                runOnUiThread(() -> {
                    Toast.makeText(SelectEventActivity.this, error, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

}
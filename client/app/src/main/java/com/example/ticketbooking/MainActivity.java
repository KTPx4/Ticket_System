package com.example.ticketbooking;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.ticketbooking.databinding.ActivityMainUserBinding;
import com.example.ticketbooking.home.adapter.HomeUserFragment;

import java.util.List;

import model.ticket.MyTicket;
import modules.CalenderNotify;
import modules.DateConverter;
import modules.LocalStorageManager;
import services.TicketService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    ActivityMainUserBinding binding;
    private LocalStorageManager localStorageManager;

    private ExecutorService executorService;
    private static final int REQUEST_CALENDAR_PERMISSION = 100;
    private TicketService ticketService;
    private CalenderNotify calenderNotify;
    private void requestCalendarPermissions() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.WRITE_CALENDAR, android.Manifest.permission.READ_CALENDAR},
                    REQUEST_CALENDAR_PERMISSION);
        }
        else {
            // Quyền đã được cấp, tiếp tục xử lý logic
            createEventNotify();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Khởi tạo ExecutorService
        executorService = Executors.newSingleThreadExecutor();

        ticketService = new TicketService(this);
        calenderNotify = new CalenderNotify(this);

        // Khởi tạo LocalStorageManager
        localStorageManager = new LocalStorageManager(this);

        requestCalendarPermissions();

        String token = localStorageManager.getLoginToken();

        // Đặt trang chính là HomeUserFragment
//        if (savedInstanceState == null) {
//            showFragment(new HomeUserFragment());
//        }
        showFragment(new HomeUserFragment());

        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.home) {
                showFragment(new HomeUserFragment());
            } else if (id == R.id.tickets) {
                showFragment(new TicketsUserFragment());
            } else if (id == R.id.account) {
                showFragment(new AccountUserFragment());
            }
            return true;
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CALENDAR_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Call get myticket and add event to calendar
                createEventNotify();
            }
            else {


            }
        }
    }

    private void createEventNotify() {
        executorService.execute(() -> {
            // Logic lấy dữ liệu và thêm sự kiện vào lịch
            ticketService.GetMyTicket(new TicketService.MyTicketCallback() {
                @Override
                public void onSuccess(List<MyTicket> events) {
                    try{
                        // Giao diện: Hiển thị thông báo khi hoàn thành
                        for (MyTicket event : events) {
                            String eventId = event.getEvent().get_id();
                            String eventName = event.getEvent().getName();
                            String startTime = DateConverter.convertToHCM(event.getEvent().getDate().getStart());
//                            calenderNotify.testAddEvent();
                            calenderNotify.addOrUpdateEvent(eventId, eventName, startTime);
                            Log.d("EVENT", "Sự kiện đã được thêm vào lịch: " + eventName);
                        }
                        runOnUiThread(() ->{
                        });
                    }
                    catch (Exception e) {
                        Log.d("ERROR ADD EVENT CALENDER", "error when add event to calender" + e.getMessage());
                        throw new RuntimeException(e);
                    }

                }

                @Override
                public void onFailure(String error) {
                    runOnUiThread(() ->
                            Toast.makeText(MainActivity.this, error, Toast.LENGTH_SHORT).show()
                    );
                }
            });
        });
    }


    private void showFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_fragment, fragment);
        fragmentTransaction.commit();
    }


}

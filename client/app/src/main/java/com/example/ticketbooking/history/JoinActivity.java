package com.example.ticketbooking.history;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ticketbooking.R;
import com.example.ticketbooking.history.adapter.EventAdapter;

import java.util.ArrayList;
import java.util.List;

import model.event.Event;
import services.AccountService;

public class JoinActivity extends AppCompatActivity {

    RecyclerView recycEvent;
    private List<Event> dataEvent = new ArrayList<>();
    EventAdapter eventAdapter;
    AccountService accountService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.history_activity_join);
        accountService = new AccountService(this);

        recycEvent = findViewById(R.id.recyEvent);
        recycEvent.setLayoutManager(new LinearLayoutManager(this));

        eventAdapter = new EventAdapter(this, dataEvent, id -> {
            Intent intent = new Intent(this, HistoryActivity.class);
            intent.putExtra("eventId", id);
            startActivity(intent);
        });

        recycEvent.setAdapter(eventAdapter);
        eventAdapter.notifyDataSetChanged();
        LoadEvent();
    }

    private void LoadEvent()
    {
        try{
            accountService.GetJoinEvent(new AccountService.GetJoiCallBack() {
                @Override
                public void onSuccess(List<Event> listData) {
                    runOnUiThread(()->{
                        dataEvent.clear();
                        dataEvent.addAll(listData);
                        eventAdapter.notifyDataSetChanged();
                    });
                }

                @Override
                public void onFailure(String err) {
                    runOnUiThread(()->{
                        Toast.makeText(JoinActivity.this, err, Toast.LENGTH_SHORT).show();
                    });
                }
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
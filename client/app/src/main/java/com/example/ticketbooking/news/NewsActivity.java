package com.example.ticketbooking.news;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ticketbooking.R;
import com.example.ticketbooking.news.adapter.NewsAdapter;

import java.util.ArrayList;
import java.util.List;

import model.event.News;
import services.EventService;

public class NewsActivity extends AppCompatActivity {
    private RecyclerView listNews;
    private EditText txtName;
    private String idEvent = "";
    EventService eventService;
    List<News> dataListNews = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.news_activity_main);
        listNews = findViewById(R.id.listNews);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());

        listNews.setLayoutManager(linearLayoutManager);
        txtName = findViewById(R.id.txtName);
        eventService = new EventService(this);
        getFromIntent();

    }
    private void getFromIntent()
    {
        Intent intent = getIntent();
        idEvent = intent.getStringExtra("idEvent");
        String name = intent.getStringExtra("name");
        name = name == null ? "Tin tức sự kiện" : name;
        txtName.setText(name);
        if(idEvent.isEmpty())
        {
            Toast.makeText(this, "Thiếu id sự kiện", Toast.LENGTH_SHORT).show();
            this.finish();
            return;
        }
        loadNews();
    }
    private void loadNews()
    {
        if(idEvent.isEmpty())
        {
            return;
        }
        eventService.GetNews(idEvent, -1, new EventService.CallBackGetAllNews() {
            @Override
            public void onSuccess(List<News> list) {
                try{
                    runOnUiThread(()->{
                        dataListNews.clear();
                        dataListNews.addAll(list);

                        NewsAdapter newsAdapter = new NewsAdapter(getApplicationContext(), list, id -> {
                            Intent intent = new Intent(getApplicationContext(), NewsDetailsActivity.class);
                            intent.putExtra("id", id);
                            startActivity(intent);
                        });
                        listNews.setAdapter(newsAdapter);
                        newsAdapter.notifyDataSetChanged();
                    });
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

            }

            @Override
            public void onFailure(String error) {
                runOnUiThread(()->{

                });
            }
        });

    }
}
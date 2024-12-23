package com.example.ticketbooking.news;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.ticketbooking.R;

import model.event.News;
import services.EventService;

public class NewsDetailsActivity extends AppCompatActivity {
    TextView tvTitle, tvDate, tvContent;
    ImageView imgNews;
    String idNews = "";
    String nameEvent = "";
    EditText txtName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.news_activity_details);
        tvContent = findViewById(R.id.tvContent);
        tvTitle = findViewById(R.id.tvTitle);
        txtName = findViewById(R.id.txtName);
        tvDate = findViewById(R.id.tvDate);
        imgNews = findViewById(R.id.btnImage);

        getFromIntent();
    }
    void getFromIntent()
    {
        Intent intent = getIntent();
        idNews = intent.getStringExtra("id");

        if(idNews == null || idNews.isEmpty())
        {
            Toast.makeText(this, "Thiếu id của thông báo", Toast.LENGTH_SHORT).show();
            this.finish();
        }


        EventService eventService = new EventService(this);
        eventService.GetNewsById(idNews, new EventService.CallBackGetNews() {
            @Override
            public void onSuccess(News news, String eventName) {
                try{
                    tvTitle.setText(news.getTitle());
                    tvContent.setText(news.getContent());
                    tvDate.setText(news.getCreatedAt());
                    nameEvent = eventName;
                    txtName.setText(nameEvent);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            @Override
            public void onFailure(String error) {

            }
        });
    }
}
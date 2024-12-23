package com.example.ticketbooking.news.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ticketbooking.R;
import com.example.ticketbooking.news.NewsDetailsActivity;

import java.util.List;

import model.event.News;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {
    private Context context;
    private List<News> listNews;
    private ViewDetailCallBack callBack;

    public interface ViewDetailCallBack{
        public void startIntent(String id);
    }

    public NewsAdapter(Context context, List<News> listNews, ViewDetailCallBack callback) {
        this.context = context;
        this.listNews = listNews;
        this.callBack = callback;
    }


    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.event_item_news, parent, false);
        return new NewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        holder.bind(context, listNews.get(position), callBack);
    }

    @Override
    public int getItemCount() {
        return listNews.size();
    }

    public static class NewsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private ImageView imgNews;
        private TextView tvTitle, textViewDate, tvDesc;
        private String id;
        private ConstraintLayout layoutMain;
        private Context context;
        private ViewDetailCallBack callBack;

        public NewsViewHolder(@NonNull View itemView) {
            super(itemView);
            imgNews = itemView.findViewById(R.id.btnImage);
            tvTitle = itemView.findViewById(R.id.textView);
            textViewDate = itemView.findViewById(R.id.tvDate);
            layoutMain = itemView.findViewById(R.id.layoutMain);
            tvDesc = itemView.findViewById(R.id.tvDesc);
            tvDesc.setVisibility(View.VISIBLE);

            tvTitle.setOnClickListener(this);
            tvDesc.setOnClickListener(this);
            imgNews.setOnClickListener(this);
            textViewDate.setOnClickListener(this);

        }

        public void bind(Context context, News news, ViewDetailCallBack callBack)
        {
            try{
                this.callBack = callBack;
                this.context = context;
                tvTitle.setText(news.getTitle());
                textViewDate.setText(news.getCreatedAt());
                tvDesc.setText(news.getContent());
                this.id = news.get_id();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void onClick(View view) {
            int idV = view.getId();
            if(idV == R.id.btnImage || idV == R.id.textView ||idV == R.id.tvDesc)
            {
                Log.d("OKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKK", "onClick: " + this.id);
               callBack.startIntent(this.id);
            }
        }
    }
}

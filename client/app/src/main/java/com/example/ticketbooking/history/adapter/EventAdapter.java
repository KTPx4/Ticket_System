package com.example.ticketbooking.history.adapter;

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

import com.bumptech.glide.Glide;
import com.example.ticketbooking.R;

import java.util.List;

import model.event.Event;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ItemViewHolder> {

    private List<Event> listData;
    private Context context;
    private CallBack callBack;

    public interface CallBack{
        public void ClickEvent(String id);
    }

    public EventAdapter(Context context, List<Event> listData, CallBack callBack) {
        this.listData = listData;
        this.context = context;
        this.callBack = callBack;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.history_item_event, parent, false);
        return new ItemViewHolder(view, context, callBack);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        holder.bind(listData.get(position));
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private Context context;
        private ImageView imgEvent;
        private TextView tvName, tvId, tvDate;
        private ConstraintLayout layoutMain;

        private String idEvent = "";
        private CallBack callBack;

        public ItemViewHolder(@NonNull View itemView, Context context, CallBack callBack) {
            super(itemView);
            this.context = context;
            imgEvent = itemView.findViewById(R.id.imgEvent);
            tvName = itemView.findViewById(R.id.tvName);
            tvId = itemView.findViewById(R.id.tvId);
            tvDate = itemView.findViewById(R.id.tvDate);
            layoutMain = itemView.findViewById(R.id.layoutMain);

            imgEvent.setOnClickListener(this);
            tvName.setOnClickListener(this);
            tvId.setOnClickListener(this);
            tvDate.setOnClickListener(this);
            layoutMain.setOnClickListener(this);
            this.callBack = callBack;
        }

        public void bind(Event event)
        {
            idEvent = event.get_id();

            String url = context.getString(R.string.server_url) + "/public/event/"  +idEvent + "/" + event.getImage();
            Log.d("EVENT URL", "bind: " + url);
            Glide.with(context).load(url).error(context.getDrawable(R.drawable.img_event)).into(imgEvent);
            tvName.setText(event.getName());
            tvId.setText(event.get_id());
            tvDate.setText(event.getDate().getStart());

        }

        @Override
        public void onClick(View view) {
            int idv = view.getId();

            if(idv == R.id.imgEvent || idv == R.id.tvName || idv == R.id.tvId || idv == R.id.tvDate || idv == R.id.layoutMain )
            {
                callBack.ClickEvent(idEvent);
            }
        }
    }
}

package com.example.ticketbooking.history.adapter;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.ticketbooking.R;

import java.util.List;

public class FileAdapter extends RecyclerView.Adapter<FileAdapter.FileViewHolder> {

    private Context context;
    private List<Uri> fileUris;

    public FileAdapter(Context context, List<Uri> fileUris) {
        this.context = context;
        this.fileUris = fileUris;
    }

    @Override
    public FileViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_file, parent, false);
        return new FileViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FileViewHolder holder, int position) {
        Uri uri = fileUris.get(position);
        Glide.with(context)
                .load(uri)
                .into(holder.fileImageView);
    }

    @Override
    public int getItemCount() {
        return fileUris.size();
    }

    public static class FileViewHolder extends RecyclerView.ViewHolder {
        ImageView fileImageView;

        public FileViewHolder(View itemView) {
            super(itemView);
            fileImageView = itemView.findViewById(R.id.fileImageView);
        }
    }
}

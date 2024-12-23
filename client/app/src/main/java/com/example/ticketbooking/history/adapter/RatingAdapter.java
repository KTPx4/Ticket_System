package com.example.ticketbooking.history.adapter;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ticketbooking.R;

import java.util.List;

import model.Rating;

public class RatingAdapter extends RecyclerView.Adapter<RatingAdapter.RatingViewHolder> {

    private Context context;
    private List<Rating> listRating;
    private CallBack callBack;
    public interface CallBack{
        public void ShowMedia(String url, Boolean isVideo);
    }

    public RatingAdapter(Context context, List<Rating> listRating, CallBack callBack)
    {
        this.context = context;
        this.listRating = listRating;
        this.callBack = callBack;
    }

    @NonNull
    @Override
    public RatingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.history_item_post, parent, false);
        return new RatingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RatingViewHolder holder, int position) {
        holder.bind(context, listRating.get(position), callBack);
    }

    @Override
    public int getItemCount() {
        return listRating.size();
    }

    public static class RatingViewHolder extends RecyclerView.ViewHolder{
        private Context context;

        private TextView userNameTextView, commentDateTextView, commentTextView;

        RatingBar ratingBar;
        ImageView avatarImageView ;
        GridLayout mediaGridLayout;

        private String URL_HISTORY = "";
        private String URL_ACCOUNT ="";

        public RatingViewHolder(@NonNull View itemView) {
            super(itemView);
            userNameTextView = itemView.findViewById(R.id.userNameTextView);
            commentTextView = itemView.findViewById(R.id.commentTextView);
            commentDateTextView = itemView.findViewById(R.id.commentDateTextView);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            avatarImageView = itemView.findViewById(R.id.avatarImageView);
            mediaGridLayout = itemView.findViewById(R.id.mediaGridLayout);

        }

        public void bind(Context context, Rating rating, CallBack callBack)
        {
            try
            {
                this.context = context;
                URL_HISTORY = context.getString(R.string.server_url) + "/public/history";
                URL_ACCOUNT = context.getString(R.string.server_url) + "/public/account";

                if(rating != null)
                {

                    userNameTextView.setText(rating.getAccount().getName());
                    commentDateTextView.setText(rating.getCreatedAt());
                    commentTextView.setText(rating.getComment());
                    ratingBar.setRating((float)rating.getRating());
                    List<Rating.FileRating> listFile = rating.getFile();

                    String rootURL = URL_HISTORY + "/" + rating.get_id();

                    String myAVT = URL_ACCOUNT + "/" + rating.getAccount().get_id() + "/" + rating.getAccount().getImage();

                    Glide.with(context)
                            .load(myAVT)
                            .error(R.drawable.ic_avatar_placeholder)
                            .into(avatarImageView);


                    for (Rating.FileRating file : listFile) {
                        boolean isVideo = file.getTypeFile().equalsIgnoreCase("video");
                        String fileUrl = rootURL + "/" + file.getName(); // URL của ảnh/video

                        // Tạo ImageView hoặc VideoView
                        if (isVideo) {
                            // Tạo VideoView
                            VideoView videoView = new VideoView(context);
                            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                            params.width = dpToPx(50); // Convert dp to pixels
                            params.height = dpToPx(60);

                            params.setMargins(dpToPx(4), dpToPx(4), dpToPx(4), dpToPx(4));
                            videoView.setLayoutParams(params);
                            videoView.setVideoPath(fileUrl);
                            videoView.seekTo(100); // Load một khung hình để hiển thị
                            mediaGridLayout.addView(videoView);

                            // Xử lý phóng to
                            videoView.setOnClickListener(v -> {
//                                showMediaDialog(fileUrl, true); // true: là video
                                callBack.ShowMedia(fileUrl, true);
                            });

                        } else {
                            // Tạo ImageView
                            ImageView imageView = new ImageView(context);
                            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                            params.width = dpToPx(50); // Convert dp to pixels
                            params.height = dpToPx(60);
                            params.setMargins(dpToPx(4), dpToPx(4), dpToPx(4), dpToPx(4));
                            imageView.setLayoutParams(params);
                            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

                            // Tải ảnh (sử dụng thư viện Glide hoặc Picasso)
                            Glide.with(context).load(fileUrl).into(imageView);

                            mediaGridLayout.addView(imageView);

                            // Xử lý phóng to
                            imageView.setOnClickListener(v -> {
//                                showMediaDialog(fileUrl, false); // false: là ảnh
                                callBack.ShowMedia(fileUrl, false);

                            });
                        }
                    }

                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        }

        private int dpToPx(int dp) {
            return Math.round(dp * context.getResources().getDisplayMetrics().density);
        }

        private void showMediaDialog(String url, boolean isVideo) {
            Dialog dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_media_viewer); // Layout cho media

            // Tìm view
            ImageView imageView = dialog.findViewById(R.id.imageView);
            VideoView videoView = dialog.findViewById(R.id.videoView);

            if (isVideo) {
                imageView.setVisibility(View.GONE);
                videoView.setVisibility(View.VISIBLE);
                videoView.setVideoPath(url);
                videoView.start();
            } else {
                videoView.setVisibility(View.GONE);
                imageView.setVisibility(View.VISIBLE);
                Glide.with(context).load(url).into(imageView);
            }

            dialog.show();
        }

    }
}

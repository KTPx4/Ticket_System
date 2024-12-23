package com.example.ticketbooking.history;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ticketbooking.R;
import com.example.ticketbooking.history.adapter.RatingAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import model.Coupon;
import model.Rating;
import services.CouponService;
import services.HistoryService;
import services.response.history.RSGetAll;
import services.response.history.RSMyPost;

public class HistoryActivity extends AppCompatActivity implements View.OnClickListener{

    TextView tvId, tvName, tvDate, tvLocation, tvRating,tvPage, userNameTextView, commentDateTextView, commentTextView, count;

    RecyclerView listPost;

    FloatingActionButton btnAddPost;
    ImageButton btnNext, btnBack, btnDelete;
    RatingBar ratingBar;
    View myPost;
    ImageView avatarImageView ;
    GridLayout mediaGridLayout;

    ProgressBar loading;

    HistoryService historyService;
    private String URL_HISTORY = "";
    private String URL_ACCOUNT ="";

    private String eventId = "";
    private String myPostId = "";
    private List<Rating> dataListRating = new ArrayList<>();
    private boolean canPost = false, isWaiting = true;
    RatingAdapter adapter;
    private static  int page = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.history_activity_main);
        historyService = new HistoryService(this);

        URL_HISTORY = getString(R.string.server_url) + "/public/history";
        URL_ACCOUNT = getString(R.string.server_url) + "/public/account";

        tvPage = findViewById(R.id.tvPage);
        tvId = findViewById(R.id.tvId);
        tvName = findViewById(R.id.tvName);
        tvLocation = findViewById(R.id.tvLocation);
        tvDate = findViewById(R.id.tvDate);
        tvRating = findViewById(R.id.tvRating);
        loading = findViewById(R.id.loading);
        btnAddPost = findViewById(R.id.btnAdd);
        btnNext = findViewById(R.id.btnNext);
        btnBack = findViewById(R.id.btnBack);
        count = findViewById(R.id.count);

        listPost = findViewById(R.id.listPost);
        listPost.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RatingAdapter(getApplicationContext(), dataListRating, (url, isVideo) -> {
            runOnUiThread(()->{
                showMediaDialog(url, isVideo);
            });
        });
        listPost.setAdapter(adapter);

        myPost = findViewById(R.id.layoutMyPost);

        // my post
        userNameTextView = myPost.findViewById(R.id.userNameTextView);
        commentDateTextView = myPost.findViewById(R.id.commentDateTextView);
        commentTextView = myPost.findViewById(R.id.commentTextView);
        avatarImageView = myPost.findViewById(R.id.avatarImageView);
        ratingBar = myPost.findViewById(R.id.ratingBar);
        btnDelete = myPost.findViewById(R.id.btnDelete);

        btnDelete.setVisibility(View.VISIBLE);
        btnDelete.setOnClickListener(this);
        btnAddPost.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        btnBack.setOnClickListener(this);

        mediaGridLayout = myPost.findViewById(R.id.mediaGridLayout);

        LoadData();

    }

    private void LoadData()
    {
        isWaiting = true;
        loading.setVisibility(View.VISIBLE);
        listPost.setVisibility(View.GONE);
        GetFromIntent();
        GetMyPost();
    }

    private void GetFromIntent()
    {
        Intent intent = getIntent();
        eventId = intent.getStringExtra("eventId");
        if(eventId == null || eventId.isEmpty())
        {
            Toast.makeText(this, "Thiếu id sự kiện", Toast.LENGTH_SHORT);
            this.finish();
            return;
        }
        GetAllPost();
    }

    private void updateMyPost(Rating rating)
    {
        canPost = false;
        // Clear existing media in the GridLayout
        mediaGridLayout.removeAllViews();
        myPostId = rating.get_id();
        myPost.setVisibility(View.VISIBLE);
        userNameTextView.setText(rating.getAccount().getName());
        commentDateTextView.setText(rating.getCreatedAt());
        commentTextView.setText(rating.getComment());
        ratingBar.setRating((float)rating.getRating());
        List<Rating.FileRating> listFile = rating.getFile();

        String rootURL = URL_HISTORY + "/" + rating.get_id();

        String myAVT = URL_ACCOUNT + "/" + rating.getAccount().get_id() + "/" + rating.getAccount().getImage();

        Glide.with(getApplicationContext())
                .load(myAVT)
                .error(R.drawable.ic_avatar_placeholder)
                .into(avatarImageView);


        for (Rating.FileRating file : listFile) {
            boolean isVideo = file.getTypeFile().equalsIgnoreCase("video");
            String fileUrl = rootURL + "/" + file.getName(); // URL của ảnh/video

            // Tạo ImageView hoặc VideoView
            if (isVideo) {
                // Tạo VideoView
                VideoView videoView = new VideoView(getApplicationContext());
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
                    showMediaDialog(fileUrl, true); // true: là video
                });

            } else {
                // Tạo ImageView
                ImageView imageView = new ImageView(getApplicationContext());
                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.width = dpToPx(50); // Convert dp to pixels
                params.height = dpToPx(60);
                params.setMargins(dpToPx(4), dpToPx(4), dpToPx(4), dpToPx(4));
                imageView.setLayoutParams(params);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

                // Tải ảnh (sử dụng thư viện Glide hoặc Picasso)
                Glide.with(getApplicationContext()).load(fileUrl).into(imageView);

                mediaGridLayout.addView(imageView);

                // Xử lý phóng to
                imageView.setOnClickListener(v -> {
                    showMediaDialog(fileUrl, false); // false: là ảnh
                });
            }
        }
    }

    private void GetMyPost()
    {
        try{

            historyService.GetMyPost(eventId, new HistoryService.GetMyPostCallBack() {
                @Override
                public void onSuccess(RSMyPost result) {
                    Rating rating = result.getData();
                    if(rating != null)
                    {
                        runOnUiThread(()->{
                            updateMyPost(rating);
                        });
                    }
                    else{
                        canPost = true;
                    }

                }

                @Override
                public void onFailure(String error) {
                    runOnUiThread(()->{
                        Toast.makeText(getApplicationContext(), error, Toast.LENGTH_SHORT).show();
                    });
                }
            });

        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    private void GetAllPost()
    {
        try{
            historyService.GetAllPost(eventId, page, new HistoryService.GetAllCallBack() {
                @Override
                public void onSuccess(RSGetAll result) {

                    runOnUiThread(()->{
                        List<Rating> listRating = result.getData();
                        RSGetAll.RSRatingEvent Event = result.getEvent();
                        RSGetAll.RSRatingPagination Pagination = result.getPagination();
                        boolean canNext = Pagination.isHasNextPage();
                        boolean canBack = Pagination.isHasPrevPage();

                        int visiblePost = Event.isHasPost() ? View.VISIBLE : View.GONE;
                        int visibleNext = canNext ? View.VISIBLE : View.INVISIBLE;
                        int visibleBack = canBack ? View.VISIBLE : View.INVISIBLE;

                        isWaiting = false;
                        count.setText(Pagination.getTotalCount() + "");
                        tvId.setText(Event.get_id());
                        tvName.setText(Event.getName());
                        tvLocation.setText(Event.getLocation());
                        tvDate.setText(Event.getDate());
                        tvRating.setText("" + Event.getRating());
                        tvPage.setText("" + (Pagination.getCurrentPage() + 1));
                        btnAddPost.setVisibility(visiblePost);
                        btnNext.setVisibility(visibleNext);
                        btnBack.setVisibility(visibleBack);
                        loading.setVisibility(View.GONE);
                        listPost.setVisibility(View.VISIBLE);

                        dataListRating.clear();
                        if(listRating != null && listRating.size() > 0)
                        {
                            dataListRating.addAll(listRating);
                        }
                        adapter.notifyDataSetChanged();

                    });


                }

                @Override
                public void onFailure(String error) {
                    isWaiting = false;
                    runOnUiThread(()->{
                        loading.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(), error, Toast.LENGTH_SHORT).show();
                    });
                }
            });
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private ActivityResultLauncher<Intent> ActivityLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    LoadData();
                }
            }
    );
    @Override
    public void onClick(View view) {
        int idv = view.getId();

        if(isWaiting) return;

        if(idv == R.id.btnDelete)
        {
            Log.d("DELETE", "onClick: " + myPostId);
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle("Xóa bài viết")
                    .setMessage("Bạn có muốn xóa bài viết này")
                    .setPositiveButton("Hủy", (dia, which)->{
                        dia.dismiss();
                    })
                    .setNegativeButton("Xóa", (dialog2, which) -> {
                        historyService.DelMyPost(myPostId, new HistoryService.GetMyPostCallBack() {
                            @Override
                            public void onSuccess(RSMyPost result) {
                                runOnUiThread(()->{
                                    Toast.makeText(getApplicationContext(), "Đã xóa bài viết", Toast.LENGTH_SHORT).show();
                                    myPost.setVisibility(View.GONE);
                                    LoadData();
                                    dialog2.dismiss();
                                });
                            }

                            @Override
                            public void onFailure(String error) {
                                runOnUiThread(()->{
                                    Toast.makeText(getApplicationContext(), error, Toast.LENGTH_SHORT).show();
                                    dialog2.dismiss();
                                });
                            }
                        });

                    }).create();

            dialog.show();
            // Tùy chỉnh màu văn bản của các nút
            dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK); // Màu cho nút "Hủy"
            dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_NEGATIVE).setTextColor(getColor(R.color.inValid_Ticket));  // Màu cho nút "Xóa"

        }
        else if(idv == R.id.btnAdd)
        {
//            if(!canPost)
//            {
//                Toast.makeText(getApplicationContext(), "Bạn đã có bài đăng rồi!", Toast.LENGTH_SHORT).show();
//                return;
//            }
            Intent intent = new Intent(this, PostActivity.class);
            intent.putExtra("eventId", eventId);
            ActivityLauncher.launch(intent);
        }
        else if(idv == R.id.btnNext)
        {
            isWaiting= true;
            page += 1;
            LoadData();
        }
        else if(idv == R.id.btnBack)
        {
            isWaiting= true;
            page -= 1;
            LoadData();
        }

    }


    private int dpToPx(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }

    private void showMediaDialog(String url, boolean isVideo) {
        Dialog dialog = new Dialog(this);
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
            Glide.with(this).load(url).into(imageView);
        }

        dialog.show();
    }

}
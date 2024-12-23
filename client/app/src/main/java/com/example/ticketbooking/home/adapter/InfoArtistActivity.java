package com.example.ticketbooking.home.adapter;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.ticketbooking.R;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import modules.LocalStorageManager;
import services.AccountHomeService;

public class InfoArtistActivity extends Activity {
    private ImageView img_artist;
    private ImageButton btn_back;
    private TextView tv_originName, tv_birthDay, tv_duration, tv_more,tv_artistName;
    private Button btn_follow;
    private AccountHomeService accountHomeService;
    private LocalStorageManager localStorageManager;
    private String userId = "";
    private String artistId = "";
    private boolean isFollowing = false; // To store the follow status

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_artist);

        img_artist = findViewById(R.id.img_artist);
        tv_originName = findViewById(R.id.tv_originName);
        tv_birthDay = findViewById(R.id.tv_birthDay);
        tv_duration = findViewById(R.id.tv_duration);
        tv_more = findViewById(R.id.tv_more);
        btn_follow = findViewById(R.id.btn_follow);
        btn_back = findViewById(R.id.btn_back);
        tv_artistName = findViewById(R.id.tv_artistName);

        localStorageManager = new LocalStorageManager(this);
        userId = localStorageManager.getIdUser();
        Log.d("InfoArtistActivity", "User ID: " + userId);

        artistId = getIntent().getStringExtra("artistId");
        Log.d("InfoArtistActivity", "Artist ID: " + artistId);

        accountHomeService = new AccountHomeService(this);

        // Set up the back button
        btn_back.setOnClickListener(v -> onBackPressed());

        // Set up the follow/unfollow button listener (this will be updated after fetching artist data)
        btn_follow.setOnClickListener(v -> {
            if (isFollowing) {
                UnfollowArtist(artistId);
            } else {
                followArtist(artistId);
            }
        });

        fetchArtistsByID(artistId);
    }

    private void fetchArtistsByID(String artistId) {
        String serverUrl = getString(R.string.server_url);
        accountHomeService.getArtistById(artistId, new AccountHomeService.ResponseCallback() {
            @Override
            public void onSuccess(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    JSONObject data = jsonResponse.optJSONObject("data");
                    if (data != null) {
                        JSONObject desc = data.optJSONObject("desc");
                        if (desc != null) {
                            // Lấy thông tin nghệ sĩ
                            String originName = desc.optString("originName");
                            String artistName = desc.optString("artistName");
                            String birthDay = desc.optString("birthDay");
                            String duration = desc.optString("duration");
                            String more = desc.optString("more");

                            List<String> followersList = new ArrayList<>();
                            for (int i = 0; i < data.optJSONArray("followers").length(); i++) {
                                followersList.add(data.optJSONArray("followers").optString(i));
                            }

                            isFollowing = followersList.contains(userId); // Update follow status

                            Log.d("InfoArtistActivity", "isFollowing " + isFollowing);

                            // Sử dụng runOnUiThread để đảm bảo cập nhật UI trên luồng chính
                            runOnUiThread(() -> {
                                // Cập nhật dữ liệu lên các thành phần UI
                                tv_originName.setText(originName);
                                tv_birthDay.setText(birthDay);
                                tv_duration.setText(duration);
                                tv_artistName.setText(artistName);
                                tv_more.setText(more);

                                // Tạo URL của hình ảnh
                                String imageUrl = serverUrl + "/public/artist/" + artistId + "/" + data.optString("image");

                                // Ghi log URL hình ảnh để kiểm tra
                                Log.d("InfoArtistActivity", "Image URL: " + imageUrl);

                                // Tải hình ảnh bằng Glide
                                Glide.with(InfoArtistActivity.this)
                                        .load(imageUrl)
                                        .into(img_artist);

                                // Update follow button text based on follow status
                                if (isFollowing) {
                                    btn_follow.setText("Bỏ theo dõi");
                                    btn_follow.setBackgroundTintList(ContextCompat.getColorStateList(InfoArtistActivity.this, R.color.gray));
                                } else {
                                    btn_follow.setText("Theo dõi");
                                    btn_follow.setBackgroundTintList(ContextCompat.getColorStateList(InfoArtistActivity.this, R.color.Blush_Pink));
                                }
                            });
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("InfoArtistActivity", "Lỗi khi phân tích dữ liệu nghệ sĩ: " + e.getMessage());
                }
            }

            @Override
            public void onFailure(String message) {
                Log.e("InfoArtistActivity", "Lỗi khi lấy dữ liệu nghệ sĩ: " + message);
            }
        });
    }

    private void followArtist(String artistId) {
        accountHomeService.FollowArtist(artistId, new AccountHomeService.ResponseCallback() {
            @Override
            public void onSuccess(String success) {
                Log.d("InfoArtistActivity", "Follow artist success: " + success);
                fetchArtistsByID(artistId); // Refresh the artist data
            }

            @Override
            public void onFailure(String error) {
                Log.d("InfoArtistActivity", "Follow artist failed: " + error);
            }
        });
    }

    private void UnfollowArtist(String artistId) {
        accountHomeService.UnFollowArtist(artistId, new AccountHomeService.ResponseCallback() {
            @Override
            public void onSuccess(String success) {
                Log.d("InfoArtistActivity", "UnFollow artist success: " + success);
                fetchArtistsByID(artistId); // Refresh the artist data
            }

            @Override
            public void onFailure(String error) {
                Log.d("InfoArtistActivity", "UnFollow artist failed: " + error);
            }
        });
    }
}

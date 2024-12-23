package com.example.ticketbooking.history;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ticketbooking.R;
import com.example.ticketbooking.history.adapter.FileAdapter;

import java.util.ArrayList;

import services.HistoryService;
import services.response.history.RSMyPost;

public class PostActivity extends AppCompatActivity {
    private RatingBar ratingBar;
    private TextView ratingValue;
    private EditText commentInput;
    private Button btnUploadFile, btnSubmit;
    private TextView fileCount;
    private ProgressBar loading;

    private RecyclerView recyclerViewFiles;
    private FileAdapter fileAdapter;

    private static final int FILE_REQUEST_CODE = 100;
    private ArrayList<Uri> selectedFiles = new ArrayList<>();
    private static final int MAX_FILES = 5;
    private String eventId = "";

    private static final int REQUEST_CODE_STORAGE_PERMISSION = 100;
    private HistoryService historyService;
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE_STORAGE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with file upload
                OpenUpload();
            } else {
                // Permission denied
                Toast.makeText(this, "Permission denied. Cannot access files.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.history_activity_post);
        eventId = getIntent().getStringExtra("eventId");

        if(eventId == null || eventId.isEmpty())
        {
            Toast.makeText(this, "Id sự kiện bị thiếu", Toast.LENGTH_SHORT).show();
            this.finish();
            return;
        }
        historyService = new HistoryService(this);

        // Initialize UI elements
        ratingBar = findViewById(R.id.ratingBar);
        ratingBar.setRating(5);
        ratingValue = findViewById(R.id.ratingValue);
        commentInput = findViewById(R.id.commentInput);
        btnUploadFile = findViewById(R.id.btnUploadFile);
        btnSubmit = findViewById(R.id.btnSubmit);
        fileCount = findViewById(R.id.fileCount);
        recyclerViewFiles = findViewById(R.id.recyclerViewFiles);
        loading = findViewById(R.id.loading);

        // Setup RecyclerView
//        recyclerViewFiles.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        GridLayoutManager layoutManager = new GridLayoutManager(this, 3); // 3 cột
        recyclerViewFiles.setLayoutManager(layoutManager);
        fileAdapter = new FileAdapter(this, selectedFiles);
        recyclerViewFiles.setAdapter(fileAdapter);

        // Setup RatingBar
        ratingBar.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
            int roundedRating = Math.round(rating); // Ensure integer value
            ratingValue.setText("Rating: " + roundedRating);
        });

        // File upload
        btnUploadFile.setOnClickListener(v -> {
            // Check and request permission
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    // If permission is not granted, request it
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            REQUEST_CODE_STORAGE_PERMISSION);
                }
                else{
                    OpenUpload();
                }
            }
        });


        // Submit button
        btnSubmit.setOnClickListener(v -> {
            int rating = Math.round(ratingBar.getRating());
            String comment = commentInput.getText().toString();
            if (rating < 1) {
                Toast.makeText(this, "Please select a rating", Toast.LENGTH_SHORT).show();
                return;
            }
            Log.d("CreateCommentActivity", "Rating: " + rating);
            Log.d("CreateCommentActivity", "Comment: " + comment);
            Log.d("CreateCommentActivity", "Files: " + selectedFiles.size());

            // TODO: Upload data to server
            loading.setVisibility(View.VISIBLE);
            btnSubmit.setVisibility(View.GONE);
            SendPost(rating, comment);
        });
    }

    private void SendPost(int rating, String comment)
    {
        historyService.Post(eventId, rating, comment, selectedFiles, new HistoryService.GetMyPostCallBack() {
            @Override
            public void onSuccess(RSMyPost result) {
                runOnUiThread(()->{
                    loading.setVisibility(View.GONE);
                    btnSubmit.setVisibility(View.VISIBLE);
                    Toast.makeText(getApplicationContext(), "Đăng bài thành công", Toast.LENGTH_SHORT).show();
                    Intent resultIntent = new Intent();
                    setResult(RESULT_OK, resultIntent);
                    finish();

                });
            }

            @Override
            public void onFailure(String error) {
                runOnUiThread(()->{
                    loading.setVisibility(View.GONE);
                    btnSubmit.setVisibility(View.VISIBLE);
                    Toast.makeText(getApplicationContext(), error, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void OpenUpload()
    {
        if (selectedFiles.size() >= MAX_FILES) {
            Toast.makeText(this, "Maximum file limit reached", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(intent, FILE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FILE_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                if (data.getClipData() != null) { // Multiple files selected
                    for (int i = 0; i < data.getClipData().getItemCount(); i++) {
                        if (selectedFiles.size() < MAX_FILES) {
                            Uri fileUri = data.getClipData().getItemAt(i).getUri();
                            selectedFiles.add(fileUri);
                        } else {
                            Toast.makeText(this, "Maximum file limit reached", Toast.LENGTH_SHORT).show();
                            break;
                        }
                    }
                } else if (data.getData() != null) { // Single file selected
                    if (selectedFiles.size() < MAX_FILES) {
                        selectedFiles.add(data.getData());
                    } else {
                        Toast.makeText(this, "Maximum file limit reached", Toast.LENGTH_SHORT).show();
                    }
                }
                fileCount.setText("Files selected: " + selectedFiles.size());
                fileAdapter.notifyDataSetChanged();
            }
        }
    }
}

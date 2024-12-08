package com.example.ticketbooking.ticket;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.ticketbooking.R;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import modules.JwtTokenHelper;
import modules.LocalStorageManager;

public class TicketInfoActivity extends AppCompatActivity implements View.OnClickListener {
    private static String idUser;
    private static String idTicket;
    private static String typeTicket;
    private static String locaTicket;
    private static String posTicket;
    private static String token ;
    private JwtTokenHelper jwtTokenHelper;
    LocalStorageManager localStorageManager;
    private CountDownTimer countDownTimer;

    private Button btnGenerate, btnClose;
    private TextView txtType, txtPosition, txtLocation,txtId, txtTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.ticket_activity_info);
        jwtTokenHelper = new JwtTokenHelper(getApplicationContext());
        localStorageManager = new LocalStorageManager(getApplicationContext());
        initView();
        getFromIntent();
        displayQrCode();
    }

    void initView()
    {
        txtId = findViewById(R.id.ticket_tvId);
        txtPosition = findViewById(R.id.ticket_tvPosition);
        txtLocation = findViewById(R.id.ticket_tvLocation);
        txtType = findViewById(R.id.ticket_tvType);
        txtTime = findViewById(R.id.ticket_tvTime);
        btnGenerate = findViewById(R.id.ticket_btnGenerate);
        btnClose = findViewById(R.id.ticket_btnClose);
        btnGenerate.setOnClickListener(this);
        btnClose.setOnClickListener(this);
    }
    void getFromIntent()
    {
        Intent intent = getIntent();
        idTicket = intent.getStringExtra("ticketId");
        typeTicket = intent.getStringExtra("type");
        locaTicket = intent.getStringExtra("location");
        posTicket = intent.getStringExtra("position");

        idUser = localStorageManager.getIdUser();
        if(idTicket.isEmpty() || idUser.isEmpty())
        {
            Toast.makeText(getApplicationContext(),"Id vé hoặc Id người dùng bị thiếu", Toast.LENGTH_SHORT).show();
            this.finish();
        }

        txtId.setText(idTicket);
        txtType.setText("Loại vé: "+ typeTicket);
        txtLocation.setText("Khu vực: "+ locaTicket);
        txtPosition.setText("Vị trí: " + posTicket);
    }


    private void displayQrCode() {
        try {
            ImageView qrCodeImageView = findViewById(R.id.ticket_ivQrCode);
            token = jwtTokenHelper.createToken(idUser, idTicket);
            // Tạo mã QR
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.encodeBitmap(token, com.google.zxing.BarcodeFormat.QR_CODE, 200, 200);

            // Hiển thị mã QR
            qrCodeImageView.setImageBitmap(bitmap);
            startCountdown(); // Reset đếm ngược 60 giây
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Không thể tạo mã QR", Toast.LENGTH_SHORT).show();
        }
    }

    private void startCountdown() {
        // Hủy bỏ bộ đếm trước đó nếu có
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        // Tạo bộ đếm mới
        countDownTimer = new CountDownTimer(60000, 1000) { // 60 giây, cập nhật mỗi giây
            @Override
            public void onTick(long millisUntilFinished) {
                long secondsRemaining = millisUntilFinished / 1000;
                txtTime.setText("QR hết hạn sau: " + secondsRemaining + "s");
            }

            @Override
            public void onFinish() {
                txtTime.setText("QR đã hết hạn!");
            }
        };

        countDownTimer.start(); // Bắt đầu đếm ngược
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if(id == R.id.ticket_btnGenerate)
        {
            displayQrCode();
        }
        else if(id == R.id.ticket_btnClose){
            this.finish();
        }
    }
}
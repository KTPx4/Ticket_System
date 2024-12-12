package com.example.ticketbooking.order;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ticketbooking.R;

public class CheckOutCard extends AppCompatActivity {
    private static String SUCCESS_URL = "http://localhost:3001/checkout/payment-success";
    private boolean isPaymentSuccess = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ticket_activity_check_out_card);

        // Cập nhật SUCCESS_URL từ file strings.xml
        SUCCESS_URL = getString(R.string.server_url) + "/checkout/payment-success";

        // Nhận URL thanh toán từ Intent
        String paymentUrl = getIntent().getStringExtra("PAYMENT_URL");
        if (paymentUrl == null || paymentUrl.isEmpty()) {
            Toast.makeText(this, "URL thanh toán không hợp lệ", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Thiết lập WebView
        WebView webView = findViewById(R.id.webview_payment);
        webView.getSettings().setJavaScriptEnabled(true); // Bật JavaScript
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                String url = request.getUrl().toString();
                Log.d("CheckOutCard", "Loading URL: " + url);

                // Kiểm tra URL thành công
                if (url.startsWith(SUCCESS_URL)) {
                    // Hiển thị thông báo
                    Toast.makeText(getApplicationContext(), "Thanh toán thành công", Toast.LENGTH_SHORT).show();
                    // Đánh dấu thành công và không cho phép load lại
                    isPaymentSuccess = true;

                }
                return super.shouldOverrideUrlLoading(view, request);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                // Kiểm tra nếu URL là SUCCESS_URL và thanh toán thành công
                if (url.startsWith(SUCCESS_URL)) {
                    // Đánh dấu thành công và xử lý kết quả
                    isPaymentSuccess = true;

                }
            }
        });

        webView.setWebChromeClient(new WebChromeClient()); // Hỗ trợ các sự kiện trên WebView
        webView.loadUrl(paymentUrl); // Load URL từ Stripe

        // Floating Action Button để đóng Activity
        Button fabClose = findViewById(R.id.fab_close);
        fabClose.setOnClickListener(v -> {
            AlertDialog dig =  new AlertDialog.Builder(this)
                    .setTitle("Đóng")
                    .setMessage("Bạn có muốn đóng thanh toán?")
                    .setNegativeButton("Thoát", (dialog, which) -> {
                        if(isPaymentSuccess)
                        {
                            Intent resultIntent = new Intent();
                            resultIntent.putExtra("isSuccess", true);
                            setResult(RESULT_OK, resultIntent);
                        }
                        finish();
                    })
                    .setPositiveButton("Không", (dialog2, which) -> {
                        dialog2.dismiss();
                    })
                    .create();
            dig.show();
            dig.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK); // Màu cho nút "Hủy"
            dig.getButton(androidx.appcompat.app.AlertDialog.BUTTON_NEGATIVE).setTextColor(getApplicationContext().getColor(R.color.inValid_Ticket));  // Màu cho nút "Xóa"
        }); // Đóng Activity
    }
}

package com.example.ticketbooking.order;
import android.os.Build;
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import vn.momo.momo_partner.AppMoMoLib;
import vn.momo.momo_partner.MoMoParameterNameMap;
import com.example.ticketbooking.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class CheckOutActivity extends AppCompatActivity {

    private String amount = "10000000";
    private String fee = "0";
    int environment = 0;//developer default
    private String merchantName = "Ticket Booking";
    private String merchantCode = "MOMONPMB20210629";
    private String merchantNameLabel = "Nhà cung cấp";
    private String description = "Thanh toán đặt vé";

    Button btnMomo;
    TextView tvMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_check_out);
        AppMoMoLib.getInstance().setEnvironment(AppMoMoLib.ENVIRONMENT.DEVELOPMENT); // AppMoMoLib.ENVIRONMENT.PRODUCTION

        btnMomo = findViewById(R.id.btn_momo);
        tvMessage = findViewById(R.id.tvMessage);
        btnMomo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String orderID = "123";
                JSONObject objExtraData = new JSONObject();
                try {
                    objExtraData.put("ticket_code", "");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                requestPayment(orderID , objExtraData);
            }
        });
    }

    //Get token through MoMo app
    private void requestPayment(String orderId, JSONObject objExtraData) {
        AppMoMoLib.getInstance().setAction(AppMoMoLib.ACTION.PAYMENT);
        AppMoMoLib.getInstance().setActionType(AppMoMoLib.ACTION_TYPE.GET_TOKEN);
        Map<String, Object> eventValue = new HashMap<>();
        String ipnUrl = this.getString(R.string.server_url) + "/api/v1/order/" + orderId + "/valid";

        //client Required
        eventValue.put("merchantname", merchantName); //Tên đối tác. được đăng ký tại https://business.momo.vn. VD: Google, Apple, Tiki , CGV Cinemas
        eventValue.put("merchantcode", merchantCode); //Mã đối tác, được cung cấp bởi MoMo tại https://business.momo.vn
        eventValue.put("amount", amount); //Kiểu integer
        eventValue.put("orderId", orderId); //uniqueue id cho Bill order, giá trị duy nhất cho mỗi đơn hàng
        eventValue.put("orderLabel", "Mã đơn hàng"); //gán nhãn
        eventValue.put("notifyUrl", ipnUrl); //gán nhãn

        //client Optional - bill info
        eventValue.put("merchantnamelabel", "Dịch vụ");//gán nhãn
        eventValue.put("fee", 0); //Kiểu integer
        eventValue.put("description", description); //mô tả đơn hàng - short description

        //client extra data
        String requestId = merchantCode + "merchant_billId_" + System.currentTimeMillis();
        Log.d("MOMO", "requestId: " + requestId);


        eventValue.put("requestId", requestId);
        eventValue.put("partnerCode", merchantCode);
        //Example extra data

        eventValue.put("extraData", objExtraData.toString());

        eventValue.put("extra", "");

        AppMoMoLib.getInstance().requestMoMoCallBack(this, eventValue);
    }

    //Get token callback from MoMo app an submit to server side
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AppMoMoLib.getInstance().REQUEST_CODE_MOMO && resultCode == -1) {
            if (data != null) {

                Bundle extras = data.getExtras();
                if (extras != null) {
                    // Duyệt qua từng phần tử trong Bundle và log
                    for (String key : extras.keySet()) {
                        Object value = extras.get(key);
                        try {
                            // Dùng Gson để chuyển đổi giá trị thành JSON nếu có thể
                            Gson gson = new Gson();
                            String jsonValue = gson.toJson(value);
                            Log.d("MOMO_RESPONSE", "Key: " + key + " | Value: " + jsonValue);
                        } catch (Exception e) {
                            // Nếu không thể serialize, in kiểu dữ liệu và giá trị thô
                            Log.d("MOMO_RESPONSE", "Key: " + key + " | Value (raw): " + value + " | Type: " + value.getClass().getName());
                        }
                    }
                } else {
                    Log.d("MOMO_RESPONSE", "No extras in Intent");
                }

                if (data.getIntExtra("status", -1) == 0) {
                    //TOKEN IS AVAILABLE
                    String message = data.getStringExtra("message");
                    tvMessage.setText("message: " + "Get token " + message);
                    String token = data.getStringExtra("data"); //Token response
                    String phoneNumber = data.getStringExtra("phonenumber");
                    String env = data.getStringExtra("env");
                    if (env == null) {
                        env = "app";
                    }

                    if (token != null && !token.equals("")) {
                        // TODO: send phoneNumber & token to your server side to process payment with MoMo server
                        // IF Momo topup success, continue to process your order
                    } else {
                        tvMessage.setText("message: " + "no info");
                    }
                }
                else if (data.getIntExtra("status", -1) == 1) {
                    //TOKEN FAIL
                    String message = data.getStringExtra("message") != null ? data.getStringExtra("message") : "Thất bại";
                    tvMessage.setText("message: " + message);
                }
                else if (data.getIntExtra("status", -1) == 2) {
                    //TOKEN FAIL
                    tvMessage.setText("message: " + "no info");
                }
                else {
                    //TOKEN FAIL
                    tvMessage.setText("message: " + "no info");
                }
            }
            else {
                tvMessage.setText("message: " + "no info");
            }
        }
        else {
            tvMessage.setText("message: " + "no info err");
        }
    }



}
package com.example.ticketbooking.order;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.ticketbooking.R;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.ticket.MyPending;
import model.ticket.Ticket;
import model.ticket.TicketInfo;
import modules.MoneyFormatter;
import services.OrderService;
import services.response.order.DataGetCheckOut;
import vn.momo.momo_partner.AppMoMoLib;

public class CheckOutActivity extends AppCompatActivity implements  View.OnClickListener{
    private String idBuyTicket;
    private String typePayment;
    private String tokenCheckout = "";

    private List<String> listId;
    private List<TicketInfo> listTicketData = new ArrayList<>();

    private OrderService orderService;
    private String couponCode = "";

    boolean isWaiting = false, isLoading = true;
    ArrayAdapter arrayAdapter ;

    ConstraintLayout layoutMain;
    TextView tvName, tvType, tvTotal, tvDiscount, tvDiscountCoupon, tvFinalMoney;
    EditText edCoupon;
    ListView lvListTicket;
    ProgressBar prWaiting, prLoading;

    ImageButton btnAddCoupon, btnClose;
    Button btnVisa, btnMomo;

    // MOMO
    private String amount = "-1";
    private String fee = "0";
    int environment = 0;//developer default
    private String merchantName = "Ticket Booking";
    private String merchantCode = "MOMONPMB20210629";
    private String merchantNameLabel = "Nhà cung cấp";
    private String description = "Thanh toán đặt vé";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.ticket_activity_check_out);
        AppMoMoLib.getInstance().setEnvironment(AppMoMoLib.ENVIRONMENT.DEVELOPMENT); // AppMoMoLib.ENVIRONMENT.PRODUCTION
        orderService = new OrderService(this);
        initView();
        getFromIntent();
        getCheckOut();

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id == R.id.btnClose)
        {
            this.finish();
        }

        if(isWaiting || isLoading)
        {
            Toast.makeText(this, "Vui lòng đợi", Toast.LENGTH_SHORT).show();
            return;
        }

        if(id == R.id.btnAdd)
        {
            couponCode = edCoupon.getText().toString();
            if(couponCode == null || couponCode.isEmpty())
            {
                Toast.makeText(this, "Vui lòng nhập code", Toast.LENGTH_SHORT).show();
                return;
            }
            isWaiting = true;
            setWaiting();
            getCheckOut();
            return;
        }

        if(tokenCheckout == null || tokenCheckout.isEmpty())
        {
            AlertDialog dig =  new AlertDialog.Builder(this)
                    .setTitle("Thiếu thông tin")
                    .setMessage("Không có thôn tin token thanh toán. Vui lòng tải lại thanh toán")
                    .setPositiveButton("Ok", (dialog, which) -> {
                        dialog.dismiss();
                        this.finish();
                    }).create();
            dig.show();
            return;
        }

        isWaiting = true;
        setWaiting();

        if(id == R.id.btnVisa)
        {

        }
        else if(id == R.id.btnMomo)
        {

            JSONObject objExtraData = new JSONObject();
            try {
                objExtraData.put("ticket_code", "");

            } catch (JSONException e) {
                e.printStackTrace();
            }
            requestPayment(idBuyTicket , objExtraData);
        }
    }
    private void setWaiting()
    {

        int statusView = isWaiting  ?  View.INVISIBLE : View.VISIBLE;
        int statusProcessBar = isWaiting  ? View.VISIBLE : View.INVISIBLE;
        btnAddCoupon.setVisibility(statusView);
        btnVisa.setVisibility(statusView);
        btnMomo.setVisibility(statusView);

        prWaiting.setVisibility(statusProcessBar);

    }
    private void initView()
    {
        tvName = findViewById(R.id.tvName);
        tvType = findViewById(R.id.tvType);
        tvTotal = findViewById(R.id.tvTotal);
        tvDiscount = findViewById(R.id.tvDiscount);
        tvDiscountCoupon = findViewById(R.id.tvDiscountCoupon);
        tvFinalMoney = findViewById(R.id.tvFinalMoney);
        edCoupon = findViewById(R.id.edCoupon);
        lvListTicket = findViewById(R.id.listTicket);
        btnAddCoupon = findViewById(R.id.btnAdd);
        btnClose = findViewById(R.id.btnClose);
        btnVisa = findViewById(R.id.btnVisa);
        btnMomo = findViewById(R.id.btnMomo);
        prWaiting = findViewById(R.id.waiting);
        prLoading = findViewById(R.id.loading);
        layoutMain = findViewById(R.id.layoutMain);

        btnAddCoupon.setOnClickListener(this);
        btnClose.setOnClickListener(this);
        btnVisa.setOnClickListener(this);
        btnMomo.setOnClickListener(this);

        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_2, android.R.id.text1, listTicketData){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text1 = (TextView) view.findViewById(android.R.id.text1);
                TextView text2 = (TextView) view.findViewById(android.R.id.text2);
                TicketInfo ticketIf = listTicketData.get(position);
                Ticket ticket = ticketIf.getTicket();
                String name = ticket.getInfo().getTypeTicket() +" - " + ticket.getInfo().getLocation() +" - " + ticket.getPosition();
                String price = MoneyFormatter.formatCurrency(ticket.getInfo().getPrice()) + "";
                text1.setText(name);
                text2.setText(price);
                return view;
            }
        };

        lvListTicket.setAdapter(arrayAdapter);

    }

    private void getFromIntent()
    {
        Intent intent = getIntent();
        idBuyTicket = intent.getStringExtra("idBuyTicket");
        listId = intent.getStringArrayListExtra("listInfo");

        if(idBuyTicket == null || idBuyTicket.isEmpty())
        {
            Toast.makeText(this, "Thiếu id hóa đơn, không thể thanh toán", Toast.LENGTH_SHORT).show();
            this.finish();
            return;
        }
        if(listId == null)
        {
            listId = new ArrayList<>();
        }


    }
    private void updateView(DataGetCheckOut data)
    {

        try{
            MyPending myPending = data.getBuyTicket();

            String typePay = myPending.getEvent().getName().toLowerCase().equals("all")? "Tất cả": "Từng thành viên";
            layoutMain.setVisibility(View.VISIBLE);
            tvName.setText(myPending.getEvent().getName());
            tvType.setText(typePay);
            tvTotal.setText(MoneyFormatter.formatCurrency(data.getPrice()) + "");
            tvDiscount.setText(MoneyFormatter.formatCurrency(data.getDiscount()) + "");
            tvDiscountCoupon.setText(MoneyFormatter.formatCurrency(data.getCouponDiscount()) + "");
            tvFinalMoney.setText(MoneyFormatter.formatCurrency(data.getFinalPrice()) + "");

            listTicketData.clear();

            listTicketData.addAll(myPending.getTicketInfo());
            arrayAdapter.notifyDataSetChanged();
            isLoading = false;
            isWaiting = false;
            setWaiting();
            amount = data.getFinalPrice() + "";
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    private void getCheckOut()
    {
        orderService.GetCheckOut(idBuyTicket, couponCode, listId, new OrderService.GetCheckOutCallback() {
            @Override
            public void onSuccess(DataGetCheckOut data, String token) {
                runOnUiThread(()->{
                    Toast.makeText(CheckOutActivity.this, "Tải thông tin thành công", Toast.LENGTH_SHORT).show();
                    tokenCheckout = token;
                    updateView(data);
                });
            }

            @Override
            public void onFailure(String error) {
                runOnUiThread(()->{
                    Toast.makeText(CheckOutActivity.this, error, Toast.LENGTH_SHORT).show();
                    isLoading = false;
                    isWaiting = false;
                    setWaiting();
                });
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
        isWaiting = false;
        setWaiting();

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
                        }
                        catch (Exception e)
                        {
                            // Nếu không thể serialize, in kiểu dữ liệu và giá trị thô
                            Log.d("MOMO_RESPONSE", "Key: " + key + " | Value (raw): " + value + " | Type: " + value.getClass().getName());
                        }
                    }
                } else {
                    Log.d("MOMO_RESPONSE", "No extras in Intent");
                }

                if (data.getIntExtra("status", -1) == 0) {
                    //TOKEN IS AVAILABLE
                    /*
                        String message = data.getStringExtra("message");
                        tvMessage.setText("message: " + "Get token " + message);
                        String phoneNumber = data.getStringExtra("phonenumber");
                        String env = data.getStringExtra("env");
                        if (env == null) {
                            env = "app";
                        }
                    */

                    String token = data.getStringExtra("data"); //Token response

                    if (token != null && !token.equals("")) {
                        // Call order service to valid transaction

                    } else {
//                        tvMessage.setText("message: " + "no info");
                    }
                }
                else if (data.getIntExtra("status", -1) == 1) {
                    //TOKEN FAIL
                    String message = data.getStringExtra("message") != null ? data.getStringExtra("message") : "Thất bại";
//                    tvMessage.setText("message: " + message);
                }
                else if (data.getIntExtra("status", -1) == 2) {
                    //TOKEN FAIL
//                    tvMessage.setText("message: " + "no info");
                }
                else {
                    //TOKEN FAIL
//                    tvMessage.setText("message: " + "no info");
                }
            }
            else {
//                tvMessage.setText("message: " + "no info");
            }
        }
        else {
//            tvMessage.setText("message: " + "no info err");
        }
    }
}
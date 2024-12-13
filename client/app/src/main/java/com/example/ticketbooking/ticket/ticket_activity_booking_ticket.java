package com.example.ticketbooking.ticket;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ticketbooking.R;
import com.example.ticketbooking.order.CheckOutActivity;
import com.example.ticketbooking.ticket.viewcustom.ZoomableView;

import java.util.ArrayList;
import java.util.List;

import modules.MoneyFormatter;
import services.EventService;
import services.OrderService;
import services.response.event.RSGetAllTicket;


public class ticket_activity_booking_ticket extends AppCompatActivity {
    EventService eventService;
    OrderService orderService;
    Button btnBook;
    TextView tvMoney;
    GridLayout grLocationA, grLocationB, grLocationC;
    ZoomableView zoomableView;
    ProgressBar loading;
    boolean isLoading = true;
    String idEvent = "675c36e019aa03318c988626";
    List<String> listTicketId = new ArrayList<>();
    long finalMoney = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ticket_booking_ticket);
        eventService = new EventService(this);
        orderService = new OrderService(this);

        zoomableView = findViewById(R.id.zoomableView);
        btnBook = findViewById(R.id.btnBooking);
        loading = findViewById(R.id.loading);
        tvMoney = findViewById(R.id.tvMoney);
        setEffectBtn();

        // Tìm các GridLayout
         grLocationA = findViewById(R.id.grLocationA);
         grLocationB = findViewById(R.id.grLocationB);
         grLocationC = findViewById(R.id.grLocationC);

         // get intent
         getFromIntent();

        // Get Data
        getData();

        btnBook.setOnClickListener((v)->{
            if(isLoading)
            {
                Toast.makeText(getApplicationContext(), "Vui lòng đợi tải dữ liệu", Toast.LENGTH_SHORT).show();
                return;
            }

            if(listTicketId.stream().count() < 1)
            {
                Toast.makeText(getApplicationContext(), "Vui lòng chọn vé", Toast.LENGTH_SHORT).show();
                return;
            }

            orderService.BuyTicket(idEvent, listTicketId, new OrderService.ValidCallback() {
                @Override
                public void onSuccess(String success) {
                    runOnUiThread(()->{
                        AlertDialog dig =  new AlertDialog.Builder(ticket_activity_booking_ticket.this)
                                .setTitle("Taọ đơn vé thành Công")
                                .setMessage("Đơn vé của bạn đã được tạo. Hãy vào màn hình thanh toán để xem nhé")
                                .setPositiveButton("Ok luôn", (dialog, which) -> {
                                    Intent resultIntent = new Intent();
                                    setResult(RESULT_OK, resultIntent);
                                    ticket_activity_booking_ticket.this.finish();
                                }).create();
                        dig.show();
                    });
                }

                @Override
                public void onFailure(String error) {
                    runOnUiThread(()->{
                        Toast.makeText(getApplicationContext(), error, Toast.LENGTH_SHORT).show();
                    });
                }
            });

        });

    }

    private void getFromIntent()
    {
        Intent intent = getIntent();
        idEvent = intent.getStringExtra("idEvent");
        if(idEvent == null || idEvent.isEmpty())
        {
            Toast.makeText(this, "Không có id sự kiện!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
    }

    private void getData()
    {
        eventService.GetAllTicket(idEvent, new EventService.CallBackGetTicket() {
            @Override
            public void onSuccess(List<RSGetAllTicket.LocationInfo> data) {
                runOnUiThread(()->{
                    isLoading = false;
                    updateView();

                    for( RSGetAllTicket.LocationInfo lInfo : data)
                    {
                        String location = lInfo.getLocation().toLowerCase();
                        if(location.equals("a"))
                        {
                            addSeatsToGrid(grLocationA, lInfo.getTypes() );
                        }
                        else if(location.equals("b"))
                        {

                            addSeatsToGrid(grLocationB,  lInfo.getTypes());

                        }
                        else{

                            addSeatsToGrid(grLocationC,  lInfo.getTypes());
                        }
                        // Thêm 60 View vào từng GridLayout

                    }
                });

            }

            @Override
            public void onFailure(String error) {

                runOnUiThread(()->{
                    AlertDialog dig =  new AlertDialog.Builder(ticket_activity_booking_ticket.this)
                            .setTitle("Tải thông tin thất bại")
                            .setMessage(error)
                            .setPositiveButton("Thoát", (dialog, which) -> {
                                ticket_activity_booking_ticket.this.finish();
                                return;
                            }).create();
                    dig.show();
                });
            }
        });
    }

    void updateView()
    {
        int display = !isLoading ? View.VISIBLE : View.GONE;
        int progress = isLoading ? View.VISIBLE : View.GONE;
        zoomableView.setVisibility(display);
        loading.setVisibility(progress);
    }


    private void addSeatsToGrid(GridLayout gridLayout, List<RSGetAllTicket.TypeInfo> listType) {
        int totalSeats = 60; // Số lượng ghế
        int columns = 10;    // Số cột của GridLayout

        gridLayout.setColumnCount(columns);

        RSGetAllTicket.TypeInfo infoVip1 = new RSGetAllTicket.TypeInfo();
        RSGetAllTicket.TypeInfo infoVip2 = new RSGetAllTicket.TypeInfo();
        RSGetAllTicket.TypeInfo infoNormal = new RSGetAllTicket.TypeInfo();

        for(RSGetAllTicket.TypeInfo type : listType)
        {
            String tp = type.getType().toLowerCase();
            if(tp.equals("vip 1"))
            {
                infoVip1 = type;
            }
            else if(tp.equals("vip 2"))
            {
                infoVip2 = type;
            }
            else if(tp.equals("normal"))
            {
                infoNormal = type;
            }
        }


        List<RSGetAllTicket.TypeInfo> listTypeParse = new ArrayList<>();
        listTypeParse.add(infoVip1);
        listTypeParse.add(infoVip2);
        listTypeParse.add(infoNormal);

        for (RSGetAllTicket.TypeInfo typeInf : listTypeParse) {

            List<RSGetAllTicket.TicketInfo> listTicket = typeInf.getTickets();

            long price = typeInf.getPrice();

            for(RSGetAllTicket.TicketInfo ticket : listTicket)
            {
                boolean isValid = ticket.getAccBuy() == null ? true : ticket.getAccBuy().isEmpty() ;

                String id = ticket.get_id();
                int position = ticket.getPosition();

                // Tạo View đại diện cho ghế
                View seat = new View(this);
                GridLayout.LayoutParams params = new GridLayout.LayoutParams();

                // Kích thước và lề cho ghế
                params.width = dpToPx(13);
                params.height = dpToPx(13);
                params.setMargins(dpToPx(1), dpToPx(1), dpToPx(1), dpToPx(1));

                seat.setLayoutParams(params);

                TicketData ticketData = new TicketData(id, position, price);

                seat.setTag( ticketData );

                seat.setBackgroundColor(getResources().getColor(R.color.ticket_valid)); // Màu mặc định

                if(!isValid)
                {
                    seat.setBackgroundColor(getApplication().getColor(R.color.ticket_sold)); // Màu ghế trống
                }

                // Thêm sự kiện click
                seat.setOnClickListener(v -> {
                    TicketData ticketDT = (TicketData) seat.getTag(); // Lấy dữ liệu TicketData từ Tag
                    if(!isValid)
                    {
                        Toast.makeText(this, "Vé đã được mua", Toast.LENGTH_SHORT).show();
                        return;
                    }


                    if (ticketDT.isSelected())
                    {

                        // Nếu đã chọn, đổi về trạng thái bỏ chọn
                        listTicketId.remove(ticketDT.getId());
                        seat.setBackgroundColor(getApplication().getColor(R.color.ticket_valid)); // Màu ghế trống
                        finalMoney -= price;
                        ticketDT.setSelected(false); // Cập nhật trạng thái
                    }
                    else
                    {
                        // Nếu chưa chọn, đổi về trạng thái chọn
                        listTicketId.add(ticketDT.getId());
                        seat.setBackgroundColor(getApplication().getColor(R.color.ticket_selected)); // Màu ghế đã chọn
                        finalMoney += price;
                        ticketDT.setSelected(true); // Cập nhật trạng thái
                    }

                    // Gắn lại TicketData vào Tag để lưu thay đổi
                    seat.setTag(ticketDT);
                    tvMoney.setText(MoneyFormatter.formatCurrency(finalMoney) +"");
                    // Debug: Hiển thị trạng thái mới
//                    Toast.makeText(this, "Seat ID: " + ticketDT.getId() + ", Selected: " + ticketDT.isSelected(), Toast.LENGTH_SHORT).show();
                });

                // Thêm View vào GridLayout
                gridLayout.addView(seat);
            }

        }
    }

    public class TicketData {
        private String id;

        private int position;
        private double price;
        private boolean isSelected;

        public TicketData(String id, int position, double price) {
            this.id = id;

            this.position = position;
            this.price = price;
            isSelected = false;
        }

        public boolean isSelected() {
            return isSelected;
        }

        public void setSelected(boolean selected) {
            isSelected = selected;
        }

        public void setId(String id) {
            this.id = id;
        }


        public int getPosition() {
            return position;
        }

        public void setPosition(int position) {
            this.position = position;
        }

        public void setPrice(double price) {
            this.price = price;
        }

        public String getId() {
            return id;
        }

        public double getPrice() {
            return price;
        }
    }

    private void setEffectBtn()
    {
        // Create border drawable
        GradientDrawable border = new GradientDrawable();
        border.setColor(Color.WHITE);  // Background color
        border.setCornerRadius(8);  // Corner radius
        border.setStroke(2, Color.parseColor("#A33757"));  // Border color and width

        // Create ripple effect
        RippleDrawable rippleDrawable = new RippleDrawable(
                ColorStateList.valueOf(Color.parseColor("#A33757")),
                border, null);

        // Set background to the button
        btnBook.setBackground(rippleDrawable);
    }

    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density + 0.5f);
    }

}
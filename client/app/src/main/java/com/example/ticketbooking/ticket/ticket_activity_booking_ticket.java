package com.example.ticketbooking.ticket;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ticketbooking.R;
import com.example.ticketbooking.ticket.viewcustom.ZoomableView;

public class ticket_activity_booking_ticket extends AppCompatActivity {
    Button btnBook;
    ZoomableView zoomableView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ticket_booking_ticket);

        zoomableView = findViewById(R.id.zoomableView);
        btnBook = findViewById(R.id.btnBooking);
        setEffectBtn();


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
}
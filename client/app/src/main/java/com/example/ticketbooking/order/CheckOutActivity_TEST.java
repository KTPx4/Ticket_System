package com.example.ticketbooking.order;
import android.util.Log;
import com.google.gson.Gson;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import vn.momo.momo_partner.AppMoMoLib;

import com.example.ticketbooking.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class CheckOutActivity_TEST extends AppCompatActivity {



    Button btnMomo;
    TextView tvMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_check_out);

        btnMomo = findViewById(R.id.btn_momo);
        tvMessage = findViewById(R.id.tvMessage);
        btnMomo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }





}
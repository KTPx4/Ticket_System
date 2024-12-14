package com.example.ticketbooking;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.example.ticketbooking.coupon.CouponActivity;

import org.json.JSONObject;

import modules.LocalStorageManager;
import services.AccountHomeService;
import services.EventService;
import services.HomeService;

public class AccountUserFragment extends Fragment {
    private TextView txt_logout,txt_Name,txt_Point,txt_Email,txt_delhistory,txt_PaymentHistory , tvPoint;
    private ImageView imgUser;
    private Button button_edit;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the fragment layout
        View view = inflater.inflate(R.layout.fragment_account_user, container, false);

        // Initialize the logout TextView
        txt_logout = view.findViewById(R.id.txt_logout);
        txt_Name = view.findViewById(R.id.txt_Name);
        txt_Point = view.findViewById(R.id.txt_Point);
        imgUser = view.findViewById(R.id.imgUser);
        txt_Email = view.findViewById(R.id.txt_Email);
        txt_delhistory = view.findViewById(R.id.txt_delhistory);
        txt_PaymentHistory = view.findViewById(R.id.txt_PaymentHistory);
        button_edit = view.findViewById(R.id.button_edit);
        tvPoint = view.findViewById(R.id.txt_exchangePoints);
        tvPoint.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), CouponActivity.class);
            startActivity(intent);
        });

        fetchAccountData();

        txt_logout.setOnClickListener(v -> Logout());

        button_edit.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ChangeInfoUserActivity.class);
            startActivity(intent);
        });


        txt_delhistory.setOnClickListener(v -> DeleteHistory());

//        txt_PaymentHistory.setOnClickListener(v -> PaymentHistory());

        return view;
    }

    private void Logout() {
        // Clear all data in LocalStorageManager
        LocalStorageManager.clearAllData(getContext());

        // Navigate back to IntroActivity
        Intent intent = new Intent(getActivity(), IntroActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

        // Optionally, finish the current activity if you want to close it
        getActivity().finish();
    }

    private void DeleteHistory() {
        AccountHomeService accountHomeService = new AccountHomeService(getContext());
        accountHomeService.deleteAllHistory(new AccountHomeService.ResponseCallback() {
            @Override
            public void onSuccess(String success) {
                // Ensure UI changes happen on the main thread
                getActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(), "History deleted successfully!", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onFailure(String error) {
                // Ensure UI changes happen on the main thread
                getActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(), "Failed to delete history: " + error, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        fetchAccountData();
    }

    private void fetchAccountData() {
        AccountHomeService accountHomeService = new AccountHomeService(getContext());
        accountHomeService.getAccountInfo(new AccountHomeService.ResponseCallback() {
            @Override
            public void onSuccess(String success) {
                // Parse the response and update UI elements
                try {
                    JSONObject jsonResponse = new JSONObject(success);
                    JSONObject data = jsonResponse.getJSONObject("data");

                    String name = data.getString("name");
                    int points = data.getInt("point");
                    String email = data.getString("email");
                    String imageUrl = "https://ticket-system-l5j0.onrender.com/public/account/" + data.getString("_id") + "/" + data.getString("image");

                    // Ensure UI changes happen on the main thread
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // Set values to UI elements
                            txt_Name.setText(name);
                            txt_Point.setText(String.valueOf(points));
                            txt_Email.setText(String.valueOf(email));
                            // Load the image with Glide
                            Glide.with(getContext())
                                    .load(imageUrl)
                                    .into(imgUser);
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Failed to load account data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }


}

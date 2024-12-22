package com.example.ticketbooking.home.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.ticketbooking.R;

import modules.LocalStorageManager;
import services.HomeService;

public class FilterBottomSheetFragment extends DialogFragment {

    private Button btnApplyFilters;
    private Button btnCancelFilters;
    private RadioButton rb_Hanoi, rb_HCM, rb_Dalat, rb_other;
    private RadioButton rb_music, rb_comedy, rb_art, rb_special;
    private LocalStorageManager localStorageManager;
    private Filter.FilterListener filterListener;
    private HomeService homeService;

    public FilterBottomSheetFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.event_fragment_filter_bottom_sheet, container, false);

        btnApplyFilters = view.findViewById(R.id.btnApplyFilters);
        btnCancelFilters = view.findViewById(R.id.btnCancelFilters);
        rb_Hanoi = view.findViewById(R.id.rb_Hanoi);
        rb_HCM = view.findViewById(R.id.rb_HCM);
        rb_Dalat = view.findViewById(R.id.rb_Dalat);
        rb_music = view.findViewById(R.id.rb_music);
        rb_comedy = view.findViewById(R.id.rb_comedy);
        rb_art = view.findViewById(R.id.rb_art);
        rb_special = view.findViewById(R.id.rb_special);

        localStorageManager = new LocalStorageManager(getContext());
        homeService = new HomeService(getContext()); // Initialize HomeService

        loadFilters();

        btnApplyFilters.setOnClickListener(v -> {
            String location = getSelectedLocation();
            String type = getSelectedType();

            if (getActivity() instanceof SearchUserActivity) {
                ((SearchUserActivity) getActivity()).filterEvents(location, type);
            }

            saveFilters();
            dismiss();
        });

        btnCancelFilters.setOnClickListener(v -> {
            SharedPreferences prefs = getContext().getSharedPreferences("filters", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.clear(); // Clear all filters
            editor.apply();
            dismiss();
        });

        return view;
    }

    private String getSelectedLocation() {
        if (rb_Hanoi.isChecked()) return "Hà Nội";
        if (rb_HCM.isChecked()) return "Hồ Chí Minh";
        if (rb_Dalat.isChecked()) return "Đà Lạt";
        return "";
    }

    private String getSelectedType() {
        if (rb_music.isChecked()) return "âm nhạc";
        if (rb_comedy.isChecked()) return "hài kịch";
        if (rb_art.isChecked()) return "nghệ thuật";
        if (rb_special.isChecked()) return "sự kiện đặc biệt";
        return ""; // Default to empty if none selected
    }


    // Save the selected filter values to SharedPreferences
    private void saveFilters() {
        SharedPreferences prefs = getContext().getSharedPreferences("filters", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putBoolean("Hanoi", rb_Hanoi.isChecked());
        editor.putBoolean("HCM", rb_HCM.isChecked());
        editor.putBoolean("Dalat", rb_Dalat.isChecked());

        editor.putBoolean("Music", rb_music.isChecked());
        editor.putBoolean("Comedy", rb_comedy.isChecked());
        editor.putBoolean("Art", rb_art.isChecked());
        editor.putBoolean("Special", rb_special.isChecked());
        editor.apply();
    }

    // Load the previously saved filter values from SharedPreferences
    private void loadFilters() {
        SharedPreferences prefs = getContext().getSharedPreferences("filters", Context.MODE_PRIVATE);

        rb_Hanoi.setChecked(prefs.getBoolean("Hanoi", false));
        rb_HCM.setChecked(prefs.getBoolean("HCM", false));
        rb_Dalat.setChecked(prefs.getBoolean("Dalat", false));

        rb_music.setChecked(prefs.getBoolean("Music", false));
        rb_comedy.setChecked(prefs.getBoolean("Comedy", false));
        rb_art.setChecked(prefs.getBoolean("Art", false));
        rb_special.setChecked(prefs.getBoolean("Special", false));

        String savedDate = prefs.getString("SelectedDate", "");
    }
}

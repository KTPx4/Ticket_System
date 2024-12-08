package com.example.ticketbooking;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import modules.LocalStorageManager;

public class FilterBottomSheetFragment extends DialogFragment {

    private Button btnApplyFilters;
    private Button btnCancelFilters;
    private RadioButton rb_Hanoi, rb_HCM, rb_Dalat, rb_other;
    private RadioButton rb_music, rb_comedy, rb_art, rb_special;
    private LocalStorageManager localStorageManager;
    private EditText edt_time;

    public FilterBottomSheetFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_filter_bottom_sheet, container, false);

        btnApplyFilters = view.findViewById(R.id.btnApplyFilters);
        btnCancelFilters = view.findViewById(R.id.btnCancelFilters);
        rb_Hanoi = view.findViewById(R.id.rb_Hanoi);
        rb_HCM = view.findViewById(R.id.rb_HCM);
        rb_Dalat = view.findViewById(R.id.rb_Dalat);
        rb_other = view.findViewById(R.id.rb_other);
        rb_music = view.findViewById(R.id.rb_music);
        rb_comedy = view.findViewById(R.id.rb_comedy);
        rb_art = view.findViewById(R.id.rb_art);
        rb_special = view.findViewById(R.id.rb_special);
        edt_time = view.findViewById(R.id.edt_time);

        localStorageManager = new LocalStorageManager(getContext());

        loadFilters();

        btnApplyFilters.setOnClickListener(v -> {
            saveFilters();
            dismiss();
        });

        edt_time.setOnClickListener(v -> showDatePicker());

        btnCancelFilters.setOnClickListener(v -> {
            SharedPreferences prefs = getContext().getSharedPreferences("filters", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.remove("Hanoi");
            editor.remove("HCM");
            editor.remove("Dalat");
            editor.remove("Other");

            editor.remove("Music");
            editor.remove("Comedy");
            editor.remove("Art");
            editor.remove("Special");

            editor.remove("SelectedDate");

            editor.apply();

            dismiss();
        });


        return view;
    }
    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getContext(),
                (view, year, month, dayOfMonth) -> {
                    // Định dạng ngày theo DD/MM/YYYY
                    Calendar selectedDate = Calendar.getInstance();
                    selectedDate.set(year, month, dayOfMonth);
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                    edt_time.setText(dateFormat.format(selectedDate.getTime()));
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        datePickerDialog.show();
    }

    // Lưu trạng thái của các lựa chọn vào LocalStorageManager (hoặc SharedPreferences)
    private void saveFilters() {
        SharedPreferences prefs = getContext().getSharedPreferences("filters", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putBoolean("Hanoi", rb_Hanoi.isChecked());
        editor.putBoolean("HCM", rb_HCM.isChecked());
        editor.putBoolean("Dalat", rb_Dalat.isChecked());
        editor.putBoolean("Other", rb_other.isChecked());

        editor.putBoolean("Music", rb_music.isChecked());
        editor.putBoolean("Comedy", rb_comedy.isChecked());
        editor.putBoolean("Art", rb_art.isChecked());
        editor.putBoolean("Special", rb_special.isChecked());

        editor.putString("SelectedDate", edt_time.getText().toString());

        editor.apply();
    }


    // Đọc trạng thái các lựa chọn từ LocalStorageManager (hoặc SharedPreferences)
    private void loadFilters() {
        SharedPreferences prefs = getContext().getSharedPreferences("filters", Context.MODE_PRIVATE);

        rb_Hanoi.setChecked(prefs.getBoolean("Hanoi", false));
        rb_HCM.setChecked(prefs.getBoolean("HCM", false));
        rb_Dalat.setChecked(prefs.getBoolean("Dalat", false));
        rb_other.setChecked(prefs.getBoolean("Other", false));

        rb_music.setChecked(prefs.getBoolean("Music", false));
        rb_comedy.setChecked(prefs.getBoolean("Comedy", false));
        rb_art.setChecked(prefs.getBoolean("Art", false));
        rb_special.setChecked(prefs.getBoolean("Special", false));

        String savedDate = prefs.getString("SelectedDate", "");
        edt_time.setText(savedDate);
    }

    public interface OnFilterAppliedListener {
        void onFilterApplied(String location, String eventType,String selectedDate);
    }


}

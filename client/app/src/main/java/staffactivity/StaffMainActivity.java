package staffactivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.ticketbooking.IntroActivity;
import com.example.ticketbooking.LoginActivity;
import com.example.ticketbooking.R;

import modules.LocalStorageManager;

public class StaffMainActivity extends AppCompatActivity implements View.OnClickListener {
    Button btnScan, btnLogout;
    ProgressBar loading;
    private static boolean isWaiting = false;
    LocalStorageManager localStorageManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.staff_activity_main);
        btnScan = findViewById(R.id.btnScan);
        loading = findViewById(R.id.loading);
        localStorageManager = new LocalStorageManager(this);
        btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(this);
        btnScan.setOnClickListener(this);
    }
    private void setLoading( ) {
        isWaiting = !isWaiting;
        int display = !isWaiting ? View.VISIBLE : View.INVISIBLE;
        int waiting = isWaiting ? View.INVISIBLE : View.VISIBLE;

        btnScan.setVisibility(display);
        loading.setVisibility(waiting);

    }
    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id == R.id.btnScan)
        {
//            setLoading();
            Intent intent = new Intent(this, SelectEventActivity.class);
            startActivity(intent);
        }
        else if(id == R.id.btnLogout)
        {
            new AlertDialog.Builder(this)
                    .setTitle("Đăng xuất")
                    .setPositiveButton("Ok", (dialog, which) -> {
                        localStorageManager.clearLoginToken();
                        localStorageManager.clearLoginOption();
                        Intent intent = new Intent(this, IntroActivity.class);
                        startActivity(intent);
                        finish();
                    })
                    .setNegativeButton("Không", (dialog2, which) -> dialog2.dismiss())
                    .create()
                    .show();

        }
    }
}
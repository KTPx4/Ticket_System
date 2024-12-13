package staffactivity;

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

import com.example.ticketbooking.R;

public class StaffMainActivity extends AppCompatActivity implements View.OnClickListener {
    Button btnScan;
    ProgressBar loading;
    private static boolean isWaiting = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.staff_activity_main);
        btnScan = findViewById(R.id.btnScan);
        loading = findViewById(R.id.loading);
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
    }
}
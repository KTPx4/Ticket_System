package staffactivity;

import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.ticketbooking.R;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ticketbooking.ticket.adapter.EventAdapter;
import com.google.zxing.Result;
import com.google.zxing.ResultPoint;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;

import java.util.ArrayList;
import java.util.List;

import services.EventService;
import staffactivity.adapter.TicketAdapter;

public class ScanTicketActivity extends AppCompatActivity {
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 100;

    private DecoratedBarcodeView barcodeView;
    private RecyclerView recyclerView;
    private TicketAdapter adapter;
    private List<String> scannedTickets;
    private EventService eventService;
    private static String idEvent= "6753135fbb6630356315742" ;

    private String lastScannedCode = "";
    private long lastScanTime = 0;

    private boolean hasCameraPermission() {
        return ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }
    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initializeScanner(); // Khởi tạo scanner nếu quyền được cấp
            } else {
                Toast.makeText(this, "Ứng dụng cần quyền truy cập camera để quét mã QR.", Toast.LENGTH_LONG).show();
                finish(); // Thoát Activity nếu quyền không được cấp
            }
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_ticket);
        // Kiểm tra quyền camera
        if (!hasCameraPermission()) {
            requestCameraPermission();
        } else {
            initializeScanner(); // Chỉ khởi tạo nếu quyền đã được cấp
        }

    }
    private void initializeScanner() {
        setContentView(R.layout.activity_scan_ticket);
        eventService = new EventService(getApplicationContext());
        // Khởi tạo RecyclerView
        scannedTickets = new ArrayList<>();
        recyclerView = findViewById(R.id.staff_scan_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TicketAdapter(scannedTickets);
        recyclerView.setAdapter(adapter);

        // Khởi tạo BarcodeView
        barcodeView = findViewById(R.id.staff_scan_camera_view);
        barcodeView.decodeContinuous(new BarcodeCallback() {
            @Override
            public void barcodeResult(BarcodeResult result) {
                handleScannedCode(result.getText());
            }

            @Override
            public void possibleResultPoints(List<ResultPoint> resultPoints) {}
        });
    }



    private void handleScannedCode(String code) {

        long currentTime = System.currentTimeMillis();

        // Tránh quét liên tiếp cùng một mã trong khoảng thời gian ngắn
        if (code.equals(lastScannedCode) && (currentTime - lastScanTime < 5000)) {
            return; // Bỏ qua nếu cùng mã và dưới 5 giây
        }

        lastScannedCode = code;
        lastScanTime = currentTime;

        // Gửi request đến server
        eventService.ScanTicket(idEvent, code, new EventService.ResponseCallback() {
            @Override
            public void onSuccess(String success) {
                runOnUiThread(() -> {
                    Toast.makeText(getApplicationContext(), success, Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onFailure(String error) {
                runOnUiThread(() -> {
                    Toast.makeText(getApplicationContext(), error, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        barcodeView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        barcodeView.pause();
    }
}
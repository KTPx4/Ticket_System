package com.example.ticketbooking.ticket;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ticketbooking.R;
import com.example.ticketbooking.ticket.adapter.edit.MemberAdapter;

import java.util.ArrayList;
import java.util.List;

import model.account.Account;
import model.ticket.MyPending;
import modules.LocalStorageManager;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import services.AccountService;
import services.OrderService;
import services.TicketService;

public class EditPendingActivity extends AppCompatActivity implements View.OnClickListener {
    String idBuyTicket;
    String typePayment;
    String myId;

    List<Account> listMember = new ArrayList<>();
    private MemberAdapter memberAdapter;
    TextView tvName;
    ProgressBar loading, waiting;
    Spinner spType;
    RecyclerView recyListMember;
    Button btnSave, btnClose;
    ImageButton btnAdd;
    LinearLayout layoutMain;
    TicketService ticketService;
    private MyPending myPending;
    private AccountService accountService;
    private OrderService orderService;
    private LocalStorageManager localStorageManager;
    private Boolean isWaiting = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.ticket_activity_edit_pending);
        accountService = new AccountService(this);
        orderService = new OrderService(this);
        ticketService = new TicketService(this);
        localStorageManager = new LocalStorageManager(this);
        myId = localStorageManager.getIdUser();
        getFromIntent();

    }

    void getFromIntent()
    {
        Intent intent = getIntent();
        idBuyTicket = intent.getStringExtra("idBuyTicket");

        if(idBuyTicket == null || idBuyTicket.isEmpty())
        {
            Toast.makeText(this, "Không có id thông tin hóa đơn", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        ticketService.GetPendingById(idBuyTicket, new TicketService.PendingCallback() {
            @Override
            public void onSuccess(MyPending pendings) {
                runOnUiThread(()->{
                    myPending = pendings;
                    initView();
                });
            }

            @Override
            public void onFailure(String error) {
                runOnUiThread(()->{
                    Toast.makeText(getApplicationContext(), error, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
    void initView()
    {

        if(myPending == null || myPending.get_id().isEmpty())
        {
            Toast.makeText(this, "Không có thông tin hóa đơn", Toast.LENGTH_SHORT).show();
//            finish();
            return;
        }
        loading = findViewById(R.id.loading);
        layoutMain = findViewById(R.id.layoutMain);
        tvName = findViewById(R.id.tvName);
        spType = findViewById(R.id.spType);
        recyListMember = findViewById(R.id.listMember);
        btnSave = findViewById(R.id.btnSave);
        btnClose = findViewById(R.id.btnClose);
        btnAdd = findViewById(R.id.btnAdd);
        waiting =findViewById(R.id.waiting);
        tvName.setText(myPending.getEvent().getName());
        loading.setVisibility(View.GONE);
        layoutMain.setVisibility(View.VISIBLE);
        btnSave.setOnClickListener(this);
        btnClose.setOnClickListener(this);
        btnAdd.setOnClickListener(this);
        initSpinnerType();
        initListMember();
    }

    private void initSpinnerType()
    {

        List<Pair<String, String>> typeList = new ArrayList<>();
        typeList.add(new Pair<>("all", "Tất cả"));
        typeList.add(new Pair<>("single", "Từng thành viên"));

        int pos = typeList.get(0).first.equals(myPending.getTypePayment()) ? 0 : 1;
        typePayment = typeList.get(pos).first;

        // Chuyển đổi danh sách thành danh sách String chỉ chứa "display"
        List<String> displayList = new ArrayList<>();
        for (Pair<String, String> pair : typeList) {
            displayList.add(pair.second); // Lấy giá trị hiển thị
        }
        // Tạo ArrayAdapter từ danh sách "display"
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, // Context
                android.R.layout.simple_spinner_item, // Layout cho mỗi item
                displayList // Danh sách hiển thị
        );

        // Cấu hình dropdown layout
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Gắn adapter cho Spinner
        spType.setAdapter(adapter);
        spType.setSelection(pos);
        spType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Lấy cặp {value, display} tương ứng từ typeList
                Pair<String, String> selectedType = typeList.get(position);

                // Lấy giá trị "value" và "display"
                String value = selectedType.first;
                String display = selectedType.second;

                typePayment = value;

                Log.d("Spinner", "Selected Value: " + value + ", Display: " + display);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Xử lý khi không có gì được chọn (nếu cần)
            }
        });
    }

    private void initListMember() {
        List<Account> listMember = myPending.getMembers();

        recyListMember.setLayoutManager(new LinearLayoutManager(this));
        memberAdapter = new MemberAdapter(listMember, this);
        recyListMember.setAdapter(memberAdapter);
    }

    private void showAddMemberDialog() {
        // Tạo View cho dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_member, null);
        EditText etEmail = dialogView.findViewById(R.id.etEmail);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);
        Button btnSave = dialogView.findViewById(R.id.btnSave);

        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        dialog.show();

        // Xử lý sự kiện khi nhấn Cancel
        btnCancel.setOnClickListener(v -> dialog.dismiss());

        // Xử lý sự kiện khi nhấn Save
        btnSave.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            if (email.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập email", Toast.LENGTH_SHORT).show();
                return;
            }

            // Gọi service để tìm tài khoản
            accountService.findByEmail(email, new AccountService.OnFindByEmailCallback() {
                @Override
                public void onSuccess(Account account) {
                    runOnUiThread(() -> {
                        // Kiểm tra trùng lặp
                        boolean isDuplicate = listMember.stream().anyMatch(member -> member.getEmail().equals(account.getEmail()));
                        if (isDuplicate || account.get_id().equals(myId)) {
                            Toast.makeText(getApplicationContext(), "Thành viên đã tồn tại", Toast.LENGTH_SHORT).show();
                        } else {
                            // Thêm tài khoản vào danh sách
                            // Thêm tài khoản vào danh sách thông qua adapter
                            memberAdapter.addMember(account);
                            Toast.makeText(getApplicationContext(), "Thêm thành viên thành công", Toast.LENGTH_SHORT).show();
                        }
                        dialog.dismiss();
                    });
                }

                @Override
                public void onFailure(String error) {
                    runOnUiThread(() -> Toast.makeText(getApplicationContext(), error, Toast.LENGTH_SHORT).show());
                }
            });
        });
    }
    void setWaiting()
    {
        waiting.setVisibility(isWaiting ? View.VISIBLE : View.GONE);
        btnSave.setVisibility(!isWaiting ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id == R.id.btnClose)
        {
            this.finish();
        }
        else if(id == R.id.btnSave)
        {
            if(isWaiting)
            {
                return;
            }
            isWaiting = true;
            setWaiting();
            listMember = memberAdapter.getListMember();
            List<String> listId = new ArrayList<>();

            listMember.forEach(account -> {
                listId.add(account.get_id());
            });

            orderService.UpdateOrder(myPending.get_id(), typePayment, listId, new OrderService.UpdateCallback() {
                @Override
                public void onSuccess(MyPending myPending) {
                    runOnUiThread(()->{
                        Toast.makeText(getApplicationContext(), "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("idBuyTicket", myPending.get_id());
                        setResult(RESULT_OK, resultIntent);
                        finish();
                        isWaiting = false;
                        setWaiting();
                    });
                }

                @Override
                public void onFailure(String error) {
                    runOnUiThread(()->{
                        Toast.makeText(getApplicationContext(), error , Toast.LENGTH_SHORT).show();
                        isWaiting = false;
                        setWaiting();
                    });
                }
            });


        }
        else if(id == R.id.btnAdd)
        {
            showAddMemberDialog();
        }


    }

}
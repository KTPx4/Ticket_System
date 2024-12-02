package services;

import android.content.Context;
import android.util.Log;

import com.example.ticketbooking.R;


import org.json.JSONObject;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AccountService {
    private Context context;
    String SERVER = "";
    private final OkHttpClient client;
    public AccountService(Context context) {
        client = new OkHttpClient();
        this.context = context;
        this.SERVER = context.getString(R.string.server_url);
    }

    public void registerAccount(String email, String password, OnRegisterCallback callback) {
        new Thread(() -> {
            try {
                // Kiểm tra đầu vào
                if (email == null || email.isEmpty() || password == null || password.isEmpty()) {
                    callback.onFailure("Email hoặc mật khẩu không được để trống");
                    return;
                }
                // Tạo RequestBody dạng form
                RequestBody formBody = new FormBody.Builder()
                        .add("email", email)
                        .add("pass", password)
                        .build();

                // URL API
                String url = SERVER + "/api/v1/account";

                // Tạo Request
                Request request = new Request.Builder()
                        .url(url)
                        .post(formBody) // Sử dụng formBody thay vì JSON
                        .build();

                // Gửi Request và nhận Response
                Response response = client.newCall(request).execute();

                // Log thông tin phản hồi
                StringBuilder logMessage = new StringBuilder();
                logMessage.append("Status Code: ").append(response.code()).append("\n");
                logMessage.append("Headers: ").append(response.headers()).append("\n");
                logMessage.append("Body: ");
                String responseBody = response.body().string();
                if (response.body() != null) {
                    logMessage.append(responseBody);
                } else {
                    logMessage.append("No body");
                }

                // Ghi log
                Log.d("HTTP Response", logMessage.toString());

                // Xử lý kết quả
                JSONObject jsonResponse = new JSONObject(responseBody);

                if (response.isSuccessful()) {
                    String token  =jsonResponse.optString("data", "");
                    callback.onSuccess(token);

                } else {
                    if(response.code() == 400)
                    {

                        String mess  =jsonResponse.optString("message", "Đăng ký thất bại!");
                        Log.d("Message Response", mess);
                        callback.onFailure(mess);
                    }
                    else{
                        callback.onFailure("Đăng ký thất bại! Mã lỗi: " + response.code());
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
                callback.onFailure("Đã xảy ra lỗi: " + e.getMessage());
            }
        }).start();
    }

    public void loginUser(String email, String password, OnRegisterCallback callback) {

    }

    public void loginStaff(String user, String password, OnRegisterCallback callback) {

    }

    public void verifyStaff(String token, OnRegisterCallback callback) {

    }
    public void verifyUser(String token, OnRegisterCallback callback) {

    }

    // Interface callback để xử lý kết quả
    public interface OnRegisterCallback {
        void onSuccess(String message);

        void onFailure(String error);
    }
}

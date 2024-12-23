package services;

import android.content.Context;
import android.util.Log;

import com.example.ticketbooking.R;
import com.google.gson.Gson;


import org.json.JSONObject;

import java.util.List;

import model.account.Account;
import model.event.Event;
import modules.LocalStorageManager;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import services.response.account.RSGetJoin;
import services.response.account.ResponseAccount;
import services.response.ticket.ResponseMyTicket;
import services.response.ticket.ResponsePending;

public class AccountService {
    private Context context;
    String SERVER = "";
    private final OkHttpClient client;
    private LocalStorageManager localStorageManager;
    private String myToken ;
    public AccountService(Context context) {
        client = new OkHttpClient();
        this.context = context;
        this.SERVER = context.getString(R.string.server_url);
        this.localStorageManager = new LocalStorageManager(context);
        myToken = localStorageManager.getLoginToken();
    }

    public void registerAccount(String email, String password, ResponseCallback callback) {
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

                }
                else {
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

    public void loginUser(String email, String password, ResponseCallback callback) {
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
                String url = SERVER + "/api/v1/account/login";

                // Tạo Request
                Request request = new Request.Builder()
                        .url(url)
                        .post(formBody)
                        .build();

                // Gửi Request và nhận Response
                Response response = client.newCall(request).execute();
                String responseBody = response.body() != null ? response.body().string() : "";

                // Xử lý kết quả
                JSONObject jsonResponse = new JSONObject(responseBody);

                // Log thông tin phản hồi
                StringBuilder logMessage = new StringBuilder();
                logMessage.append("Status Code: ").append(response.code()).append("\n");
                logMessage.append("Headers: ").append(response.headers()).append("\n");
                logMessage.append("Body: ");
                if (response.body() != null) {
                    logMessage.append(responseBody);
                } else {
                    logMessage.append("No body");
                }

                // Ghi log
                Log.d("HTTP Response", logMessage.toString());

                if (response.isSuccessful()) {
                    String token = jsonResponse.optString("data", ""); // Token từ server
                    verifyUser(token, new OnVerifyCallback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onFailure(String error) {

                        }
                    });
                    callback.onSuccess(token);
                } else {
                    String error = jsonResponse.optString("message", "Đăng nhập thất bại!");
                    callback.onFailure(error);
                }
            } catch (Exception e) {
                e.printStackTrace();
                callback.onFailure("Đã xảy ra lỗi: " + e.getMessage());
            }
        }).start();
    }


    public void loginStaff(String user, String password, ResponseCallback callback) {
        new Thread(() -> {
            try {
                // Kiểm tra đầu vào
                if (user == null || user.isEmpty() || password == null || password.isEmpty()) {
                    callback.onFailure("Email hoặc mật khẩu không được để trống");
                    return;
                }

                // Tạo RequestBody dạng form
                RequestBody formBody = new FormBody.Builder()
                        .add("user", user)
                        .add("pass", password)
                        .build();

                // URL API
                String url = SERVER + "/api/v1/staff/login";

                // Tạo Request
                Request request = new Request.Builder()
                        .url(url)
                        .post(formBody)
                        .build();

                // Gửi Request và nhận Response
                Response response = client.newCall(request).execute();
                String responseBody = response.body() != null ? response.body().string() : "";

                // Xử lý kết quả
                JSONObject jsonResponse = new JSONObject(responseBody);

                // Log thông tin phản hồi
                StringBuilder logMessage = new StringBuilder();
                logMessage.append("Status Code: ").append(response.code()).append("\n");
                logMessage.append("Headers: ").append(response.headers()).append("\n");
                logMessage.append("Body: ");
                if (response.body() != null) {
                    logMessage.append(responseBody);
                } else {
                    logMessage.append("No body");
                }

                // Ghi log
                Log.d("HTTP Response", logMessage.toString());

                if (response.isSuccessful()) {
                    String token = jsonResponse.optString("data", ""); // Token từ server
                    verifyStaff(token, new ResponseCallback() {
                        @Override
                        public void onSuccess(String message) {

                        }

                        @Override
                        public void onFailure(String error) {

                        }
                    });
                    callback.onSuccess(token);
                } else {
                    String error = jsonResponse.optString("message", "Đăng nhập thất bại!");
                    callback.onFailure(error);
                }
            } catch (Exception e) {
                e.printStackTrace();
                callback.onFailure("Đã xảy ra lỗi: " + e.getMessage());
            }
        }).start();
    }

    public void verifyStaff(String token, ResponseCallback callback) {
        new Thread(() -> {
            try {
                String url = SERVER + "/api/v1/staff/verify";

                // Create request with Bearer token
                Request request = new Request.Builder()
                        .url(url)
                        .header("Authorization", "Bearer " + token)
                        .build();

                // Send request and get response
                Response response = client.newCall(request).execute();

                String responseBody = response.body() != null ? response.body().string() : "";
// Xử lý kết quả
                JSONObject jsonResponse = new JSONObject(responseBody);

                if (response.isSuccessful()) {
                    String id = jsonResponse.optString("data", ""); // Token từ server
                    localStorageManager.saveIdUser(id);
                    callback.onSuccess("Xác thực thành công");
                } else {
                    callback.onFailure("Token is not valid for user.");
                }
            } catch (Exception e) {
                e.printStackTrace();
                callback.onFailure("Verification failed: " + e.getMessage());
            }
        }).start();
    }

    public void verifyUser(String token, OnVerifyCallback callback) {
        new Thread(() -> {
            try {
                String url = SERVER + "/api/v1/account/verify";

                // Create request with Bearer token
                Request request = new Request.Builder()
                        .url(url)
                        .header("Authorization", "Bearer " + token)
                        .build();

                // Send request and get response
                Response response = client.newCall(request).execute();

                String responseBody = response.body() != null ? response.body().string() : "";
// Xử lý kết quả
                JSONObject jsonResponse = new JSONObject(responseBody);

                if (response.isSuccessful()) {
                    String id = jsonResponse.optString("data", ""); // Token từ server
                    localStorageManager.saveIdUser(id);
                    callback.onSuccess();
                } else {
                    callback.onFailure("Token is not valid for user.");
                }
            } catch (Exception e) {
                e.printStackTrace();
                callback.onFailure("Verification failed: " + e.getMessage());
            }
        }).start();
    }

    public void findByEmail(String email,OnFindByEmailCallback callback )
    {
        if(email == null || email.isEmpty())
        {
            callback.onFailure("Không có email");
            return;
        }

        new Thread(() -> {
            try {
                String url = SERVER + "/api/v1/account/find?email=" + email;

                // Create request with Bearer token
                Request request = new Request.Builder()
                        .url(url)
                        .header("Authorization", "Bearer " + myToken)
                        .build();

                // Send request and get response
                Response response = client.newCall(request).execute();

                String responseBody = response.body().string();
                Gson gson = new Gson();
                ResponseAccount responseData = gson.fromJson(responseBody, ResponseAccount.class);
                if (response.isSuccessful()) {

                    Log.d("GetMyTicket", "Response Body: " + responseBody);


                    Log.d("ParsedData", gson.toJson(responseData));

                    callback.onSuccess(responseData.getData());

                } else {
                    callback.onFailure(responseData.getMessage());
                }
            } catch (Exception e) {
                e.printStackTrace();
                callback.onFailure("Tải thông tin thất bại" + e.getMessage());
            }
        }).start();
    }

    public void GetJoinEvent(GetJoiCallBack callBack)
    {
        new Thread(() -> {
            try {
                String url = SERVER + "/api/v1/account/event";

                // Create request with Bearer token
                Request request = new Request.Builder()
                        .url(url)
                        .header("Authorization", "Bearer " + myToken)
                        .build();

                // Send request and get response
                Response response = client.newCall(request).execute();
                String responseBody = response.body().string();

                Log.d("GetJoinEvent", "Response Body: " + responseBody);

                Gson gson = new Gson();
                RSGetJoin responseData = gson.fromJson(responseBody, RSGetJoin.class);

                Log.d("ParsedData", gson.toJson(responseData));

                if (response.isSuccessful()) {
                    callBack.onSuccess(responseData.getData());
                }
                else {
                    callBack.onFailure(responseData.getMessage());
                }
            } catch (Exception e) {
                e.printStackTrace();
                callBack.onFailure("Tải dữ liệu thất bại" + e.getMessage());
            }
        }).start();
    }



    // Interface callback để xử lý kết quả
    public interface GetJoiCallBack{
        public void onSuccess(List<Event> listData);
        public void onFailure(String err);
    }

    public interface ResponseCallback {
        void onSuccess(String message);

        void onFailure(String error);
    }

    public interface OnVerifyCallback {
        void onSuccess();
        void onFailure(String error);
    }

    public interface OnFindByEmailCallback {
        void onSuccess(Account account);
        void onFailure(String error);
    }
}

package services;

import android.content.Context;
import android.util.Log;

import com.example.ticketbooking.R;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import modules.LocalStorageManager;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;

public class AccountHomeService {
    private Context context;
    private static String SERVER;
    private final OkHttpClient client;
    private final LocalStorageManager localStorageManager;
    private String token;

    public AccountHomeService(Context context)
    {
        this.context = context;
        client = new OkHttpClient();
        this.SERVER = context.getString(R.string.server_url);
        localStorageManager = new LocalStorageManager(context);
        this.token = localStorageManager.getLoginToken();
    }

    public void getAccountInfo(final ResponseCallback callback) {
        String url = SERVER + "/api/v1/account"; // API endpoint for account info

        // Create a request with Authorization header
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + token) // Add Bearer token
                .build();

        // Make the request in a background thread
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Response response = client.newCall(request).execute();
                    if (response.isSuccessful()) {
                        String responseBody = response.body().string();
                        JSONObject jsonResponse = new JSONObject(responseBody);

                        String message = jsonResponse.optString("message");
                        if (message != null && message.equals("Lấy thông tin thành công")) {
                            // Handle the successful response
                            callback.onSuccess(responseBody);
                        } else {
                            // If message is not successful
                            callback.onFailure("Failed to get account information");
                        }
                    } else {
                        callback.onFailure("Request failed: " + response.message());
                    }
                } catch (Exception e) {
                    Log.e("AccountHomeService", "Error retrieving account info", e);
                    callback.onFailure("Error: " + e.getMessage());
                }
            }
        }).start();
    }

    public void deleteAllHistory(final ResponseCallback callback) {
        String url = SERVER + "/api/v1/account/history"; // API endpoint for deleting all history

        // Create a DELETE request with Authorization header
        Request request = new Request.Builder()
                .url(url)
                .delete() // Specify DELETE method
                .addHeader("Authorization", "Bearer " + token) // Add Bearer token
                .build();

        // Make the request in a background thread
        new Thread(() -> {
            try {
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    JSONObject jsonResponse = new JSONObject(responseBody);

                    String message = jsonResponse.optString("message");
                    if (message != null && message.equals("Xóa lịch sử thành công")) {
                        // Handle the successful response
                        callback.onSuccess(responseBody);
                    } else {
                        // If message is not successful
                        callback.onFailure("Failed to delete history");
                    }
                } else {
                    callback.onFailure("Request failed: " + response.message());
                }
            } catch (Exception e) {
                Log.e("AccountHomeService", "Error deleting history", e);
                callback.onFailure("Error: " + e.getMessage());
            }
        }).start();
    }

    public void updateAccount(String name, String address, final ResponseCallback callback) {
        String url = SERVER + "/api/v1/account"; // API endpoint for updating account info

        // Tạo request body với name và address
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("name", name);
            jsonObject.put("address", address);
        } catch (JSONException e) {
            Log.e("AccountHomeService", "Error creating JSON body", e);
            callback.onFailure("Error: " + e.getMessage());
            return;
        }

        RequestBody body = RequestBody.create(jsonObject.toString(), MediaType.parse("application/json"));

        // Tạo request PATCH với Authorization header
        Request request = new Request.Builder()
                .url(url)
                .patch(body) // Specify PATCH method
                .addHeader("Authorization", "Bearer " + token) // Add Bearer token
                .build();

        // Thực hiện request trong background thread
        new Thread(() -> {
            try {
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    JSONObject jsonResponse = new JSONObject(responseBody);

                    String message = jsonResponse.optString("message");
                    if (message != null && message.equals("Chỉnh sửa thông tin thành công")) {
                        // Xử lý phản hồi thành công
                        callback.onSuccess(responseBody);
                    } else {
                        // Nếu message không thành công
                        callback.onFailure("Failed to update account information");
                    }
                } else {
                    callback.onFailure("Request failed: " + response.message());
                }
            } catch (Exception e) {
                Log.e("AccountHomeService", "Error updating account", e);
                callback.onFailure("Error: " + e.getMessage());
            }
        }).start();
    }

    public void updateImage(File imageFile, final ResponseCallback callback) {
        String url = SERVER + "/api/v1/account/image"; // API endpoint for updating the image

        // Create the request body for the image
        RequestBody imageBody = RequestBody.create(imageFile, MediaType.parse("image/*"));

        // Create the multipart form body, which includes the image
        MultipartBody.Part imagePart = MultipartBody.Part.createFormData("image", imageFile.getName(), imageBody);

        // If you need additional fields in the form data, you can add them here
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        builder.addPart(imagePart);

        // Create a multipart request body
        RequestBody requestBody = builder.build();

        // Create the request with Authorization header and the multipart body
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + token) // Add Bearer token
                .post(requestBody) // Use POST for multipart data
                .build();

        // Perform the request in a background thread
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Execute the request
                    Response response = client.newCall(request).execute();
                    if (response.isSuccessful()) {
                        String responseBody = response.body().string();
                        JSONObject jsonResponse = new JSONObject(responseBody);

                        String message = jsonResponse.optString("message");
                        if (message != null && message.equals("Thay đổi thông tin thành công")) {
                            // Handle the successful response
                            callback.onSuccess(responseBody);
                        } else {
                            // If message is not successful
                            callback.onFailure("Failed to update image");
                        }
                    } else {
                        callback.onFailure("Request failed: " + response.message());
                    }
                } catch (Exception e) {
                    Log.e("AccountHomeService", "Error updating image", e);
                    callback.onFailure("Error: " + e.getMessage());
                }
            }
        }).start();
    }



    public interface ResponseCallback {
        void onSuccess(String success);
        void onFailure(String error);
    }


}

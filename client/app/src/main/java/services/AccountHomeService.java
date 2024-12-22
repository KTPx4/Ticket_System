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

    public void resetAccount(String email, final ResponseCallback callback) {
        String url = SERVER + "/api/v1/account/reset"; // API endpoint for resetting the account

        // Create the request body with email
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("email", email); // Add email to the request body
        } catch (JSONException e) {
            Log.e("AccountHomeService", "Error creating JSON body for reset", e);
            callback.onFailure("Error: " + e.getMessage());
            return;
        }

        // Create request body for the POST request
        RequestBody body = RequestBody.create(jsonObject.toString(), MediaType.parse("application/json"));

        // Create the request with the Authorization header (optional, depending on your API requirements)
        Request request = new Request.Builder()
                .url(url)
                .post(body) // Use POST method for sending the data
                .build();

        // Execute the request in a background thread
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Response response = client.newCall(request).execute();
                    if (response.isSuccessful()) {
                        String responseBody = response.body().string();
                        JSONObject jsonResponse = new JSONObject(responseBody);

                        // Check if response message indicates success
                        String message = jsonResponse.optString("message");
                        if (message != null && message.equals("Reset successful")) {
                            // Handle the successful response
                            callback.onSuccess(responseBody);
                        } else {
                            // If message is not successful
                            callback.onFailure("Failed to reset account");
                        }
                    } else {
                        callback.onFailure("Request failed: " + response.message());
                    }
                } catch (Exception e) {
                    Log.e("AccountHomeService", "Error resetting account", e);
                    callback.onFailure("Error: " + e.getMessage());
                }
            }
        }).start();
    }

    public void getEventById(String eventId, final ResponseCallback callback) {
        String url = SERVER + "/api/v1/event/" + eventId;

        // Create a request with Authorization header (if needed)
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
                        if (message != null && message.equals("Lấy dữ liệu sự kiện thành công")) {
                            // Handle the successful response
                            callback.onSuccess(responseBody);
                        } else {
                            // If message is not successful
                            callback.onFailure("Failed to retrieve event data");
                        }
                    } else {
                        callback.onFailure("Request failed: " + response.message());
                    }
                } catch (Exception e) {
                    Log.e("AccountHomeService", "Error retrieving event data", e);
                    callback.onFailure("Error: " + e.getMessage());
                }
            }
        }).start();
    }


    public void getArtistById(String artistId, final ResponseCallback callback) {
        String url = SERVER + "/api/v1/artist/" + artistId; // API endpoint for artist info

        // Create a request without an Authorization header
        Request request = new Request.Builder()
                .url(url)
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
                        if (message != null && message.equals("Lấy thành công nghệ sĩ")) {
                            // Handle the successful response
                            callback.onSuccess(responseBody);
                        } else {
                            // If message is not successful
                            callback.onFailure("Failed to retrieve artist data");
                        }
                    } else {
                        callback.onFailure("Request failed: " + response.message());
                    }
                } catch (Exception e) {
                    Log.e("AccountHomeService", "Error retrieving artist data", e);
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

    public void updatePassword(String oldPass, String newPass, final ResponseCallback callback) {
        String url = SERVER + "/api/v1/account/password"; // API endpoint for updating password

        // Create the FormBody for the POST request
        RequestBody formBody = new FormBody.Builder()
                .add("oldPass", oldPass)
                .add("newPass", newPass)
                .build();

        // Create the request with Authorization header
        Request request = new Request.Builder()
                .url(url)
                .put(formBody) // Specify PUT method with form body
                .addHeader("Authorization", "Bearer " + token) // Add Bearer token
                .build();

        // Perform the request in a background thread
        new Thread(() -> {
            try {
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    JSONObject jsonResponse = new JSONObject(responseBody);

                    String message = jsonResponse.optString("message");
                    if (message != null && message.equals("Cập nhật mật khẩu thành công")) {
                        // Handle the successful response
                        callback.onSuccess(responseBody);
                    } else {
                        // If message is not successful
                        callback.onFailure("Failed to update password");
                    }
                } else {
                    callback.onFailure("Request failed: " + response.message());
                }
            } catch (Exception e) {
                Log.e("AccountHomeService", "Error updating password", e);
                callback.onFailure("Error: " + e.getMessage());
            }
        }).start();
    }

    public void FollowArtist(String artistId, ResponseCallback callback) {
        new Thread(() -> {
            try {
                String url = SERVER + "/api/v1/artist/" + artistId + "/follow";

                // Create request with Bearer token, no body is added
                Request request = new Request.Builder()
                        .url(url)
                        .header("Authorization", "Bearer " + token)
                        .post(RequestBody.create(null, new byte[0])) // Empty body
                        .build();

                // Send request and get response
                Response response = client.newCall(request).execute();

                try {
                    String responseBody = response.body() != null ? response.body().string() : "";
                    Log.d("FollowArtist", "Response Body: " + responseBody);

                    // Handle the response
                    JSONObject jsonResponse = new JSONObject(responseBody);
                    String message = jsonResponse.optString("message", "Theo dõi thất bại!");

                    if (response.isSuccessful()) {
                        callback.onSuccess(message);
                    } else {
                        callback.onFailure("Failed: " + message);
                    }
                } finally {
                    // Ensure response is closed
                    response.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
                callback.onFailure("Theo dõi nghệ sĩ thất bại: " + e.getMessage());
            }
        }).start();
    }

    public void UnFollowArtist(String artistId, ResponseCallback callback) {
        new Thread(() -> {
            try {
                String url = SERVER + "/api/v1/artist/" + artistId + "/follow";

                // Create request with Bearer token, no body is added
                Request request = new Request.Builder()
                        .url(url)
                        .header("Authorization", "Bearer " + token)
                        .delete()
                        .build();

                // Send request and get response
                Response response = client.newCall(request).execute();

                try {
                    String responseBody = response.body() != null ? response.body().string() : "";
                    Log.d("FollowArtist", "Response Body: " + responseBody);

                    // Handle the response
                    JSONObject jsonResponse = new JSONObject(responseBody);
                    String message = jsonResponse.optString("message", "Bỏ Theo dõi thất bại!");

                    if (response.isSuccessful()) {
                        callback.onSuccess(message);
                    } else {
                        callback.onFailure("Failed: " + message);
                    }
                } finally {
                    // Ensure response is closed
                    response.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
                callback.onFailure("Bỏ Theo dõi nghệ sĩ thất bại: " + e.getMessage());
            }
        }).start();
    }

    public void FollowEvent(String eventId, EventService.ResponseCallback callback) {
        new Thread(() -> {
            try {
                // Construct the URL for following the event
                String url = SERVER + "/api/v1/event/" + eventId + "/follow";

                // Create a POST request with Bearer token, but no body
                Request request = new Request.Builder()
                        .url(url)
                        .header("Authorization", "Bearer " + token)
                        .post(RequestBody.create(null, new byte[0])) // Empty body
                        .build();

                // Send the request and get the response
                Response response = client.newCall(request).execute();

                try {
                    String responseBody = response.body() != null ? response.body().string() : "";
                    Log.d("FollowEvent", "Response Body: " + responseBody);

                    // Process the response
                    JSONObject jsonResponse = new JSONObject(responseBody);
                    String message = jsonResponse.optString("message", "Follow event failed!");

                    if (response.isSuccessful()) {
                        callback.onSuccess(message); // Notify success
                    } else {
                        callback.onFailure("Failed: " + message); // Notify failure
                    }
                } finally {
                    // Ensure the response is closed
                    response.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
                callback.onFailure("Follow event failed: " + e.getMessage());
            }
        }).start();
    }

    public void UnFollowEvent(String eventId, EventService.ResponseCallback callback) {
        new Thread(() -> {
            try {
                // Construct the URL for following the event
                String url = SERVER + "/api/v1/event/" + eventId + "/follow";

                // Create a POST request with Bearer token, but no body
                Request request = new Request.Builder()
                        .url(url)
                        .header("Authorization", "Bearer " + token)
                        .delete()
                        .build();

                // Send the request and get the response
                Response response = client.newCall(request).execute();

                try {
                    String responseBody = response.body() != null ? response.body().string() : "";
                    Log.d("FollowEvent", "Response Body: " + responseBody);

                    // Process the response
                    JSONObject jsonResponse = new JSONObject(responseBody);
                    String message = jsonResponse.optString("message", "UnFollow event failed!");

                    if (response.isSuccessful()) {
                        callback.onSuccess(message); // Notify success
                    } else {
                        callback.onFailure("Failed: " + message); // Notify failure
                    }
                } finally {
                    // Ensure the response is closed
                    response.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
                callback.onFailure("UnFollow event failed: " + e.getMessage());
            }
        }).start();
    }

    public interface ResponseCallback {
        void onSuccess(String success);
        void onFailure(String error);
    }


}

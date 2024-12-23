package services;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.example.ticketbooking.R;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import modules.FileUtils;
import modules.LocalStorageManager;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import services.response.coupon.RSGetCoupon;
import services.response.history.RSGetAll;
import services.response.history.RSMyPost;

public class HistoryService {
    private Context context;
    private static String SERVER;
    private final OkHttpClient client;
    private final LocalStorageManager localStorageManager;
    private String token;

    public HistoryService(Context context)
    {
        this.context = context;
        client = new OkHttpClient();
        this.SERVER = context.getString(R.string.server_url);
        localStorageManager = new LocalStorageManager(context);
        this.token = localStorageManager.getLoginToken();
    }

    public void GetAllPost(String id, int page,GetAllCallBack callback)
    {
        if(id.isEmpty())
        {
            callback.onFailure("Vui lòng cung cấp id sự kiện");
            return;
        }

        if(page < 0 )
        {
            callback.onFailure("page phải > 0");
            return;
        }

        new Thread(() -> {
            try {
                String url = SERVER + "/api/v1/history/" + id + "?page=" + page;

                // Create request with Bearer token
                Request request = new Request.Builder()
                        .url(url)
                        .header("Authorization", "Bearer " + token)
                        .build();

                // Send request and get response
                Response response = client.newCall(request).execute();
                String responseBody = response.body().string();
                Gson gson = new Gson();
                RSGetAll responseData = gson.fromJson(responseBody, RSGetAll.class);

                if (response.isSuccessful()) {

                    Log.d("Get ALL", "Response Body: " + responseBody);

                    Log.d("ParsedData", gson.toJson(responseData));
                    callback.onSuccess(responseData);

                }
                else {
                    callback.onFailure(responseData.getMessage());
                }
            } catch (Exception e) {
                e.printStackTrace();
                callback.onFailure("Tải dữ liệu thất bại" + e.getMessage());
            }
        }).start();
    }

   public void GetMyPost(String id,GetMyPostCallBack callback)
    {
        if(id.isEmpty())
        {
            callback.onFailure("Vui lòng cung cấp id sự kiện");
            return;
        }
        new Thread(() -> {
            try {
                String url = SERVER + "/api/v1/history/" + id +"/me" ;

                // Create request with Bearer token
                Request request = new Request.Builder()
                        .url(url)
                        .header("Authorization", "Bearer " + token)
                        .build();

                // Send request and get response
                Response response = client.newCall(request).execute();
                String responseBody = response.body().string();
                Gson gson = new Gson();

                RSMyPost responseData = gson.fromJson(responseBody, RSMyPost.class);

                if (response.isSuccessful()) {

                    Log.d("Get Coupon", "Response Body: " + responseBody);

                    Log.d("ParsedData", gson.toJson(responseData));
                    callback.onSuccess(responseData);

                }
                else {
                    callback.onFailure(responseData.getMessage());
                }
            } catch (Exception e) {
                e.printStackTrace();
                callback.onFailure("Tải dữ liệu thất bại" + e.getMessage());
            }
        }).start();
    }

    public void DelMyPost(String id, GetMyPostCallBack callback)
    {
        if(id == null || id.isEmpty())
        {
            callback.onFailure("Thiếu id bài viết");
            return;
        }
        new Thread(() -> {
            try {
                String url = SERVER + "/api/v1/history/" + id;

                // Create request with Bearer token
                Request request = new Request.Builder()
                        .url(url)
                        .header("Authorization", "Bearer " + token)
                        .delete(RequestBody.create("", MediaType.parse("application/json")))
                        .build();

                // Send request and get response
                Response response = client.newCall(request).execute();
                String responseBody = response.body().string();
                Gson gson = new Gson();

                RSMyPost responseData = gson.fromJson(responseBody, RSMyPost.class);

                if (response.isSuccessful()) {

                    Log.d("Get Coupon", "Response Body: " + responseBody);

                    Log.d("ParsedData", gson.toJson(responseData));
                    callback.onSuccess(responseData);

                }
                else {
                    callback.onFailure(responseData.getMessage());
                }
            } catch (Exception e) {
                e.printStackTrace();
                callback.onFailure("Tải dữ liệu thất bại" + e.getMessage());
            }
        }).start();
    }

    public void Post(String eventId, int rating, String comment, ArrayList<Uri> files, GetMyPostCallBack callback) {
        new Thread(() -> {
            try {

                String url = SERVER + "/api/v1/history";

                OkHttpClient client = new OkHttpClient.Builder()
                        .connectTimeout(30, TimeUnit.SECONDS)
                        .readTimeout(30, TimeUnit.SECONDS)
                        .build();

                // Build multipart form data
                MultipartBody.Builder multipartBuilder = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("event", eventId)
                        .addFormDataPart("rating", String.valueOf(rating))
                        .addFormDataPart("comment", comment);

                // Add files as individual parts
                for (Uri uri : files) {
                    String filePath = FileUtils.getPathFromUri(context, uri);
                    if (filePath != null) {
                        File file = new File(filePath);
                        multipartBuilder.addFormDataPart(
                                "files", // Field name expected by server (array field)
                                file.getName(),
                                RequestBody.create(file, MediaType.parse(getMimeType(uri)))
                        );
                    }
                }

                RequestBody requestBody = multipartBuilder.build();

                // Create request with Bearer token if required
                Request request = new Request.Builder()
                        .url(url)
                        .post(requestBody)
                        .addHeader("Authorization", "Bearer " + token) // Replace with actual token
                        .build();

                // Execute request
                Call call = client.newCall(request);
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                        callback.onFailure("Request failed: " + e.getMessage());
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response.isSuccessful())
                        {
                            String responseBody = response.body().string();
                            Gson gson = new Gson();
                            RSMyPost responseData = gson.fromJson(responseBody, RSMyPost.class);
                            callback.onSuccess(responseData);

                        }
                        else
                        {
                            String responseBody = response.body().string();
                            Gson gson = new Gson();
                            RSMyPost errorResponse = gson.fromJson(responseBody, RSMyPost.class);
                            callback.onFailure(errorResponse.getMessage());
                        }
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
                callback.onFailure("Tải dữ liệu thất bại: " + e.getMessage());
            }
        }).start();
    }

    private String getMimeType(Uri uri) {
        String type = context.getContentResolver().getType(uri);
        return type != null ? type : "application/octet-stream";
    }

    public interface GetMyPostCallBack
    {
        public void onSuccess(RSMyPost result);
        public void onFailure(String error);
    }

    public interface GetAllCallBack{
        public void onSuccess(RSGetAll result);
        public void onFailure(String error);
    }
}

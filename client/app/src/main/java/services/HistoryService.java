package services;

import android.content.Context;
import android.util.Log;

import com.example.ticketbooking.R;
import com.google.gson.Gson;

import modules.LocalStorageManager;
import okhttp3.OkHttpClient;
import okhttp3.Request;
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

   public void GetMyPost(GetMyPostCallBack callback)
    {

        new Thread(() -> {
            try {
                String url = SERVER + "/api/v1/history" ;

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

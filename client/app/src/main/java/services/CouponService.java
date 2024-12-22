package services;

import android.content.Context;
import android.util.Log;

import com.example.ticketbooking.R;
import com.google.gson.Gson;

import java.util.List;

import model.Coupon;
import model.ticket.MyTicket;
import modules.LocalStorageManager;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import services.response.coupon.RSGetCoupon;
import services.response.ticket.ResponseMyTicket;

public class CouponService {

    private Context context;
    private static String SERVER;
    private final OkHttpClient client;
    private final LocalStorageManager localStorageManager;
    private String token;

    public CouponService(Context context)
    {
        this.context = context;
        client = new OkHttpClient();
        this.SERVER = context.getString(R.string.server_url);
        localStorageManager = new LocalStorageManager(context);
        this.token = localStorageManager.getLoginToken();
    }

    public void GetPublicCoupon(CouponCallBack callback)
    {
        new Thread(() -> {
            try {
                String url = SERVER + "/api/v1/coupon";

                // Create request with Bearer token
                Request request = new Request.Builder()
                        .url(url)
                        .header("Authorization", "Bearer " + token)
                        .build();

                // Send request and get response
                Response response = client.newCall(request).execute();
                String responseBody = response.body().string();
                Gson gson = new Gson();
                RSGetCoupon responseData = gson.fromJson(responseBody, RSGetCoupon.class);
                if (response.isSuccessful()) {

                    Log.d("Get Coupon", "Response Body: " + responseBody);

                    Log.d("ParsedData", gson.toJson(responseData));
                    callback.onSuccess(responseData.getData(), responseData.getPoint());

                }
                else {
                    callback.onFailure(responseData.getMessage());
                }
            } catch (Exception e) {
                e.printStackTrace();
                callback.onFailure("Tải vé thất bại" + e.getMessage());
            }
        }).start();
    }

    public void GetMyCoupon(CouponCallBack callback)
    {
        new Thread(() -> {
            try {
                String url = SERVER + "/api/v1/coupon/me";

                // Create request with Bearer token
                Request request = new Request.Builder()
                        .url(url)
                        .header("Authorization", "Bearer " + token)
                        .build();

                // Send request and get response
                Response response = client.newCall(request).execute();
                String responseBody = response.body().string();
                Gson gson = new Gson();
                RSGetCoupon responseData = gson.fromJson(responseBody, RSGetCoupon.class);
                if (response.isSuccessful()) {

                    Log.d("Get My Coupon", "Response Body: " + responseBody);

                    Log.d("ParsedData", gson.toJson(responseData));
                    callback.onSuccess(responseData.getData(), responseData.getPoint());

                }
                else {
                    callback.onFailure(responseData.getMessage());
                }
            } catch (Exception e) {
                e.printStackTrace();
                callback.onFailure("Tải vé thất bại" + e.getMessage());
            }
        }).start();
    }

    public void ExchangeCoupon(long point ,CouponCallBack callback)
    {
        new Thread(() -> {
            try {
                String url = SERVER + "/api/v1/coupon/exchange?type=" + point;

                // Create request with Bearer token
                Request request = new Request.Builder()
                        .url(url)
                        .header("Authorization", "Bearer " + token)
                        .post( RequestBody.create("", MediaType.parse("application/json")))
                        .build();

                // Send request and get response
                Response response = client.newCall(request).execute();
                String responseBody = response.body().string();
                Gson gson = new Gson();
                RSGetCoupon responseData = gson.fromJson(responseBody, RSGetCoupon.class);
                if (response.isSuccessful()) {

                    Log.d("Get Coupon", "Response Body: " + responseBody);

                    Log.d("ParsedData", gson.toJson(responseData));
                    callback.onSuccess(responseData.getData(), responseData.getPoint());

                }
                else {
                    callback.onFailure(responseData.getMessage());
                }
            } catch (Exception e) {
                e.printStackTrace();
                callback.onFailure("Tải vé thất bại" + e.getMessage());
            }
        }).start();
    }

    public interface CouponCallBack {
        void onSuccess(List<Coupon> coupon, long point);
        void onFailure(String error);
    }

}

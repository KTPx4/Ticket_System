package services;
import model.ticket.*;

import android.content.Context;
import android.util.Log;

import com.example.ticketbooking.R;
import com.google.gson.Gson;

import java.util.List;

import modules.LocalStorageManager;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import services.response.ticket.ResponseMyPending;
import services.response.ticket.ResponseMyTicket;
import services.response.ticket.ResponsePending;

public class TicketService {

    private Context context;
    private static String SERVER;
    private final OkHttpClient client;
    private final LocalStorageManager localStorageManager;
    private String token;

    public TicketService(Context context)
    {
        this.context = context;
        client = new OkHttpClient();
        this.SERVER = context.getString(R.string.server_url);
        localStorageManager = new LocalStorageManager(context);
        this.token = localStorageManager.getLoginToken();
    }

    public void GetMyTicket(MyTicketCallback callback)
    {
        new Thread(() -> {
            try {
                String url = SERVER + "/api/v1/account/ticket";

                // Create request with Bearer token
                Request request = new Request.Builder()
                        .url(url)
                        .header("Authorization", "Bearer " + token)
                        .build();

                // Send request and get response
                Response response = client.newCall(request).execute();

                if (response.isSuccessful()) {
                    String responseBody = response.body().string();

                    Log.d("GetMyTicket", "Response Body: " + responseBody);

                    Gson gson = new Gson();
                    ResponseMyTicket responseData = gson.fromJson(responseBody, ResponseMyTicket.class);
                    Log.d("ParsedData", gson.toJson(responseData));
                    callback.onSuccess(responseData.getData());
                } else {
                    callback.onFailure("Token is not valid for user.");
                }
            } catch (Exception e) {
                e.printStackTrace();
                callback.onFailure("Tải vé thất bại" + e.getMessage());
            }
        }).start();
    }

    public void GetMyPending(MyPendingCallback callback)
    {
        new Thread(() -> {
            try {
                String url = SERVER + "/api/v1/account/pending";

                // Create request with Bearer token
                Request request = new Request.Builder()
                        .url(url)
                        .header("Authorization", "Bearer " + token)
                        .build();

                // Send request and get response
                Response response = client.newCall(request).execute();
                String responseBody = response.body().string();
                Gson gson = new Gson();
                ResponseMyPending responseData = gson.fromJson(responseBody, ResponseMyPending.class);

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

    public void GetPendingById(String id, PendingCallback callback)
    {
        if(id == null || id.isEmpty())
        {
            callback.onFailure("Không có id");
            return;
        }

        new Thread(() -> {
            try {
                String url = SERVER + "/api/v1/account/pending/" + id;

                // Create request with Bearer token
                Request request = new Request.Builder()
                        .url(url)
                        .header("Authorization", "Bearer " + token)
                        .build();

                // Send request and get response
                Response response = client.newCall(request).execute();
                String responseBody = response.body().string();
                Gson gson = new Gson();
                ResponsePending responseData = gson.fromJson(responseBody, ResponsePending.class);

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


    public interface MyTicketCallback {
        void onSuccess(List<MyTicket> events);
        void onFailure(String error);
    }

    public interface MyPendingCallback {
        void onSuccess(List<MyPending> pendings);
        void onFailure(String error);
    }
    public interface PendingCallback {
        void onSuccess(MyPending pendings);
        void onFailure(String error);
    }


}

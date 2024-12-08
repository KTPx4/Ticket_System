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

    public void GetMyTicket(ResponseCallback callback)
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

    public interface ResponseCallback {
        void onSuccess(List<MyTicket> events);
        void onFailure(String error);
    }

    public class ResponseMyTicket {
        private String message;
        private int length;
        private List<MyTicket> data;

        public ResponseMyTicket() {
        }

        public ResponseMyTicket(String message, int length, List<MyTicket> data) {
            this.message = message;
            this.length = length;
            this.data = data;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public int getLength() {
            return length;
        }

        public void setLength(int length) {
            this.length = length;
        }

        public List<MyTicket> getData() {
            return data;
        }

        public void setData(List<MyTicket> data) {
            this.data = data;
        }
        // Getters and Setters
    }

}

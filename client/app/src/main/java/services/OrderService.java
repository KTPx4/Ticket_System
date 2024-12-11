package services;

import android.content.Context;
import android.util.Log;

import com.example.ticketbooking.R;
import com.google.gson.Gson;

import java.util.List;

import model.ticket.MyPending;
import modules.LocalStorageManager;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import services.response.ticket.ResponseMyPending;
import services.response.ticket.ResponsePending;

public class OrderService {
    private Context context;
    private static String SERVER;
    private final OkHttpClient client;
    private final LocalStorageManager localStorageManager;
    private String token;
    public OrderService(Context context)
    {
        this.context = context;
        client = new OkHttpClient();
        this.SERVER = context.getString(R.string.server_url);
        localStorageManager = new LocalStorageManager(context);
        this.token = localStorageManager.getLoginToken();
    }

    public void UpdateOrder(String id, String typePayment, List<String> listId , UpdateCallback callback )
    {
        if(id == null || id.isEmpty())
        {
            callback.onFailure("Không có id");
            return;
        }
        new Thread(() -> {
            try {
                String url = SERVER + "/api/v1/order/" + id;

                // Chuyển thông tin Java List sang JSON Payload
                Gson gson = new Gson();
                String jsonPayload = gson.toJson(new RequestPayload(listId, typePayment));

                // Tạo RequestBody
                RequestBody body = RequestBody.create(
                        jsonPayload,
                        MediaType.parse("application/json")
                );

                // Create request with Bearer token
                Request request = new Request.Builder()
                        .url(url)
                        .header("Authorization", "Bearer " + token)
                        .put(body)
                        .build();

                // Send request and get response
                Response response = client.newCall(request).execute();
                String responseBody = response.body().string();

                ResponsePending responseData = gson.fromJson(responseBody, ResponsePending.class);

                if (response.isSuccessful()) {

                    Log.d("ORDER", "Response Body: " + responseBody);
                    Log.d("ORDER ParsedData", gson.toJson(responseData));
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

    public interface UpdateCallback {
        void onSuccess(MyPending myPending);
        void onFailure(String error);
    }

    class RequestPayload {
        private List<String> members;
        private String typePayment;

        public RequestPayload(List<String> members, String typePayment) {
            this.members = members;
            this.typePayment = typePayment;
        }

        public List<String> getMembers() {
            return members;
        }

        public String getTypePayment() {
            return typePayment;
        }
    }
}

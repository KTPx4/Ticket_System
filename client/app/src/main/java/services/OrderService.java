package services;

import android.content.Context;
import android.util.Log;

import com.example.ticketbooking.R;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.List;

import model.ticket.MyPending;
import modules.LocalStorageManager;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import services.response.order.DataGetCheckOut;
import services.response.order.ResponeGetCheckOut;
import services.response.order.ResponseValid;
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

    public void BuyTicket(String id, List<String> listId, ValidCallback callback )
    {
        if(id == null || id.isEmpty())
        {
            callback.onFailure("Không có id sự kiện");
            return;
        }
        if(token == null || token.isEmpty())
        {
            callback.onFailure("Không có id người dùng");
            return;
        }

        new Thread(() -> {
            try {
                String url = SERVER + "/api/v1/order" ;

                // Chuyển thông tin Java List sang JSON Payload
                Gson gson = new Gson();
                String jsonPayload = gson.toJson(new BuyTicketPayload(id, listId));

                // Tạo RequestBody
                RequestBody body = RequestBody.create(
                        jsonPayload,
                        MediaType.parse("application/json")
                );

                // Create request with Bearer token
                Request request = new Request.Builder()
                        .url(url)
                        .header("Authorization", "Bearer " + token)
                        .post(body)
                        .build();

                // Send request and get response
                Response response = client.newCall(request).execute();
                String responseBody = response.body().string();

                ResponseValid responseData = gson.fromJson(responseBody, ResponseValid.class);

                if (response.isSuccessful()) {

                    Log.d("ORDER", "Response Body: " + responseBody);
                    Log.d("ORDER ParsedData", gson.toJson(responseData));
                    callback.onSuccess(responseData.getMessage());

                } else {
                    callback.onFailure(responseData.getMessage());
                }
            } catch (Exception e) {
                e.printStackTrace();
                callback.onFailure("Tải thông tin thất bại" + e.getMessage());
            }
        }).start();
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
                String jsonPayload = gson.toJson(new UpdatePayload(listId, typePayment));

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

    public void DeleteInfo(String id, String info,  ValidCallback callback )
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

                // Tạo payload
                JSONObject payload = new JSONObject();
                payload.put("info", info);

                // Chuyển JSONObject sang String
                String jsonPayload = payload.toString();

                // Tạo RequestBody
                RequestBody body = RequestBody.create(
                        jsonPayload,
                        MediaType.parse("application/json")
                );

                // Create request with Bearer token
                Request request = new Request.Builder()
                        .url(url)
                        .header("Authorization", "Bearer " + token)
                        .delete(body)
                        .build();

                // Send request and get response
                Response response = client.newCall(request).execute();
                String responseBody = response.body().string();

                ResponseValid responseData = gson.fromJson(responseBody, ResponseValid.class);

                if (response.isSuccessful()) {

                    Log.d("ORDER", "Response Body: " + responseBody);
                    Log.d("ORDER ParsedData", gson.toJson(responseData));
                    callback.onSuccess(responseData.getMessage());

                } else {
                    callback.onFailure(responseData.getMessage());
                }
            } catch (Exception e) {
                e.printStackTrace();
                callback.onFailure("Tải thông tin thất bại" + e.getMessage());
            }
        }).start();
    }

    public void GetCheckOut(String id, String couponCode, List<String> listId , GetCheckOutCallback callback )
    {
        if(id == null || id.isEmpty())
        {
            callback.onFailure("Không có id");
            return;
        }

        new Thread(() -> {
            try {
                String url = SERVER + "/api/v1/order/" + id +"/checkout";

                // Chuyển thông tin Java List sang JSON Payload
                Gson gson = new Gson();
                String jsonPayload = gson.toJson(new GetCheckOutPayload(listId, couponCode));

                // Tạo RequestBody
                RequestBody body = RequestBody.create(
                        jsonPayload,
                        MediaType.parse("application/json")
                );

                // Create request with Bearer token
                Request request = new Request.Builder()
                        .url(url)
                        .header("Authorization", "Bearer " + token)
                        .post(body)
                        .build();

                // Send request and get response
                Response response = client.newCall(request).execute();
                String responseBody = response.body().string();

                ResponeGetCheckOut responseData = gson.fromJson(responseBody, ResponeGetCheckOut.class);

                if (response.isSuccessful())
                {

                    Log.d("ORDER-CHECKOUT", "Response Body: " + responseBody);
                    Log.d("ORDER-CHECKOUT ParsedData", gson.toJson(responseData));
                    callback.onSuccess(responseData.getData(), responseData.getToken());
                }
                else
                {
                    callback.onFailure(responseData.getMessage());
                }

            } catch (Exception e) {
                e.printStackTrace();
                callback.onFailure("Tải thông tin thất bại" + e.getMessage());
            }
        }).start();
    }


    public void GetCheckOutStripe(String id, String couponCode, List<String> listId , GetCheckOutStripeCallback callback )
    {
        if(id == null || id.isEmpty())
        {
            callback.onFailure("Không có id");
            return;
        }

        new Thread(() -> {
            try {
                String url = SERVER + "/api/v1/order/" + id +"/stripe-checkout";

                // Chuyển thông tin Java List sang JSON Payload
                Gson gson = new Gson();
                String jsonPayload = gson.toJson(new GetCheckOutPayload(listId, couponCode));

                // Tạo RequestBody
                RequestBody body = RequestBody.create(
                        jsonPayload,
                        MediaType.parse("application/json")
                );

                // Create request with Bearer token
                Request request = new Request.Builder()
                        .url(url)
                        .header("Authorization", "Bearer " + token)
                        .post(body)
                        .build();

                // Send request and get response
                Response response = client.newCall(request).execute();
                String responseBody = response.body().string();

                ResponseStripe responseData = gson.fromJson(responseBody, ResponseStripe.class);

                if (response.isSuccessful())
                {

                    Log.d("ORDER-CHECKOUT", "Response Body: " + responseBody);
                    Log.d("ORDER-CHECKOUT ParsedData", gson.toJson(responseData));
                    callback.onSuccess(responseData.getUrl());
                }
                else
                {
                    callback.onFailure(responseData.getStatus());
                }

            } catch (Exception e) {
                e.printStackTrace();
                callback.onFailure("Tải thông tin thất bại" + e.getMessage());
            }
        }).start();
    }


    public void PostValid(String id, String orderToken, String payment , ValidCallback callback )
    {
        if(id == null || id.isEmpty() || orderToken == null || orderToken.isEmpty())
        {
            callback.onFailure("Không có id hoặc token");
            return;
        }
        String secret = context.getString(R.string.ORDER_SECRET_START);
        new Thread(() -> {
            try {
                String url = SERVER + "/api/v1/order/" + id +"/valid";

                // Chuyển thông tin Java List sang JSON Payload
                Gson gson = new Gson();
                String jsonPayload = gson.toJson(new ValidPayload(orderToken, payment, secret ));

                // Tạo RequestBody
                RequestBody body = RequestBody.create(
                        jsonPayload,
                        MediaType.parse("application/json")
                );

                // Create request with Bearer token
                Request request = new Request.Builder()
                        .url(url)
                        .header("Authorization", "Bearer " + token)
                        .post(body)
                        .build();

                // Send request and get response
                Response response = client.newCall(request).execute();
                String responseBody = response.body().string();

                ResponseValid responseData = gson.fromJson(responseBody, ResponseValid.class);

                Log.d("ORDER-VALID", "Response Body: " + responseBody);
                Log.d("ORDER-VALID ParsedData", gson.toJson(responseData));
                if (response.isSuccessful())
                {
                    callback.onSuccess( responseData.getMessage());
                }
                else
                {
                    callback.onFailure(responseData.getMessage());
                }

            } catch (Exception e) {
                e.printStackTrace();
                callback.onFailure("Tải thông tin thất bại" + e.getMessage());
            }
        }).start();
    }




    public interface ValidCallback {
        void onSuccess(String success);
        void onFailure(String error);
    }

    public interface UpdateCallback {
        void onSuccess(MyPending myPending);
        void onFailure(String error);
    }

    public interface GetCheckOutCallback {
        void onSuccess(DataGetCheckOut myPending, String token);
        void onFailure(String error);
    }

    public interface GetCheckOutStripeCallback {
        void onSuccess(String url);
        void onFailure(String error);
    }

    class BuyTicketPayload{
        private String event;
        private List<String> tickets;

        public BuyTicketPayload() {
        }

        public BuyTicketPayload(String event, List<String> tickets) {
            this.event = event;
            this.tickets = tickets;
        }

        public String getEvent() {
            return event;
        }

        public void setEvent(String event) {
            this.event = event;
        }

        public List<String> getTickets() {
            return tickets;
        }

        public void setTickets(List<String> tickets) {
            this.tickets = tickets;
        }
    }
    class ValidPayload{
        private String token;
        private String payment;
        private String secret;

        public ValidPayload(String token, String payment, String secret) {
            this.token = token;
            this.payment = payment;
            this.secret = secret;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getPayment() {
            return payment;
        }

        public void setPayment(String payment) {
            this.payment = payment;
        }

        public String getSecret() {
            return secret;
        }

        public void setSecret(String secret) {
            this.secret = secret;
        }
    }
    class UpdatePayload {
        private List<String> members;
        private String typePayment;

        public UpdatePayload(List<String> members, String typePayment) {
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
    class GetCheckOutPayload {
        private List<String> infos;
        private String coupon;

        public GetCheckOutPayload() {
        }

        public GetCheckOutPayload(List<String> infos, String coupon) {
            this.infos = infos;
            this.coupon = coupon;
        }

        public List<String> getInfos() {
            return infos;
        }

        public void setInfos(List<String> infos) {
            this.infos = infos;
        }

        public String getCoupon() {
            return coupon;
        }

        public void setCoupon(String coupon) {
            this.coupon = coupon;
        }
    }

    class ResponseStripe
    {
        private String status;
        private String url;

        public ResponseStripe() {
        }

        public ResponseStripe(String status, String url) {
            this.status = status;
            this.url = url;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
}

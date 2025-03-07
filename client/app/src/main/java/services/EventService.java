package services;

import android.content.Context;
import android.util.Log;

import com.example.ticketbooking.R;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.List;

import model.event.News;
import modules.LocalStorageManager;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import services.response.event.RSGetAllEvent;
import services.response.event.RSGetAllTicket;
import services.response.event.RSGetAllNews;
import services.response.event.RSGetNews;

public class EventService {
    private Context context;
    private static String SERVER;
    private final OkHttpClient client;
    private final LocalStorageManager localStorageManager;
    private String token;

    public EventService(Context context)
    {
        this.context = context;
        client = new OkHttpClient();
        this.SERVER = context.getString(R.string.server_url);
        localStorageManager = new LocalStorageManager(context);
        this.token = localStorageManager.getLoginToken();
    }

    public void GetNews(String idEvent, int quan, CallBackGetAllNews callBack)
    {
        if(idEvent == null || idEvent.isEmpty())
        {
            callBack.onFailure("Thiếu id sự kiện");
            return;
        }
        new Thread(()->{
            try{
                String url = SERVER + "/api/v1/event/" + idEvent + "/news?quan=" + quan;
                Request request = new Request.Builder()
                        .url(url)
                        .header("Authorization", "Bearer " + token)
                        .build();
                Response response = client.newCall(request).execute();

                try{
                    String responeBody = response.body() != null ? response.body().string() : "";
                    Log.d("GetNews", "Response Body: " + responeBody);
                    Gson gson = new Gson();
                    RSGetAllNews rs = gson.fromJson(responeBody, RSGetAllNews.class);
                    if(response.isSuccessful())
                    {
                        callBack.onSuccess(rs.getData());
                    }
                    else{
                        callBack.onFailure(rs.getMessage());
                    }

                }
                finally {
                    // Đảm bảo đóng response trong mọi trường hợp
                    response.close();
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
                callBack.onFailure("Tải tin tức thất bại: " + e.getMessage());

            }
        }).start();
    }

    public void GetNewsById(String idNews,  CallBackGetNews callBack)
    {
        if(idNews == null || idNews.isEmpty())
        {
            callBack.onFailure("Thiếu id tin tức");
            return;
        }
        new Thread(()->{
            try{
                String url = SERVER + "/api/v1/news/" + idNews  ;
                Request request = new Request.Builder()
                        .url(url)
                        .header("Authorization", "Bearer " + token)
                        .build();
                Response response = client.newCall(request).execute();

                try{
                    String responeBody = response.body() != null ? response.body().string() : "";
                    Log.d("GetNews", "Response Body: " + responeBody);
                    Gson gson = new Gson();
                    RSGetNews rs = gson.fromJson(responeBody, RSGetNews.class);
                    if(response.isSuccessful())
                    {
                        callBack.onSuccess(rs.getData(), rs.getEventName());
                    }
                    else{
                        callBack.onFailure(rs.getMessage());
                    }

                }
                finally {
                    // Đảm bảo đóng response trong mọi trường hợp
                    response.close();
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
                callBack.onFailure("Tải tin tức thất bại: " + e.getMessage());

            }
        }).start();
    }

    public void ScanTicket(String idEvent, String ticketCode, ResponseCallback callback) {
        new Thread(() -> {
            try {
                String url = SERVER + "/api/v1/event/" + idEvent + "/scan";

                // Tạo RequestBody dạng form
                RequestBody formBody = new FormBody.Builder()
                        .add("token", ticketCode)
                        .build();

                // Create request with Bearer token
                Request request = new Request.Builder()
                        .url(url)
                        .header("Authorization", "Bearer " + token)
                        .post(formBody)
                        .build();

                // Send request and get response
                Response response = client.newCall(request).execute();

                try {
                    String responseBody = response.body() != null ? response.body().string() : "";
                    Log.d("ScanTicket", "Response Body: " + responseBody);

                    // Xử lý kết quả
                    JSONObject jsonResponse = new JSONObject(responseBody);
                    String message = jsonResponse.optString("message", "Quét thất bại!");

                    if (response.isSuccessful()) {
                        callback.onSuccess(message);
                    } else {
                        callback.onFailure("Failed: " + message);
                    }
                } finally {
                    // Đảm bảo đóng response trong mọi trường hợp
                    response.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
                callback.onFailure("Tải vé thất bại: " + e.getMessage());
            }
        }).start();
    }

    public void GetAllTicket(String idEvent, CallBackGetTicket callback)
    {
        new Thread(() -> {
            try {
                String url = SERVER + "/api/v1/event/" + idEvent + "/ticket";

                // Create request with Bearer token
                Request request = new Request.Builder()
                        .url(url)
                        .header("Authorization", "Bearer " + token)
                        .build();

                // Send request and get response
                Response response = client.newCall(request).execute();

                try {
                    String responseBody = response.body() != null ? response.body().string() : "";
                    Log.d("GetAllTicket", "Response Body: " + responseBody);

                    // Xử lý kết quả
                    Gson gson = new Gson();
                    RSGetAllTicket responseData = gson.fromJson(responseBody, RSGetAllTicket.class);

                    if (response.isSuccessful()) {
                        callback.onSuccess(responseData.getData());
                    } else {
                        callback.onFailure("Lấy danh sách vé thất bại");
                    }
                } finally {
                    // Đảm bảo đóng response trong mọi trường hợp
                    response.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
                callback.onFailure("Tải vé thất bại: " + e.getMessage());
            }
        }).start();
    }

    public void GetAllEvent( CallBackGetAllEvent  callback)
    {
        new Thread(() -> {
            try {
                String url = SERVER + "/api/v1/event/"  ;

                // Create request with Bearer token
                Request request = new Request.Builder()
                        .url(url)
                        .build();

                // Send request and get response
                Response response = client.newCall(request).execute();

                try {
                    String responseBody = response.body() != null ? response.body().string() : "";
                    Log.d("GetAllTicket", "Response Body: " + responseBody);

                    // Xử lý kết quả
                    Gson gson = new Gson();
                    RSGetAllEvent responseData = gson.fromJson(responseBody, RSGetAllEvent.class);

                    if (response.isSuccessful()) {
                        callback.onSuccess( responseData.getData());
                    } else {
                        callback.onFailure("Lấy danh sách vé thất bại");
                    }
                } finally {
                    // Đảm bảo đóng response trong mọi trường hợp
                    response.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
                callback.onFailure("Tải vé thất bại: " + e.getMessage());
            }
        }).start();
    }

    public interface ResponseCallback {
        void onSuccess(String success);
        void onFailure(String error);
    }

    public interface  CallBackGetTicket{
        void onSuccess(List<RSGetAllTicket.LocationInfo> data);
        void onFailure(String error);
    }

    public interface CallBackGetAllEvent{
        void onSuccess(List<RSGetAllEvent.REvent> events);
        void onFailure(String error);
    }

    public interface CallBackGetAllNews {
        void onSuccess(List<News> listNews);
        void onFailure(String error);
    }
    public interface CallBackGetNews{
        void onSuccess(News news, String eventName);
        void onFailure(String error);
    }
}

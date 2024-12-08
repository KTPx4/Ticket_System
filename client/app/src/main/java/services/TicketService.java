package services;

import android.content.Context;

import com.example.ticketbooking.R;

import java.util.List;

import model.ticket.MyEvent;
import modules.LocalStorageManager;
import okhttp3.OkHttpClient;

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

    }

    public interface ResponseCallback {
        void onSuccess(List<MyEvent> events);
        void onFailure(String error);
    }
}

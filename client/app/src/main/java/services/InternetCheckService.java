package services;
import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.example.ticketbooking.IntroActivity;
import com.example.ticketbooking.NoInternetActivity;

import java.util.List;

import modules.InternetConnection;
import modules.LocalStorageManager;

public class InternetCheckService extends Service {

    private Handler handler;
    private Runnable runnable;
    private static final int CHECK_INTERVAL = 5000; // 3 giây
    private static LocalStorageManager localStorageManager;

    @Override
    public void onCreate() {
        super.onCreate();
        localStorageManager = new LocalStorageManager(this);
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                checkInternet();
                handler.postDelayed(this, CHECK_INTERVAL);
            }
        };
        handler.post(runnable);
    }


    private boolean isCurrentActivity(Class<?> activityClass) {
        // Dùng ActivityManager để lấy thông tin về Activity hiện tại
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager != null) {
            List<ActivityManager.RunningTaskInfo> taskInfo = activityManager.getRunningTasks(1);
            if (!taskInfo.isEmpty()) {
                String currentActivity = taskInfo.get(0).topActivity.getClassName();
                return currentActivity.equals(activityClass.getName());
            }
        }
        return false;
    }
    private void checkInternet() {
        if (!InternetConnection.isConnected(this)) {

            if(!isCurrentActivity(NoInternetActivity.class))
             {
                 Intent intent = new Intent(getApplicationContext(), NoInternetActivity.class);
                 intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                 startActivity(intent);
             }

        } else {
//            if (isInternetLost) {
//                isInternetLost = false;
//                Intent intent = new Intent(getApplicationContext(), IntroActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(intent);
//            }
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}


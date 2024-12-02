package modules;

import android.content.Context;
import android.content.SharedPreferences;

public class LocalStorageManager {
    private static final String PREF_NAME = "AppPreferences";

    private final SharedPreferences sharedPreferences;

    public LocalStorageManager(Context context) {
        this.sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    // Lưu dữ liệu
    public void saveLoginOption(String value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("login-option", value);
        editor.apply(); // Lưu dữ liệu không đồng bộ
    }

    // Đọc dữ liệu
    public String getLoginOption() {
        return sharedPreferences.getString("login-option", ""); // Giá trị mặc định là "default"
    }



    public void saveLoginToken(String value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("login-token", value);
        editor.apply(); // Lưu dữ liệu không đồng bộ
    }

    public String getLoginToken() {
        return sharedPreferences.getString("login-token", ""); // Giá trị mặc định là "default"
    }

    // Xóa dữ liệu
    public void clearLoginOption() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("login-option");
        editor.apply();
    }

}

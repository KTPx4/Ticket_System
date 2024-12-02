package modules;

import android.content.Context;
import android.content.SharedPreferences;

public class LocalStorageManager {
    private static final String PREF_NAME = "AppPreferences";  // Tên SharedPreferences

    private final SharedPreferences sharedPreferences;

    // Constructor, khởi tạo đối tượng SharedPreferences
    public LocalStorageManager(Context context) {
        this.sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    // Lưu login option
    public void saveLoginOption(String value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("login-option", value);
        editor.apply(); // Lưu dữ liệu không đồng bộ
    }

    // Đọc login option
    public String getLoginOption() {
        return sharedPreferences.getString("login-option", ""); // Giá trị mặc định là ""
    }

    // Lưu login token
    public void saveLoginToken(String value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("login-token", value);
        editor.apply(); // Lưu dữ liệu không đồng bộ
    }

    // Đọc login token
    public String getLoginToken() {
        return sharedPreferences.getString("login-token", ""); // Giá trị mặc định là ""
    }

    // Xóa login option
    public void clearLoginOption() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("login-option");
        editor.apply(); // Lưu dữ liệu không đồng bộ
    }

    // Xóa login token
    public void clearLoginToken() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("login-token");
        editor.apply(); // Lưu dữ liệu không đồng bộ
    }
}

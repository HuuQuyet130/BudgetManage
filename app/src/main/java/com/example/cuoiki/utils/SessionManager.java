package com.example.cuoiki.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    private static final String PREF_NAME = "BitMoneySession";
    private static final String KEY_USERNAME = "username";

    private final SharedPreferences sharedPreferences;

    public SessionManager(Context context) {
        sharedPreferences = context.getSharedPreferences(
                PREF_NAME,
                Context.MODE_PRIVATE
        );
    }

    // Lưu username sau khi đăng nhập
    public void saveLogin(String username) {
        sharedPreferences.edit()
                .putString(KEY_USERNAME, username)
                .apply();
    }

    // Lấy username đang đăng nhập
    public String getUsername() {
        return sharedPreferences.getString(KEY_USERNAME, null);
    }

    // Kiểm tra đã đăng nhập chưa
    public boolean isLoggedIn() {
        return getUsername() != null;
    }

    // Đăng xuất
    public void logout() {
        sharedPreferences.edit().clear().apply();
    }
}
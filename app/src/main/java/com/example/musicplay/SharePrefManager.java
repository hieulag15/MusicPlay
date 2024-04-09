package com.example.musicplay;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.musicplay.domain.User;
import com.google.gson.Gson;

public class SharePrefManager {
    private static final String SHARED_PREF_NAME = "registerlogin";
    private static final String KEY_USER = "user";
    private static SharePrefManager mInstance;
    private static Context ctx;
    private final SharedPreferences sharedPreferences;
    private final Gson gson = new Gson();

    private SharePrefManager(Context context) {
        ctx = context;
        sharedPreferences = ctx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized SharePrefManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new SharePrefManager(context);
        }
        return mInstance;
    }

    //Chua user data trong shared preferences
    public void userLogin(User user) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String userJson = gson.toJson(user);
        editor.putString(KEY_USER, userJson);
        editor.apply();
    }

    public boolean isLoggedIn() {
        return sharedPreferences.getString(KEY_USER, null) != null;
    }

    public User getUser() {
        String userJson = sharedPreferences.getString(KEY_USER, null);
        return gson.fromJson(userJson, User.class);
    }

    public void logout() {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(KEY_USER);
        editor.apply();
    }

}

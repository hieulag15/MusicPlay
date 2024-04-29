package com.example.musicplay;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.example.musicplay.domain.User;


public class SharedPrefManager {
    private static final String SHARED_PREF_NAME = "registerlogin";
    private static final String KEY_ID = "keyid";
    private static final String KEY_PHONE = "keyphone";
    private static final String KEY_FIRSTNAME = "keyfirstname";
    private static final String KEY_LASTNAME = "keylastname";
    private static final String KEY_EMAIL = "keyemail";

    private static final String KEY_PASSWORD = "keypassword";

    private static final String KEY_ROLE = "keyrole";

    private static SharedPrefManager mInstance;
    private static Context ctx;

    //khỏi tạo constructor
    private SharedPrefManager(Context context) {
        ctx = context;
    }

    public static synchronized SharedPrefManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new SharedPrefManager(context);
        }
        return mInstance;
    }

    //this method will store the user data in shared preferences
    public void userLogin (User user) {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(KEY_ID, user.getId());
        editor.putString(KEY_PHONE, user.getPhone());
        editor.putString(KEY_EMAIL, user.getEmail());
        editor.putString(KEY_FIRSTNAME, user.getFirst_name());
        editor.putString(KEY_LASTNAME, user.getLast_name());
        editor.putString(KEY_ROLE, user.getRole());
        editor.putString(KEY_PASSWORD, user.getPassword());
        editor.apply();
    }
    public boolean isLoggedIn () {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_PHONE, null) != null;
    }
    public User getUser () {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return new User(
                sharedPreferences.getLong(KEY_ID, -1),
                sharedPreferences.getString(KEY_PHONE, null),
                sharedPreferences.getString(KEY_FIRSTNAME, null), sharedPreferences.getString(KEY_LASTNAME, null), sharedPreferences.getString(KEY_EMAIL, null),sharedPreferences.getString(KEY_PASSWORD, null)
        );
    }
    public void logout () {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        ctx.startActivity(new Intent(ctx, LoginActivity.class));
    }
}

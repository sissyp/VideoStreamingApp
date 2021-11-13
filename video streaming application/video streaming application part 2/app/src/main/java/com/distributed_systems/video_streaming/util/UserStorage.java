package com.distributed_systems.video_streaming.util;

import android.content.Context;
import android.content.SharedPreferences;


public class UserStorage {
    private SharedPreferences pref = null;
    private Context parentActivity;
    public static String APP_KEY;

    public UserStorage(Context context) {
        parentActivity = context;
        APP_KEY = context.getPackageName().replaceAll("\\.", "_").toLowerCase();
    }

    public void setString(String key, String value) {
        pref = parentActivity.getSharedPreferences(APP_KEY,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public String getString(String key) {
        pref = parentActivity.getSharedPreferences(APP_KEY,
                Context.MODE_PRIVATE);
        return pref.getString(key, "");

    }
}

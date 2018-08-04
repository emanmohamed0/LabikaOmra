package com.app.emaneraky.omrati.constans;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;

import java.util.Locale;

public class Global {
    static Locale myLocale;
    public static final int REQUEST_CHECKSETTING_GPS = 0X1;

    public static void setLocale(Context context, String language) {
        myLocale = new Locale(language);
        Resources res = context.getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        save_local(context, language);
        res.updateConfiguration(conf, context.getResources().getDisplayMetrics());
    }

    public static void save_local(Context context, String language) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("language", language);
        editor.commit();

    }

    public static String checkLocal(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString("language", "");
    }

    public static void set_UserId(Context context, String key, String userId) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, userId);
        editor.commit();
    }

    public static String get_UserID(Context context, String key) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(key, "");
    }
}

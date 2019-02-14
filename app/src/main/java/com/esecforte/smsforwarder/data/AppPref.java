package com.esecforte.smsforwarder.data;

import android.content.Context;
import android.content.SharedPreferences;

public class AppPref {
    public static final String PREF_NAME = "app-data";
    public static final String USER_PH_NO_DATA = "user-ph-no-data";
    public static final String SHOW_OPTM_DIALOG = "show opt dialog";
    private static AppPref appPref;

    SharedPreferences sharedPreferences;
    private Context appContext;

    AppPref(Context context) {
        appContext = context.getApplicationContext();
        sharedPreferences = appContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

    }

    public static AppPref getInstance(Context context) {
        if (appPref == null)
            appPref = new AppPref(context);
        return appPref;
    }


    public String getString(String key) {
        return sharedPreferences.getString(key, "");
    }

    public void putString(String key, String value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();

    }


    public boolean getBool(String key) {
        return sharedPreferences.getBoolean(key, false);
    }

    public void putBool(String key, boolean value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();

    }

    public int getSuccessSmsCount(boolean isIncreaseCount) {
        String key = "success-sms-count";
        int count = sharedPreferences.getInt(key, 0);
        if (!isIncreaseCount)
            return count;
        SharedPreferences.Editor editor = sharedPreferences.edit();
        int value = count + 1;
        editor.putInt(key, value);
        editor.apply();
        return value;
    }

    public int getFailedSmsCount(boolean isIncreaseCount) {
        String key = "failed-sms-count";
        int count = sharedPreferences.getInt(key, 0);
        if (!isIncreaseCount)
            return count;
        SharedPreferences.Editor editor = sharedPreferences.edit();
        int value = count + 1;
        editor.putInt(key, value);
        editor.apply();
        return value;
    }


}

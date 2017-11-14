package com.piapps.flashcard.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by abduaziz on 8/23/17.
 */

public class Prefs {

    static String PREFS = "APP_PREFS";

    public static void saveToPrefs(Context context, String key, String value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
        editor.apply();
    }

    public static void saveToPrefs(Context context, String key, boolean value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.commit();
        editor.apply();
    }

    public static void saveToPrefs(Context context, String key, float value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat(key, value);
        editor.commit();
        editor.apply();
    }

    public static void saveToPrefs(Context context, String key, int value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.commit();
        editor.apply();
    }

    public static void saveToPrefs(Context context, String key, long value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(key, value);
        editor.commit();
        editor.apply();
    }

    public static int getInt(Context context, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(key, -100);
    }

    public static float getFloat(Context context, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(key, -100);
    }

    public static long getLong(Context context, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        return sharedPreferences.getLong(key, -100l);
    }

    public static boolean getBoolean(Context context, String key, boolean def) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(key, def);
    }

    public static String getString(Context context, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        return sharedPreferences.getString(key, "");
    }

    public static String getString(Context context, String key, String def) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        return sharedPreferences.getString(key, def);
    }
}

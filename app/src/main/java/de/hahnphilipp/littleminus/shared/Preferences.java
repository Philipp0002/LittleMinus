package de.hahnphilipp.littleminus.shared;

import android.content.Context;
import android.content.SharedPreferences;

import de.hahnphilipp.littleminus.LittleMinusApplication;

public class Preferences {

    public static SharedPreferences getSharedPreferences() {
        return LittleMinusApplication.context
                .getSharedPreferences("littleminus_prefs", Context.MODE_PRIVATE);
    }

    public static SharedPreferences.Editor getEditor() {
        return getSharedPreferences().edit();
    }

    public static void putString(String key, String value) {
        SharedPreferences.Editor editor = getEditor();
        editor.putString(key, value);
        editor.apply();
    }

    public static String getString(String key, String defaultValue) {
        return getSharedPreferences().getString(key, defaultValue);
    }

    public static void putBoolean(String key, boolean value) {
        SharedPreferences.Editor editor = getEditor();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public static boolean getBoolean(String key, boolean defaultValue) {
        return getSharedPreferences().getBoolean(key, defaultValue);
    }
}

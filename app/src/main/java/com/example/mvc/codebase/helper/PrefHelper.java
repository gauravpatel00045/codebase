package com.example.mvc.codebase.helper;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.util.Log;
import com.example.mvc.codebase.MyApplication;
import com.example.mvc.codebase.api.RestClient;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Map;

/**
 * This class contain required getter and setter methods to store and
 * get All the preference of App eg. setString(key,value), getString(key,defaultValue)
 */

public class PrefHelper {

    //shared pref file name
    private static final String APP_PREF = "appPreference";
    // Faster pref saving for high performance
    private static final Method sApplyMethod = findApplyMethod();
    public static final String KEY_DEVICE_TOKEN = "deviceToken";
    public static final String KEY_CURRENT_LOGGED_IN_USER = "currentLogInUserDetails";
    private static SharedPreferences preferences;
    private static SharedPreferences.Editor editor;
    private static PrefHelper instance;
    public static final String KEY_SAVED_MESSAGE_LIST = "messageList";
    public static final String KEY_MESSAGE_UPDATE_DATE = "messageUpdateDate";
    public static final String KEY_ZOOM_INST = "zoomInstFirstTime";


    //constructor
    private PrefHelper() {
    }

    /**
     * @return instance (PrefHelper) : to get PrefHelper instance
     */
    @SuppressLint("CommitPrefEdits")
    public static PrefHelper getInstance() {
        int PRIVATE_MODE = 0;
        preferences = MyApplication.getInstance().getSharedPreferences(APP_PREF, PRIVATE_MODE);
        editor = preferences.edit();
        if (instance == null) {
            instance = new PrefHelper();
        }
        return instance;
    }


    private static Method findApplyMethod() {
        try {
            final Class<SharedPreferences.Editor> cls = SharedPreferences.Editor.class;
            return cls.getMethod("apply");
        } catch (final NoSuchMethodException unused) {
            // fall through
        }
        return null;
    }

    /**
     * This method apply change in preferences
     *
     * @param editor (SharedPreferences.Editor) : editor
     */
    private static void apply(final SharedPreferences.Editor editor) {
        if (sApplyMethod != null) {
            try {
                sApplyMethod.invoke(editor);
                return;
            } catch (final InvocationTargetException unused) {
                // fall through
            } catch (final IllegalAccessException unused) {
                // fall through
            }
        }
        editor.commit();
    }

    /**
     * This method get integer value in preferences
     *
     * @param key          (String)       : key for unique string value
     * @param defaultValue (int) : defaultValue to be passed
     * @return (int) : it return  key value that user passed as a key
     * e.g. customerId - return value - 23
     */
    public int getInt(final String key, final int defaultValue) {
        return preferences.getInt(key, defaultValue);
    }

    /**
     * This method store integer value in preferences
     *
     * @param key   (String) : key for unique string value
     * @param value (int)  : value to be stored
     */
    public void setInt(final String key, final int value) {
        editor.putInt(key, value);
        apply(editor);
    }

    /**
     * This method get boolean value from preferences
     *
     * @param key          (String)           : key for unique string value
     * @param defaultValue (long) : defaultValue to be passed
     * @return (long) : it return  key value that user passed as a key
     */
    public long getLong(final String key, final long defaultValue) {
        return preferences.getLong(key, defaultValue);
    }

    /**
     * This method store the long dataType value in preferences
     *
     * @param key   (String) : key for unique string value
     * @param value (long) : defaultValue to be passed
     */
    public void setLong(final String key, final long value) {
        editor.putLong(key, value);
        apply(editor);
    }

    /**
     * This method get the double dataType value from preferences
     *
     * @param key   (String)   : key for unique string value
     * @param value (double) : defaultValue to be passed]
     * @return (int) : it return  key value that user passed as a key
     * e.g. customerId - return value - 2365786354358
     */
    public long getDouble(final String key, final double value) {
        return preferences.getLong(key, Double.doubleToRawLongBits(value));
    }

    /**
     * This method store the double dataType value in preferences
     *
     * @param key(String)   : key for unique string value
     * @param value(double) : defaultValue to be passed
     */
    public void setDouble(final String key, final double value) {
        editor.putLong(key, Double.doubleToRawLongBits(value));
        apply(editor);
    }

    /**
     * This method return the string value from preferences
     *
     * @param key          (String)          : key for unique string value
     * @param defaultValue (String) : defaultValue to be passed
     * @return (String) : it return  key value that user passed as a key
     * e.g. lblAddress - return value - Address
     */
    public String getString(final String key, final String defaultValue) {
        return preferences.getString(key, defaultValue);
    }

    /**
     * This method store String value in preferences
     *
     * @param key   (String)   : key for unique string value
     * @param value (String) : value to be stored
     */
    public void setString(final String key, final String value) {
        editor.putString(key, value);
        apply(editor);
    }

    /**
     * This method store String value in preferences
     *
     * @param key  (String) : key for unique string value
     * @param list (Map)   : value to be stored
     */
    public void setString(final String key, final Map list) {
        editor.putString(key, RestClient.gson.toJson(list));
        apply(editor);
    }

    /**
     * This method get boolean value from preferences
     *
     * @param key          (String)           : key for unique string value
     * @param defaultValue (boolean) : defaultValue to be passed
     * @return (int) : it return  key value that user passed as a key
     * e.g. isActive - return value - true or false
     */
    public boolean getBoolean(final String key, final boolean defaultValue) {
        return preferences.getBoolean(key, defaultValue);
    }

    /**
     * This method store the boolean value in preferences
     *
     * @param key   (String)    : key for unique string value
     * @param value (boolean) : defaultValue to be passed
     */
    public void setBoolean(final String key, final boolean value) {
        editor.putBoolean(key, value);
        apply(editor);
    }

    /**
     * TODO stub is generated but developer need to test
     * This method delete the value from preferences
     *
     * @param key (String) : key for unique string value
     */
    public void deletePreference(final String key) {
        editor.remove(key);
        apply(editor);
    }

    /**
     * TODO stub is generated but developer need to test
     * This method remove values that to be passed in array list asa key
     *
     * @param keyList (ArrayList<String>) : key list to remove values from preference
     */
    public void deletePreferences(final ArrayList<String> keyList) {
        if (keyList != null && keyList.size() > 0) {
//			Editor editor = null;
            for (int i = 0; i < keyList.size(); i++) {
                editor.remove(keyList.remove(i));
                apply(editor);
            }
        }
    }

    /**
     * Delete all preference of app, Except passed argument
     *
     * @param exceptPrefKeyList (ArrayList<String>) : key list to not remove values from preference
     */
    public void deletePreferencesExcept(final ArrayList<String> exceptPrefKeyList) {

        Map<String, ?> keys = preferences.getAll();

        for (Map.Entry<String, ?> entry : keys.entrySet()) {
            Log.d("map values", entry.getKey() + ": " +
                    entry.getValue().toString());
            if (!exceptPrefKeyList.contains(entry.getKey())) {
                editor.remove(entry.getKey());
                apply(editor);
            }
        }
    }

    /**
     * This method delete all preference files from App
     */
    public void deleteAllPreferences() {
        editor.clear();
        apply(editor);
    }
}
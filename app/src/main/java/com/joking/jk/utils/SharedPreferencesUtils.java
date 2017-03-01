package com.joking.jk.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Administrator on 2016/9/26.
 * 单例模式
 */

public class SharedPreferencesUtils {
    private static final String SharedPreferences_Name = "config";

    //    private static Context mContext = null;
    private static SharedPreferences mPref = null;

    private static void init(Context context) {
        /**
         * 不同的context却不创建新的mPref，会不会内存泄漏？
         */
//        if (mPref == null || mContext != context) {
//            mContext = context;//内存泄漏！！！
//            mPref = mContext.getSharedPreferences(SharedPreferences_Name, context.MODE_PRIVATE);
//        }
        if (mPref == null) {
            mPref = context.getSharedPreferences(SharedPreferences_Name, Context.MODE_PRIVATE);
        }
    }

    public static boolean getBoolean(Context context, String key, boolean defaultValue) {
        init(context);
        return mPref.getBoolean(key, defaultValue);
    }

    public static void setBoolean(Context context, String key, boolean value) {
        init(context);
        mPref.edit().putBoolean(key, value).apply();
    }

    public static String getString(Context context, String key, String defaultValue) {
        init(context);
        return mPref.getString(key, defaultValue);
    }

    public static void setString(Context context, String key, String value) {
        init(context);
        mPref.edit().putString(key, value).apply();
    }

    public static int getInt(Context context, String key, int defaultValue) {
        init(context);
        return mPref.getInt(key, defaultValue);
    }

    public static void setInt(Context context, String key, int value) {
        init(context);
        mPref.edit().putInt(key, value).apply();
    }
}

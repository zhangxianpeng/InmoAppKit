package com.inmo.xiaomiwps.utils;

import android.text.TextUtils;
import android.util.Log;

public class LogUtils {
    public static final String TAG = "InmoAppKit";
    private static boolean isLogSwitch = true;

    public void setLogSwitch(boolean logSwitch) {
        isLogSwitch = logSwitch;
    }

    public static void i(String s) {
        if(isLogSwitch) {
            Log.i(TAG, "" + s);
        }
    }
    public static void i(String t, String s) {
        if(isLogSwitch) {
            Log.i(TextUtils.isEmpty(t) ? TAG : t, "" + s);
        }
    }
    public static void w(String s) {
        if(isLogSwitch) {
            Log.w(TAG, "" + s);
        }
    }
    public static void w(String t, String s) {
        if(isLogSwitch) {
            Log.w(TextUtils.isEmpty(t) ? TAG : t, "" + s);
        }
    }
    public static void e(String s) {
        if(isLogSwitch) {
            Log.e(TAG, "" + s);
        }
    }
    public static void e(String t, String s) {
        if(isLogSwitch) {
            Log.e(TextUtils.isEmpty(t) ? TAG : t, "" + s);
        }
    }
    public static void d(String s) {
        if(isLogSwitch) {
            Log.d(TAG, "" + s);
        }
    }
    public static void d(String t, String s) {
        if(isLogSwitch) {
            Log.d(TextUtils.isEmpty(t) ? TAG : t, "" + s);
        }
    }
}

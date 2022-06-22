package com.inmo.xiaomiwps;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

import com.inmo.xiaomiwps.utils.AccessibilityUtils;
import com.inmo.xiaomiwps.utils.LogUtils;

public class InmoAppKit {
    public static final String VERSION = "1.0.9";
    private Context context;

    public InmoAppKit() {

    }

    public void init(Context context) {
        this.context = context;
        String parentPackageName = "";
        try {
            parentPackageName = context.getPackageManager().
                    getPackageInfo(context.getPackageName(), 0).packageName;
            Log.d(LogUtils.TAG, "current version: " + VERSION);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        String serviceName = parentPackageName + "/com.inmo.xiaomiwps.AppAssitService";

        AccessibilityUtils.getInstance().setAccessibilitySettingsOn(context, serviceName);
//        Settings.Secure.putString(context.getContentResolver(),
//                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES,
//                "com.inmo.xiaomiwps/com.inmo.xiaomiwps.AppAssitService");


    }

    private int getAccessibilityEnabled() {
        int accessibilityEnabled = -1;
        try {
            accessibilityEnabled = Settings.Secure.getInt(context.getApplicationContext().getContentResolver(),
                    Settings.Secure.ACCESSIBILITY_ENABLED);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        return accessibilityEnabled;
    }

    public void startAppKit() {
        if (context != null) {
            if (getAccessibilityEnabled() == 0) {
                init(context);
            }
            Intent intent = new Intent(context, AppAssitService.class);
            context.startService(intent);
        } else {
            Log.i(LogUtils.TAG, "context is null");
        }
    }

    public void stopAppKit() {
        if (context != null) {
            Intent intent = new Intent(context, AppAssitService.class);
            context.stopService(intent);
        } else {
            Log.i(LogUtils.TAG, "context is null");
        }
    }


}

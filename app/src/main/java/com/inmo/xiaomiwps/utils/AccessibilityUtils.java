package com.inmo.xiaomiwps.utils;

import android.content.Context;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

/**
 * private void readComponentNamesFromStringLocked(String names,
 *             Set<ComponentName> outComponentNames,
 *             boolean doMerge) {
 *         if (!doMerge) {
 *             outComponentNames.clear();
 *         }
 *         if (names != null) {
 *             TextUtils.SimpleStringSplitter splitter = mStringColonSplitter;
 *             splitter.setString(names);
 *             while (splitter.hasNext()) {
 *                 String str = splitter.next();
 *                 if (str == null || str.length() <= 0) {
 *                     continue;
 *                 }
 *                 ComponentName enabledService = ComponentName.unflattenFromString(str);
 *                 if (enabledService != null) {
 *                     outComponentNames.add(enabledService);
 *                 }
 *             }
 *         }
 *     }
 */
public class AccessibilityUtils {
    private static AccessibilityUtils instance;

    private AccessibilityUtils() {

    }

    public static AccessibilityUtils getInstance() {
        if(instance == null) {
            instance = new AccessibilityUtils();
        }
        return instance;
    }

    /**
     * 检测辅助功能是否开启
     *
     * @param context
     * @return boolean
     */
    public boolean setAccessibilitySettingsOn(Context context, String serviceName) {
        // 对应的服务
        Log.i(LogUtils.TAG, "current app serviceName:" + serviceName);
        int accessibilityEnabled = getAccessibilityEnabled(context);
        Log.v(LogUtils.TAG, "current accessibilityEnabled = " + accessibilityEnabled);
        TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');

        //如果服务总开关没打开，则打开开关
//        if (accessibilityEnabled == 0) {
//            Settings.Secure.putInt(context.getContentResolver(),
//                    Settings.Secure.ACCESSIBILITY_ENABLED, 1);
//        }
        Settings.Secure.putInt(context.getContentResolver(),
                Settings.Secure.ACCESSIBILITY_ENABLED, 1);

//        if (accessibilityEnabled == 1) {
            Log.v(LogUtils.TAG, "***ACCESSIBILITY IS ENABLED*** -----------------");
            String currentAccessibilityList = getAppAccessibilityName(context);
            Log.v(LogUtils.TAG, "current running serviceName" + currentAccessibilityList);
            if (currentAccessibilityList != null) {
                mStringColonSplitter.setString(currentAccessibilityList);
                while (mStringColonSplitter.hasNext()) {
                    String accessibilityService = mStringColonSplitter.next();

                    Log.v(LogUtils.TAG, "-------------- > accessibilityService :: " + accessibilityService + " " + serviceName);
                    if (accessibilityService.equalsIgnoreCase(serviceName)) { //找到本app服务在系统列表里
                        Log.v(LogUtils.TAG, "found: " + serviceName + "'s accessibility is switched on!");
                        return true;
                    }
                }
                //如果当前服务列表里没有本app的服务，则在后面追加
                StringBuilder sb = new StringBuilder(currentAccessibilityList);
                sb.append(":");
                sb.append(serviceName);
                Log.i(LogUtils.TAG, "after append list : " + sb.toString());
                Settings.Secure.putString(context.getContentResolver(),
                        Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES,
                        sb.toString());
                return true;
            } else { //如果当前服务列表里没有任何在运行的服务，则直接添加
                Log.i(LogUtils.TAG, "immediate add to list : " + serviceName);
                Settings.Secure.putString(context.getContentResolver(),
                        Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES,
                        serviceName);
                return true;
            }
//        } else {
//            Log.v(LogUtils.TAG, "***ACCESSIBILITY IS DISABLED***");
//        }
//        return false;
    }

    private int getAccessibilityEnabled(Context context) {
        int accessibilityEnabled = -1;
        try {
            accessibilityEnabled = Settings.Secure.getInt(context.getApplicationContext().getContentResolver(),
                    Settings.Secure.ACCESSIBILITY_ENABLED);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        return accessibilityEnabled;
    }

//    private boolean isAppAssitServiceRegister(String appServiceName) {
//        if(TextUtils.isEmpty(appServiceName)) {
//            Log.i(LogUtils.TAG, "isAppAssitServiceRegister appServiceName is null");
//            return false;
//        }
//        return (appServiceName.contains("com.inmo.xiaomiwps.AppAssitService");
//    }

    private String getAppAccessibilityName(Context context) {
        String settingValue = Settings.Secure.getString(context.getApplicationContext().getContentResolver(),
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
        return settingValue;
    }
}

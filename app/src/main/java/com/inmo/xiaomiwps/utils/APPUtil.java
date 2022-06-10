package com.inmo.xiaomiwps.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @desc APP通用工具类
 * @auth AISPEECH
 * @date 2016-01-13
 * @copyright aispeech.com
 */
public class APPUtil {

    private static final String TAG = "AIOS-Adapter-AppUtil";
    private static APPUtil mUtil;
    private static final String APP_PRIORITY_CONFIG = "configs/app_priority.xml";

    private Context mContext;
    private Process process;
    private Map<String, Integer> mapPriority = new HashMap<String, Integer>();


//    public static synchronized APPUtil getInstance(Context context) {
//
//        if (mUtil == null) {
//            mUtil = new APPUtil();
//        }
//        return mUtil;
//    }

    public APPUtil(Context context) {
        this.mContext = context;
//        initPriority();
    }



    /**
     * 根据ShareID判断
     *
     * @return 是否是系统级应用
     */
    public boolean isSystemUid() {
        String sharedUserId = "";
        try {
            PackageInfo pi = mContext.getPackageManager().getPackageInfo(
                    mContext.getPackageName(), 0);
            sharedUserId = pi.sharedUserId;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return null != sharedUserId && sharedUserId.equals("android.uid.system");
    }

    /**
     * 通过包名检测APP是否安装
     *
     * @param packageName 包名
     * @return true or false
     */
    public boolean isInstalled(String packageName) {
        boolean isInstalled = false;

        if (!TextUtils.isEmpty(packageName)) {
            PackageInfo packageInfo;
            try {
                packageInfo = mContext.getPackageManager().getPackageInfo(packageName, 0);
            } catch (PackageManager.NameNotFoundException e) {
                packageInfo = null;
                e.printStackTrace();
            }

            if (packageInfo == null) {
                isInstalled = false;
            } else {
                isInstalled = true;
            }
        }
        LogUtils.i(TAG, packageName + "is installed ? " + isInstalled);
        return isInstalled;
    }

    /**
     * 听过包名检测APP是否运行
     *
     * @param packName
     * @return
     */
    public boolean isRunning(String packName) {

        ActivityManager am = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(100);
        boolean isAppRunning = false;
        for (ActivityManager.RunningTaskInfo info : list) {
            if (info.topActivity.getPackageName().equals(packName) || info.baseActivity.getPackageName().equals(packName)) {
                isAppRunning = true;
                break;
            }
        }
        LogUtils.i(TAG, packName + "is running ? " + isAppRunning);
        return isAppRunning;
    }

    /**
     * 根据包名强行停止一个应用，需要系统签名，并且push到/system/app目录下
     *
     * @param pkgName 包名
     */
    public void forceStopPackage(String pkgName) {
        ActivityManager am = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        try {
            Method method = Class.forName("android.app.ActivityManager").getMethod("forceStopPackage", String.class);
            method.invoke(am, pkgName);
        } catch (NoSuchMethodException e) {
            LogUtils.e(TAG, "NoSuchMethodException: " + e.toString());
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            LogUtils.e(TAG, "ClassNotFoundException: " + e.toString());
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            LogUtils.e(TAG, "IllegalAccessException "+ e.toString());
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    /**
     * 结束进程
     */
    public void killProcess(String packageName) {
        try {
            process = Runtime.getRuntime().exec("su \n");
            process = Runtime.getRuntime().exec("am force-stop " + packageName + " \n");
            process = Runtime.getRuntime().exec("exit \n");
            LogUtils.e(TAG, "closeApplication " + packageName + " by aios-adapter!!");
        } catch (IOException e) {
            forceStopPackage(packageName);
            e.printStackTrace();
        }
    }

    /**
     * 初始化进程
     */
    private void initProcess() {
        if (process == null) {
            try {
                process = Runtime.getRuntime().exec("su \n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 关闭输出流
     */
    private void closeOutputStream() {
        if (process != null)
            try {
                process.getOutputStream().close();
                process = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    /**
     * 回到主页
     */
    public void goHomePage() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);// 注意
        intent.addCategory(Intent.CATEGORY_HOME);
        mContext.startActivity(intent);
        LogUtils.i(TAG, "go to home page");
    }

    /**
     * 结束掉某个APP，需传入包名
     *
     * @param pkg 应用包名
     */
    public boolean closeApplication(String pkg) {
        if (!isInstalled(pkg)) {
            LogUtils.d(TAG, "在本地找不到此应用！");
            return false;
        }
        if (isRunning(pkg)) {
            LogUtils.d(TAG, "closeApplication application!!!");
            try{
                initProcess();
                killProcess(pkg);
                closeOutputStream();
            }catch(Exception e){
                forceStopPackage(pkg);
            }
            return true;
        } else {
            LogUtils.d(TAG, "无运行中的此应用");
            return false;
        }
    }

    /**
     * 通过包名打开应用 返回true:成功 返回false:失败
     */
    public boolean openApplication(String pkgName) {
        if (TextUtils.isEmpty(pkgName)) {
            return false;
        }

        LogUtils.i(TAG, "The package will be open : " + pkgName);
        Intent intent = mContext.getPackageManager().getLaunchIntentForPackage(pkgName);
        if (intent == null) {
            return false;
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
        return true;
    }

    public void ShowPriorityActivity() {
        ActivityManager am = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(2);
        if (list.size() >= 2) {
            int top0 = mapPriority.get(list.get(0).baseActivity.getPackageName())
                    == null ? 100 : mapPriority.get(list.get(0).baseActivity.getPackageName());
            int top1 = mapPriority.get(list.get(1).baseActivity.getPackageName())
                    == null ? 100 : mapPriority.get(list.get(1).baseActivity.getPackageName());
            LogUtils.d(TAG ,"top0：" + list.get(0).baseActivity.getPackageName()  +"-"+top0 +
                    ",top1：" + list.get(1).baseActivity.getPackageName()  +"-"+top1);
            if (top0 == 100 || top1 == 100) {
                return;
            }
            if (top0 > top1) {
                openApplication(list.get(1).baseActivity.getPackageName());
            }
        }
    }

    /**
     * 判断当前的任务栈是否含有比  讲故事/笑话等这类多媒体播报以及天气、股票、限行等这类即时资讯信息展示UI 优先级更高的应用
     */
    public boolean isPriorityActivityExist(){
        ActivityManager am = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(1);

        int priority = mapPriority.get(list.get(0).baseActivity.getPackageName())
                == null ? 100 : mapPriority.get(list.get(0).baseActivity.getPackageName());
        LogUtils.d(TAG , "isPriorityActivityExist：" + list.get(0).baseActivity.getPackageName()  +"-"+priority );

        return priority < 4;
    }

    public  String getRunningActivityName() {
        ActivityManager activityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        String runningActivity = activityManager.getRunningTasks(1).get(0).topActivity
                .getClassName();
        return runningActivity;
    }

}

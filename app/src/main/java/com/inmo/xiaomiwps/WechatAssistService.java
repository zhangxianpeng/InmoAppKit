package com.inmo.xiaomiwps;

import android.accessibilityservice.AccessibilityService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.List;

/**
 * author lijianwen 20220509
 * 微信辅助服务
 */
public class WechatAssistService extends AccessibilityService {
    private static final String TAG = "WechatAssistService";
    private static final String WECHAT_NAME = "com.tencent.mm";

    @Override
    public void onCreate() {
        super.onCreate();
        IntentFilter intentFilter = new IntentFilter("com.ljw.testwindow");
        registerReceiver(receiver, intentFilter);
        Log.i(TAG, "oncreate");

    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            getRootWindows();
        }
    };

    private void getRootWindows() {
//        if (event.getPackageName() != null && event.getPackageName().toString().contains("com.tencent.mm")) {
//            AccessibilityNodeInfo source = event.getSource();
//            if(source != null) {
//                List<AccessibilityNodeInfo> list = source.findAccessibilityNodeInfosByText("登录");
//                if (list != null && !list.isEmpty()) {
//                    Log.i(TAG, "find login info: " + list.get(0).getText());
//                }
//            }
//        }
        AccessibilityNodeInfo root = getRootInActiveWindow();
        List<AccessibilityNodeInfo> infos = root.findAccessibilityNodeInfosByText("登录");
        if(infos != null && infos.size() > 0) {
            for(AccessibilityNodeInfo info : infos) {
                if(info != null && info.getPackageName().toString().contains(WECHAT_NAME)) {
                    if("登录".equalsIgnoreCase(info.getText().toString().trim())) { //未登录
                        info.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        Rect rect = new Rect();
                        info.getBoundsInScreen(rect);
                        Log.i(TAG, "login centerx: " + rect.centerX() + ", centery: " + rect.centerY());
                    }
                }
            }
        } else {
            Log.i(TAG, "find not info: ");
        }
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {

    }

    @Override
    public void onInterrupt() {

    }
}

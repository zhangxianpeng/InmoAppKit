package com.inmo.xiaomiwps;

import android.accessibilityservice.AccessibilityService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.inmo.xiaomiwps.node.LesinkNode;
import com.inmo.xiaomiwps.node.Node;
import com.inmo.xiaomiwps.node.XiaomiWpsNode;
import com.inmo.xiaomiwps.utils.LogUtils;
import com.inmo.xiaomiwps.utils.ProcessUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * author lijianwen 2022.5.5
 * APP 辅助服务
 */
public class AppAssitService extends AccessibilityService {
    private static final String TAG = "AppAssitService";
    private static final String SENSOR_CONTROL = "com.inmoglass.sensorcontrol";//屏蔽头控窗体变化
    private Map<String, Node> nodeMap = new HashMap<>();
    private Node xiaoMiWpsNode;
    private Node lesinkNode;

    @Override
    public void onCreate() {
        initNodes();
        super.onCreate();
        Log.i(TAG, "AppAssitService onCreate");
//        IntentFilter intentFilter = new IntentFilter("com.ljw.testroot");
//        registerReceiver(receiver, intentFilter);
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            Log.i(TAG, "rootname: " + ProcessUtils.getForegroundProcessName(context) + "ss: " + (5 / 0));
        }
    };

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
    }

    @Override
    protected boolean onKeyEvent(KeyEvent event) {
        LogUtils.i(TAG, "onKeyEvent: " + event.getKeyCode());
        processKeyevent(event);
        return super.onKeyEvent(event);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
//        AccessibilityNodeInfo nodeInfo = event.getSource();//当前界面的可访问节点信息
        if (event == null || event.getPackageName() == null) {
            return;
        }
        if (!isValidEvent(event)) {
            return;
        }

        processAccessbilityEvent(event);

    }

    /**
     * 创建node
     */
    private void initNodes() {
        xiaoMiWpsNode = new XiaomiWpsNode(this);
        nodeMap.put(XiaomiWpsNode.XIAOMI_WPS_PAGE, xiaoMiWpsNode);
        lesinkNode = new LesinkNode(this);
        nodeMap.put(LesinkNode.LESINK_PAGE, lesinkNode);

        for (Node node : nodeMap.values()) {
            node.init();
        }
    }

    /**
     * node处理event事件
     */
    private void processAccessbilityEvent(AccessibilityEvent event) {
        for (Node node : nodeMap.values()) {
            node.processAccessibilityEvent(event);
        }
    }

    /**
     * node处理keyevent事件
     */
    private void processKeyevent(KeyEvent event) {
        String rootPackageName = getRootWindowPackageName();
        Node node = nodeMap.get(rootPackageName);
        if (node != null) {
            node.processKeyEvent(event);
        }
    }

    /**
     * 销毁node
     *
     * @return
     */
    private void destroyNodes() {
        for (Node node : nodeMap.values()) {
            node.destroy();
        }
    }

    /**
     * 获取当前置顶的页面
     */
    private String getRootWindowPackageName() {
        AccessibilityNodeInfo rootnodeInfo = getRootInActiveWindow();
        if (rootnodeInfo != null && rootnodeInfo.getPackageName() != null) {
            return rootnodeInfo.getPackageName().toString().trim();
        }
        return null;
    }

    /**
     * 规避某些窗体事件
     *
     * @param event
     * @return
     */
    private boolean isValidEvent(AccessibilityEvent event) {
//                        || event.getPackageName().toString().trim().equalsIgnoreCase(XIAOMI_WPS)
        if (event.getPackageName().toString().trim().equalsIgnoreCase(SENSOR_CONTROL)) {
            return false;
        }
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "AppAssitService onDestroy");
        destroyNodes();
    }

    @Override
    public void onInterrupt() {
        Log.i(TAG, "AppAssitService onInterrupt");
    }
}

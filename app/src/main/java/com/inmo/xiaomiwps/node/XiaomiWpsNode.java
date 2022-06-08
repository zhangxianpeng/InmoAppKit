package com.inmo.xiaomiwps.node;

import android.accessibilityservice.AccessibilityService;
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import androidx.annotation.NonNull;

import com.inmo.xiaomiwps.App;
import com.inmo.xiaomiwps.R;
import com.inmo.xiaomiwps.WindowsChangeEvent;
import com.inmo.xiaomiwps.utils.AutoControlUtil;
import com.inmo.xiaomiwps.utils.LogUtils;
import com.inmo.xiaomiwps.utils.ProcessUtils;


import static android.view.KeyEvent.KEYCODE_DPAD_DOWN_LEFT;
import static android.view.KeyEvent.KEYCODE_DPAD_DOWN_RIGHT;

/**
 * 监控xiaomiwps
 */
public class XiaomiWpsNode implements Node {
    private static final String TAG = "XiaomiWpsNode";
    public static final String XIAOMI_WPS_PAGE = "cn.wps.moffice_eng.xiaomi.lite";
    public static final String XIAOMI_DOC_PAGE = "cn.wps.moffice_eng.xiaomi.lite:writer";
    public static final String XIAOMI_WPS = "com.inmo.xiaomiwps";
    public static final String XIAOMI_CRASH_ACTIVITY = "cn.wps.moffice.plugin.app.crash.CrashActivity";

    private boolean isFirstEnter = false;
    private AutoControlUtil controlUtil;
    private Context context;
    private static final int NEXT_PAGE = 290;
    private static final int PRE_PAGE = 291;
    private static final int BACK = 289;
    WindowManager windowManager;
    WindowManager.LayoutParams layoutParams;
    private View tipView;

    public XiaomiWpsNode(Context context) {
        this.context = context;
    }

    @Override
    public void init() {
        controlUtil = new AutoControlUtil();
        initThread();
    }

    @Override
    public void processKeyEvent(KeyEvent event) {
        LogUtils.i(TAG, "onKeyEvent: " + event.getKeyCode());
        switch (event.getKeyCode()) {
            case NEXT_PAGE:
            case KEYCODE_DPAD_DOWN_RIGHT:
                LogUtils.i(TAG, "isXiaoMiDocRun: " + isXiaoMiDocRun());
                if (isNeedToEnterPlay()) {
                    handler.removeMessages(NEED_CLICKPLAY);
                    handler.sendEmptyMessageDelayed(NEED_CLICKPLAY, 100);
                } else {
                    if (isXiaoMiDocRun()) {
                        mClickHandler.removeMessages(SWIPE_NEXT_PAGE);
                        mClickHandler.sendEmptyMessageDelayed(SWIPE_NEXT_PAGE, 100);
                    } else {
                        mClickHandler.removeMessages(CLICK_NEXT_PAGE);
                        mClickHandler.sendEmptyMessageDelayed(CLICK_NEXT_PAGE, 100);
                    }
                }
                break;
            case PRE_PAGE:
            case KEYCODE_DPAD_DOWN_LEFT:
                if (isNeedToEnterPlay()) {
                    handler.removeMessages(NEED_CLICKPLAY);
                    handler.sendEmptyMessageDelayed(NEED_CLICKPLAY, 100);
                } else {
                    if (isXiaoMiDocRun()) {
                        mClickHandler.removeMessages(SWIPE_PREV_PAGE);
                        mClickHandler.sendEmptyMessageDelayed(SWIPE_PREV_PAGE, 100);
                    } else {
                        mClickHandler.removeMessages(CLICK_PREV_PAGE);
                        mClickHandler.sendEmptyMessageDelayed(CLICK_PREV_PAGE, 100);
                    }
                }

                break;
//            case BACK:
//                if(handler.hasMessages(SYSTEM_HOME)) {
//                    handler.removeMessages(SYSTEM_HOME);
//                }
//                handler.sendEmptyMessageDelayed(SYSTEM_HOME, 200);
//                break;
        }
    }

    @Override
    public void processAccessibilityEvent(AccessibilityEvent event) {
        LogUtils.i(TAG, "onAccessibilityEvent: " + event.getEventType() + "toString: " + event.toString());
//        if (isToolbarPage(event)) { //第一次进应用，包名不一样
//            handler.removeMessages(NEED_CLICKPLAY);
//            handler.sendEmptyMessageDelayed(NEED_CLICKPLAY, 100);
//        }
//        event.getEventType() == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED
//                ||
        if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
//            if(isXiaoMiWpsCrash(event)) {
//
//            }
            if (isXiaoMiWpsRun()) {
                if (!isFirstEnter) {
                    LogUtils.i(TAG, "enter xxxiaomi");
                    isFirstEnter = true;
                    handler.removeMessages(ENTER_XIAOMIWPS);
                    handler.sendEmptyMessageDelayed(ENTER_XIAOMIWPS, 100);

                }

                //                    if (isNeedToEnterPlay()) {
//                        handler.removeMessages(NEED_CLICKPLAY);
//                        handler.sendEmptyMessageDelayed(NEED_CLICKPLAY, 100);
//                    }

//                else if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED) {
//                    handler.removeMessages(NEED_CLICKPLAY);
//                    handler.sendEmptyMessageDelayed(NEED_CLICKPLAY, 100);
//                }
            } else {
                if (isFirstEnter) {
                    LogUtils.i(TAG, "exit xxxiaomi");
                    handler.removeMessages(EXIT_XIAOMIWPS);
                    handler.sendEmptyMessageDelayed(EXIT_XIAOMIWPS, 100);
                    isFirstEnter = false;
                }
            }
        }
    }

    @Override
    public void destroy() {
        if (mHandlerThread != null) {
            mHandlerThread.quitSafely();
        }

        if (mClickHandler != null) {
            mClickHandler.removeCallbacksAndMessages(null);
        }

        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
        isFirstEnter = false;
    }

    /**
     * 获取正在运行的进程名字
     */
    private boolean isXiaoMiWpsRun() {
        return ProcessUtils.getForegroundProcessName(context).contains(XIAOMI_WPS_PAGE);
    }

    /**
     * 小米wps crash
     */
    private boolean isXiaoMiWpsCrash(AccessibilityEvent crashInfo) {
        if(crashInfo == null) {
            return false;
        }
        return crashInfo.getClassName().toString().trim().contains(XIAOMI_CRASH_ACTIVITY);
    }

    /**
     * 是否打开Doc文件
     */
    private boolean isXiaoMiDocRun() {
        return ProcessUtils.getForegroundProcessName(context).contains(XIAOMI_DOC_PAGE);
    }

    private static final int CHILD_LEVEL = 4;

    /**
     * 是否需要点击播放按钮
     *
     * @return
     */
    private boolean isNeedToEnterPlay() {
        AccessibilityNodeInfo rootnodeInfo = ((AccessibilityService) context).getRootInActiveWindow();

//        AccessibilityNodeInfo firstNode = rootnodeInfo.getChild(0);
//        if(firstNode == null) {
//            return false;
//        }

        int currentLevel = 0;

        while (currentLevel < CHILD_LEVEL && rootnodeInfo != null) {
            currentLevel += 1;
            rootnodeInfo = rootnodeInfo.getChild(0);
        }

        if (rootnodeInfo == null) {
            return false;
        } else {
            LogUtils.i(TAG, "rootnodeInfo count: " + rootnodeInfo.getChildCount());
            if (rootnodeInfo.getChildCount() > 4) {//大于4说明有工具栏
                return true;
            } else {
                return false;
            }
        }

//        int count = rootnodeInfo
//                .getChild(0)
//                .getChild(0)
//                .getChild(0)
//                .getChild(0)
//                .getChild(0)
//                .getChild(0)
//                .getChildCount();

    }

    /**
     * 是否处于小米wps的阅读页面
     *
     * @param packageName
     * @return
     */
    private boolean isXiaoMiPage(String packageName) {
        return packageName.trim().equalsIgnoreCase(XIAOMI_WPS_PAGE);
    }

    /**
     * 是否在工具条页面
     */
    private boolean isToolbarPage(AccessibilityEvent event) {
        boolean ToolbarPage = event.getEventType() == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED &&
                event.getPackageName().toString().trim().equalsIgnoreCase(XIAOMI_WPS) &&
                (event.getClassName() != null && event.getClassName().toString().equalsIgnoreCase("android.widget.LinearLayout"));
        return ToolbarPage;
    }

    private HandlerThread mHandlerThread;
    private Handler mClickHandler;//串行执行命令
    private static final int CLICK_ENTER_KEY = 0x01;
    private static final int CLICK_NEXT_PAGE = 0x02;
    private static final int CLICK_PREV_PAGE = 0x11;

    private static final int SWIPE_NEXT_PAGE = 0x05;
    private static final int SWIPE_PREV_PAGE = 0x06;
    private static final int XIAOMI_CRASH_CLICK = 0x20;

    private void initThread() {
        mHandlerThread = new HandlerThread("xiaomiwps_contrlthread");
        mHandlerThread.start();
        mClickHandler = new Handler(mHandlerThread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case CLICK_ENTER_KEY:
                        LogUtils.i(TAG, "testquit CLICK_ENTER_KEY");
                        if (isXiaoMiWpsRun()) {
//                            controlUtil.simulateClick(159, 369);
                            controlUtil.simulateClick(270, 366);
                        }
                        break;
                    case CLICK_NEXT_PAGE:
                        if (isFirstEnter && isXiaoMiWpsRun()) {
                            LogUtils.i(TAG, "testquit CLICK_NEXT_PAGE");
                            controlUtil.simulateClick(560, 212);
                        }
                        break;
                    case CLICK_PREV_PAGE:
                        if (isFirstEnter && isXiaoMiWpsRun()) {
                            LogUtils.i(TAG, "testquit CLICK_PREV_PAGE");
                            controlUtil.simulateClick(83, 212);
                        }
                        break;
                    case SWIPE_NEXT_PAGE:
                        if (isFirstEnter && isXiaoMiWpsRun()) {
                            LogUtils.i(TAG, "testquit SWIPE_NEXT_PAGE");
                            controlUtil.simulateSwipe(320, 300, true, 10);
                        }
                        break;
                    case SWIPE_PREV_PAGE:
                        if (isFirstEnter && isXiaoMiWpsRun()) {
                            LogUtils.i(TAG, "testquit SWIPE_PREV_PAGE");
                            controlUtil.simulateSwipe(320, 100, false, 10);
                        }
                        break;
                    case KeyEvent.KEYCODE_BACK:
                        if(isXiaoMiWpsRun()) {
                            LogUtils.i(TAG, "testquit KEYCODE_BACK");
                            controlUtil.simulateKey(KeyEvent.KEYCODE_BACK);
                        }
                        break;
                    case KeyEvent.KEYCODE_HOME:
                        if(isXiaoMiWpsRun()) {
                            LogUtils.i(TAG, "testquit KEYCODE_HOME");
                            controlUtil.simulateKey(KeyEvent.KEYCODE_HOME);
                        }
                        break;
                    case XIAOMI_CRASH_CLICK:
//                        controlUtil.simulateClick(83, 212);
                        break;
                }
            }
        };

    }

    private static final int ENTER_XIAOMIWPS = 0x01;
    private static final int EXIT_XIAOMIWPS = 0x02;
    private static final int NEED_CLICKPLAY = 0x03;
    private static final int SYSTEM_EXIT = 0x04;
    private static final int SYSTEM_HOME = 0x10;
    private static final int XIAOMI_CRASH = 0x15;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case ENTER_XIAOMIWPS:
//                    Toast.makeText(context, "进入小米wps", Toast.LENGTH_SHORT).show();
                    addTipView();
                    break;
                case EXIT_XIAOMIWPS:
//                    Toast.makeText(context, "退出小米wps", Toast.LENGTH_SHORT).show();
                    mClickHandler.removeCallbacksAndMessages(null);
                    handler.removeCallbacksAndMessages(null);
                    removeTipView();
                    break;
                case NEED_CLICKPLAY:
                    if (isNeedToEnterPlay()) {
                        mClickHandler.sendEmptyMessageDelayed(CLICK_ENTER_KEY, 300);//2秒后点击播放
                    }
                    break;
                case SYSTEM_EXIT:
                    mClickHandler.sendEmptyMessage(KeyEvent.KEYCODE_BACK);
                    break;
                case SYSTEM_HOME:
                    mClickHandler.sendEmptyMessage(KeyEvent.KEYCODE_HOME);
                    break;
                case XIAOMI_CRASH:
                    mClickHandler.sendEmptyMessage(KeyEvent.KEYCODE_HOME);
                    break;
            }
        }
    };

    private void addTipView() {
        if (tipView == null) {
            tipView = View.inflate(context, R.layout.tip_bg, null);
        }
        if (windowManager == null) {
            windowManager = (WindowManager) context.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        }
        if (layoutParams == null) {
            layoutParams = new WindowManager.LayoutParams();
        }
        layoutParams.format = PixelFormat.RGBA_8888;
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
        layoutParams.width = 130;
        layoutParams.height = 40;
        layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;

        layoutParams.gravity = Gravity.TOP | Gravity.LEFT;
        layoutParams.x = 510;
        layoutParams.y = 20;
        windowManager.addView(tipView, layoutParams);
    }

    private void removeTipView() {
        if (windowManager != null && tipView != null) {
            windowManager.removeViewImmediate(tipView);
        }
    }
}

package com.inmo.xiaomiwps.node;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.accessibility.AccessibilityEvent;

import androidx.annotation.NonNull;

import com.inmo.xiaomiwps.utils.AutoControlUtil;
import com.inmo.xiaomiwps.utils.LogUtils;

import java.util.List;

import static android.view.KeyEvent.KEYCODE_BACK;

public class LesinkNode implements Node {
    public static final String LESINK_PAGE = "com.inmo.lesink";
    private Context context;
    private static final String LESINK_ACTIVITY = "com.hpplay.sdk.sink.business.BusinessActivity";
    private AutoControlUtil controlUtil;

    private HandlerThread mHandlerThread;
    private Handler mClickHandler;
    private static final String TAG = "LesinkNode";

    public LesinkNode(Context context) {
        this.context = context;
    }

    @Override
    public void init() {
        LogUtils.d(TAG, "LesinkNode init");
        controlUtil = new AutoControlUtil();
        initThread();
    }

    @Override
    public void processKeyEvent(KeyEvent event) {
        LogUtils.d(TAG, "LesinkNode processKeyEvent:" + event.getKeyCode() + ",currentActivityName:" + getCurrentActivityName(context));
        if (TextUtils.isEmpty(getCurrentActivityName(context))) {
            return;
        }
        if (TextUtils.equals(getCurrentActivityName(context), LESINK_ACTIVITY)) {
            LogUtils.d(TAG, "equal");
            if (event.getKeyCode() == KEYCODE_BACK) {
                handler.sendEmptyMessageDelayed(BACK_EVENT, 100);
            }
        }
    }

    private static final int BACK_EVENT = 0x15;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == BACK_EVENT) {
                mClickHandler.sendEmptyMessage(KEYCODE_BACK);
            }
        }
    };

    private void initThread() {
        mHandlerThread = new HandlerThread("lesink_contrlthread");
        mHandlerThread.start();
        mClickHandler = new Handler(mHandlerThread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == KEYCODE_BACK) {
                    LogUtils.d(TAG, "controlUtil.simulateKey(KeyEvent.KEYCODE_BACK);");
                    controlUtil.simulateKey(KEYCODE_BACK);
                }
            }
        };

    }

    @Override
    public void processAccessibilityEvent(AccessibilityEvent event) {
        LogUtils.d(TAG, "LesinkNode processAccessibilityEvent:" + event.getPackageName());
    }

    @Override
    public void destroy() {
        LogUtils.d(TAG, "LesinkNode destroy");
        if (mHandlerThread != null) {
            mHandlerThread.quitSafely();
        }

        if (mClickHandler != null) {
            mClickHandler.removeCallbacksAndMessages(null);
        }

        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }

    public static String getCurrentActivityName(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Activity.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
        ComponentName componentInfo = taskInfo.get(0).topActivity;
        return componentInfo.getClassName();
    }

}

package com.inmo.xiaomiwps.utils;

import android.app.Instrumentation;
import android.os.SystemClock;
import android.view.MotionEvent;

public class AutoControlUtil {
    private Instrumentation mInstrumentation;

    public AutoControlUtil() {

    }

    //模拟点击事件
    public void simulateClick(final int x, final int y) {
        if (mInstrumentation == null) {
            mInstrumentation = new Instrumentation();
        }
        mInstrumentation.sendPointerSync(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(),
                MotionEvent.ACTION_DOWN, x, y, 0));
        mInstrumentation.sendPointerSync(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(),
                MotionEvent.ACTION_UP, x, y, 0));
    }

    //模拟按下事件
    public void simulateKey(int code) {
        if (mInstrumentation == null) {
            mInstrumentation = new Instrumentation();
        }
        mInstrumentation.sendKeyDownUpSync(code);
    }

    /**
     * 模拟滑动
     * @param x 起始x坐标
     * @param y 起始y坐标
     * @param isUp true：上滑  false：下滑
     * @param step
     */
    public void simulateSwipe(int x, int y, boolean isUp, int step) {
        if (mInstrumentation == null) {
            mInstrumentation = new Instrumentation();
        }
        mInstrumentation.sendPointerSync(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(),
                MotionEvent.ACTION_DOWN, x, y, 0));
        mInstrumentation.sendPointerSync(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(),
                MotionEvent.ACTION_MOVE, x, y, 0));
        mInstrumentation.sendPointerSync(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(),
                MotionEvent.ACTION_MOVE, x, isUp ? (y - step * 1) : (y + step * 1), 0));
        mInstrumentation.sendPointerSync(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(),
                MotionEvent.ACTION_MOVE, x, isUp ? (y - step * 2) : (y + step * 2), 0));
        mInstrumentation.sendPointerSync(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(),
                MotionEvent.ACTION_MOVE, x, isUp ? (y - step * 3) : (y + step * 3), 0));
        mInstrumentation.sendPointerSync(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(),
                MotionEvent.ACTION_MOVE, x, isUp ? (y - step * 4) : (y + step * 4), 0));
        mInstrumentation.sendPointerSync(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(),
                MotionEvent.ACTION_UP, x, isUp ? (y - step * 4) : (y + step * 4), 0));
    }

}

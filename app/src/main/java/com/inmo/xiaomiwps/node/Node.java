package com.inmo.xiaomiwps.node;

import android.view.KeyEvent;
import android.view.accessibility.AccessibilityEvent;

public interface Node {
    void init();
    void processKeyEvent(KeyEvent event);
    void processAccessibilityEvent(AccessibilityEvent event);
    void destroy();
}

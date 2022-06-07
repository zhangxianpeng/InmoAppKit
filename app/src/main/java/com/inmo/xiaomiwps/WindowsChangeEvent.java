package com.inmo.xiaomiwps;

public class WindowsChangeEvent {
    private String type;
    private boolean isOpen;

    public WindowsChangeEvent(String type, boolean isOpen) {
        this.type = type;
        this.isOpen = isOpen;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }
}

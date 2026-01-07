package com.tencent.qqnt.kernel.nativeinterface;

public final class TimeEventOp {
    public int action;
    public TimeEvent events = new TimeEvent();

    public int getAction() {
        return this.action;
    }

    public TimeEvent getEvents() {
        return this.events;
    }
}

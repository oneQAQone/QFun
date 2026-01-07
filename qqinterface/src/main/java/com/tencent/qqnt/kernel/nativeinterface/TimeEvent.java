package com.tencent.qqnt.kernel.nativeinterface;

public final class TimeEvent {
    public String content = "";
    public String copywriter = "";
    public long time;
    public int type;

    public String getContent() {
        return this.content;
    }

    public String getCopywriter() {
        return this.copywriter;
    }

    public long getTime() {
        return this.time;
    }

    public int getType() {
        return this.type;
    }
}

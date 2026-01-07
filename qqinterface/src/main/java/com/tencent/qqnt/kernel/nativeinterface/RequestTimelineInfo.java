package com.tencent.qqnt.kernel.nativeinterface;

public final class RequestTimelineInfo {
    public long requestInvokeTime;
    public long requestSendTime;
    public long responseCallbackTime;
    public long responseRecvTime;

    public long getRequestInvokeTime() {
        return this.requestInvokeTime;
    }

    public long getRequestSendTime() {
        return this.requestSendTime;
    }

    public long getResponseCallbackTime() {
        return this.responseCallbackTime;
    }

    public long getResponseRecvTime() {
        return this.responseRecvTime;
    }

    public void setRequestInvokeTime(long j) {
        this.requestInvokeTime = j;
    }

    public void setRequestSendTime(long j) {
        this.requestSendTime = j;
    }

    public void setResponseCallbackTime(long j) {
        this.responseCallbackTime = j;
    }

    public void setResponseRecvTime(long j) {
        this.responseRecvTime = j;
    }
}

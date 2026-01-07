package com.tencent.qqnt.kernel.nativeinterface;

import java.util.ArrayList;

public final class GroupAudioMemNumChangeNotifyInfo {
    public int businessType;
    public int expireTimestamp;
    public long groupCode;
    public boolean isNativeTimeout;
    public ArrayList<Long> memberUins;
    public int onlineCount;
    public long seq;

    public GroupAudioMemNumChangeNotifyInfo() {
        this.memberUins = new ArrayList<>();
    }

    public int getBusinessType() {
        return this.businessType;
    }

    public int getExpireTimestamp() {
        return this.expireTimestamp;
    }

    public long getGroupCode() {
        return this.groupCode;
    }

    public boolean getIsNativeTimeout() {
        return this.isNativeTimeout;
    }

    public ArrayList<Long> getMemberUins() {
        return this.memberUins;
    }

    public int getOnlineCount() {
        return this.onlineCount;
    }

    public long getSeq() {
        return this.seq;
    }

    public GroupAudioMemNumChangeNotifyInfo(long j, int i, int i2, ArrayList<Long> arrayList, long j2, int i3, boolean z) {
        this.groupCode = j;
        this.onlineCount = i;
        this.expireTimestamp = i2;
        this.memberUins = arrayList;
        this.seq = j2;
        this.businessType = i3;
        this.isNativeTimeout = z;
    }
}

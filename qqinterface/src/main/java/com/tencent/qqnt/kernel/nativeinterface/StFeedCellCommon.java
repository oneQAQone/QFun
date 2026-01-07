package com.tencent.qqnt.kernel.nativeinterface;

import java.util.ArrayList;

public final class StFeedCellCommon {
    public int appId;
    public int bizId;
    public String clientKey;
    public String curLikeKey;
    public ArrayList<BytesEntry> extendInfo;
    public String feedId;
    public int subId;
    public long time;

    public StFeedCellCommon() {
        this.feedId = "";
        this.curLikeKey = "";
        this.clientKey = "";
        this.extendInfo = new ArrayList<>();
    }

    public int getAppId() {
        return this.appId;
    }

    public int getBizId() {
        return this.bizId;
    }

    public String getClientKey() {
        return this.clientKey;
    }

    public String getCurLikeKey() {
        return this.curLikeKey;
    }

    public ArrayList<BytesEntry> getExtendInfo() {
        return this.extendInfo;
    }

    public String getFeedId() {
        return this.feedId;
    }

    public int getSubId() {
        return this.subId;
    }

    public long getTime() {
        return this.time;
    }

    public void setAppId(int i) {
        this.appId = i;
    }

    public void setBizId(int i) {
        this.bizId = i;
    }

    public void setClientKey(String str) {
        this.clientKey = str;
    }

    public void setCurLikeKey(String str) {
        this.curLikeKey = str;
    }

    public void setExtendInfo(ArrayList<BytesEntry> arrayList) {
        this.extendInfo = arrayList;
    }

    public void setFeedId(String str) {
        this.feedId = str;
    }

    public void setSubId(int i) {
        this.subId = i;
    }

    public void setTime(long j) {
        this.time = j;
    }

    public StFeedCellCommon(int i, int i2, long j, String str, String str2, int i3, String str3, ArrayList<BytesEntry> arrayList) {
        this.feedId = "";
        this.curLikeKey = "";
        this.clientKey = "";
        this.appId = i;
        this.subId = i2;
        this.time = j;
        this.feedId = str;
        this.curLikeKey = str2;
        this.bizId = i3;
        this.clientKey = str3;
        this.extendInfo = arrayList;
    }
}

package com.tencent.qqnt.kernel.nativeinterface;

import java.util.ArrayList;

public final class StCommonExt {
    public ArrayList<Entry> mapInfo = new ArrayList<>();
    public String attachInfo = "";
    public ArrayList<BytesEntry> mapBytesInfo = new ArrayList<>();
    public ArrayList<StUserAccountEntry> mapUserAccount = new ArrayList<>();

    public String getAttachInfo() {
        return this.attachInfo;
    }

    public ArrayList<BytesEntry> getMapBytesInfo() {
        return this.mapBytesInfo;
    }

    public ArrayList<Entry> getMapInfo() {
        return this.mapInfo;
    }

    public ArrayList<StUserAccountEntry> getMapUserAccount() {
        return this.mapUserAccount;
    }

    public void setAttachInfo(String str) {
        this.attachInfo = str;
    }

    public void setMapBytesInfo(ArrayList<BytesEntry> arrayList) {
        this.mapBytesInfo = arrayList;
    }

    public void setMapInfo(ArrayList<Entry> arrayList) {
        this.mapInfo = arrayList;
    }

    public void setMapUserAccount(ArrayList<StUserAccountEntry> arrayList) {
        this.mapUserAccount = arrayList;
    }
}

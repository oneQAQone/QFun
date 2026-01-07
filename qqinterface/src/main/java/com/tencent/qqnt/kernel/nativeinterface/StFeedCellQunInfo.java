package com.tencent.qqnt.kernel.nativeinterface;

public final class StFeedCellQunInfo {
    public String qunId;

    public StFeedCellQunInfo() {
        this.qunId = "";
    }

    public String getQunId() {
        return this.qunId;
    }

    public StFeedCellQunInfo(String str) {
        this.qunId = str;
    }
}

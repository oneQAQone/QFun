package com.tencent.qqnt.kernel.nativeinterface;

import java.util.ArrayList;

public final class MemoryInfo {
    public ArrayList<MemoryVideo> memoryList = new ArrayList<>();
    public String aggregationPageUrl = "";

    public String getAggregationPageUrl() {
        return this.aggregationPageUrl;
    }

    public ArrayList<MemoryVideo> getMemoryList() {
        return this.memoryList;
    }
}

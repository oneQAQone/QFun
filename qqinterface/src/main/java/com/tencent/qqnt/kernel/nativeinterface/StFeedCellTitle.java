package com.tencent.qqnt.kernel.nativeinterface;

import java.util.ArrayList;

public final class StFeedCellTitle {
    public ArrayList<StRichMsg> title = new ArrayList<>();
    public String titleUrl = "";

    public ArrayList<StRichMsg> getTitle() {
        return this.title;
    }

    public String getTitleUrl() {
        return this.titleUrl;
    }
}

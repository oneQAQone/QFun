package com.tencent.qqnt.kernel.nativeinterface;

import java.util.ArrayList;

public final class Picture {
    public String id = "";
    public ArrayList<URL> urls = new ArrayList<>();
    public String localCachePath = "";

    public String getId() {
        return this.id;
    }

    public String getLocalCachePath() {
        return this.localCachePath;
    }

    public ArrayList<URL> getUrls() {
        return this.urls;
    }
}

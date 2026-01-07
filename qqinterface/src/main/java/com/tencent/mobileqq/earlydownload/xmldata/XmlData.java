package com.tencent.mobileqq.earlydownload.xmldata;

import com.tencent.common.app.AppInterface;

public abstract class XmlData {
    public static final int STATE_LOADING = 2;
    public static final int STATE_NONE = 0;
    public static final int STATE_SUCCESS = 1;

    public String MD5;
    public boolean StoreBackup;
    public int Version;
    public long downSize;
    public boolean hasResDownloaded;
    public boolean isUserClick;
    public boolean load2G;
    public boolean load3G;
    public int loadState;
    public boolean loadWifi;
    public boolean net_2_2G;
    public boolean net_2_3G;
    public boolean net_2_wifi;
    public boolean notPreDownloadInLowEndPhone;
    public int reqLoadCount;
    public String strLog;
    public String strPkgName;
    public String strResName;
    public String strResURL_big;
    public String strResURL_small;
    public long tLoadFail;
    public long tStart;
    public long totalSize;

    public XmlData() {
    }

    public static String packageNameOf(AppInterface appInterface, String str) {
        return null;
    }

    public abstract String getSharedPreferencesName();

    public abstract String getStrResName();

    public void updateServerInfo(XmlData xmlData) {
    }
}

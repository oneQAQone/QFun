package com.tencent.mobileqq.qqcommon.api;

import com.tencent.mobileqq.qroute.QRouteApi;

public interface INetworkUtilApi extends QRouteApi {
    int getConnRetryTimes(int i);

    boolean is4G();

    boolean is5G();

    boolean isNetworkAvailable();

    boolean isWifi();
}

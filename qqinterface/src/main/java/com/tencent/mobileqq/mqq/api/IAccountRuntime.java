package com.tencent.mobileqq.mqq.api;

import android.content.Context;
import com.tencent.mobileqq.qroute.QRouteApi;

public interface IAccountRuntime extends QRouteApi {
    String getAccount();

    Context getApplicationContext();
}

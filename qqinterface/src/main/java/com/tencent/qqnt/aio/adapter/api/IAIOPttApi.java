package com.tencent.qqnt.aio.adapter.api;

import com.tencent.mobileqq.qroute.QRouteApi;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import mqq.app.AppRuntime;

public interface IAIOPttApi extends QRouteApi {
    int getPttFileDuration(@NotNull String str);

    boolean isAutoChangeText(@Nullable AppRuntime appRuntime);
}

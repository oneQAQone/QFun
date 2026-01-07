package com.tencent.mobileqq.vas.api;

import com.tencent.mobileqq.vip.IVipStatusManager;

import org.jetbrains.annotations.NotNull;

import mqq.app.api.IRuntimeService;

public interface IVasSingedApi extends IRuntimeService {
    @NotNull
    IVipStatusManager getVipStatus();
}

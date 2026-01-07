package com.tencent.mobileqq.vip;

import com.tencent.mobileqq.vas.api.IVasManager;

import org.jetbrains.annotations.Nullable;

public interface IVipStatusManager extends IVasManager {
    int getPrivilegeFlags(@Nullable String str);

    boolean isBigClub();

    boolean isSVip();

    boolean isStar();

    boolean isSuperQQ();

    boolean isVip();
}

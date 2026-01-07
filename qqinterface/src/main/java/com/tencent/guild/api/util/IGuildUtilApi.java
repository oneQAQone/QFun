package com.tencent.guild.api.util;

import android.content.Context;
import com.tencent.mobileqq.qroute.QRouteApi;
import com.tencent.qqnt.kernel.nativeinterface.NetStatusType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface IGuildUtilApi extends QRouteApi {
    @NotNull
    String getAppVersion();

    @Nullable
    byte[] getGUID();

    @NotNull
    String getNetworkName();

    @NotNull
    NetStatusType getNetworkType();

    boolean isApplicationForeground();

    void showToast(@NotNull Context context, @NotNull CharSequence charSequence, int i);
}

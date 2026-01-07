package com.tencent.mobileqq.unitedconfig_android.api;

import com.tencent.mobileqq.qroute.QRouteApi;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

import kotlin.Deprecated;

public interface IUnitedConfigManager extends QRouteApi {
    boolean fetch(@NotNull ArrayList<String> arrayList, boolean z);

    @Deprecated(message = "开关类型配置已不再使用")
    void fetchSwitch(@NotNull ArrayList<String> arrayList, boolean z);

    int getConfigVersion(@NotNull String str);

    @NotNull
    ArrayList<String> getGroups(@NotNull String str);

    @Nullable
    String getResourcePath(@NotNull String str);

    boolean isSwitchOn(@NotNull String str, boolean z);

    @NotNull
    String loadAsString(@NotNull String str, @NotNull String str2);

    byte[] loadRawConfig(@NotNull String str, byte[] bArr);

    void removeLocal(@NotNull String str);
}

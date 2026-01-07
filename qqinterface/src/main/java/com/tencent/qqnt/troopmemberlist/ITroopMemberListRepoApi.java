package com.tencent.qqnt.troopmemberlist;

import androidx.lifecycle.LifecycleOwner;

import com.tencent.mobileqq.data.troop.TroopMemberInfo;
import com.tencent.mobileqq.data.troop.TroopMemberNickInfo;
import com.tencent.mobileqq.qroute.QRouteApi;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;

public interface ITroopMemberListRepoApi extends QRouteApi {

    void fetchTroopMemberUid(String uin, Function2<? super Boolean, ? super String, Unit> function2);

    void fetchTroopMemberUin(String uid, Function2<? super Boolean, ? super String, Unit> function2);
    @Nullable
    TroopMemberInfo getTroopMemberFromCache(@Nullable String str, @Nullable String str2, @Nullable LifecycleOwner lifecycleOwner, @NotNull String str3);

    void fetchTroopMemberName(@Nullable String str, @Nullable String str2, @Nullable LifecycleOwner lifecycleOwner, @NotNull String str3, @Nullable Function1<? super TroopMemberNickInfo, Unit> function1);

    void fetchTroopMemberName(@Nullable String str, @Nullable String str2, @NotNull String str3, @Nullable Function1<? super TroopMemberNickInfo, Unit> function1);

    @Nullable
    TroopMemberInfo getTroopMemberFromCacheOrFetchAsync(@Nullable String groupId, @Nullable String userId, @Nullable LifecycleOwner lifecycleOwner, @NotNull String str3, @Nullable i gVar);

    @Deprecated(since = "兼容旧逻辑，过渡用，新逻辑不要使用")
    @Nullable
    TroopMemberInfo getTroopMemberInfoSync(@Nullable String str, @Nullable String str2, @Nullable LifecycleOwner lifecycleOwner, @NotNull String str3);

    @Deprecated(since = "兼容旧逻辑，过渡用，新逻辑不要使用")
    @Nullable
    TroopMemberInfo getTroopMemberInfoSync(@Nullable String str, @Nullable String str2, @Nullable LifecycleOwner lifecycleOwner, @NotNull String str3, long j);

    @Nullable
    TroopMemberInfo getTroopMemberWithExtFromCacheOrFetchAsync(@Nullable String str, @Nullable String str2, @Nullable LifecycleOwner lifecycleOwner, @NotNull String str3, @Nullable i iVar);

    @Deprecated(since = "兼容旧逻辑，过渡用，新逻辑不要使用")
    @Nullable
    TroopMemberInfo getTroopMemberWithExtInfoSync(@Nullable String str, @Nullable String str2, @Nullable LifecycleOwner lifecycleOwner, @NotNull String str3);
}

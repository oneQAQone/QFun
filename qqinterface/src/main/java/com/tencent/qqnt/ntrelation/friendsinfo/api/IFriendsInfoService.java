package com.tencent.qqnt.ntrelation.friendsinfo.api;

import com.tencent.mobileqq.qroute.QRouteApi;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface IFriendsInfoService extends QRouteApi {
    List getAllFriend(@Nullable String str);
    String getNickWithUid(@NotNull String uid,@Nullable String str);
    String getRemarkWithUid(@NotNull String uid,@Nullable String str);
    boolean isFriend(@NotNull String uid, @Nullable String str);
}

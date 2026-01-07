package com.tencent.mobileqq.data.troop;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TroopMemberNickInfo {

    @Nullable
    private static Boolean enableShowNameOpt;

    @NotNull
    private final String autoRemark;

    @Nullable
    private String cachePinyinAll;

    @Nullable
    private String cachePinyinFirst;

    @Nullable
    private String cacheShowNameForPinyin;

    @NotNull
    private final String colorNick;
    private final int colorNickId;

    @NotNull
    private final String friendNick;

    @NotNull
    private final String troopNick;

    @NotNull
    private final String troopUin;

    @NotNull
    private final String uid;

    @NotNull
    private final String uin;

    public TroopMemberNickInfo(@NotNull String str, @NotNull String str2, @NotNull String str3, @NotNull String str4, int i, @NotNull String str5, @NotNull String str6, @NotNull String str7, @Nullable String str8, @Nullable String str9, @Nullable String str10) {
        this.troopUin = str;
        this.uin = str2;
        this.uid = str3;
        this.colorNick = str4;
        this.colorNickId = i;
        this.troopNick = str5;
        this.autoRemark = str6;
        this.friendNick = str7;
        this.cacheShowNameForPinyin = str8;
        this.cachePinyinFirst = str9;
        this.cachePinyinAll = str10;
    }

    private String component10() {
        return this.cachePinyinFirst;
    }

    private String component11() {
        return this.cachePinyinAll;
    }

    private String component9() {
        return this.cacheShowNameForPinyin;
    }

    @NotNull
    public final String component1() {
        return this.troopUin;
    }

    @NotNull
    public final String component2() {
        return this.uin;
    }

    @NotNull
    public final String component3() {
        return this.uid;
    }

    @NotNull
    public final String component4() {
        return this.colorNick;
    }

    public final int component5() {
        return this.colorNickId;
    }

    @NotNull
    public final String component6() {
        return this.troopNick;
    }

    @NotNull
    public final String component7() {
        return this.autoRemark;
    }

    @NotNull
    public final String component8() {
        return this.friendNick;
    }

    @NotNull
    public final String getAutoRemark() {
        return this.autoRemark;
    }

    @NotNull
    public final String getColorNick() {
        return this.colorNick;
    }

    public final int getColorNickId() {
        return this.colorNickId;
    }

    @NotNull
    public final String getFriendNick() {
        return this.friendNick;
    }

    @NotNull
    public final String getTroopNick() {
        return this.troopNick;
    }

    @NotNull
    public final String getTroopUin() {
        return this.troopUin;
    }

    @NotNull
    public final String getUid() {
        return this.uid;
    }

    @NotNull
    public final String getUin() {
        return this.uin;
    }

    @NotNull
    public String toString() {
        return "TroopMemberNickInfo(troopUin=" + this.troopUin + ", uin=" + this.uin + ", uid=" + this.uid + ", colorNick=" + this.colorNick + ", colorNickId=" + this.colorNickId + ", troopNick=" + this.troopNick + ", autoRemark=" + this.autoRemark + ", friendNick=" + this.friendNick + ", cacheShowNameForPinyin=" + this.cacheShowNameForPinyin + ", cachePinyinFirst=" + this.cachePinyinFirst + ", cachePinyinAll=" + this.cachePinyinAll + ")";
    }
}

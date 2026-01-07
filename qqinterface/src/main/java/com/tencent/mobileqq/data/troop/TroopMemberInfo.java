package com.tencent.mobileqq.data.troop;

import com.tencent.qqnt.kernelpublic.nativeinterface.MemberRole;
import java.io.Serializable;

public class TroopMemberInfo implements Serializable {

    public static final long VALUE_DISTANCE_TO_SELF_UNKOWN = -100;
    protected static final int VALUE_INVALID = -100;
    public static final long VALUE_MEMBER_CLOSE_SHARE_LBS = -1001;
    public int addState;

    @Deprecated(since = "推荐使用TroopMemberNickInfo")
    public String autoremark;
    public long credit_level;
    public String displayedNamePinyinFirst;

    // @Nullable
    // public TroopMemberInfoExt extInfo;
    public int flagEx = 0;

    @Deprecated(since = "推荐使用TroopMemberNickInfo")
    public String friendnick;
    public long gagTimeStamp;
    public String honorList;
    public boolean isTroopFollowed;
    public long join_time;
    public long last_active_time;
    public int mBigClubVipType;
    public byte mHonorRichFlag;
    public boolean mIsShielded;
    public int mVipType;
    public String memberUid;
    public String memberuin;
    public TroopMemberNickInfo nickInfo;
    public int realLevel;
    public MemberRole role;

    // @Nullable
    // public TroopMemberSpecialTitleInfo specialTitleInfo;
    public int titleId;

    @Deprecated(since = "推荐使用TroopMemberNickInfo")
    public String troopColorNick;

    @Deprecated(since = "推荐使用TroopMemberNickInfo")
    public int troopColorNickId;

    @Deprecated(since = "推荐使用TroopMemberNickInfo")
    public String troopnick;
    public String troopuin;

    public TroopMemberInfo(String str, String str2) {
        this.troopuin = str;
        this.memberuin = str2;
    }

    public boolean isValidMember() {
        MemberRole memberRole = this.role;
        return memberRole != null && memberRole != MemberRole.UNSPECIFIED && memberRole != MemberRole.STRANGER;
    }
}

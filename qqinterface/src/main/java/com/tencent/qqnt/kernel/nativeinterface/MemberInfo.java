package com.tencent.qqnt.kernel.nativeinterface;

import com.tencent.qqnt.kernelpublic.nativeinterface.MemberRole;

public class MemberInfo {
    public int bigClubFlag;
    public int bigClubLevel;
    public int cardNameId;
    public int cardType;
    public int creditLevel;
    public int globalGroupLevel;
    public int globalGroupPoint;
    public boolean isDelete;
    public boolean isRobot;
    public boolean isSpecialConcerned;
    public boolean isSpecialShielded;
    public int joinTime;
    public int lastSpeakTime;
    public int memberFlag;
    public int memberFlagExt;
    public int memberFlagExt2;
    public int memberLevel;
    public int memberMobileFlag;
    public int memberRealLevel;
    public int memberTitleId;
    public int mssVipType;
    public int richFlag;
    public int shutUpTime;
    public long specialTitleExpireTime;
    public long uin;
    public int userShowFlag;
    public int userShowFlagNew;
    long serialVersionUID = 1;
    public String uid = "";
    public String qid = "";
    public String nick = "";
    public String remark = "";
    public String cardName = "";
    public MemberRole role = MemberRole.values()[0];
    public String avatarPath = "";
    public byte[] groupHonor = new byte[0];
    public String memberSpecialTitle = "";
    public String autoRemark = "";

}

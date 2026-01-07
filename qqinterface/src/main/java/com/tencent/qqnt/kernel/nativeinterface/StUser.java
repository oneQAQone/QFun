package com.tencent.qqnt.kernel.nativeinterface;

public final class StUser {
    public int canFollow;
    public String customId;
    public String dittoUin;
    public boolean isSpecial;
    public boolean isSuperLike;
    public boolean isSweet;
    public int isfollowed;
    public String nick;
    public String polyId;
    public String portrait;
    public StarInfo starInfo;
    public String uid;
    public String uin;
    public YellowInfo yellowInfo;

    public StUser() {
        this.uid = "";
        this.nick = "";
        this.yellowInfo = new YellowInfo();
        this.starInfo = new StarInfo();
        this.customId = "";
        this.polyId = "";
        this.portrait = "";
        this.uin = "";
        this.dittoUin = "";
    }

    public int getCanFollow() {
        return this.canFollow;
    }

    public String getCustomId() {
        return this.customId;
    }

    public String getDittoUin() {
        return this.dittoUin;
    }

    public boolean getIsSpecial() {
        return this.isSpecial;
    }

    public boolean getIsSuperLike() {
        return this.isSuperLike;
    }

    public boolean getIsSweet() {
        return this.isSweet;
    }

    public int getIsfollowed() {
        return this.isfollowed;
    }

    public String getNick() {
        return this.nick;
    }

    public String getPolyId() {
        return this.polyId;
    }

    public String getPortrait() {
        return this.portrait;
    }

    public StarInfo getStarInfo() {
        return this.starInfo;
    }

    public String getUid() {
        return this.uid;
    }

    public String getUin() {
        return this.uin;
    }

    public YellowInfo getYellowInfo() {
        return this.yellowInfo;
    }

    public void setCanFollow(int i) {
        this.canFollow = i;
    }

    public void setCustomId(String str) {
        this.customId = str;
    }

    public void setDittoUin(String str) {
        this.dittoUin = str;
    }

    public void setIsSpecial(boolean z) {
        this.isSpecial = z;
    }

    public void setIsSuperLike(boolean z) {
        this.isSuperLike = z;
    }

    public void setIsSweet(boolean z) {
        this.isSweet = z;
    }

    public void setIsfollowed(int i) {
        this.isfollowed = i;
    }

    public void setNick(String str) {
        this.nick = str;
    }

    public void setPolyId(String str) {
        this.polyId = str;
    }

    public void setPortrait(String str) {
        this.portrait = str;
    }

    public void setStarInfo(StarInfo starInfo) {
        this.starInfo = starInfo;
    }

    public void setUid(String str) {
        this.uid = str;
    }

    public void setUin(String str) {
        this.uin = str;
    }

    public void setYellowInfo(YellowInfo yellowInfo) {
        this.yellowInfo = yellowInfo;
    }

    public StUser(String str, String str2, YellowInfo yellowInfo, StarInfo starInfo, boolean z, boolean z2, boolean z3, String str3, String str4, String str5, int i, int i2, String str6, String str7) {
        this.uid = "";
        this.nick = "";
        this.yellowInfo = new YellowInfo();
        this.uid = str;
        this.nick = str2;
        this.yellowInfo = yellowInfo;
        this.starInfo = starInfo;
        this.isSweet = z;
        this.isSpecial = z2;
        this.isSuperLike = z3;
        this.customId = str3;
        this.polyId = str4;
        this.portrait = str5;
        this.canFollow = i;
        this.isfollowed = i2;
        this.uin = str6;
        this.dittoUin = str7;
    }
}

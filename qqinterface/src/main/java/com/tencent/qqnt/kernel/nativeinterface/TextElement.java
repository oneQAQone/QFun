package com.tencent.qqnt.kernel.nativeinterface;

public final class TextElement implements IKernelModel {
    public Long atChannelId;
    public String atNtUid;
    public Integer atRoleColor;
    public Long atRoleId;
    public String atRoleName;
    public long atTinyId;
    public int atType;
    public long atUid;
    public String content;
    public LinkInfo linkInfo;
    public Integer needNotify;
    public Integer subElementType;

    public TextElement() {
        this.content = "";
    }

    public Long getAtChannelId() {
        return this.atChannelId;
    }

    public String getAtNtUid() {
        return this.atNtUid;
    }

    public Integer getAtRoleColor() {
        return this.atRoleColor;
    }

    public Long getAtRoleId() {
        return this.atRoleId;
    }

    public String getAtRoleName() {
        return this.atRoleName;
    }

    public long getAtTinyId() {
        return this.atTinyId;
    }

    public int getAtType() {
        return this.atType;
    }

    public long getAtUid() {
        return this.atUid;
    }

    public String getContent() {
        return this.content;
    }

    public LinkInfo getLinkInfo() {
        return this.linkInfo;
    }

    public Integer getNeedNotify() {
        return this.needNotify;
    }

    public Integer getSubElementType() {
        return this.subElementType;
    }

    public void setAtChannelId(Long l) {
        this.atChannelId = l;
    }

    public void setAtNtUid(String str) {
        this.atNtUid = str;
    }

    public void setAtRoleColor(Integer num) {
        this.atRoleColor = num;
    }

    public void setAtRoleId(Long l) {
        this.atRoleId = l;
    }

    public void setAtRoleName(String str) {
        this.atRoleName = str;
    }

    public void setAtTinyId(long j) {
        this.atTinyId = j;
    }

    public void setAtType(int i) {
        this.atType = i;
    }

    public void setAtUid(long j) {
        this.atUid = j;
    }

    public void setContent(String str) {
        this.content = str;
    }

    public void setLinkInfo(LinkInfo linkInfo) {
        this.linkInfo = linkInfo;
    }

    public void setNeedNotify(Integer num) {
        this.needNotify = num;
    }

    public void setSubElementType(Integer num) {
        this.subElementType = num;
    }

    public TextElement(String str, int i, long j, long j2, String str2, Integer num, Long l, LinkInfo linkInfo, Long l2, Integer num2, String str3, Integer num3) {
        this.content = str;
        this.atType = i;
        this.atUid = j;
        this.atTinyId = j2;
        this.atNtUid = str2;
        this.subElementType = num;
        this.atChannelId = l;
        this.linkInfo = linkInfo;
        this.atRoleId = l2;
        this.atRoleColor = num2;
        this.atRoleName = str3;
        this.needNotify = num3;
    }
}

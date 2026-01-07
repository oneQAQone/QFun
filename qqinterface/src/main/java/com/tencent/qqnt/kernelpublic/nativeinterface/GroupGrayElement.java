package com.tencent.qqnt.kernelpublic.nativeinterface;

import java.io.Serializable;

public final class GroupGrayElement implements Serializable {
    public String adminNick;
    public String adminRemark;
    public String adminUid;
    public CreateGroupGrayElement createGroup;
    public String groupName;
    public MemberAddGrayElement memberAdd;
    public String memberNick;
    public String memberRemark;
    public String memberUid;
    public GroupGrayElementRole role;
    long serialVersionUID;
    public GroupShutUpGrayElement shutUp;
    public GroupGrayElementType type;

    public GroupGrayElement() {
        this.serialVersionUID = 1L;
        this.type = GroupGrayElementType.values()[0];
        this.role = GroupGrayElementRole.values()[0];
        this.groupName = "";
        this.memberUid = "";
        this.memberNick = "";
        this.memberRemark = "";
        this.adminUid = "";
        this.adminNick = "";
        this.adminRemark = "";
    }

    public String getAdminNick() {
        return this.adminNick;
    }

    public String getAdminRemark() {
        return this.adminRemark;
    }

    public String getAdminUid() {
        return this.adminUid;
    }

    public CreateGroupGrayElement getCreateGroup() {
        return this.createGroup;
    }

    public String getGroupName() {
        return this.groupName;
    }

    public MemberAddGrayElement getMemberAdd() {
        return this.memberAdd;
    }

    public String getMemberNick() {
        return this.memberNick;
    }

    public String getMemberRemark() {
        return this.memberRemark;
    }

    public String getMemberUid() {
        return this.memberUid;
    }

    public GroupGrayElementRole getRole() {
        return this.role;
    }

    public GroupShutUpGrayElement getShutUp() {
        return this.shutUp;
    }

    public GroupGrayElementType getType() {
        return this.type;
    }

    public GroupGrayElement(GroupGrayElementType groupGrayElementType, GroupGrayElementRole groupGrayElementRole, String str, String str2, String str3, String str4, String str5, String str6, String str7, CreateGroupGrayElement createGroupGrayElement, MemberAddGrayElement memberAddGrayElement, GroupShutUpGrayElement groupShutUpGrayElement) {
        this.serialVersionUID = 1L;
        this.type = GroupGrayElementType.values()[0];
        GroupGrayElementRole groupGrayElementRole2 = GroupGrayElementRole.values()[0];
        this.type = groupGrayElementType;
        this.role = groupGrayElementRole;
        this.groupName = str;
        this.memberUid = str2;
        this.memberNick = str3;
        this.memberRemark = str4;
        this.adminUid = str5;
        this.adminNick = str6;
        this.adminRemark = str7;
        this.createGroup = createGroupGrayElement;
        this.memberAdd = memberAddGrayElement;
        this.shutUp = groupShutUpGrayElement;
    }
}

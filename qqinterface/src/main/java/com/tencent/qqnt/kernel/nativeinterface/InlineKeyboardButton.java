package com.tencent.qqnt.kernel.nativeinterface;

import java.util.ArrayList;

public final class InlineKeyboardButton {
    public int anchor;
    public boolean atBotShowChannelList;
    public int clickLimit;
    public String data;
    public boolean enter;
    public FeedBackData feedBackData;
    public String groupId;
    public long hideSeconds;
    public String id;
    public boolean isReply;
    public String label;
    public InlineKetBoardModal modal;
    public int permissionType;
    public ArrayList<String> specifyRoleIds;
    public ArrayList<String> specifyTinyids;
    public int style;
    public ArrayList<SubscribeMsgTemplateID> subscribeDataTemplateIds;
    public int type;
    public String unsupportTips;
    public String visitedLabel;

    public InlineKeyboardButton() {
        this.id = "";
        this.label = "";
        this.visitedLabel = "";
        this.unsupportTips = "";
        this.data = "";
        this.specifyRoleIds = new ArrayList<>();
        this.specifyTinyids = new ArrayList<>();
        this.groupId = "";
        this.subscribeDataTemplateIds = new ArrayList<>();
        this.feedBackData = new FeedBackData();
        this.modal = new InlineKetBoardModal();
    }

    public int getAnchor() {
        return this.anchor;
    }

    public boolean getAtBotShowChannelList() {
        return this.atBotShowChannelList;
    }

    public int getClickLimit() {
        return this.clickLimit;
    }

    public String getData() {
        return this.data;
    }

    public boolean getEnter() {
        return this.enter;
    }

    public FeedBackData getFeedBackData() {
        return this.feedBackData;
    }

    public String getGroupId() {
        return this.groupId;
    }

    public long getHideSeconds() {
        return this.hideSeconds;
    }

    public String getId() {
        return this.id;
    }

    public boolean getIsReply() {
        return this.isReply;
    }

    public String getLabel() {
        return this.label;
    }

    public InlineKetBoardModal getModal() {
        return this.modal;
    }

    public int getPermissionType() {
        return this.permissionType;
    }

    public ArrayList<String> getSpecifyRoleIds() {
        return this.specifyRoleIds;
    }

    public ArrayList<String> getSpecifyTinyids() {
        return this.specifyTinyids;
    }

    public int getStyle() {
        return this.style;
    }

    public ArrayList<SubscribeMsgTemplateID> getSubscribeDataTemplateIds() {
        return this.subscribeDataTemplateIds;
    }

    public int getType() {
        return this.type;
    }

    public String getUnsupportTips() {
        return this.unsupportTips;
    }

    public String getVisitedLabel() {
        return this.visitedLabel;
    }

    public InlineKeyboardButton(String str, String str2, String str3, int i, int i2, int i3, String str4, String str5, boolean z, int i4, ArrayList<String> arrayList, ArrayList<String> arrayList2, boolean z2, int i5, boolean z3, ArrayList<SubscribeMsgTemplateID> arrayList3, FeedBackData feedBackData) {
        this.id = "";
        this.label = "";
        this.visitedLabel = "";
        this.unsupportTips = "";
        this.data = "";
        this.specifyRoleIds = new ArrayList<>();
        this.specifyTinyids = new ArrayList<>();
        this.groupId = "";
        this.subscribeDataTemplateIds = new ArrayList<>();
        this.feedBackData = new FeedBackData();
        this.modal = new InlineKetBoardModal();
        this.id = str;
        this.label = str2;
        this.visitedLabel = str3;
        this.style = i;
        this.type = i2;
        this.clickLimit = i3;
        this.unsupportTips = str4;
        this.data = str5;
        this.atBotShowChannelList = z;
        this.permissionType = i4;
        this.specifyRoleIds = arrayList;
        this.specifyTinyids = arrayList2;
        this.isReply = z2;
        this.anchor = i5;
        this.enter = z3;
        this.subscribeDataTemplateIds = arrayList3;
        this.feedBackData = feedBackData;
    }
}

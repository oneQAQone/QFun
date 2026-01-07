package com.tencent.qqnt.kernel.nativeinterface;

import java.util.ArrayList;

public final class TofuRecordElement {
    public ArrayList<TofuActionButton> actionButtons;
    public String background;
    public int beancurstyle;
    public String busiExtra;
    public long busiid;
    public String busiuuid;
    public ArrayList<TofuContent> contentlist;
    public TofuContent descriptionContent;
    public String icon;
    public ArrayList<Long> uinlist;

    public TofuRecordElement() {
        this.busiuuid = "";
        this.uinlist = new ArrayList<>();
        this.actionButtons = new ArrayList<>();
    }

    public ArrayList<TofuActionButton> getActionButtons() {
        return this.actionButtons;
    }

    public String getBackground() {
        return this.background;
    }

    public int getBeancurstyle() {
        return this.beancurstyle;
    }

    public String getBusiExtra() {
        return this.busiExtra;
    }

    public long getBusiid() {
        return this.busiid;
    }

    public String getBusiuuid() {
        return this.busiuuid;
    }

    public ArrayList<TofuContent> getContentlist() {
        return this.contentlist;
    }

    public TofuContent getDescriptionContent() {
        return this.descriptionContent;
    }

    public String getIcon() {
        return this.icon;
    }

    public ArrayList<Long> getUinlist() {
        return this.uinlist;
    }

    public TofuRecordElement(long j, String str, int i, TofuContent tofuContent, ArrayList<TofuContent> arrayList, String str2, String str3, ArrayList<Long> arrayList2, String str4) {
        this.busiuuid = "";
        this.uinlist = new ArrayList<>();
        this.actionButtons = new ArrayList<>();
        this.busiid = j;
        this.busiuuid = str;
        this.beancurstyle = i;
        this.descriptionContent = tofuContent;
        this.contentlist = arrayList;
        this.background = str2;
        this.icon = str3;
        this.uinlist = arrayList2;
        this.busiExtra = str4;
    }
}

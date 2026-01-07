package com.tencent.qqnt.kernel.nativeinterface;

import java.util.ArrayList;

public final class AlbumAccessPermission {
    public int permissionType;
    public boolean supportBlackAndWhiteList;
    public String question = "";
    public String answer = "";
    public ArrayList<StUser> whiteList = new ArrayList<>();
    public ArrayList<StUser> blackList = new ArrayList<>();

    public String getAnswer() {
        return this.answer;
    }

    public ArrayList<StUser> getBlackList() {
        return this.blackList;
    }

    public int getPermissionType() {
        return this.permissionType;
    }

    public String getQuestion() {
        return this.question;
    }

    public boolean getSupportBlackAndWhiteList() {
        return this.supportBlackAndWhiteList;
    }

    public ArrayList<StUser> getWhiteList() {
        return this.whiteList;
    }
}

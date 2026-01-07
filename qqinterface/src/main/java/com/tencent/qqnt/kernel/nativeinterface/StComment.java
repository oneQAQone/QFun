package com.tencent.qqnt.kernel.nativeinterface;

import java.util.ArrayList;

public final class StComment {
    public long replyNum;
    public long time;
    public String id = "";
    public StUser user = new StUser();
    public ArrayList<StRichMsg> content = new ArrayList<>();
    public ArrayList<StReply> replys = new ArrayList<>();
    public String clientKey = "";
    public ArrayList<StMedia> mediaItems = new ArrayList<>();
    public StLike like = new StLike();

    public String getClientKey() {
        return this.clientKey;
    }

    public ArrayList<StRichMsg> getContent() {
        return this.content;
    }

    public String getId() {
        return this.id;
    }

    public StLike getLike() {
        return this.like;
    }

    public ArrayList<StMedia> getMediaItems() {
        return this.mediaItems;
    }

    public long getReplyNum() {
        return this.replyNum;
    }

    public ArrayList<StReply> getReplys() {
        return this.replys;
    }

    public long getTime() {
        return this.time;
    }

    public StUser getUser() {
        return this.user;
    }

    public void setClientKey(String str) {
        this.clientKey = str;
    }

    public void setContent(ArrayList<StRichMsg> arrayList) {
        this.content = arrayList;
    }

    public void setId(String str) {
        this.id = str;
    }

    public void setLike(StLike stLike) {
        this.like = stLike;
    }

    public void setMediaItems(ArrayList<StMedia> arrayList) {
        this.mediaItems = arrayList;
    }

    public void setReplyNum(long j) {
        this.replyNum = j;
    }

    public void setReplys(ArrayList<StReply> arrayList) {
        this.replys = arrayList;
    }

    public void setTime(long j) {
        this.time = j;
    }

    public void setUser(StUser stUser) {
        this.user = stUser;
    }
}

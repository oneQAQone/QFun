package com.tencent.qqnt.kernel.nativeinterface;

import java.util.ArrayList;

public final class StReply {
    public long time;
    public String id = "";
    public StUser user = new StUser();
    public ArrayList<StRichMsg> content = new ArrayList<>();
    public String clientKey = "";
    public StUser targetUser = new StUser();
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

    public StUser getTargetUser() {
        return this.targetUser;
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

    public void setTargetUser(StUser stUser) {
        this.targetUser = stUser;
    }

    public void setTime(long j) {
        this.time = j;
    }

    public void setUser(StUser stUser) {
        this.user = stUser;
    }
}

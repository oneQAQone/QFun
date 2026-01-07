package com.tencent.qqnt.kernel.nativeinterface;

public final class StLike {
    public int count;
    public String id;
    public int ownerStatus;
    public StUser postUser;
    public int status;

    public StLike() {
        this.id = "";
        this.postUser = new StUser();
    }

    public int getCount() {
        return this.count;
    }

    public String getId() {
        return this.id;
    }

    public int getOwnerStatus() {
        return this.ownerStatus;
    }

    public StUser getPostUser() {
        return this.postUser;
    }

    public int getStatus() {
        return this.status;
    }

    public void setCount(int i) {
        this.count = i;
    }

    public void setId(String str) {
        this.id = str;
    }

    public void setOwnerStatus(int i) {
        this.ownerStatus = i;
    }

    public void setPostUser(StUser stUser) {
        this.postUser = stUser;
    }

    public void setStatus(int i) {
        this.status = i;
    }

    public StLike(String str, int i, int i2, StUser stUser, int i3) {
        this.id = "";
        new StUser();
        this.id = str;
        this.count = i;
        this.status = i2;
        this.postUser = stUser;
        this.ownerStatus = i3;
    }
}

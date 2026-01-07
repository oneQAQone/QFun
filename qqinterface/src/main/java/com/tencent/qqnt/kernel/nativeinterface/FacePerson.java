package com.tencent.qqnt.kernel.nativeinterface;

public final class FacePerson {
    public String facePersonId = "";
    public String faceAvatarUrl = "";
    public UserData userData = new UserData();
    public Activity activity = new Activity();
    public AlbumSummary summary = new AlbumSummary();

    public Activity getActivity() {
        return this.activity;
    }

    public String getFaceAvatarUrl() {
        return this.faceAvatarUrl;
    }

    public String getFacePersonId() {
        return this.facePersonId;
    }

    public AlbumSummary getSummary() {
        return this.summary;
    }

    public UserData getUserData() {
        return this.userData;
    }
}

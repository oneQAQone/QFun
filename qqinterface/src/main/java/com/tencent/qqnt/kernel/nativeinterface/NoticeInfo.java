package com.tencent.qqnt.kernel.nativeinterface;

import java.util.ArrayList;

public final class NoticeInfo {
    public long facePersonCount;
    public String noticeId = "";
    public String noticeText = "";
    public ArrayList<FacePerson> facePersons = new ArrayList<>();

    public long getFacePersonCount() {
        return this.facePersonCount;
    }

    public ArrayList<FacePerson> getFacePersons() {
        return this.facePersons;
    }

    public String getNoticeId() {
        return this.noticeId;
    }

    public String getNoticeText() {
        return this.noticeText;
    }
}

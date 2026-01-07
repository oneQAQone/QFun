package com.tencent.qqnt.kernel.nativeinterface;

import java.util.ArrayList;

public final class Notice {
    public long createTime;
    public int noticeType;
    public String noticeId = "";
    public NoticePattonInfo noticePatton = new NoticePattonInfo();
    public StFeed feed = new StFeed();
    public ArrayList<Integer> opMask = new ArrayList<>();
    public StUser opUser = new StUser();
    public PattonAction action = new PattonAction();
    public StCommonExt ext = new StCommonExt();
    public StComment feedComment = new StComment();
    public StReply feedReply = new StReply();
    public ArrayList<StRichMsg> noticeMessage = new ArrayList<>();

    public PattonAction getAction() {
        return this.action;
    }

    public void setAction(PattonAction pattonAction) {
        this.action = pattonAction;
    }

    public long getCreateTime() {
        return this.createTime;
    }

    public void setCreateTime(long j) {
        this.createTime = j;
    }

    public StCommonExt getExt() {
        return this.ext;
    }

    public void setExt(StCommonExt stCommonExt) {
        this.ext = stCommonExt;
    }

    public StFeed getFeed() {
        return this.feed;
    }

    public void setFeed(StFeed stFeed) {
        this.feed = stFeed;
    }

    public StComment getFeedComment() {
        return this.feedComment;
    }

    public void setFeedComment(StComment stComment) {
        this.feedComment = stComment;
    }

    public StReply getFeedReply() {
        return this.feedReply;
    }

    public void setFeedReply(StReply stReply) {
        this.feedReply = stReply;
    }

    public String getNoticeId() {
        return this.noticeId;
    }

    public void setNoticeId(String str) {
        this.noticeId = str;
    }

    public ArrayList<StRichMsg> getNoticeMessage() {
        return this.noticeMessage;
    }

    public void setNoticeMessage(ArrayList<StRichMsg> arrayList) {
        this.noticeMessage = arrayList;
    }

    public NoticePattonInfo getNoticePatton() {
        return this.noticePatton;
    }

    public void setNoticePatton(NoticePattonInfo noticePattonInfo) {
        this.noticePatton = noticePattonInfo;
    }

    public int getNoticeType() {
        return this.noticeType;
    }

    public void setNoticeType(int i) {
        this.noticeType = i;
    }

    public ArrayList<Integer> getOpMask() {
        return this.opMask;
    }

    public void setOpMask(ArrayList<Integer> arrayList) {
        this.opMask = arrayList;
    }

    public StUser getOpUser() {
        return this.opUser;
    }

    public void setOpUser(StUser stUser) {
        this.opUser = stUser;
    }
}

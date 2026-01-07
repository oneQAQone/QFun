package com.tencent.qqnt.kernel.nativeinterface;

import java.io.Serializable;
import java.util.ArrayList;

public final class ReplyElement implements IKernelModel, Serializable {
    public String anonymousNickName;
    public Integer originalMsgState;
    public long replayMsgId;
    public Long replayMsgRootCommentCnt;
    public Long replayMsgRootMsgId;
    public Long replayMsgRootSeq;
    public Long replayMsgSeq;
    public Long replyMsgClientSeq;
    public int replyMsgRevokeType;
    public Long replyMsgTime;
    public Long senderUid;
    public String senderUidStr;
    long serialVersionUID;
    public boolean sourceMsgExpired;
    public Long sourceMsgIdInRecords;
    public boolean sourceMsgIsIncPic;
    public String sourceMsgText;
    public ArrayList<ReplyAbsElement> sourceMsgTextElems;

    public ReplyElement() {
        this.serialVersionUID = 1L;
        this.sourceMsgTextElems = new ArrayList<>();
    }

    public String getAnonymousNickName() {
        return this.anonymousNickName;
    }

    public Integer getOriginalMsgState() {
        return this.originalMsgState;
    }

    public long getReplayMsgId() {
        return this.replayMsgId;
    }

    public Long getReplayMsgRootCommentCnt() {
        return this.replayMsgRootCommentCnt;
    }

    public Long getReplayMsgRootMsgId() {
        return this.replayMsgRootMsgId;
    }

    public Long getReplayMsgRootSeq() {
        return this.replayMsgRootSeq;
    }

    public Long getReplayMsgSeq() {
        return this.replayMsgSeq;
    }

    public Long getReplyMsgClientSeq() {
        return this.replyMsgClientSeq;
    }

    public int getReplyMsgRevokeType() {
        return this.replyMsgRevokeType;
    }

    public Long getReplyMsgTime() {
        return this.replyMsgTime;
    }

    public Long getSenderUid() {
        return this.senderUid;
    }

    public String getSenderUidStr() {
        return this.senderUidStr;
    }

    public boolean getSourceMsgExpired() {
        return this.sourceMsgExpired;
    }

    public Long getSourceMsgIdInRecords() {
        return this.sourceMsgIdInRecords;
    }

    public boolean getSourceMsgIsIncPic() {
        return this.sourceMsgIsIncPic;
    }

    public String getSourceMsgText() {
        return this.sourceMsgText;
    }

    public ArrayList<ReplyAbsElement> getSourceMsgTextElems() {
        return this.sourceMsgTextElems;
    }

    public void setAnonymousNickName(String str) {
        this.anonymousNickName = str;
    }

    public void setOriginalMsgState(Integer num) {
        this.originalMsgState = num;
    }

    public void setReplayMsgId(long j) {
        this.replayMsgId = j;
    }

    public void setReplayMsgRootCommentCnt(Long l) {
        this.replayMsgRootCommentCnt = l;
    }

    public void setReplayMsgRootMsgId(Long l) {
        this.replayMsgRootMsgId = l;
    }

    public void setReplayMsgRootSeq(Long l) {
        this.replayMsgRootSeq = l;
    }

    public void setReplayMsgSeq(Long l) {
        this.replayMsgSeq = l;
    }

    public void setReplyMsgClientSeq(Long l) {
        this.replyMsgClientSeq = l;
    }

    public void setReplyMsgRevokeType(int i) {
        this.replyMsgRevokeType = i;
    }

    public void setReplyMsgTime(Long l) {
        this.replyMsgTime = l;
    }

    public void setSenderUid(Long l) {
        this.senderUid = l;
    }

    public void setSenderUidStr(String str) {
        this.senderUidStr = str;
    }

    public void setSourceMsgExpired(boolean z) {
        this.sourceMsgExpired = z;
    }

    public void setSourceMsgIdInRecords(Long l) {
        this.sourceMsgIdInRecords = l;
    }

    public void setSourceMsgIsIncPic(boolean z) {
        this.sourceMsgIsIncPic = z;
    }

    public void setSourceMsgText(String str) {
        this.sourceMsgText = str;
    }

    public void setSourceMsgTextElems(ArrayList<ReplyAbsElement> arrayList) {
        this.sourceMsgTextElems = arrayList;
    }

    public ReplyElement(long j, Long l, Long l2, Long l3, Long l4, Long l5, String str, ArrayList<ReplyAbsElement> arrayList, Long l6, String str2, Long l7, Long l8, int i, boolean z, boolean z2, String str3, Integer num) {
        this.serialVersionUID = 1L;
        this.replayMsgId = j;
        this.replayMsgSeq = l;
        this.replayMsgRootSeq = l2;
        this.replayMsgRootMsgId = l3;
        this.replayMsgRootCommentCnt = l4;
        this.sourceMsgIdInRecords = l5;
        this.sourceMsgText = str;
        this.sourceMsgTextElems = arrayList;
        this.senderUid = l6;
        this.senderUidStr = str2;
        this.replyMsgClientSeq = l7;
        this.replyMsgTime = l8;
        this.replyMsgRevokeType = i;
        this.sourceMsgIsIncPic = z;
        this.sourceMsgExpired = z2;
        this.anonymousNickName = str3;
        this.originalMsgState = num;
    }
}

package com.tencent.qqnt.kernel.nativeinterface;

import java.util.ArrayList;
import java.util.HashMap;

public class MsgRecord {
    public AnonymousExtInfo anonymousExtInfo;
    public int atType;
    public ArrayList<MsgElement> auxiliaryElements;
    public int avatarFlag;
    public String avatarMeta;
    public String avatarPendant;
    public int categoryManage;
    public String channelId;
    public String channelName;
    public int chatType;
    public long clientSeq;
    public long cntSeq;
    public long commentCnt;
    public int directMsgFlag;

    public boolean editable;
    public ArrayList<MsgElement> elements;

    public byte[] extInfoForUI;
    public String feedId;
    public Integer fileGroupSize;
    public long fromAppid;

    public long fromUid;
    public byte[] generalFlags;
    public long guildCode;
    public String guildId;
    public String guildName;
    public boolean isImportMsg;
    public boolean isOnlineMsg;
    public boolean isSupportRoamMsg;

    public HashMap<Integer, MsgAttributeInfo> msgAttrs;
    public byte[] msgEventInfo;
    public long msgId;
    public byte[] msgMeta;
    public long msgRandom;
    public long msgSeq;
    public long msgTime;
    public int msgType;

    public int nameType;
    public String peerName;
    public String peerUid;
    public long peerUin;

    public long recallTime;
    public ArrayList<MsgRecord> records;
    public long roleId;
    public int roleType;
    public String sendMemberName;
    public String sendNickName;
    public String sendRemarkName;
    public int sendStatus;
    public int sendType;
    public String senderUid;
    public long senderUin;
    public int sourceType;
    public int subMsgType;
    public long timeStamp;
    public long totalSeq;

    public HashMap<Integer, MsgAttributeInfo> getMsgAttrs() {
        return this.msgAttrs;
    }

    public AnonymousExtInfo getAnonymousExtInfo() {
        return this.anonymousExtInfo;
    }

    public int getAtType() {
        return this.atType;
    }

    public int getAvatarFlag() {
        return this.avatarFlag;
    }

    public String getAvatarMeta() {
        return this.avatarMeta;
    }

    public String getAvatarPendant() {
        return this.avatarPendant;
    }

    public int getCategoryManage() {
        return this.categoryManage;
    }

    public String getChannelId() {
        return this.channelId;
    }

    public String getChannelName() {
        return this.channelName;
    }

    public int getChatType() {
        return this.chatType;
    }

    public long getClientSeq() {
        return this.clientSeq;
    }

    public long getCntSeq() {
        return this.cntSeq;
    }

    public long getCommentCnt() {
        return this.commentCnt;
    }

    public int getDirectMsgFlag() {
        return this.directMsgFlag;
    }

    public boolean getEditable() {
        return this.editable;
    }

    public ArrayList<MsgElement> getElements() {
        return this.elements;
    }

    public byte[] getExtInfoForUI() {
        return this.extInfoForUI;
    }

    public String getFeedId() {
        return this.feedId;
    }

    public Integer getFileGroupSize() {
        return this.fileGroupSize;
    }

    public long getFromAppid() {
        return this.fromAppid;
    }

    public long getFromUid() {
        return this.fromUid;
    }

    public byte[] getGeneralFlags() {
        return this.generalFlags;
    }

    public long getGuildCode() {
        return this.guildCode;
    }

    public String getGuildId() {
        return this.guildId;
    }

    public String getGuildName() {
        return this.guildName;
    }

    public boolean getIsImportMsg() {
        return this.isImportMsg;
    }

    public boolean getIsOnlineMsg() {
        return this.isOnlineMsg;
    }

    public byte[] getMsgEventInfo() {
        return this.msgEventInfo;
    }

    public long getMsgId() {
        return this.msgId;
    }

    public byte[] getMsgMeta() {
        return this.msgMeta;
    }

    public long getMsgRandom() {
        return this.msgRandom;
    }

    public long getMsgSeq() {
        return this.msgSeq;
    }

    public long getMsgTime() {
        return this.msgTime;
    }

    public int getMsgType() {
        return this.msgType;
    }

    public int getNameType() {
        return this.nameType;
    }

    public String getPeerName() {
        return this.peerName;
    }

    public String getPeerUid() {
        return this.peerUid;
    }

    public long getPeerUin() {
        return this.peerUin;
    }

    public long getRecallTime() {
        return this.recallTime;
    }

    public ArrayList<MsgRecord> getRecords() {
        return this.records;
    }

    public long getRoleId() {
        return this.roleId;
    }

    public int getRoleType() {
        return this.roleType;
    }

    public String getSendMemberName() {
        return this.sendMemberName;
    }

    public String getSendNickName() {
        return this.sendNickName;
    }

    public String getSendRemarkName() {
        return this.sendRemarkName;
    }

    public int getSendStatus() {
        return this.sendStatus;
    }

    public int getSendType() {
        return this.sendType;
    }

    public String getSenderUid() {
        return this.senderUid;
    }

    public long getSenderUin() {
        return this.senderUin;
    }

    public int getSubMsgType() {
        return this.subMsgType;
    }

    public long getTimeStamp() {
        return this.timeStamp;
    }
}

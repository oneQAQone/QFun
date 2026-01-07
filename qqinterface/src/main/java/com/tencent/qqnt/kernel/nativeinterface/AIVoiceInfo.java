package com.tencent.qqnt.kernel.nativeinterface;

public final class AIVoiceInfo {
    public long groupCode;
    public String voiceTimbreID = "";
    public String text = "";

    public long getGroupCode() {
        return this.groupCode;
    }

    public String getText() {
        return this.text;
    }

    public String getVoiceTimbreID() {
        return this.voiceTimbreID;
    }
}

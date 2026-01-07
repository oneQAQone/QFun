package com.tencent.qqnt.kernel.nativeinterface;

public final class SubscribeMsgTemplateID {
    public String customTemplateId;
    public int templateId;

    public SubscribeMsgTemplateID() {
        this.customTemplateId = "";
    }

    public String getCustomTemplateId() {
        return this.customTemplateId;
    }

    public int getTemplateId() {
        return this.templateId;
    }

    public SubscribeMsgTemplateID(int i, String str) {
        this.templateId = i;
        this.customTemplateId = str;
    }
}

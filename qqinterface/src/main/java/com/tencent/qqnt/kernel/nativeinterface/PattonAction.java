package com.tencent.qqnt.kernel.nativeinterface;

public final class PattonAction {
    public int actionType;
    public String schemaUrl = "";

    public int getActionType() {
        return this.actionType;
    }

    public String getSchemaUrl() {
        return this.schemaUrl;
    }
}

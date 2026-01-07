package com.tencent.qqnt.kernelpublic.nativeinterface;

import java.io.Serializable;

public final class CalendarElement implements Serializable {
    public long expireTimeMs;
    public String msg;
    public String schema;
    public int schemaType;
    long serialVersionUID;
    public String summary;

    public CalendarElement() {
        this.serialVersionUID = 1L;
        this.summary = "";
        this.msg = "";
        this.schema = "";
    }

    public long getExpireTimeMs() {
        return this.expireTimeMs;
    }

    public String getMsg() {
        return this.msg;
    }

    public String getSchema() {
        return this.schema;
    }

    public int getSchemaType() {
        return this.schemaType;
    }

    public String getSummary() {
        return this.summary;
    }

    public CalendarElement(String str, String str2, long j, int i, String str3) {
        this.serialVersionUID = 1L;
        this.summary = str;
        this.msg = str2;
        this.expireTimeMs = j;
        this.schemaType = i;
        this.schema = str3;
    }
}

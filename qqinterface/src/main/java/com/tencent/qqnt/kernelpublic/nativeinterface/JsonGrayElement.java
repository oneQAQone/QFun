package com.tencent.qqnt.kernelpublic.nativeinterface;


import java.io.Serializable;

public final class JsonGrayElement implements Serializable {
    public long busiId;
    public boolean isServer;
    public String jsonStr;
    public String recentAbstract;
    long serialVersionUID;
    public XmlToJsonParam xmlToJsonParam;

    public JsonGrayElement() {
        this.serialVersionUID = 1L;
        this.jsonStr = "";
        this.recentAbstract = "";
    }

    public long getBusiId() {
        return this.busiId;
    }

    public boolean getIsServer() {
        return this.isServer;
    }

    public String getJsonStr() {
        return this.jsonStr;
    }

    public String getRecentAbstract() {
        return this.recentAbstract;
    }

    public XmlToJsonParam getXmlToJsonParam() {
        return this.xmlToJsonParam;
    }

    public JsonGrayElement(long j, String str, String str2, boolean z, XmlToJsonParam xmlToJsonParam) {
        this.serialVersionUID = 1L;
        this.busiId = j;
        this.jsonStr = str;
        this.recentAbstract = str2;
        this.isServer = z;
        this.xmlToJsonParam = xmlToJsonParam;
    }
}

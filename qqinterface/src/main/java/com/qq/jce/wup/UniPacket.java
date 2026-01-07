package com.qq.jce.wup;

public class UniPacket {
    public UniPacket() {
    }

    public UniPacket(boolean z) {
    }

    public String getFuncName() {
        return null;
    }

    public void setFuncName(String str) {
    }

    public int getOldRespIret() {
        return 0;
    }

    public void setOldRespIret(int i2) {
    }

    public int getPackageVersion() {
        return 0;
    }

    public int getRequestId() {
        return 0;
    }

    public void setRequestId(int i2) {
    }

    public String getServantName() {
        return null;
    }

    public void setServantName(String str) {
    }

    public <T> void put(String str, T t) {
        throw new IllegalArgumentException("put name can not startwith . , now is " + str);
    }

    public <T> T get(String str, T t, Object obj) {
        return null;
    }

    public <T> T getByClass(String str, T t) {
        return null;
    }

    public void decode(byte[] bArr) {
        throw new IllegalArgumentException("decode package must include size head");
    }

    public String getEncodeName() {
        return null;
    }

    public void setEncodeName(String str) {
    }

    public byte[] encode() {
        return null;
    }
}

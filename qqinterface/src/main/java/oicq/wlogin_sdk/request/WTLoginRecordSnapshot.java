package oicq.wlogin_sdk.request;

public class WTLoginRecordSnapshot {
    public long a2GenerateTime;
    public int appid;
    public long expireTime;
    public long uin;
    public String uid = "";
    public byte[] a1 = new byte[0];
    public byte[] a1Key = new byte[0];
    public byte[] noPicSig = new byte[0];
    public byte[] a2 = new byte[0];
    public byte[] a2Key = new byte[0];
    public byte[] d2 = new byte[0];
    public byte[] d2Key = new byte[0];
    public String account = "";

    public byte[] getA1() {
        return this.a1;
    }

    public byte[] getA1Key() {
        return this.a1Key;
    }

    public byte[] getA2() {
        return this.a2;
    }

    public long getA2GenerateTime() {
        return this.a2GenerateTime;
    }

    public byte[] getA2Key() {
        return this.a2Key;
    }

    public String getAccount() {
        return this.account;
    }

    public int getAppid() {
        return this.appid;
    }

    public byte[] getD2() {
        return this.d2;
    }

    public byte[] getD2Key() {
        return this.d2Key;
    }

    public long getExpireTime() {
        return this.expireTime;
    }

    public byte[] getNoPicSig() {
        return this.noPicSig;
    }

    public String getUid() {
        return this.uid;
    }

    public long getUin() {
        return this.uin;
    }
}

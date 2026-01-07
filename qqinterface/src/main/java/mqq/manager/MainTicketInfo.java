package mqq.manager;

import com.tencent.commonsdk.util.HexUtil;

public class MainTicketInfo {
    private String a2 = "";
    private byte[] d2 = new byte[0];
    private byte[] d2Key = new byte[0];

    public String getA2() {
        return this.a2;
    }

    public byte[] getD2() {
        return this.d2;
    }

    public byte[] getD2Key() {
        return this.d2Key;
    }

    public void setA2(String str) {
        this.a2 = str;
    }

    public void setD2(byte[] bArr) {
        this.d2 = bArr;
    }

    public void setD2Key(byte[] bArr) {
        this.d2Key = bArr;
    }

    public String toString() {
        return "MainTicketInfo{a2='" + this.a2 + "', d2=" + HexUtil.bytes2HexStr(this.d2) + ", d2Key=" + HexUtil.bytes2HexStr(this.d2Key) + '}';
    }
}

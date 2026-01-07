package com.tencent.qqnt.kernel.nativeinterface;

import androidx.annotation.NonNull;

public final class SessionTicket {
    public String a2;
    public String d2;
    public String d2Key;

    public SessionTicket() {
        this.a2 = "";
        this.d2 = "";
        this.d2Key = "";
    }

    public String getA2() {
        return this.a2;
    }

    public String getD2() {
        return this.d2;
    }

    public String getD2Key() {
        return this.d2Key;
    }

    @NonNull
    public String toString() {
        return "SessionTicket{a2=" + this.a2 + ",d2=" + this.d2 + ",d2Key=" + this.d2Key + ",}";
    }

    public SessionTicket(String str, String str2, String str3) {
        this.a2 = str;
        this.d2 = str2;
        this.d2Key = str3;
    }
}

package com.tencent.qphone.base.remote;

import java.util.HashMap;

public class SimpleAccount {
    public static final String _ISLOGINED = "_isLogined";
    public static final String _LOGINPROCESS = "_loginedProcess";
    public static final String _LOGINTIME = "_loginTime";
    public static final String _UIN = "_uin";
    private static final String tag = "SimpleAccount";

    public static boolean isSameAccount(SimpleAccount simpleAccount, SimpleAccount simpleAccount2) {
        return false;
    }

    public static SimpleAccount parseSimpleAccount(String str) {
        return null;
    }

    public boolean containsKey(String str) {
        return false;
    }

    public boolean equals(Object obj) {
        return false;
    }

    public String getAttribute(String str, String str2) {
        return str2;
    }

    public HashMap<String, String> getAttributes() {
        return null;
    }

    public String getLoginProcess() {
        return "";
    }

    public String getUin() {
        return "";
    }

    public boolean isLogined() {
        return false;
    }

    public String removeAttribute(String str) {
        return "";
    }

    public void setAttribute(String str, String str2) {
        throw new RuntimeException("key found space ");
    }

    public void setLoginProcess(String str) {
    }

    public void setUin(String str) {
    }

    public String toStoreString() {
        return "";
    }

    public String toString() {
        return "";
    }
}

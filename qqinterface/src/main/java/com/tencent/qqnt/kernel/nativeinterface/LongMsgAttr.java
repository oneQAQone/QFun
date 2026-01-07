package com.tencent.qqnt.kernel.nativeinterface;

public final class LongMsgAttr {
    public Integer fetchLongMsgErrCode;

    public LongMsgAttr() {
    }

    public Integer getFetchLongMsgErrCode() {
        return this.fetchLongMsgErrCode;
    }

    public LongMsgAttr(Integer num) {
        this.fetchLongMsgErrCode = num;
    }
}

package com.tencent.qqnt.kernel.nativeinterface;

public final class AttaReportData {
    public String sgrpStreamPginSourceName = "";
    public String sgrpVisitFrom = "";
    public String sgrpSessionId = "";

    public String getSgrpSessionId() {
        return this.sgrpSessionId;
    }

    public String getSgrpStreamPginSourceName() {
        return this.sgrpStreamPginSourceName;
    }

    public String getSgrpVisitFrom() {
        return this.sgrpVisitFrom;
    }
}

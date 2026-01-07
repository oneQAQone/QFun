package com.tencent.qqnt.kernel.nativeinterface;

public final class GetBannerAndNoticeRsp {
    public boolean needConfirmPrivacyPolicy;
    public int result;
    public int seq;
    public String errMsg = "";
    public BannerInfo bannerInfo = new BannerInfo();
    public NoticeInfo noticeInfo = new NoticeInfo();
    public String privacyPolicyContent = "";

    public BannerInfo getBannerInfo() {
        return this.bannerInfo;
    }

    public String getErrMsg() {
        return this.errMsg;
    }

    public boolean getNeedConfirmPrivacyPolicy() {
        return this.needConfirmPrivacyPolicy;
    }

    public NoticeInfo getNoticeInfo() {
        return this.noticeInfo;
    }

    public String getPrivacyPolicyContent() {
        return this.privacyPolicyContent;
    }

    public int getResult() {
        return this.result;
    }

    public int getSeq() {
        return this.seq;
    }
}

package com.tencent.qqnt.kernel.nativeinterface;

import java.util.ArrayList;

public final class GetFaceAlbumCoverRsp {
    public long facePersonCount;
    public boolean needConfirmPrivacyPolicy;
    public int result;
    public int seq;
    public String errMsg = "";
    public ArrayList<FacePerson> facePersons = new ArrayList<>();
    public String privacyPolicyContent = "";
    public FaceTagFeatureIntro faceTagFeatureIntro = new FaceTagFeatureIntro();

    public String getErrMsg() {
        return this.errMsg;
    }

    public long getFacePersonCount() {
        return this.facePersonCount;
    }

    public ArrayList<FacePerson> getFacePersons() {
        return this.facePersons;
    }

    public FaceTagFeatureIntro getFaceTagFeatureIntro() {
        return this.faceTagFeatureIntro;
    }

    public boolean getNeedConfirmPrivacyPolicy() {
        return this.needConfirmPrivacyPolicy;
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

package com.tencent.qqnt.kernel.nativeinterface;

import java.util.ArrayList;

public final class FaceAlbumCover {
    public long facePersonCount;
    public boolean needConfirmPrivacyPolicy;
    public ArrayList<FacePerson> facePersons = new ArrayList<>();
    public String name = "";
    public String privacyPolicyContent = "";

    public long getFacePersonCount() {
        return this.facePersonCount;
    }

    public ArrayList<FacePerson> getFacePersons() {
        return this.facePersons;
    }

    public String getName() {
        return this.name;
    }

    public boolean getNeedConfirmPrivacyPolicy() {
        return this.needConfirmPrivacyPolicy;
    }

    public String getPrivacyPolicyContent() {
        return this.privacyPolicyContent;
    }
}

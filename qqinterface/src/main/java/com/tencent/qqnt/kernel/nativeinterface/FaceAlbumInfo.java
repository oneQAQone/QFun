package com.tencent.qqnt.kernel.nativeinterface;

import java.util.ArrayList;

public final class FaceAlbumInfo {
    public long facePersonCount;
    public ArrayList<FacePerson> facePersons = new ArrayList<>();
    public long favoriteFacePersonCount;

    public long getFacePersonCount() {
        return this.facePersonCount;
    }

    public ArrayList<FacePerson> getFacePersons() {
        return this.facePersons;
    }

    public long getFavoriteFacePersonCount() {
        return this.favoriteFacePersonCount;
    }
}

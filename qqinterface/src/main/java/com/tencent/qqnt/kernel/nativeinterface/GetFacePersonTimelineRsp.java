package com.tencent.qqnt.kernel.nativeinterface;

import java.util.ArrayList;

public final class GetFacePersonTimelineRsp {
    public int filterMaskMaxPullCount;
    public boolean hasNextPage;
    public boolean hasPrePage;
    public boolean isEnd;
    public boolean isFromCache;
    public int result;
    public int seq;
    public int totalCount;
    public String errMsg = "";
    public ArrayList<Media> medias = new ArrayList<>();
    public String nextPageCookie = "";
    public QzoneMediaInfo qzoneMedia = new QzoneMediaInfo();
    public FacePerson facePerson = new FacePerson();
    public String prevPageCookie = "";

    public String getErrMsg() {
        return this.errMsg;
    }

    public FacePerson getFacePerson() {
        return this.facePerson;
    }

    public int getFilterMaskMaxPullCount() {
        return this.filterMaskMaxPullCount;
    }

    public boolean getHasNextPage() {
        return this.hasNextPage;
    }

    public boolean getHasPrePage() {
        return this.hasPrePage;
    }

    public boolean getIsEnd() {
        return this.isEnd;
    }

    public boolean getIsFromCache() {
        return this.isFromCache;
    }

    public ArrayList<Media> getMedias() {
        return this.medias;
    }

    public String getNextPageCookie() {
        return this.nextPageCookie;
    }

    public String getPrevPageCookie() {
        return this.prevPageCookie;
    }

    public QzoneMediaInfo getQzoneMedia() {
        return this.qzoneMedia;
    }

    public int getResult() {
        return this.result;
    }

    public int getSeq() {
        return this.seq;
    }

    public int getTotalCount() {
        return this.totalCount;
    }
}

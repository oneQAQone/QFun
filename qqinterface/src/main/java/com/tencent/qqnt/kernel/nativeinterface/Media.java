package com.tencent.qqnt.kernel.nativeinterface;

public final class Media implements IKernelModel {
    public String mediaId = "";
    public FaceMediaType mediaType = FaceMediaType.values()[0];
    public String mediaUrl = "";
    public long timestamp;

    public String getMediaId() {
        return this.mediaId;
    }

    public void setMediaId(String str) {
        this.mediaId = str;
    }

    public FaceMediaType getMediaType() {
        return this.mediaType;
    }

    public void setMediaType(FaceMediaType faceMediaType) {
        this.mediaType = faceMediaType;
    }

    public String getMediaUrl() {
        return this.mediaUrl;
    }

    public void setMediaUrl(String str) {
        this.mediaUrl = str;
    }

    public long getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(long j) {
        this.timestamp = j;
    }
}

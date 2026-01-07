package com.tencent.qqnt.kernel.nativeinterface;

public final class AlbumInfo {
    public ActiveAlbumInfo activeAlbum;
    public String albumId;
    public boolean allowShare;
    public String bitmap;
    public int busiType;
    public StFeedCellComment commentInfo;
    public StMedia cover;
    public int coverType;
    public long createTime;
    public StUser creator;
    public String defaultDesc;
    public String desc;
    public FamilyAlbumMeta familyAlbum;
    public boolean isShareAlbum;
    public boolean isSubscribe;
    public long lastUploadTime;
    public StFeedCellLike likeInfo;
    public LoverAlbumMeta loverAlbum;
    public MemoryInfo memoryInfo;
    public long modifyTime;
    public String name;
    public AlbumOpInfo opInfo;
    public String owner;
    public AlbumAccessPermission permission;
    public int qzAlbumType;
    public ShareAlbumMeta shareAlbum;
    public int sortType;
    public int status;
    public long topFlag;
    public TravelAlbumMeta travelAlbum;
    public long uploadNumber;
    public StFeedCellVisitor visitorInfo;

    public AlbumInfo() {
        this.albumId = "";
        this.owner = "";
        this.name = "";
        this.desc = "";
        this.cover = new StMedia();
        this.creator = new StUser();
        this.permission = new AlbumAccessPermission();
        this.bitmap = "";
        this.shareAlbum = new ShareAlbumMeta();
        this.familyAlbum = new FamilyAlbumMeta();
        this.loverAlbum = new LoverAlbumMeta();
        this.travelAlbum = new TravelAlbumMeta();
        this.visitorInfo = new StFeedCellVisitor();
        this.defaultDesc = "";
        this.opInfo = new AlbumOpInfo();
        this.activeAlbum = new ActiveAlbumInfo();
        this.memoryInfo = new MemoryInfo();
        this.likeInfo = new StFeedCellLike();
        this.commentInfo = new StFeedCellComment();
    }

    public ActiveAlbumInfo getActiveAlbum() {
        return this.activeAlbum;
    }

    public String getAlbumId() {
        return this.albumId;
    }

    public boolean getAllowShare() {
        return this.allowShare;
    }

    public String getBitmap() {
        return this.bitmap;
    }

    public int getBusiType() {
        return this.busiType;
    }

    public StFeedCellComment getCommentInfo() {
        return this.commentInfo;
    }

    public StMedia getCover() {
        return this.cover;
    }

    public int getCoverType() {
        return this.coverType;
    }

    public long getCreateTime() {
        return this.createTime;
    }

    public StUser getCreator() {
        return this.creator;
    }

    public String getDefaultDesc() {
        return this.defaultDesc;
    }

    public String getDesc() {
        return this.desc;
    }

    public FamilyAlbumMeta getFamilyAlbum() {
        return this.familyAlbum;
    }

    public boolean getIsShareAlbum() {
        return this.isShareAlbum;
    }

    public boolean getIsSubscribe() {
        return this.isSubscribe;
    }

    public long getLastUploadTime() {
        return this.lastUploadTime;
    }

    public StFeedCellLike getLikeInfo() {
        return this.likeInfo;
    }

    public LoverAlbumMeta getLoverAlbum() {
        return this.loverAlbum;
    }

    public MemoryInfo getMemoryInfo() {
        return this.memoryInfo;
    }

    public long getModifyTime() {
        return this.modifyTime;
    }

    public String getName() {
        return this.name;
    }

    public AlbumOpInfo getOpInfo() {
        return this.opInfo;
    }

    public String getOwner() {
        return this.owner;
    }

    public AlbumAccessPermission getPermission() {
        return this.permission;
    }

    public int getQzAlbumType() {
        return this.qzAlbumType;
    }

    public ShareAlbumMeta getShareAlbum() {
        return this.shareAlbum;
    }

    public int getSortType() {
        return this.sortType;
    }

    public int getStatus() {
        return this.status;
    }

    public long getTopFlag() {
        return this.topFlag;
    }

    public TravelAlbumMeta getTravelAlbum() {
        return this.travelAlbum;
    }

    public long getUploadNumber() {
        return this.uploadNumber;
    }

    public StFeedCellVisitor getVisitorInfo() {
        return this.visitorInfo;
    }

    public void setActiveAlbum(ActiveAlbumInfo activeAlbumInfo) {
        this.activeAlbum = activeAlbumInfo;
    }

    public void setAlbumId(String str) {
        this.albumId = str;
    }

    public void setAllowShare(boolean z) {
        this.allowShare = z;
    }

    public void setBitmap(String str) {
        this.bitmap = str;
    }

    public void setBusiType(int i) {
        this.busiType = i;
    }

    public void setCommentInfo(StFeedCellComment stFeedCellComment) {
        this.commentInfo = stFeedCellComment;
    }

    public void setCover(StMedia stMedia) {
        this.cover = stMedia;
    }

    public void setCoverType(int i) {
        this.coverType = i;
    }

    public void setCreateTime(long j) {
        this.createTime = j;
    }

    public void setCreator(StUser stUser) {
        this.creator = stUser;
    }

    public void setDefaultDesc(String str) {
        this.defaultDesc = str;
    }

    public void setDesc(String str) {
        this.desc = str;
    }

    public void setFamilyAlbum(FamilyAlbumMeta familyAlbumMeta) {
        this.familyAlbum = familyAlbumMeta;
    }

    public void setIsShareAlbum(boolean z) {
        this.isShareAlbum = z;
    }

    public void setIsSubscribe(boolean z) {
        this.isSubscribe = z;
    }

    public void setLastUploadTime(long j) {
        this.lastUploadTime = j;
    }

    public void setLikeInfo(StFeedCellLike stFeedCellLike) {
        this.likeInfo = stFeedCellLike;
    }

    public void setLoverAlbum(LoverAlbumMeta loverAlbumMeta) {
        this.loverAlbum = loverAlbumMeta;
    }

    public void setMemoryInfo(MemoryInfo memoryInfo) {
        this.memoryInfo = memoryInfo;
    }

    public void setModifyTime(long j) {
        this.modifyTime = j;
    }

    public void setName(String str) {
        this.name = str;
    }

    public void setOpInfo(AlbumOpInfo albumOpInfo) {
        this.opInfo = albumOpInfo;
    }

    public void setOwner(String str) {
        this.owner = str;
    }

    public void setPermission(AlbumAccessPermission albumAccessPermission) {
        this.permission = albumAccessPermission;
    }

    public void setQzAlbumType(int i) {
        this.qzAlbumType = i;
    }

    public void setShareAlbum(ShareAlbumMeta shareAlbumMeta) {
        this.shareAlbum = shareAlbumMeta;
    }

    public void setSortType(int i) {
        this.sortType = i;
    }

    public void setStatus(int i) {
        this.status = i;
    }

    public void setTopFlag(long j) {
        this.topFlag = j;
    }

    public void setTravelAlbum(TravelAlbumMeta travelAlbumMeta) {
        this.travelAlbum = travelAlbumMeta;
    }

    public void setUploadNumber(long j) {
        this.uploadNumber = j;
    }

    public void setVisitorInfo(StFeedCellVisitor stFeedCellVisitor) {
        this.visitorInfo = stFeedCellVisitor;
    }

    public AlbumInfo(String str, String str2, String str3, String str4, long j, long j2, long j3, long j4, StMedia stMedia, StUser stUser, long j5, int i, int i2, AlbumAccessPermission albumAccessPermission, boolean z, boolean z2, String str5, boolean z3, ShareAlbumMeta shareAlbumMeta, int i3, FamilyAlbumMeta familyAlbumMeta, LoverAlbumMeta loverAlbumMeta, int i4, TravelAlbumMeta travelAlbumMeta, StFeedCellVisitor stFeedCellVisitor, String str6, AlbumOpInfo albumOpInfo, ActiveAlbumInfo activeAlbumInfo) {
        this.albumId = "";
        this.owner = "";
        this.name = "";
        this.desc = "";
        this.cover = new StMedia();
        this.creator = new StUser();
        this.permission = new AlbumAccessPermission();
        this.bitmap = "";
        this.shareAlbum = new ShareAlbumMeta();
        this.familyAlbum = new FamilyAlbumMeta();
        this.loverAlbum = new LoverAlbumMeta();
        this.travelAlbum = new TravelAlbumMeta();
        this.visitorInfo = new StFeedCellVisitor();
        this.defaultDesc = "";
        this.opInfo = new AlbumOpInfo();
        this.activeAlbum = new ActiveAlbumInfo();
        this.memoryInfo = new MemoryInfo();
        this.likeInfo = new StFeedCellLike();
        this.commentInfo = new StFeedCellComment();
        this.albumId = str;
        this.owner = str2;
        this.name = str3;
        this.desc = str4;
        this.createTime = j;
        this.modifyTime = j2;
        this.lastUploadTime = j3;
        this.uploadNumber = j4;
        this.cover = stMedia;
        this.creator = stUser;
        this.topFlag = j5;
        this.busiType = i;
        this.status = i2;
        this.permission = albumAccessPermission;
        this.allowShare = z;
        this.isSubscribe = z2;
        this.bitmap = str5;
        this.isShareAlbum = z3;
        this.shareAlbum = shareAlbumMeta;
        this.qzAlbumType = i3;
        this.familyAlbum = familyAlbumMeta;
        this.loverAlbum = loverAlbumMeta;
        this.coverType = i4;
        this.travelAlbum = travelAlbumMeta;
        this.visitorInfo = stFeedCellVisitor;
        this.defaultDesc = str6;
        this.opInfo = albumOpInfo;
        this.activeAlbum = activeAlbumInfo;
    }
}

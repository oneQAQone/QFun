package com.tencent.qqnt.kernel.nativeinterface;

import java.util.ArrayList;

public interface IKernelAlbumService {
    void addAlbum(int i, AlbumInfo albumInfo, IAlbumServiceAddAlbumCallback iAlbumServiceAddAlbumCallback);

    void closeBanner(CloseBannerReq closeBannerReq, ISeqOperateCallback iSeqOperateCallback);

    void closeNewFeatureIntro(CloseNewFeatureIntroReq closeNewFeatureIntroReq, ISeqOperateCallback iSeqOperateCallback);

    void confirmPrivacyPolicyReq(ConfirmPrivacyPolicyReq confirmPrivacyPolicyReq, ISeqOperateCallback iSeqOperateCallback);

    void deleteAlbum(int i, String str, String str2, IAlbumServiceDeleteAlbumCallback iAlbumServiceDeleteAlbumCallback);

    void deleteMedias(int i, String str, String str2, ArrayList<String> arrayList, ArrayList<String> arrayList2, ArrayList<DeleteMediaItem> arrayList3, IAlbumServiceDeleteMediasCallback iAlbumServiceDeleteMediasCallback);

    void deleteQunFeed(NTDeleteQunFeedReq nTDeleteQunFeedReq, IAlbumServiceDeleteQunFeedCallback iAlbumServiceDeleteQunFeedCallback);

    void doQunComment(int i, StCommonExt stCommonExt, String str, int i2, StFeed stFeed, StComment stComment, IAlbumServiceDoQunCommentCallback iAlbumServiceDoQunCommentCallback);

    void doQunLike(int i, StCommonExt stCommonExt, int i2, StLike stLike, StFeed stFeed, IAlbumServiceDoQunLikeCallback iAlbumServiceDoQunLikeCallback);

    void doQunReply(int i, StCommonExt stCommonExt, String str, int i2, StFeed stFeed, StComment stComment, StReply stReply, IAlbumServiceDoQunReplyCallback iAlbumServiceDoQunReplyCallback);

    void getAlbumInfo(NTGetAlbumInfoReq nTGetAlbumInfoReq, IAlbumServiceGetAlbumInfoCallback iAlbumServiceGetAlbumInfoCallback);

    void getAlbumList(NTGetAlbumListReq nTGetAlbumListReq, IAlbumServiceGetAlbumListCallback iAlbumServiceGetAlbumListCallback);

    void getAllAlbumList(NTGetAllAlbumListReq nTGetAllAlbumListReq, IAlbumServiceGetAllAlbumListCallback iAlbumServiceGetAllAlbumListCallback);

    void getBannerAndNotice(GetBannerAndNoticeReq getBannerAndNoticeReq, IAlbumServiceGetBannerAndNoticeCallback iAlbumServiceGetBannerAndNoticeCallback);

    void getFaceAlbumSetting(GetFaceAlbumSettingReq getFaceAlbumSettingReq, IAlbumServiceGetFaceAlbumSettingCallback iAlbumServiceGetFaceAlbumSettingCallback);

    void getFaceIdLastViewedTime(int i, String str, String str2, IAlbumServiceGetViewTimeCallback iAlbumServiceGetViewTimeCallback);

    void getFacePersonList(GetFacePersonListReq getFacePersonListReq, IAlbumServiceGetFacePersonListCallback iAlbumServiceGetFacePersonListCallback);

    void getFacePersonTimeline(GetFacePersonTimelineReq getFacePersonTimelineReq, IAlbumServiceGetFacePersonTimelineCallback iAlbumServiceGetFacePersonTimelineCallback);

    void getFeedById(NTGetFeedByIdReq nTGetFeedByIdReq, IAlbumServiceGetFeedByIdCallback iAlbumServiceGetFeedByIdCallback);

    void getFeedFacePersonInfoDetail(GetFeedFacePersonInfoDetailReq getFeedFacePersonInfoDetailReq, IFeedFacePersonInfoDetailCallback iFeedFacePersonInfoDetailCallback);

    void getMainPage(int i, String str, IAlbumServiceGetMainPageCallback iAlbumServiceGetMainPageCallback);

    void getMediaList(NTGetMediaListReq nTGetMediaListReq, IAlbumServiceGetMediaListCallback iAlbumServiceGetMediaListCallback);

    void getMediaListTailTab(NTGetMediaListTailTabReq nTGetMediaListTailTabReq, IAlbumServiceGetMediaListTailTabCallback iAlbumServiceGetMediaListTailTabCallback);

    void getNoticeDetail(GetNoticeDetailReq getNoticeDetailReq, IAlbumServiceGetNoticeDetailCallback iAlbumServiceGetNoticeDetailCallback);

    void getQunComment(NTGetQunCommentReq nTGetQunCommentReq, IAlbumServiceGetQunCommentCallback iAlbumServiceGetQunCommentCallback);

    void getQunFeedDetail(NTGetQunFeedDetailReq nTGetQunFeedDetailReq, IAlbumServiceGetQunFeedDetailCallback iAlbumServiceGetQunFeedDetailCallback);

    void getQunFeeds(NTGetQunFeedsReq nTGetQunFeedsReq, IAlbumServiceGetQunFeedsCallback iAlbumServiceGetQunFeedsCallback);

    void getQunLikes(int i, StCommonExt stCommonExt, String str, String str2, IAlbumServiceGetQunLikesCallback iAlbumServiceGetQunLikesCallback);

    void getQunNoticeList(int i, StCommonExt stCommonExt, String str, String str2, IAlbumServiceGetQunNoticeListCallback iAlbumServiceGetQunNoticeListCallback);

    void getQunRight(NTGetQunRightReq nTGetQunRightReq, IAlbumServiceGetQunRightCallback iAlbumServiceGetQunRightCallback);

    void getRedPoints(long j, SceneType sceneType, String str, IReddotReaderServiceGetRedPointsCallback iReddotReaderServiceGetRedPointsCallback);

    void modifyAlbum(int i, AlbumInfo albumInfo, ArrayList<AlbumModifyMask> arrayList, IAlbumServiceModifyAlbumCallback iAlbumServiceModifyAlbumCallback);

    void queryQuoteToQunAlbumStatus(NTQueryQuoteToQunAlbumStatusReq nTQueryQuoteToQunAlbumStatusReq, IAlbumServiceQueryQuoteToQunAlbumStatusCallback iAlbumServiceQueryQuoteToQunAlbumStatusCallback);

    void quoteToQunAlbum(NTQuoteToQunAlbumReq nTQuoteToQunAlbumReq, IAlbumServiceQuoteToQunAlbumCallback iAlbumServiceQuoteToQunAlbumCallback);

    void quoteToQzone(NTQuoteToQzoneReq nTQuoteToQzoneReq, IAlbumServiceQuoteToQzoneCallback iAlbumServiceQuoteToQzoneCallback);

    void readNotice(ReadNoticeReq readNoticeReq, ISeqOperateCallback iSeqOperateCallback);

    void reportViewQunFeed(NTReportViewQunFeedReq nTReportViewQunFeedReq, IAlbumServiceReportViewQunFeedCallback iAlbumServiceReportViewQunFeedCallback);

    void setAlbumServiceInfo(String str, String str2, String str3);

    void setFaceIdLastViewedTime(String str, String str2, long j);

    void setMediaUserStatus(SetMediaUserStatusReq setMediaUserStatusReq, ISeqOperateCallback iSeqOperateCallback);

    void updateFaceAlbumSetting(UpdateFaceAlbumSettingReq updateFaceAlbumSettingReq, ISeqOperateCallback iSeqOperateCallback);

    void updateFacePersonFavorite(UpdateFacePersonFavoriteReq updateFacePersonFavoriteReq, ISeqOperateCallback iSeqOperateCallback);
}

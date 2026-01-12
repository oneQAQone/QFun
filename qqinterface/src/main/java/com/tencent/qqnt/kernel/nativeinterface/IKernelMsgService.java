package com.tencent.qqnt.kernel.nativeinterface;

import com.tencent.qqnt.kernelpublic.nativeinterface.LocalGrayTipElement;
import com.tencent.qqnt.kernelpublic.nativeinterface.Contact;

import java.util.ArrayList;
import java.util.HashMap;

public interface IKernelMsgService {

    class CppProxy implements IKernelMsgService {
        @Override
        public void deleteMsg(Contact contact, ArrayList<Long> msdIds, IOperateCallback iOperateCallback) {

        }

        @Override
        public void recallMsg(Contact contact, ArrayList<Long> msgIds, IOperateCallback iOperateCallback) {

        }

        @Override
        public void sendMsg(long j, Contact contact, ArrayList<MsgElement> arrayList, HashMap<Integer, MsgAttributeInfo> hashMap, IOperateCallback iOperateCallback) {

        }

        @Override
        public long generateMsgUniqueId(int i, long j) {
            return 0;
        }

        @Override
        public void forwardMsg(ArrayList<Long> arrayList, Contact contact, ArrayList<Contact> arrayList2, HashMap<Integer, MsgAttributeInfo> hashMap, IForwardOperateCallback iForwardOperateCallback) {

        }

        @Override
        public void addLocalJsonGrayTipMsg(Contact contact, JsonGrayElement jsonGrayElement, boolean z, boolean z2, IAddJsonGrayTipMsgCallback iAddJsonGrayTipMsgCallback) {

        }

        @Override
        public void addLocalGrayTipMsg(Contact contact, LocalGrayTipElement localGrayTipElement, boolean z, IOperateCallback iOperateCallback) {

        }

        @Override
        public void addLocalJsonGrayTipMsg(Contact contact, com.tencent.qqnt.kernelpublic.nativeinterface.JsonGrayElement jsonGrayElement, boolean z, boolean z2, IAddJsonGrayTipMsgCallback iAddJsonGrayTipMsgCallback) {

        }

        @Override
        public void addLocalJsonGrayTipMsgExt(Contact contact, JsonGrayMsgInfo jsonGrayMsgInfo, com.tencent.qqnt.kernelpublic.nativeinterface.JsonGrayElement jsonGrayElement, boolean z, boolean z2, IAddJsonGrayTipMsgCallback iAddJsonGrayTipMsgCallback) {

        }

        @Override
        public void getMsgsByMsgId(Contact contact, ArrayList<Long> arrayList, IMsgOperateCallback iMsgOperateCallback) {

        }
    }

    void deleteMsg(Contact contact, ArrayList<Long> msdIds, IOperateCallback iOperateCallback);

    void recallMsg(Contact contact, ArrayList<Long> msgIds, IOperateCallback iOperateCallback);

    void sendMsg(long j, Contact contact, ArrayList<MsgElement> arrayList, HashMap<Integer, MsgAttributeInfo> hashMap, IOperateCallback iOperateCallback);

    long generateMsgUniqueId(int i, long j);

    void forwardMsg(ArrayList<Long> arrayList, Contact contact, ArrayList<Contact> arrayList2, HashMap<Integer, MsgAttributeInfo> hashMap, IForwardOperateCallback iForwardOperateCallback);

    /**
     * 已被删除的旧版本接口,新版本不要使用
     */
    void addLocalJsonGrayTipMsg(Contact contact, JsonGrayElement jsonGrayElement, boolean z, boolean z2, IAddJsonGrayTipMsgCallback iAddJsonGrayTipMsgCallback);

    void addLocalGrayTipMsg(com.tencent.qqnt.kernelpublic.nativeinterface.Contact contact, LocalGrayTipElement localGrayTipElement, boolean z, IOperateCallback iOperateCallback);

    void addLocalJsonGrayTipMsg(com.tencent.qqnt.kernelpublic.nativeinterface.Contact contact, com.tencent.qqnt.kernelpublic.nativeinterface.JsonGrayElement jsonGrayElement, boolean z, boolean z2, IAddJsonGrayTipMsgCallback iAddJsonGrayTipMsgCallback);

    void addLocalJsonGrayTipMsgExt(com.tencent.qqnt.kernelpublic.nativeinterface.Contact contact, JsonGrayMsgInfo jsonGrayMsgInfo, com.tencent.qqnt.kernelpublic.nativeinterface.JsonGrayElement jsonGrayElement, boolean z, boolean z2, IAddJsonGrayTipMsgCallback iAddJsonGrayTipMsgCallback);

    /*
    ArrayList<Integer> GetMsgSubType(int i, int i2);

    boolean IsC2CStyleChatType(int i);

    boolean IsExistOldDb();

    boolean IsLocalJsonTipValid(int i);

    boolean IsTempChatType(int i);

    void JoinDragonGroupEmoji(JoinDragonGroupEmojiReq joinDragonGroupEmojiReq, IJoinDragonGroupCallback iJoinDragonGroupCallback);

    void addFavEmoji(AddFavEmojiReq addFavEmojiReq, IAddFavEmojiCallback iAddFavEmojiCallback);

    long addKernelMsgImportToolListener(IKernelMsgImportToolListener iKernelMsgImportToolListener);

    long addKernelMsgListener(IKernelMsgListener iKernelMsgListener);

    long addKernelMsgNotifyListener(IMsgNotifyListener iMsgNotifyListener);

    long addKernelTempChatSigListener(ITempChatSigListener iTempChatSigListener);

    void addLocalAVRecordMsg(Contact contact, LocalAVRecordElement localAVRecordElement, IOperateCallback iOperateCallback);

    void addLocalGrayTipMsg(Contact contact, LocalGrayTipElement localGrayTipElement, boolean z, IOperateCallback iOperateCallback);

    void addLocalJsonGrayTipMsg(Contact contact, JsonGrayElement jsonGrayElement, boolean z, boolean z2, IAddJsonGrayTipMsgCallback iAddJsonGrayTipMsgCallback);

    void addLocalJsonGrayTipMsgExt(Contact contact, JsonGrayMsgInfo jsonGrayMsgInfo, JsonGrayElement jsonGrayElement, boolean z, boolean z2, IAddJsonGrayTipMsgCallback iAddJsonGrayTipMsgCallback);

    void addLocalRecordMsg(Contact contact, long j, MsgElement msgElement, HashMap<Integer, MsgAttributeInfo> hashMap, boolean z, IOperateCallback iOperateCallback);

    void addLocalRecordMsgWithExtInfos(Contact contact, long j, AddLocalRecordMsgParams addLocalRecordMsgParams, IOperateCallback iOperateCallback);

    void addLocalTofuRecordMsg(Contact contact, TofuRecordElement tofuRecordElement, IOperateCallback iOperateCallback);

    void addRecentUsedFace(ArrayList<RecentUsedFace> arrayList);

    void addSendMsg(long j, Contact contact, ArrayList<MsgElement> arrayList, HashMap<Integer, MsgAttributeInfo> hashMap);

    String assembleMobileQQRichMediaFilePath(RichMediaFilePathInfo richMediaFilePathInfo);

    void canImportOldDbMsg(ICanImportCallback iCanImportCallback);

    void canProcessDataMigration(ICanImportCallback iCanImportCallback);

    void cancelGetRichMediaElement(RichMediaElementGetReq richMediaElementGetReq);

    void cancelSendMsg(Contact contact, long j);

    void checkMsgWithUrl(CheckUrlInfo checkUrlInfo, ICheckMsgWithUrlCallback iCheckMsgWithUrlCallback);

    void checkTabListStatus(IOperateCallback iOperateCallback);

    void clearMsgRecords(Contact contact, IClearMsgRecordsCallback iClearMsgRecordsCallback);

    void clickInlineKeyboardButton(InlineKeyboardClickInfo inlineKeyboardClickInfo, IClickInlineKeyboardButtonCallback iClickInlineKeyboardButtonCallback);

    void clickTofuActionButton(Contact contact, long j, TofuActionButton tofuActionButton, IOperateCallback iOperateCallback);

    String createUidFromTinyId(long j, long j2);

    void dataMigrationGetDataAvaiableContactList(IDataMigrationGetAvailableContactListCallback iDataMigrationGetAvailableContactListCallback);

    long dataMigrationGetMsgList(QueryMsgsParams queryMsgsParams, IDataMigrationGetMsgListCalback iDataMigrationGetMsgListCalback);

    String dataMigrationGetResourceLocalDestinyPath(DataMigrationResourceInfo dataMigrationResourceInfo);

    long dataMigrationImportMsgPbRecord(ArrayList<DataMigrationMsgInfo> arrayList, ArrayList<DataMigrationResourceInfo> arrayList2, ITaskFinishCallback iTaskFinishCallback);

    void dataMigrationSetIOSPathPrefix(String str);

    void dataMigrationStopOperation(long j);

    void delMarketEmojiTab(DelMarketEmojiTabReq delMarketEmojiTabReq, IDelMarketEmojiTableCallback iDelMarketEmojiTableCallback);

    void delRecentHiddenSession(ArrayList<RecentHiddenSesionInfo> arrayList, IOperateCallback iOperateCallback);

    void deleteAllRoamMsgs(int i, String str, IOperateCallback iOperateCallback);

    void deleteDraft(Contact contact, IOperateCallback iOperateCallback);

    void deleteFavEmoji(ArrayList<String> arrayList, IOperateCallback iOperateCallback);

    void deleteMsg(Contact contact, ArrayList<Long> arrayList, IOperateCallback iOperateCallback);

    void deleteRecentlyForwardContactInfo(long j, String str, int i, IOperateCallback iOperateCallback);

    void deleteReplyDraft(Contact contact, long j, IOperateCallback iOperateCallback);

    void downloadEmojiPic(int i, ArrayList<GproEmojiDownloadParams> arrayList, int i2, HashMap<String, String> hashMap);

    void downloadOnlineStatusBigIconByUrl(int i, String str);

    void downloadOnlineStatusCommonByUrl(String str, String str2, IDownloadCommonStatusCallback iDownloadCommonStatusCallback);

    void downloadOnlineStatusSmallIconByUrl(int i, String str);

    void downloadRichMedia(RichMediaElementGetReq richMediaElementGetReq);

    void enterOrExitAio(ArrayList<EnterOrExitAioInfo> arrayList, IOperateCallback iOperateCallback);

    void feedBackReportForMsg(Contact contact, FeedBackMsgInfo feedBackMsgInfo, FeedBackDataForMsg feedBackDataForMsg, IOperateCallback iOperateCallback);

    void fetchBottomEmojiTableList(FetchBottomEmojiTableListReq fetchBottomEmojiTableListReq, IFetchBottomEmojiTableListCallback iFetchBottomEmojiTableListCallback);

    void fetchFavEmojiList(String str, int i, boolean z, boolean z2, IFetchFavEmojiListCallback iFetchFavEmojiListCallback);

    void fetchGetHitEmotionsByWord(RelatedEmotionWordsInfo relatedEmotionWordsInfo, IFetchGetHitEmotionsByWordCallback iFetchGetHitEmotionsByWordCallback);

    void fetchLongMsg(Contact contact, long j);

    void fetchLongMsgWithCb(Contact contact, long j, IOperateCallback iOperateCallback);

    void fetchMarketEmoticonAioImage(MarketEmoticonAioImageReq marketEmoticonAioImageReq, IOperateCallback iOperateCallback);

    void fetchMarketEmoticonAuthDetail(MarketEmoticonAuthDetailReq marketEmoticonAuthDetailReq, IOperateCallback iOperateCallback);

    void fetchMarketEmoticonFaceImages(ArrayList<MarketEmoticonShowImageReq> arrayList);

    void fetchMarketEmoticonList(int i, int i2, IFetchMarketEmoticonListCallback iFetchMarketEmoticonListCallback);

    void fetchMarketEmoticonShowImage(MarketEmoticonShowImageReq marketEmoticonShowImageReq, IOperateCallback iOperateCallback);

    void fetchMarketEmotionJsonFile(int i, IOperateCallback iOperateCallback);

    void fetchStatusMgrInfo(IGetStatusMsgCallback iGetStatusMsgCallback);

    void fetchStatusUnitedConfigInfo(IGetStatusMsgCallback iGetStatusMsgCallback);

    void forwardFile(TargetFileInfo targetFileInfo, Contact contact, IOperateCallback iOperateCallback);

    void forwardMsgWithComment(ArrayList<Long> arrayList, Contact contact, ArrayList<Contact> arrayList2, ArrayList<MsgElement> arrayList3, HashMap<Integer, MsgAttributeInfo> hashMap, IForwardOperateCallback iForwardOperateCallback);

    void forwardRichMsgInVist(ArrayList<ForWardMsgInVisitReq> arrayList, ArrayList<Contact> arrayList2, IForwardOperateCallback iForwardOperateCallback);

    void forwardSubMsgWithComment(ArrayList<Long> arrayList, ArrayList<Long> arrayList2, Contact contact, ArrayList<Contact> arrayList3, ArrayList<MsgElement> arrayList4, HashMap<Integer, MsgAttributeInfo> hashMap, IForwardOperateCallback iForwardOperateCallback);

    void getABatchOfContactMsgBoxInfo(ArrayList<Contact> arrayList, IGetMsgsBoxesCallback iGetMsgsBoxesCallback);

    void getAioFirstViewLatestMsgs(Contact contact, int i, IGetAioFirstViewLatestMsgCallback iGetAioFirstViewLatestMsgCallback);

    void getAioFirstViewLatestMsgsForAioPopup(Contact contact, int i, IGetAioFirstViewLatestMsgCallback iGetAioFirstViewLatestMsgCallback);

    void getAllOnlineFileMsgs(IMsgOperateCallback iMsgOperateCallback);

    GroupAnonymousInfo getAnonymousInfo(String str);

    void getArkMsgConfig(GetArgMsgConfigReqInfo getArgMsgConfigReqInfo, IGetArkMsgConfigCallback iGetArkMsgConfigCallback);

    void getArkToMarkdownMsgTemplate(GetArkToMarkdownMsgTemplateReqInfo getArkToMarkdownMsgTemplateReqInfo, IGetArkToMarkdownMsgTemplateCallback iGetArkToMarkdownMsgTemplateCallback);

    void getAutoReplyTextList(IGetAutoReplyTextListCallback iGetAutoReplyTextListCallback);

    void getBookmarkData(String str, IBookmarkStorageGetCallback iBookmarkStorageGetCallback);

    void getContactUnreadCnt(ArrayList<Contact> arrayList, IOperateCallback iOperateCallback);

    int getCurChatImportStatusByUin(long j, int i);

    void getCurHiddenSession(IOperateHiddenSessionCallback iOperateHiddenSessionCallback);

    int getDataImportUserLevel();

    void getDraft(Contact contact, IGetDraftOperateCallback iGetDraftOperateCallback);

    void getEmojiResourcePath(int i, IGetEmojiResourcePathCallback iGetEmojiResourcePathCallback);

    void getFavMarketEmoticonInfo(int i, String str, IGetFavMarketEmoticonInfoCallback iGetFavMarketEmoticonInfoCallback);

    String getFileThumbSavePath(String str, int i, boolean z);

    String getFileThumbSavePathForSend(int i, boolean z);

    void getFirstUnreadAtMsg(Contact contact, IGetFirstUnreadAtMsgCallback iGetFirstUnreadAtMsgCallback);

    void getFirstUnreadAtallMsg(Contact contact, IFetchChannelLatestSeqCallback iFetchChannelLatestSeqCallback);

    void getFirstUnreadAtmeMsg(Contact contact, IFetchChannelLatestSeqCallback iFetchChannelLatestSeqCallback);

    void getFirstUnreadMsgSeq(Contact contact, IGetMsgSeqCallback iGetMsgSeqCallback);

    long getGroupMsgStorageTime();

    void getHotPicHotWords(EmojiHotPicGetHotWordsReqBody emojiHotPicGetHotWordsReqBody, IEmojiGetHotPicHotWordsResultCallback iEmojiGetHotPicHotWordsResultCallback);

    void getHotPicInfoListSearchString(String str, String str2, int i, int i2, boolean z, IGetHotPicInfoListCallback iGetHotPicInfoListCallback);

    void getHotPicJumpInfo(EmojiHotPicGetJumpInfoReqBody emojiHotPicGetJumpInfoReqBody, IEmojiHotPicGetJumpInfoResultCallback iEmojiHotPicGetJumpInfoResultCallback);

    void getHotPicSearchResult(EmojiHotPicSearchReqBody emojiHotPicSearchReqBody, IEmojiGetHotPicSearchResultCallback iEmojiGetHotPicSearchResultCallback);

    void getKeyWordRelatedEmoji(KeyWordRelatedEmojiInfo keyWordRelatedEmojiInfo, IOperateCallback iOperateCallback);

    void getLastLiveMsgs(MsgsReq msgsReq, IMsgsRspOperateCallback iMsgsRspOperateCallback);

    void getLastMessageList(ArrayList<Contact> arrayList, IMsgOperateCallback iMsgOperateCallback);

    void getLatestDbMsgs(Contact contact, int i, IMsgOperateCallback iMsgOperateCallback);

    void getMarketEmoticonEncryptKeys(int i, ArrayList<String> arrayList, IGetMarketEmoticonEncryptKeysCallback iGetMarketEmoticonEncryptKeysCallback);

    void getMarketEmoticonPath(int i, ArrayList<String> arrayList, MarketEmojiPathServiceType marketEmojiPathServiceType, IGetMarketEmoticonPathCallback iGetMarketEmoticonPathCallback);

    HashMap<String, MarketEmoticonPath> getMarketEmoticonPathBySync(int i, ArrayList<String> arrayList, MarketEmojiPathServiceType marketEmojiPathServiceType);

    void getMiscData(String str, IGProMiscStorageGetCallback iGProMiscStorageGetCallback);

    void getMiscDataVer2(MiscKeyInfo miscKeyInfo, IGetMiscDataCallback iGetMiscDataCallback);

    void getMqqDataImportTableNames(IDataImportTableNamesCallback iDataImportTableNamesCallback);

    void getMsgAbstract(Contact contact, long j, IGetMsgAbstractsCallback iGetMsgAbstractsCallback);

    void getMsgByClientSeqAndTime(Contact contact, long j, long j2, IMsgOperateCallback iMsgOperateCallback);

    void getMsgEmojiLikesList(Contact contact, long j, String str, long j2, String str2, boolean z, int i, IGetMsgEmojiLikesListCallback iGetMsgEmojiLikesListCallback);

    void getMsgEventFlow(Contact contact);

    void getMsgQRCode(IGetMsgQRCodeCallback iGetMsgQRCodeCallback);

    void getMsgSetting(IOperateCallback iOperateCallback);

    void getMsgWithAbstractByFilterParam(Contact contact, long j, long j2, int i, MsgFilterParams msgFilterParams, IGetMsgWithAbstractCallback iGetMsgWithAbstractCallback);

    void getMsgs(Contact contact, long j, int i, boolean z, IMsgOperateCallback iMsgOperateCallback);*/
    void getMsgsByMsgId(Contact contact, ArrayList<Long> arrayList, IMsgOperateCallback iMsgOperateCallback);

    /*
    void getMsgsBySeqAndCount(Contact contact, long j, int i, boolean z, boolean z2, IMsgOperateCallback iMsgOperateCallback);

    void getMsgsByTypeFilter(Contact contact, long j, ArrayList<MsgTypeFilter> arrayList, int i, boolean z, IMsgOperateCallback iMsgOperateCallback);

    void getMsgsByTypeFilters(Contact contact, long j, int i, boolean z, ArrayList<MsgTypeFilter> arrayList, IMsgOperateCallback iMsgOperateCallback);

    void getMsgsExt(MsgsReq msgsReq, IMsgsRspOperateCallback iMsgsRspOperateCallback);

    void getMsgsIncludeSelf(Contact contact, long j, int i, boolean z, IMsgOperateCallback iMsgOperateCallback);

    void getMsgsWithMsgTimeAndClientSeqForC2C(Contact contact, long j, long j2, int i, boolean z, boolean z2, boolean z3, IMsgOperateCallback iMsgOperateCallback);

    void getMsgsWithStatus(GetMsgsAndStatusRecord getMsgsAndStatusRecord, IGetMsgWithStatusCallback iGetMsgWithStatusCallback);

    void getMultiMsg(Contact contact, long j, long j2, IGetMultiMsgCallback iGetMultiMsgCallback);

    void getNtMsgSyncContactUnreadState(IGetNtMsgSyncContactUnreadStateCallback iGetNtMsgSyncContactUnreadStateCallback);

    void getOnLineDev(IOperateCallback iOperateCallback);

    void getOnlineFileMsgs(Contact contact, IMsgOperateCallback iMsgOperateCallback);

    void getOnlineStatusBigIconBasePath(IGetStatusMsgCallback iGetStatusMsgCallback);

    void getOnlineStatusCommonFileNameByUrl(String str, IGetStatusMsgCallback iGetStatusMsgCallback);

    void getOnlineStatusCommonPath(String str, IGetStatusMsgCallback iGetStatusMsgCallback);

    void getOnlineStatusSmallIconBasePath(IGetStatusMsgCallback iGetStatusMsgCallback);

    void getOnlineStatusSmallIconFileNameByUrl(String str, IGetStatusMsgCallback iGetStatusMsgCallback);

    void getRecallMsgsByMsgId(Contact contact, ArrayList<Long> arrayList, IMsgOperateCallback iMsgOperateCallback);

    void getRecentEmojiList(int i, IGetRecentUseEmojiListCallback iGetRecentUseEmojiListCallback);

    void getRecentHiddenSesionList(IOperateHiddenSessionCallback iOperateHiddenSessionCallback);

    void getRecentUseEmojiList(IGetRecentUseEmojiListCallback iGetRecentUseEmojiListCallback);

    void getRecentUsedFaceList(int i, IGetRecentUsedFaceListCallback iGetRecentUsedFaceListCallback);

    void getReplyDraft(Contact contact, long j, IGetDraftOperateCallback iGetDraftOperateCallback);

    void getReplyMultiFwdReferenceMsg(Contact contact, ReplyElement replyElement, IMsgOperateCallback iMsgOperateCallback);

    void getRichMediaElement(RichMediaElementGetReq richMediaElementGetReq);

    String getRichMediaFilePathForGuild(RichMediaFilePathInfo richMediaFilePathInfo);

    String getRichMediaFilePathForMobileQQSend(RichMediaFilePathInfo richMediaFilePathInfo);

    void getServiceAssistantSwitch(GetServiceAssistantSwitchReq getServiceAssistantSwitchReq, IGetServiceAssistantSwitchCallback iGetServiceAssistantSwitchCallback);

    void getSilenceUnreadCnt(IGetSilenceUnreadCntCallback iGetSilenceUnreadCntCallback);

    void getSingleMsg(Contact contact, long j, IMsgOperateCallback iMsgOperateCallback);

    void getSourceOfReplyMsg(Contact contact, long j, long j2, IMsgOperateCallback iMsgOperateCallback);

    void getSourceOfReplyMsgByClientSeqAndTime(Contact contact, long j, long j2, long j3, IMsgOperateCallback iMsgOperateCallback);

    void getSourceOfReplyMsgV2(Contact contact, long j, long j2, IMsgOperateCallback iMsgOperateCallback);

    void getTempChatInfo(int i, String str, IGetTempChatInfoCallback iGetTempChatInfoCallback);

    void grabRedBag(GrabRedBagReq grabRedBagReq, IGrabRedBagCallback iGrabRedBagCallback);

    void importDataLineMsg();

    void importOldDbMsg(IOperateCallback iOperateCallback);

    void insertMsgToMsgBox(Contact contact, long j, int i, IOperateCallback iOperateCallback);

    void insertRecentlyForwardContactInfos(ArrayList<RecentlyForwardContactInfo> arrayList, IOperateCallback iOperateCallback);

    void isHitEmojiKeyword(RelatedEmotionWordsInfo relatedEmotionWordsInfo, IOperateCallback iOperateCallback);

    void isMqqDataImportFinished(IOperateCallback iOperateCallback);

    void isMsgMatched(MatchKey matchKey, IMatchedOperateCallback iMatchedOperateCallback);

    void kickOffLine(DevInfo devInfo, IOperateCallback iOperateCallback);

    void likeOrDislikeReportForMsg(Contact contact, FeedBackMsgInfo feedBackMsgInfo, FeedBackDataForMsg feedBackDataForMsg, IOperateCallback iOperateCallback);

    void modifyBottomEmojiTableSwitchStatus(ModifyBottomEmojiTableSwitchStatusReq modifyBottomEmojiTableSwitchStatusReq, IModifyBottomEmojiTableSwitchStatusCallback iModifyBottomEmojiTableSwitchStatusCallback);

    void modifyFavEmojiDesc(ArrayList<EmojiDescInfo> arrayList, IModifyFavEmojiDescCallback iModifyFavEmojiDescCallback);

    void moveBottomEmojiTable(MoveBottomEmojiTableReq moveBottomEmojiTableReq, IMoveBottomEmojiTableCallback iMoveBottomEmojiTableCallback);

    void multiForwardMsg(ArrayList<MultiMsgInfo> arrayList, Contact contact, Contact contact2, IOperateCallback iOperateCallback);

    void multiForwardMsgWithComment(ArrayList<MultiMsgInfo> arrayList, Contact contact, Contact contact2, ArrayList<MsgElement> arrayList2, HashMap<Integer, MsgAttributeInfo> hashMap, IOperateCallback iOperateCallback);

    void operateSpecifiedMsgBoxEventTypes(Contact contact, int i, ArrayList<SpecificEventTypeInfoInMsgBox> arrayList, IOperateCallback iOperateCallback);

    void packRedBag(PackRedBagReq packRedBagReq, IPackRedBagCallback iPackRedBagCallback);

    void prepareTempChat(TempChatPrepareInfo tempChatPrepareInfo, IOperateCallback iOperateCallback);

    void pullDetail(PullDetailReq pullDetailReq, IPullDetailCallback iPullDetailCallback);

    void pullRedBagPasswordList(IPullRedBagPasswordListCallback iPullRedBagPasswordListCallback);

    void queryArkInfo(QueryArkInfoReq queryArkInfoReq, IKernelQueryArkInfoCallback iKernelQueryArkInfoCallback);

    void queryCalendar(Contact contact, long j, IQueryCalendarCallback iQueryCalendarCallback);

    void queryEmoticonMsgs(long j, long j2, long j3, QueryMsgsParams queryMsgsParams, IMsgOperateCallback iMsgOperateCallback);

    void queryFavEmojiByDesc(String str, IFetchFavEmojiListCallback iFetchFavEmojiListCallback);

    void queryFileMsgsDesktop(long j, long j2, long j3, QueryMsgsParams queryMsgsParams, IMsgOperateCallback iMsgOperateCallback);

    void queryFirstMsgSeq(Contact contact, long j, IQueryFirstMsgSeqCallback iQueryFirstMsgSeqCallback);

    void queryFirstRoamMsg(Contact contact, long j, IQueryFirstRoamMsgCallback iQueryFirstRoamMsgCallback);

    void queryMsgsAndAbstractsWithFilter(long j, long j2, long j3, QueryMsgsParams queryMsgsParams, IQueryMsgsAndAbstractsWithFilterCallback iQueryMsgsAndAbstractsWithFilterCallback);

    void queryMsgsWithFilter(long j, long j2, QueryMsgsParams queryMsgsParams, IMsgOperateCallback iMsgOperateCallback);

    void queryMsgsWithFilterEx(long j, long j2, long j3, QueryMsgsParams queryMsgsParams, IMsgOperateCallback iMsgOperateCallback);

    void queryMsgsWithFilterVer2(long j, long j2, QueryMsgsParams queryMsgsParams, IQueryMsgsWithFilterVer2Callback iQueryMsgsWithFilterVer2Callback);

    void queryPicOrVideoMsgs(long j, long j2, long j3, QueryMsgsParams queryMsgsParams, IMsgOperateCallback iMsgOperateCallback);

    void queryPicOrVideoMsgsDesktop(ChatInfo chatInfo, MsgIdInfo msgIdInfo, int i, boolean z, IMsgOperateCallback iMsgOperateCallback);

    void queryRecentlyForwardContactInfo(String str, IQueryRecentlyForwardContactInfoCallback iQueryRecentlyForwardContactInfoCallback);

    void queryRoamCalendar(Contact contact, long j, IQueryCalendarCallback iQueryCalendarCallback);

    void queryShortVideoMsgs(long j, long j2, long j3, QueryMsgsParams queryMsgsParams, IMsgOperateCallback iMsgOperateCallback);

    void queryTroopEmoticonMsgs(long j, long j2, long j3, QueryMsgsParams queryMsgsParams, IMsgOperateCallback iMsgOperateCallback);

    void queryUserSecQuality(IQueryUserSecQualityCallback iQueryUserSecQualityCallback);

    void recallMsg(Contact contact, ArrayList<Long> arrayList, IOperateCallback iOperateCallback);

    void recordEmoji(RecordType recordType, ArrayList<RecordEmojiInfo> arrayList);

    void reeditRecallMsg(Contact contact, long j, IOperateCallback iOperateCallback);

    void refuseGetRichMediaElement(RichMediaElementGetReq richMediaElementGetReq);

    void refuseReceiveOnlineFileMsg(Contact contact, long j, IOperateCallback iOperateCallback);

    void regenerateMsg(Contact contact, long j, IOperateCallback iOperateCallback);

    void registerBusinessNotification(ArrayList<BusinessInfo> arrayList, IOperateCallback iOperateCallback);

    void registerSysMsgNotification(int i, long j, ArrayList<Long> arrayList, IOperateCallback iOperateCallback);

    void removeKernelMsgListener(long j);

    void removeKernelMsgNotifyListener(long j);

    void removeKernelTempChatSigListener(long j);

    void renameAnonyChatNick(String str, IRenameAnonymousChatNickCallback iRenameAnonymousChatNickCallback);

    void replyMsgWithSourceMsgInfo(SourceMsgInfoForReplyMsg sourceMsgInfoForReplyMsg, ArrayList<MsgElement> arrayList, HashMap<Integer, MsgAttributeInfo> hashMap, IReplyMsgWithSourceMsgInfoCallback iReplyMsgWithSourceMsgInfoCallback);

    void reqToOfflineSendMsg(Contact contact, long j, IOperateCallback iOperateCallback);

    void requestTianshuAdv(ArrayList<TianShuAdPosItemData> arrayList, ITianShuGetAdvCallback iTianShuGetAdvCallback);

    void resendMsg(Contact contact, long j, IOperateCallback iOperateCallback);

    void selectPasswordRedBag(GrabRedBagReq grabRedBagReq);


    void sendMsg(long j, Contact contact, ArrayList<MsgElement> arrayList, HashMap<Integer, MsgAttributeInfo> hashMap, IOperateCallback iOperateCallback);

    void sendShowInputStatusReq(int i, int i2, String str, IOperateCallback iOperateCallback);

    void setAllC2CAndGroupMsgRead(IOperateCallback iOperateCallback);

    void setAutoReplyTextList(ArrayList<AutoReplyText> arrayList, int i, IOperateCallback iOperateCallback);

    void setBookmarkData(String str, String str2, IGProSimpleCallback iGProSimpleCallback);

    void setContactLocalTop(Contact contact, boolean z, IOperateCallback iOperateCallback);

    void setCurHiddenSession(RecentHiddenSesionInfo recentHiddenSesionInfo, IOperateCallback iOperateCallback);

    void setCurOnScreenMsgForMsgEvent(Contact contact, HashMap<Long, byte[]> hashMap);

    void setDraft(Contact contact, ArrayList<MsgElement> arrayList, IOperateCallback iOperateCallback);

    void setFocusOnBase(Contact contact);

    void setIKernelPublicAccountAdapter(IKernelPublicAccountAdapter iKernelPublicAccountAdapter);

    void setIsStopKernelFetchLongMsg(boolean z, IOperateCallback iOperateCallback);

    void setKernelMsgStatusDelegate(IKernelMsgStatusDelegate iKernelMsgStatusDelegate);

    void setLocalMsgRead(Contact contact, IOperateCallback iOperateCallback);

    void setMarkUnreadFlag(Contact contact, boolean z);

    void setMiscData(String str, String str2, IGProSimpleCallback iGProSimpleCallback);

    void setMiscDataVer2(MiscData miscData, IOperateCallback iOperateCallback);

    void setMsgEmojiLikes(Contact contact, long j, String str, long j2, boolean z, ISetMsgEmojiLikesCallback iSetMsgEmojiLikesCallback);

    void setMsgEmojiLikesForRole(Contact contact, long j, String str, long j2, long j3, long j4, boolean z, boolean z2, AttaReportData attaReportData, ISetMsgEmojiLikesForRoleCallback iSetMsgEmojiLikesForRoleCallback);

    void setMsgRead(Contact contact, IOperateCallback iOperateCallback);

    void setMsgReadAndReport(Contact contact, MsgRecord msgRecord, IOperateCallback iOperateCallback);

    void setMsgReadByChatType(int i, IOperateCallback iOperateCallback);

    void setMsgRichInfoFlag(boolean z);

    void setMsgSetting(MsgSetting msgSetting, IOperateCallback iOperateCallback);

    void setPowerStatus(boolean z);

    void setPttPlayedState(long j, Contact contact, long j2, IOperateCallback iOperateCallback);

    void setRecentHiddenSession(ArrayList<RecentHiddenSesionInfo> arrayList, IOperateCallback iOperateCallback);

    void setReplyDraft(Contact contact, long j, ArrayList<MsgElement> arrayList, IOperateCallback iOperateCallback);

    void setServiceAssistantSwitch(SetServiceAssistantSwitchReq setServiceAssistantSwitchReq, IOperateCallback iOperateCallback);

    void setSpecificMsgReadAndReport(Contact contact, long j, IOperateCallback iOperateCallback);

    void setStatus(StatusReq statusReq, IOperateCallback iOperateCallback);

    void setSubscribeFolderUsingSmallRedPoint(boolean z);

    void setToken(SetTokenReq setTokenReq, IOperateCallback iOperateCallback);

    void setTokenForMqq(byte[] bArr, IOperateCallback iOperateCallback);

    void startMsgSync();

    void stopGenerateMsg(Contact contact, long j, IOperateCallback iOperateCallback);

    void stopImportOldDbMsgAndroid();

    void switchAnonymousChat(String str, boolean z, ISwitchAnonymousChatCallback iSwitchAnonymousChatCallback);

    void switchBackGround(SwitchToBackgroundReq switchToBackgroundReq, IOperateCallback iOperateCallback);

    void switchBackGroundForMqq(byte[] bArr, IOperateCallback iOperateCallback);

    void switchForeGround(IOperateCallback iOperateCallback);

    void switchForeGroundForMqq(byte[] bArr, IOperateCallback iOperateCallback);

    void switchToOfflineGetRichMediaElement(RichMediaElementGetReq richMediaElementGetReq);

    void switchToOfflineSendMsg(Contact contact, long j);

    void tianshuMultiReport(ArrayList<TianShuReportData> arrayList, IOperateCallback iOperateCallback);

    void tianshuReport(TianShuReportData tianShuReportData, IOperateCallback iOperateCallback);

    void translatePtt2Text(long j, Contact contact, MsgElement msgElement, IOperateCallback iOperateCallback);

    void translatePtt2TextAiVoice(long j, Contact contact, MsgElement msgElement, IOperateCallback iOperateCallback);

    void unregisterBusinessNotification(ArrayList<BusinessInfo> arrayList, IOperateCallback iOperateCallback);

    void unregisterSysMsgNotification(int i, long j, ArrayList<Long> arrayList, IOperateCallback iOperateCallback);

    void updateAnonymousInfo(String str, GroupAnonymousInfo groupAnonymousInfo);

    void updateElementExtBufForUI(Contact contact, long j, long j2, byte[] bArr, IOperateCallback iOperateCallback);

    void updateMsgRecordExtPbBufForUI(Contact contact, long j, byte[] bArr, IOperateCallback iOperateCallback);

    void updatePublicAccountInfo(PublicAccountInfo publicAccountInfo, IOperateCallback iOperateCallback);
     */
}

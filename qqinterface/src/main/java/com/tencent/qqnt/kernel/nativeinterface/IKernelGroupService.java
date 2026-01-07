package com.tencent.qqnt.kernel.nativeinterface;


import java.util.ArrayList;

public interface IKernelGroupService {
    /*void ForceFetchGroupPartMemberNewExtInfo(long j, ArrayList<Long> arrayList, IGroupMemberNewExtInfoCallback iGroupMemberNewExtInfoCallback);

    void GetGroupAllMemberNewExtInfo(long j, IGroupMemberNewExtInfoCallback iGroupMemberNewExtInfoCallback);

    void GetGroupPartMemberNewExtInfo(long j, ArrayList<Long> arrayList, IGroupMemberNewExtInfoCallback iGroupMemberNewExtInfoCallback);

    void RefreshGroupAllMemberNewExtInfo(long j, IGroupMemberNewExtInfoCallback iGroupMemberNewExtInfoCallback);

    void SetGroupMemberNewExtInfo(SetGroupMemberExtInfoReq setGroupMemberExtInfoReq, IOperateCallback iOperateCallback);

    void addGroupEssence(DigestReq digestReq, IDigestCallback iDigestCallback);

    long addKernelGroupListener(IKernelGroupListener iKernelGroupListener);

    void applyTeamUp(TeamUpApplyReq teamUpApplyReq, IOperateCallback iOperateCallback);

    void batchQueryCachedGroupDetailInfo(BatchQueryCachedGroupDetailInfoReq batchQueryCachedGroupDetailInfoReq, IBatchQueryCachedGroupDetailInfoCallback iBatchQueryCachedGroupDetailInfoCallback);

    void changeGroupShieldSettingTemp(long j, boolean z, IOperateCallback iOperateCallback);

    void checkGroupMemberCache(ArrayList<Long> arrayList, IGroupMemberCacheCallback iGroupMemberCacheCallback);

    void checkIsContainMuteMember(long j, ICheckIsContainMuteMemberCallback iCheckIsContainMuteMemberCallback);

    void checkSpecialGroupBizMsg(CheckSpecialGroupBizMsgReq checkSpecialGroupBizMsgReq, ICheckSpecialGroupBizMsgCallback iCheckSpecialGroupBizMsgCallback);

    void cleanCapsuleCache(long j);

    void clearGroupNotifies(boolean z, IOperateCallback iOperateCallback);

    void clearGroupNotifiesUnreadCount(boolean z, IOperateCallback iOperateCallback);

    void clearGroupNotifyLocalUnreadCount(boolean z, int i, IOperateCallback iOperateCallback);

    void closeMultiGroupMuteTask(CloseMultiGroupMuteTaskReq closeMultiGroupMuteTaskReq, IOperateCallback iOperateCallback);

    void consumeGroupTopBanner(ConsumeGroupTopBannerReq consumeGroupTopBannerReq, IOperateCallback iOperateCallback);

    void createDragonLadder(GroupDragonLadderCreateReq groupDragonLadderCreateReq, ICreateDragonLadderCallback iCreateDragonLadderCallback);

    void createGroup(ArrayList<InviteMemberInfo> arrayList, ICreateGroupCallback iCreateGroupCallback);

    void createGroupProfileShare(CreateGroupProfileShareReq createGroupProfileShareReq, ICreateGroupProfileShareCallback iCreateGroupProfileShareCallback);

    void createGroupV2(CreateGroupReq createGroupReq, ArrayList<InviteMemberInfo> arrayList, ICreateGroupCallback iCreateGroupCallback);

    void createGroupWithMembers(String str, ArrayList<String> arrayList, ICreateGroupCallback iCreateGroupCallback);

    String createMemberListScene(long j, String str);

    void deleteGroupBulletin(long j, String str, String str2, IOperateCallback iOperateCallback);

    void deleteTeamUp(DeleteTeamUpReq deleteTeamUpReq, IOperateCallback iOperateCallback);

    void destroyGroup(long j, IOperateCallback iOperateCallback);

    void destroyGroupV2(DestroyGroupReq destroyGroupReq, IOperateCallback iOperateCallback);

    void destroyMemberListScene(String str);

    void diffDragonLadder(DiffDragonLadderReq diffDragonLadderReq, IDiffDragonLadderCallback iDiffDragonLadderCallback);

    void downloadGroupBulletinRichMedia(BulletinFeedsRichMediaDownloadReq bulletinFeedsRichMediaDownloadReq);

    void dragonLadderDocAuth(GroupDragonLadderAuthDocReq groupDragonLadderAuthDocReq, IDragonLadderDocAuthCallback iDragonLadderDocAuthCallback);

    void dragonLadderExport(GroupDragonLadderExportDocReq groupDragonLadderExportDocReq, IDragonLadderExportCallback iDragonLadderExportCallback);

    void fetchGroupEssenceList(FetchGroupEssenceListReq fetchGroupEssenceListReq, String str, IFetchGroupEssenceListCallback iFetchGroupEssenceListCallback);

    void fetchGroupNotify(boolean z, IOperateCallback iOperateCallback);

    void getAICommonVoice(GetVoiceReq getVoiceReq, IGetVoiceCallback iGetVoiceCallback);

    void getAIOBindGuildInfo(GetAIOBindGuildInfoReq getAIOBindGuildInfoReq, IGetAIOBindGuildInfoCallback iGetAIOBindGuildInfoCallback);

    void getAllCachedGroupAIOSlotInfos(IGetCachedGroupAIOSlotInfoCallback iGetCachedGroupAIOSlotInfoCallback);

    void getAllGroupPrivilegeFlag(ArrayList<Long> arrayList, int i, IGroupPrivilegeFlagCallback iGroupPrivilegeFlagCallback);

    void getAllMemberList(long j, boolean z, IGroupMemberListCallback iGroupMemberListCallback);

    void getAllMemberListV2(long j, boolean z, IGroupMemberListCallback iGroupMemberListCallback);

    void getAppCenter(GetAppCenterReq getAppCenterReq, IGetAppCenterCallback iGetAppCenterCallback);

    void getCachedGroupAIOSlotInfo(ArrayList<Long> arrayList, IGetCachedGroupAIOSlotInfoCallback iGetCachedGroupAIOSlotInfoCallback);

    void getCapsuleApp(GetCapsuleAppReq getCapsuleAppReq, boolean z, IGetCapsuleAppCallback iGetCapsuleAppCallback);

    void getCapsuleAppPro(GetCapsuleAppReq getCapsuleAppReq, boolean z, IGetCapsuleAppProCallback iGetCapsuleAppProCallback);

    void getCardAppList(GetCardAppListReq getCardAppListReq, boolean z, IGetCardAppListCallback iGetCardAppListCallback);

    void getDiscussExistInfo(long j, IDiscussSimpleInfoCallback iDiscussSimpleInfoCallback);

    void getDragonLadderDetail(GroupDragonLadderGetDetailReq groupDragonLadderGetDetailReq, IGetDragonLadderDetailCallback iGetDragonLadderDetailCallback);

    void getDragonLadderList(GroupDragonLadderGetListReq groupDragonLadderGetListReq, String str, boolean z, IGetDragonLadderListCallback iGetDragonLadderListCallback);

    void getGroupAllInfo(long j, GroupInfoSource groupInfoSource, IOperateCallback iOperateCallback);

    void getGroupArkInviteState(long j, long j2, long j3, IOperateCallback iOperateCallback);

    void getGroupAuthDetailInfo(GroupAuthDetailReq groupAuthDetailReq, IGetGroupAuthDetailInfoCallback iGetGroupAuthDetailInfoCallback);

    void getGroupAvatarWall(long j, IGroupAvatarWallCallback iGroupAvatarWallCallback);

    void getGroupBindGuilds(GetGroupBindGuildsReq getGroupBindGuildsReq, IGetGroupBindGuildsCallback iGetGroupBindGuildsCallback);

    void getGroupBulletin(long j, IOperateCallback iOperateCallback);

    void getGroupBulletinList(long j, String str, String str2, GroupBulletinListReq groupBulletinListReq, IOperateCallback iOperateCallback);

    void getGroupCanBeRelated(GetGroupCanBeRelatedReq getGroupCanBeRelatedReq, IGetGroupCanBeRelatedCallback iGetGroupCanBeRelatedCallback);

    void getGroupChatManageRobotDomainFromUrl(GetGroupChatManageRobotDomainFromUrlReq getGroupChatManageRobotDomainFromUrlReq, IGetGroupChatManageRobotDomainFromUrlCallback iGetGroupChatManageRobotDomainFromUrlCallback);

    void getGroupConfMember(long j, boolean z, IOperateCallback iOperateCallback);

    int getGroupDBVersion(int i);

    void getGroupDetailInfo(long j, GroupInfoSource groupInfoSource, IOperateCallback iOperateCallback);

    void getGroupDetailInfoByFilter(GroupDetailInfoReq groupDetailInfoReq, int i, int i2, boolean z, IGroupDetailInfoCallback iGroupDetailInfoCallback);

    void getGroupDetailInfoForMqq(long j, int i, int i2, boolean z, IOperateCallback iOperateCallback);

    void getGroupExt0xEF0Info(ArrayList<Long> arrayList, ArrayList<Long> arrayList2, GroupExtFilter groupExtFilter, boolean z, IGroupExt0xEF0InfoCallback iGroupExt0xEF0InfoCallback);

    void getGroupExtList(boolean z, IOperateCallback iOperateCallback);

    void getGroupFlagForThirdApp(ArrayList<Long> arrayList, IGetGroupFlagForThirdAppCallback iGetGroupFlagForThirdAppCallback);

    void getGroupHonorList(GroupMemberHonorReq groupMemberHonorReq, IGroupMemberHonorCallback iGroupMemberHonorCallback);

    void getGroupInfoForJoinGroup(long j, boolean z, int i, IGroupInfoForJoinCallback iGroupInfoForJoinCallback);

    void getGroupInviteNoAuthLimitNum(ArrayList<Long> arrayList, IGetGroupInviteNoAuthLimitNumCallback iGetGroupInviteNoAuthLimitNumCallback);

    void getGroupLatestEssenceList(GetGroupLatestEssenceListReq getGroupLatestEssenceListReq, IGetGroupLatestEssenceListCallback iGetGroupLatestEssenceListCallback);

    void getGroupList(boolean z, IOperateCallback iOperateCallback);

    void getGroupMedalList(GetGroupMedalListReq getGroupMedalListReq, IGetGroupMedalListCallback iGetGroupMedalListCallback);

    void getGroupMemberAuthDetailInfo(GroupMemberAuthDetailReq groupMemberAuthDetailReq, IGetGroupMemberAuthDetailInfoCallback iGetGroupMemberAuthDetailInfoCallback);

    void getGroupMemberCardInfo(GroupMemberCardInfoReq groupMemberCardInfoReq, IGetGroupMemberCardInfoCallback iGetGroupMemberCardInfoCallback);

    void getGroupMemberLevelInfo(long j, IOperateCallback iOperateCallback);

    void getGroupMemberMaxNum(long j, int i, IGroupMemberMaxNumCallback iGroupMemberMaxNumCallback);

    void getGroupMsgLimitFreq(long j, IGroupMsgLimitFreqCallback iGroupMsgLimitFreqCallback);

    void getGroupMsgMask(IOperateCallback iOperateCallback);

    void getGroupNotifiesUnreadCount(boolean z, IOperateCallback iOperateCallback);

    void getGroupPayToJoinStatus(long j, IGroupPayToJoinStatusCallback iGroupPayToJoinStatusCallback);

    void getGroupPlusAppList(GetAppListReq getAppListReq, IGetAppListCallback iGetAppListCallback);

    void getGroupPortrait(long j, IOperateCallback iOperateCallback);

    void getGroupRecommendContactArkJson(long j, IGetGroupRecommendContactArkJsonCallback iGetGroupRecommendContactArkJsonCallback);

    void getGroupRemainAtTimes(long j, IGetGroupRemainAtAllTimesOperateCallback iGetGroupRemainAtAllTimesOperateCallback);

    void getGroupSecLevelInfo(long j, int i, IGroupSecLevelInfoCallback iGroupSecLevelInfoCallback);

    void getGroupSeqAndJoinTimeForGrayTips(long j, IGroupInfoForGrayTipsCallback iGroupInfoForGrayTipsCallback);

    void getGroupShutUpMemberList(long j, IOperateCallback iOperateCallback);

    void getGroupStatisticInfo(long j, IOperateCallback iOperateCallback);

    void getGroupTagRecords(long j, IGroupTagRecordCallback iGroupTagRecordCallback);

    void getIdentityList(GetIdentityListReq getIdentityListReq, boolean z, IGetIdentityListCallback iGetIdentityListCallback);

    void getIdentityListCacheData(GetIdentityListReq getIdentityListReq, IGetIdentityListCallback iGetIdentityListCallback);

    void getIllegalMemberList(long j, IGroupMemberIllegalInfoCallback iGroupMemberIllegalInfoCallback);

    void getJoinGroupLink(GroupLinkReq groupLinkReq, IGetJoinGroupLinkCallback iGetJoinGroupLinkCallback);

    void getJoinGroupNoVerifyFlag(long j, String str, IOperateCallback iOperateCallback);

    void getMemberCommonInfo(GroupMemberCommonReq groupMemberCommonReq, IGroupMemberCommonCallback iGroupMemberCommonCallback);

    void getMemberExtInfo(GroupMemberExtReq groupMemberExtReq, IGroupMemberExtCallback iGroupMemberExtCallback);

    void getMemberInfo(long j, ArrayList<String> arrayList, boolean z, IOperateCallback iOperateCallback);

    void getMemberInfoForMqq(long j, ArrayList<String> arrayList, boolean z, IGroupMemberListCallback iGroupMemberListCallback);

    void getMemberInfoForMqqV2(long j, ArrayList<String> arrayList, boolean z, String str, IGroupMemberListCallback iGroupMemberListCallback);

    void getNextMemberList(String str, GroupMemberInfoListId groupMemberInfoListId, int i, IGroupMemberListCallback iGroupMemberListCallback);

    void getPrevMemberList(String str, GroupMemberInfoListId groupMemberInfoListId, int i, IGroupMemberListCallback iGroupMemberListCallback);

    void getRelatedGroup(GetRelatedGroupReq getRelatedGroupReq, boolean z, IGetRelatedGroupCallback iGetRelatedGroupCallback);

    void getSingleScreenNotifies(boolean z, long j, int i, IOperateCallback iOperateCallback);

    void getSingleScreenNotifiesV2(boolean z, long j, int i, int i2, IGetGroupSingleScreenNotifiesCallback iGetGroupSingleScreenNotifiesCallback);

    void getSubGroupInfo(GetSubGroupInfoReq getSubGroupInfoReq, IGetSubGroupInfoCallback iGetSubGroupInfoCallback);

    void getSwitchStatusForEssenceMsg(long j, IGroupEssenceMsgSwitchCallback iGroupEssenceMsgSwitchCallback);

    void getTeamUpDetail(GetTeamUpDetailReq getTeamUpDetailReq, IGetTeamUpDetailCallback iGetTeamUpDetailCallback);

    void getTeamUpList(GetTeamUpListReq getTeamUpListReq, IGetTeamUpListCallback iGetTeamUpListCallback);

    void getTeamUpMembers(GetTeamUpMembersReq getTeamUpMembersReq, IGetTeamUpMembersCallback iGetTeamUpMembersCallback);

    void getTeamUpTemplateList(GetTeamUpTemplateListReq getTeamUpTemplateListReq, IGetTeamUpTemplateListCallback iGetTeamUpTemplateListCallback);

    void getTopicPage(long j, long j2, String str, String str2, IGetTopicPageCallback iGetTopicPageCallback);

    void getTopicRecall(TopicRecallReq topicRecallReq, ITopicRecallCallback iTopicRecallCallback);

    void getTransferableMemberInfo(long j, IGetTransferableMemberCallback iGetTransferableMemberCallback);
    */
    void getUidByUins(ArrayList<Long> arrayList, IGroupMemberUidCallback iGroupMemberUidCallback);

    void getUinByUids(ArrayList<String> arrayList, IGroupMemberUinCallback iGroupMemberUinCallback);

    /*
    void getWxNotifyStatus(ArrayList<Long> arrayList, IGetGroupWxNotifyStatusCallback iGetGroupWxNotifyStatusCallback);

    void groupBlacklistDelApply(GroupBlacklistDelApplyReq groupBlacklistDelApplyReq, IGroupBlacklistDelApplyCallback iGroupBlacklistDelApplyCallback);

    void groupBlacklistGetAllApply(GroupBlacklistGetAllApplyReq groupBlacklistGetAllApplyReq, IGroupBlacklistGetAllApplyCallback iGroupBlacklistGetAllApplyCallback);

    void halfScreenPullNotice(HalfScreenPullNoticeReq halfScreenPullNoticeReq, IHalfScreenPullNoticeCallback iHalfScreenPullNoticeCallback);

    void halfScreenReportClick(HalfScreenReportClickReq halfScreenReportClickReq, IOperateCallback iOperateCallback);

    void inviteMembersToGroup(long j, HashMap<String, Long> hashMap, IOperateCallback iOperateCallback);

    void inviteMembersToGroupWithMsg(long j, HashMap<String, Long> hashMap, ArrayList<ShareMsgInfo> arrayList, IOperateCallback iOperateCallback);

    void inviteToGroup(long j, ArrayList<String> arrayList, IOperateCallback iOperateCallback);

    void inviteToGroupV2(InviteGroupReq inviteGroupReq, IInviteToGroupCallback iInviteToGroupCallback);

    void isEssenceMsg(EssenceKey essenceKey, ICheckEssenceCallback iCheckEssenceCallback);

    void joinDragonLadder(GroupDragonLadderJoinReq groupDragonLadderJoinReq, IJoinDragonLadderCallback iJoinDragonLadderCallback);

    void joinDragonLadderForAIO(GroupDragonLadderJoinForAIOReq groupDragonLadderJoinForAIOReq, IJoinDragonLadderForAIOCallback iJoinDragonLadderForAIOCallback);

    void joinGroup(ReqToGroup reqToGroup, IJoinGroupCallback iJoinGroupCallback);

    void kickMember(long j, ArrayList<String> arrayList, boolean z, String str, IKickMemberOperateCallback iKickMemberOperateCallback);

    void kickMemberV2(KickMemberReq kickMemberReq, IKickMemberCallback iKickMemberCallback);

    void listAllAIVoice(ListAllVoiceReq listAllVoiceReq, IListAllVoiceCallback iListAllVoiceCallback);

    void miniAppGetGroupInfo(MiniAppGetGroupInfoReq miniAppGetGroupInfoReq, IMiniAppGetGroupInfoCallback iMiniAppGetGroupInfoCallback);

    void modifyGroupDetailInfo(long j, GroupModifyInfo groupModifyInfo, IOperateCallback iOperateCallback);

    void modifyGroupDetailInfoV2(GroupModifyInfoReq groupModifyInfoReq, int i, IOperateCallback iOperateCallback);

    void modifyGroupExtInfo(GroupExtInfo groupExtInfo, IOperateCallback iOperateCallback);

    void modifyGroupExtInfoV2(GroupExtInfo groupExtInfo, GroupExtFilter groupExtFilter, IModifyGroupExtCallback iModifyGroupExtCallback);

    void modifyGroupName(long j, String str, boolean z, IOperateCallback iOperateCallback);

    void modifyGroupRemark(long j, String str, IOperateCallback iOperateCallback);

    void modifyMemberCardName(long j, String str, String str2, IOperateCallback iOperateCallback);

    void modifyMemberRole(long j, String str, MemberRole memberRole, IOperateCallback iOperateCallback);

    void modifyWxNotifyStatus(long j, GroupWxNotifyStatus groupWxNotifyStatus, IOperateCallback iOperateCallback);

    void monitorMemberList(String str, GroupMemberInfoListId groupMemberInfoListId, GroupMemberInfoListId groupMemberInfoListId2);

    void operateSysNotify(boolean z, GroupNotifyOperateMsg groupNotifyOperateMsg, IOperateCallback iOperateCallback);

    void operateSysNotifyV2(boolean z, GroupNotifyOperateMsg groupNotifyOperateMsg, IGroupNotifyOperateCallback iGroupNotifyOperateCallback);

    void postTeamUp(PostTeamUpReq postTeamUpReq, IPostTeamUpCallback iPostTeamUpCallback);

    void publishGroupBulletin(long j, String str, GroupBulletinPublishReq groupBulletinPublishReq, IOperateCallback iOperateCallback);

    void publishInstructionForNewcomers(long j, String str, GroupBulletinPublishReq groupBulletinPublishReq, IOperateCallback iOperateCallback);

    void queryAIOBindGuild(QueryAIOBindGuildReq queryAIOBindGuildReq, IQueryAIOBindGuildCallback iQueryAIOBindGuildCallback);

    void queryCachedEssenceMsg(EssenceKey essenceKey, IQueryCachedEssenceCallback iQueryCachedEssenceCallback);

    void queryGroupChatManageRobotSetting(QueryGroupChatManageRobotSettingReq queryGroupChatManageRobotSettingReq, boolean z, IQueryGroupChatManageRobotSettingCallback iQueryGroupChatManageRobotSettingCallback);

    void queryGroupMuteMemberList(long j, IQueryGroupMuteMemberListCallback iQueryGroupMuteMemberListCallback);

    void queryGroupTopBanners(QueryGroupTopBannersReq queryGroupTopBannersReq, IQueryGroupTopBannersCallback iQueryGroupTopBannersCallback);

    void queryJoinGroupCanNoVerify(JoinGroupVerifyReq joinGroupVerifyReq, IJoinGroupVerifyCallback iJoinGroupVerifyCallback);

    void quitGroup(long j, IOperateCallback iOperateCallback);

    void quitGroupV2(QuitGroupReq quitGroupReq, IOperateCallback iOperateCallback);

    void removeGroupEssence(DigestReq digestReq, IDigestCallback iDigestCallback);

    void removeGroupFromGroupList(long j);

    void removeKernelGroupListener(long j);

    void reqToJoinGroup(ReqToGroup reqToGroup, IOperateCallback iOperateCallback);

    void saveAIVoice(SaveVoiceReq saveVoiceReq, ISaveVoiceCallback iSaveVoiceCallback);

    void searchMember(String str, String str2, IOperateCallback iOperateCallback);

    void searchRelatedGroup(SearchRelatedGroupReq searchRelatedGroupReq, ISearchRelatedGroupCallback iSearchRelatedGroupCallback);

    void setAIOBindGuild(SetAIOBindGuildReq setAIOBindGuildReq, ISetAIOBindGuildCallback iSetAIOBindGuildCallback);

    void setActiveExtGroup(ActiveExtGroupReq activeExtGroupReq, IOperateCallback iOperateCallback);

    void setGroupAppList(SetGroupAppListReq setGroupAppListReq, IOperateCallback iOperateCallback);

    void setGroupChatManageRobotHandler(SetGroupChatManageRobotHandlerReq setGroupChatManageRobotHandlerReq, IOperateCallback iOperateCallback);

    void setGroupChatManageRobotSwitch(SetGroupChatManageRobotSwitchReq setGroupChatManageRobotSwitchReq, IOperateCallback iOperateCallback);

    void setGroupChatManageRobotWord(SetGroupChatManageRobotWordReq setGroupChatManageRobotWordReq, IOperateCallback iOperateCallback);

    void setGroupGeoInfo(GroupGeoInfoReq groupGeoInfoReq, IOperateCallback iOperateCallback);

    void setGroupIdentitySpec(SetGroupIdentitySpecReq setGroupIdentitySpecReq, IOperateCallback iOperateCallback);

    void setGroupMemberFlagGroceryInfo(SetGroupMemberFlagGroceryInfoReq setGroupMemberFlagGroceryInfoReq, ISetGroupMemberFlagGroceryInfoCallback iSetGroupMemberFlagGroceryInfoCallback);

    void setGroupMsgMask(long j, GroupMsgMask groupMsgMask, IOperateCallback iOperateCallback);

    void setGroupMsgMaskV2(SetGroupMsgMaskReq setGroupMsgMaskReq, int i, ISetGroupMsgMaskCallback iSetGroupMsgMaskCallback);

    void setGroupMute(SetGroupMuteReq setGroupMuteReq, IOperateCallback iOperateCallback);

    void setGroupRelationToGuild(SetGroupRelationToGuildReq setGroupRelationToGuildReq, ISetGroupRelationToGuildCallback iSetGroupRelationToGuildCallback);

    void setGroupShutUp(long j, boolean z, IOperateCallback iOperateCallback);

    void setIdentityInteractionTag(SetIdentityInteractionTagReq setIdentityInteractionTagReq, IOperateCallback iOperateCallback);

    void setIdentityTitleInfo(SetIdentityTitleInfoReq setIdentityTitleInfoReq, IOperateCallback iOperateCallback);

    void setMemberShutUp(long j, ArrayList<GroupMemberShutUpInfo> arrayList, IOperateCallback iOperateCallback);

    void setMultiGroupMuteTask(SetMultiGroupMuteTaskReq setMultiGroupMuteTaskReq, ISetMultiGroupMuteTaskCallback iSetMultiGroupMuteTaskCallback);

    void setRcvJoinVerifyMsg(RcvJoinVerifyMsgReq rcvJoinVerifyMsgReq, IRcvJoinVerifyMsgCallback iRcvJoinVerifyMsgCallback);

    void setRelatedGroup(SetRelatedGroupReq setRelatedGroupReq, ISetRelatedGroupCallback iSetRelatedGroupCallback);

    void setTop(long j, boolean z, IOperateCallback iOperateCallback);

    void shareTopic(ShareTopicReq shareTopicReq, IShareTopicCallback iShareTopicCallback);

    void teamUpCreateGroup(TeamUpCreateGroupReq teamUpCreateGroupReq, ITeamUpCreateGroupCallback iTeamUpCreateGroupCallback);

    void teamUpInviteToGroup(TeamUpInviteToGroupReq teamUpInviteToGroupReq, IOperateCallback iOperateCallback);

    void teamUpRequestToJoin(TeamUpRequestToJoinReq teamUpRequestToJoinReq, ITeamUpRequestToJoinCallback iTeamUpRequestToJoinCallback);

    void teamUpSubmitDeadline(TeamUpSubmitDeadlineReq teamUpSubmitDeadlineReq, IOperateCallback iOperateCallback);

    void topicFeedback(TopicFeedbackReq topicFeedbackReq, IOperateCallback iOperateCallback);

    void topicReport(TopicReportReq topicReportReq, ITopicReportCallback iTopicReportCallback);

    void transferGroup(long j, String str, String str2, IOperateCallback iOperateCallback);

    void transferGroupV2(long j, String str, String str2, ITransferGroupCallback iTransferGroupCallback);

    void unbindAllGuilds(UnbindAllGuildsReq unbindAllGuildsReq, IUnbindAllGuildsCallback iUnbindAllGuildsCallback);

    void updateDragonLadder(GroupDragonLadderUpdateReq groupDragonLadderUpdateReq, IUpdateDragonLadderCallback iUpdateDragonLadderCallback);

    void updateGroupInfoByMqq(GroupInfoCacheReq groupInfoCacheReq);

    void updateMemberInfoByMqq(GroupMemberCacheReq groupMemberCacheReq);

    void updateTeamUp(UpdateTeamUpReq updateTeamUpReq, IOperateCallback iOperateCallback);

    void uploadGroupBulletinPic(long j, String str, String str2, IUploadGroupBulletinPicCallback iUploadGroupBulletinPicCallback);*/
}

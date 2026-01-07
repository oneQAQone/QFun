package com.tencent.qqnt.kernel.nativeinterface;

import java.util.ArrayList;

public interface IQQNTWrapperSession {

    final class CppProxy implements IQQNTWrapperSession {

        @Override
        public void close(String str) {

        }

        @Override
        public void disableIpDirect(boolean z) {

        }

        @Override
        public IKernelAVSDKService getAVSDKService() {
            return null;
        }

        @Override
        public String getAccountPath(PathType pathType) {
            return "";
        }

        @Override
        public IKernelAddBuddyService getAddBuddyService() {
            return null;
        }

        @Override
        public IKernelAlbumService getAlbumService() {
            return null;
        }

        /*@Override
        public IKernelAvatarService getAvatarService() {
            return null;
        }

        @Override
        public IKernelBaseEmojiService getBaseEmojiService() {
            return null;
        }

        @Override
        public IKernelBatchUploadService getBatchUploadService() {
            return null;
        }

        @Override
        public IKernelBdhUploadService getBdhUploadService() {
            return null;
        }

        @Override
        public IKernelBuddyService getBuddyService() {
            return null;
        }*/

        @Override
        public ArrayList<String> getCacheErrLog() {
            return null;
        }

        /*@Override
        public IKernelCollectionService getCollectionService() {
            return null;
        }

        @Override
        public IKernelConfigMgrService getConfigMgrService() {
            return null;
        }

        @Override
        public IKernelDataReportService getDataReportService() {
            return null;
        }

        @Override
        public IKernelEmojiService getEmojiService() {
            return null;
        }

        @Override
        public IKernelFileAssistantService getFileAssistantService() {
            return null;
        }

        @Override
        public IKernelFileBridgeClientService getFileBridgeClientService() {
            return null;
        }

        @Override
        public IKernelFlashTransferService getFlashTransferService() {
            return null;
        }

        @Override
        public IKernelGroupSchoolService getGroupSchoolService() {
            return null;
        }*/

        @Override
        public IKernelGroupService getGroupService() {
            return null;
        }

        /*@Override
        public IKernelGroupTabService getGroupTabService() {
            return null;
        }

        @Override
        public IKernelGuildMsgService getGuildMsgService() {
            return null;
        }

        @Override
        public IKernelHandOffService getHandOffService() {
            return null;
        }

        @Override
        public IKernelLiteBusinessService getLiteBusinessService() {
            return null;
        }

        @Override
        public IKernelMiniAppService getMiniAppService() {
            return null;
        }*/

        @Override
        public IKernelMsgService getMsgService() {
            return null;
        }

        /*@Override
        public IKernelNearbyProService getNearbyProService() {
            return null;
        }

        @Override
        public IKernelOnlineStatusService getOnlineStatusService() {
            return null;
        }

        @Override
        public IKernelPersonalAlbumService getPersonalAlbumService() {
            return null;
        }

        @Override
        public IKernelProfileService getProfileService() {
            return null;
        }

        @Override
        public IKernelQRService getQRService() {
            return null;
        }

        @Override
        public IKernelRecentContactService getRecentContactService() {
            return null;
        }*/

        @Override
        public IKernelRichMediaService getRichMediaService() {
            return null;
        }

        /*@Override
        public IKernelRobotService getRobotService() {
            return null;
        }

        @Override
        public IKernelSearchService getSearchService() {
            return null;
        }*/

        @Override
        public String getSessionId() {
            return "";
        }

       /* @Override
        public IKernelSettingService getSettingService() {
            return null;
        }*/

        @Override
        public ArrayList<String> getShortLinkBlacklist() {
            return null;
        }

        /*@Override
        public IKernelStorageCleanService getStorageCleanService() {
            return null;
        }*/

        @Override
        public IKernelTicketService getTicketService() {
            return null;
        }

        /*@Override
        public IKernelTipOffService getTipOffService() {
            return null;
        }

        @Override
        public IKernelTrafficMonitorService getTrafficMonitorService() {
            return null;
        }*/

        @Override
        public IKernelUixConvertService getUixConvertService() {
            return null;
        }

        /*@Override
        public IKernelUnifySearchService getUnifySearchService() {
            return null;
        }

        @Override
        public IKernelUnitedConfigService getUnitedConfigService() {
            return null;
        }

        @Override
        public IKernelWiFiPhotoHostService getWiFiPhotoHostService() {
            return null;
        }

        @Override
        public void init(InitSessionConfig initSessionConfig, IDependsAdapter iDependsAdapter, IDispatcherAdapter iDispatcherAdapter, IKernelSessionListener iKernelSessionListener) {

        }

        @Override
        public void offLine(UnregisterInfo unregisterInfo, IOperateCallback iOperateCallback) {

        }*/

        @Override
        public boolean offLineSync(boolean z) {
            return false;
        }

        @Override
        public void onDispatchPush(int i, byte[] bArr) {

        }

        @Override
        public void onDispatchPushWithJson(int i, String str) {

        }

        @Override
        public void onDispatchRequestReply(long j, int i, byte[] bArr) {

        }

        /*@Override
        public void onLine(RegisterInfo registerInfo) {

        }*/

        @Override
        public void onMsfPush(String str, byte[] bArr, PushExtraInfo pushExtraInfo) {

        }

        @Override
        public void onNetReply(long j, int i, String str, byte[] bArr) {

        }

        /*@Override
        public void onSendOidbReply(long j, int i, int i2, String str, MsfRspInfo msfRspInfo) {

        }

        @Override
        public void onSendSSOReply(long j, String str, int i, String str2, MsfRspInfo msfRspInfo) {

        }

        @Override
        public void onUIConfigUpdate(UIConfig uIConfig, String str) {

        }

        @Override
        public void setOnMsfStatusChanged(MsfStatusType msfStatusType, MsfChangeReasonType msfChangeReasonType, int i) {

        }*/

        @Override
        public void setOnNetworkChanged(NetStatusType netStatusType) {

        }

        @Override
        public void setOnWeakNetChanged(boolean z) {

        }

        @Override
        public void setQimei36(String str) {

        }

        @Override
        public void switchToBackGround() {

        }

        @Override
        public void switchToFront() {

        }

        @Override
        public void updateTicket(SessionTicket sessionTicket) {

        }
    }

    void close(String str);

    void disableIpDirect(boolean z);

    IKernelAVSDKService getAVSDKService();

    String getAccountPath(PathType pathType);

    IKernelAddBuddyService getAddBuddyService();

    IKernelAlbumService getAlbumService();

//    IKernelAvatarService getAvatarService();
//
//    IKernelBaseEmojiService getBaseEmojiService();
//
//    IKernelBatchUploadService getBatchUploadService();
//
//    IKernelBdhUploadService getBdhUploadService();
//
//    IKernelBuddyService getBuddyService();

    ArrayList<String> getCacheErrLog();

//    IKernelCollectionService getCollectionService();
//
//    IKernelConfigMgrService getConfigMgrService();
//
//    IKernelDataReportService getDataReportService();
//
//    IKernelEmojiService getEmojiService();
//
//    IKernelFileAssistantService getFileAssistantService();
//
//    IKernelFileBridgeClientService getFileBridgeClientService();
//
//    IKernelFlashTransferService getFlashTransferService();
//
//    IKernelGroupSchoolService getGroupSchoolService();

    IKernelGroupService getGroupService();

    /*IKernelGroupTabService getGroupTabService();

    IKernelGuildMsgService getGuildMsgService();

    IKernelHandOffService getHandOffService();

    IKernelLiteBusinessService getLiteBusinessService();

    IKernelMiniAppService getMiniAppService();*/

    IKernelMsgService getMsgService();

    /*IKernelNearbyProService getNearbyProService();

    IKernelOnlineStatusService getOnlineStatusService();

    IKernelPersonalAlbumService getPersonalAlbumService();

    IKernelProfileService getProfileService();

    IKernelQRService getQRService();

    IKernelRecentContactService getRecentContactService();*/

    IKernelRichMediaService getRichMediaService();

    /*IKernelRobotService getRobotService();

    IKernelSearchService getSearchService();*/

    String getSessionId();

//    IKernelSettingService getSettingService();

    ArrayList<String> getShortLinkBlacklist();

//    IKernelStorageCleanService getStorageCleanService();

    IKernelTicketService getTicketService();

    /*IKernelTipOffService getTipOffService();

    IKernelTrafficMonitorService getTrafficMonitorService();*/

    IKernelUixConvertService getUixConvertService();

    /*IKernelUnifySearchService getUnifySearchService();

    IKernelUnitedConfigService getUnitedConfigService();

    IKernelWiFiPhotoHostService getWiFiPhotoHostService();

    void init(InitSessionConfig initSessionConfig, IDependsAdapter iDependsAdapter, IDispatcherAdapter iDispatcherAdapter, IKernelSessionListener iKernelSessionListener);

    void offLine(UnregisterInfo unregisterInfo, IOperateCallback iOperateCallback);*/

    boolean offLineSync(boolean z);

    void onDispatchPush(int i, byte[] bArr);

    void onDispatchPushWithJson(int i, String str);

    void onDispatchRequestReply(long j, int i, byte[] bArr);

//    void onLine(RegisterInfo registerInfo);

    void onMsfPush(String str, byte[] bArr, PushExtraInfo pushExtraInfo);

    void onNetReply(long j, int i, String str, byte[] bArr);

    /*void onSendOidbReply(long j, int i, int i2, String str, MsfRspInfo msfRspInfo);

    void onSendSSOReply(long j, String str, int i, String str2, MsfRspInfo msfRspInfo);

    void onUIConfigUpdate(UIConfig uIConfig, String str);

    void setOnMsfStatusChanged(MsfStatusType msfStatusType, MsfChangeReasonType msfChangeReasonType, int i);*/

    void setOnNetworkChanged(NetStatusType netStatusType);

    void setOnWeakNetChanged(boolean z);

    void setQimei36(String str);

    void switchToBackGround();

    void switchToFront();

    void updateTicket(SessionTicket sessionTicket);
}

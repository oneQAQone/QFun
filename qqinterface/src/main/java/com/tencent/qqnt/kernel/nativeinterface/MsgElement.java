package com.tencent.qqnt.kernel.nativeinterface;

import com.tencent.qqnt.kernelpublic.nativeinterface.CalendarElement;
import com.tencent.qqnt.kernelpublic.nativeinterface.GrayTipElement;
import com.tencent.qqnt.kernelpublic.nativeinterface.TextGiftElement;

import java.io.Serializable;

public final class MsgElement implements IKernelModel, Serializable {
    public AdelieActionBarElement actionBarElement;
    public ArkElement arkElement; // ark (json)
    public AVRecordElement avRecordElement; // 录音
    public CalendarElement calendarElement; // 日历
    public int elementGroupId;
    public long elementId;
    public int elementType;
    public byte[] extBufForUI;
    public FaceBubbleElement faceBubbleElement; // 泡泡
    public FaceElement faceElement; // 表情，Poke
    public FileElement fileElement; // 文件
    public FilterMsgElement filterMsgElement; // 滤镜
    public GiphyElement giphyElement; // Giphy
    public GrayTipElement grayTipElement; // 灰色提醒
    public InlineKeyboardElement inlineKeyboardElement; // 内联键盘
    public LiveGiftElement liveGiftElement; // 直播礼物
    public MarkdownElement markdownElement; // markdown
    public MarketFaceElement marketFaceElement;
    public MultiForwardMsgElement multiForwardMsgElement; // 多转发
    public PicElement picElement; // 表情图片
    public PrologueMsgElement prologueMsgElement;
    public PttElement pttElement; // 语音
    public AdelieRecommendedMsgElement recommendedMsgElement;
    public ReplyElement replyElement; // 回复
    long serialVersionUID = 1;
    public ShareLocationElement shareLocationElement; // 地理位置
    public StructLongMsgElement structLongMsgElement; // 长消息
    public StructMsgElement structMsgElement;
    public TaskTopMsgElement taskTopMsgElement;
    public TextElement textElement; // 文本消息
    public TextGiftElement textGiftElement;
    public TofuRecordElement tofuRecordElement;
    public VideoElement videoElement; // 视频
    public WalletElement walletElement; // 钱包
    public YoloGameResultElement yoloGameResultElement; // 游艺

    public MsgElement() {
    }

    public AdelieActionBarElement getActionBarElement() {
        return this.actionBarElement;
    }

    public ArkElement getArkElement() {
        return this.arkElement;
    }

    public AVRecordElement getAvRecordElement() {
        return this.avRecordElement;
    }

    public CalendarElement getCalendarElement() {
        return this.calendarElement;
    }

    public int getElementGroupId() {
        return this.elementGroupId;
    }

    public long getElementId() {
        return this.elementId;
    }

    public int getElementType() {
        return this.elementType;
    }

    public byte[] getExtBufForUI() {
        return this.extBufForUI;
    }

    public FaceBubbleElement getFaceBubbleElement() {
        return this.faceBubbleElement;
    }

    public FaceElement getFaceElement() {
        return this.faceElement;
    }

    public FileElement getFileElement() {
        return this.fileElement;
    }

    public FilterMsgElement getFilterMsgElement() {
        return this.filterMsgElement;
    }

    public GiphyElement getGiphyElement() {
        return this.giphyElement;
    }

    public GrayTipElement getGrayTipElement() {
        return this.grayTipElement;
    }

    public InlineKeyboardElement getInlineKeyboardElement() {
        return this.inlineKeyboardElement;
    }

    public LiveGiftElement getLiveGiftElement() {
        return this.liveGiftElement;
    }

    public MarkdownElement getMarkdownElement() {
        return this.markdownElement;
    }

    public MarketFaceElement getMarketFaceElement() {
        return this.marketFaceElement;
    }

    public MultiForwardMsgElement getMultiForwardMsgElement() {
        return this.multiForwardMsgElement;
    }

    public PicElement getPicElement() {
        return this.picElement;
    }

    public PrologueMsgElement getPrologueMsgElement() {
        return this.prologueMsgElement;
    }

    public PttElement getPttElement() {
        return this.pttElement;
    }

    public AdelieRecommendedMsgElement getRecommendedMsgElement() {
        return this.recommendedMsgElement;
    }

    public ReplyElement getReplyElement() {
        return this.replyElement;
    }

    public ShareLocationElement getShareLocationElement() {
        return this.shareLocationElement;
    }

    public StructLongMsgElement getStructLongMsgElement() {
        return this.structLongMsgElement;
    }

    public StructMsgElement getStructMsgElement() {
        return this.structMsgElement;
    }

    public TaskTopMsgElement getTaskTopMsgElement() {
        return this.taskTopMsgElement;
    }

    public TextElement getTextElement() {
        return this.textElement;
    }

    public TextGiftElement getTextGiftElement() {
        return this.textGiftElement;
    }

    public TofuRecordElement getTofuRecordElement() {
        return this.tofuRecordElement;
    }

    public VideoElement getVideoElement() {
        return this.videoElement;
    }

    public WalletElement getWalletElement() {
        return this.walletElement;
    }

    public YoloGameResultElement getYoloGameResultElement() {
        return this.yoloGameResultElement;
    }

    public void setActionBarElement(AdelieActionBarElement adelieActionBarElement) {
        this.actionBarElement = adelieActionBarElement;
    }

    public void setArkElement(ArkElement arkElement) {
        this.arkElement = arkElement;
    }

    public void setAvRecordElement(AVRecordElement aVRecordElement) {
        this.avRecordElement = aVRecordElement;
    }

    public void setCalendarElement(CalendarElement calendarElement) {
        this.calendarElement = calendarElement;
    }

    public void setElementGroupId(int i) {
        this.elementGroupId = i;
    }

    public void setElementId(long j) {
        this.elementId = j;
    }

    public void setElementType(int i) {
        this.elementType = i;
    }

    public void setExtBufForUI(byte[] bArr) {
        this.extBufForUI = bArr;
    }

    public void setFaceBubbleElement(FaceBubbleElement faceBubbleElement) {
        this.faceBubbleElement = faceBubbleElement;
    }

    public void setFaceElement(FaceElement faceElement) {
        this.faceElement = faceElement;
    }

    public void setFileElement(FileElement fileElement) {
        this.fileElement = fileElement;
    }

    public void setFilterMsgElement(FilterMsgElement filterMsgElement) {
        this.filterMsgElement = filterMsgElement;
    }

    public void setGiphyElement(GiphyElement giphyElement) {
        this.giphyElement = giphyElement;
    }

    public void setGrayTipElement(GrayTipElement grayTipElement) {
        this.grayTipElement = grayTipElement;
    }

    public void setInlineKeyboardElement(InlineKeyboardElement inlineKeyboardElement) {
        this.inlineKeyboardElement = inlineKeyboardElement;
    }

    public void setLiveGiftElement(LiveGiftElement liveGiftElement) {
        this.liveGiftElement = liveGiftElement;
    }

    public void setMarkdownElement(MarkdownElement markdownElement) {
        this.markdownElement = markdownElement;
    }

    public void setMarketFaceElement(MarketFaceElement marketFaceElement) {
        this.marketFaceElement = marketFaceElement;
    }

    public void setMultiForwardMsgElement(MultiForwardMsgElement multiForwardMsgElement) {
        this.multiForwardMsgElement = multiForwardMsgElement;
    }

    public void setPicElement(PicElement picElement) {
        this.picElement = picElement;
    }

    public void setPrologueMsgElement(PrologueMsgElement prologueMsgElement) {
        this.prologueMsgElement = prologueMsgElement;
    }

    public void setPttElement(PttElement pttElement) {
        this.pttElement = pttElement;
    }

    public void setRecommendedMsgElement(AdelieRecommendedMsgElement adelieRecommendedMsgElement) {
        this.recommendedMsgElement = adelieRecommendedMsgElement;
    }

    public void setReplyElement(ReplyElement replyElement) {
        this.replyElement = replyElement;
    }

    public void setShareLocationElement(ShareLocationElement shareLocationElement) {
        this.shareLocationElement = shareLocationElement;
    }

    public void setStructLongMsgElement(StructLongMsgElement structLongMsgElement) {
        this.structLongMsgElement = structLongMsgElement;
    }

    public void setStructMsgElement(StructMsgElement structMsgElement) {
        this.structMsgElement = structMsgElement;
    }

    public void setTaskTopMsgElement(TaskTopMsgElement taskTopMsgElement) {
        this.taskTopMsgElement = taskTopMsgElement;
    }

    public void setTextElement(TextElement textElement) {
        this.textElement = textElement;
    }

    public void setTextGiftElement(TextGiftElement textGiftElement) {
        this.textGiftElement = textGiftElement;
    }

    public void setTofuRecordElement(TofuRecordElement tofuRecordElement) {
        this.tofuRecordElement = tofuRecordElement;
    }

    public void setVideoElement(VideoElement videoElement) {
        this.videoElement = videoElement;
    }

    public void setWalletElement(WalletElement walletElement) {
        this.walletElement = walletElement;
    }

    public void setYoloGameResultElement(YoloGameResultElement yoloGameResultElement) {
        this.yoloGameResultElement = yoloGameResultElement;
    }

    public MsgElement(int i, long j, byte[] bArr, TextElement textElement, FaceElement faceElement, MarketFaceElement marketFaceElement, ReplyElement replyElement, PicElement picElement, PttElement pttElement, VideoElement videoElement, GrayTipElement grayTipElement, ArkElement arkElement, FileElement fileElement, LiveGiftElement liveGiftElement, MarkdownElement markdownElement, StructLongMsgElement structLongMsgElement, MultiForwardMsgElement multiForwardMsgElement, GiphyElement giphyElement, WalletElement walletElement, InlineKeyboardElement inlineKeyboardElement, TextGiftElement textGiftElement, CalendarElement calendarElement, YoloGameResultElement yoloGameResultElement, AVRecordElement aVRecordElement, StructMsgElement structMsgElement, FaceBubbleElement faceBubbleElement, ShareLocationElement shareLocationElement, TofuRecordElement tofuRecordElement, TaskTopMsgElement taskTopMsgElement, AdelieRecommendedMsgElement adelieRecommendedMsgElement, AdelieActionBarElement adelieActionBarElement, PrologueMsgElement prologueMsgElement, FilterMsgElement filterMsgElement) {
        this.elementType = i;
        this.elementId = j;
        this.extBufForUI = bArr;
        this.textElement = textElement;
        this.faceElement = faceElement;
        this.marketFaceElement = marketFaceElement;
        this.replyElement = replyElement;
        this.picElement = picElement;
        this.pttElement = pttElement;
        this.videoElement = videoElement;
        this.grayTipElement = grayTipElement;
        this.arkElement = arkElement;
        this.fileElement = fileElement;
        this.liveGiftElement = liveGiftElement;
        this.markdownElement = markdownElement;
        this.structLongMsgElement = structLongMsgElement;
        this.multiForwardMsgElement = multiForwardMsgElement;
        this.giphyElement = giphyElement;
        this.walletElement = walletElement;
        this.inlineKeyboardElement = inlineKeyboardElement;
        this.textGiftElement = textGiftElement;
        this.calendarElement = calendarElement;
        this.yoloGameResultElement = yoloGameResultElement;
        this.avRecordElement = aVRecordElement;
        this.structMsgElement = structMsgElement;
        this.faceBubbleElement = faceBubbleElement;
        this.shareLocationElement = shareLocationElement;
        this.tofuRecordElement = tofuRecordElement;
        this.taskTopMsgElement = taskTopMsgElement;
        this.recommendedMsgElement = adelieRecommendedMsgElement;
        this.actionBarElement = adelieActionBarElement;
        this.prologueMsgElement = prologueMsgElement;
        this.filterMsgElement = filterMsgElement;
    }
}

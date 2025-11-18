package me.yxp.qfun.hook.msg;

import android.graphics.Color;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.yxp.qfun.hook.api.OnAIOViewUpdate;
import me.yxp.qfun.hook.base.BaseSwitchHookItem;
import me.yxp.qfun.hook.base.HookItemAnnotation;
import me.yxp.qfun.javaplugin.api.MsgData;
import me.yxp.qfun.proto.InfoSyncPushOuterClass;
import me.yxp.qfun.proto.MsgPushOuterClass;
import me.yxp.qfun.proto.QQMessageOuterClass;
import me.yxp.qfun.utils.data.DataUtils;
import me.yxp.qfun.utils.hook.HookUtils;
import me.yxp.qfun.utils.hook.xpcompat.XC_MethodHook;
import me.yxp.qfun.utils.qq.FriendTool;
import me.yxp.qfun.utils.qq.NtGrayTipJsonBuilder;
import me.yxp.qfun.utils.qq.QQCurrentEnv;
import me.yxp.qfun.utils.qq.TroopTool;
import me.yxp.qfun.utils.reflect.ClassUtils;
import me.yxp.qfun.utils.reflect.FieldUtils;
import me.yxp.qfun.utils.reflect.MethodUtils;
import me.yxp.qfun.utils.thread.SyncUtils;

@HookItemAnnotation(TAG = "防撤回", desc = "协议防撤回(需保活)")
public final class RevokeMsgHook extends BaseSwitchHookItem {
    private static Method sOnMsfPushMethod;
    private Map<String, List<String>> mRetractMessageMap;
    private OnAIOViewUpdate.AIOViewUpdateListener mAddViewListener;

    @Override
    protected boolean initMethod() throws Throwable {
        Class<?> pushExtraInfo = ClassUtils.load("com.tencent.qqnt.kernel.nativeinterface.PushExtraInfo");
        sOnMsfPushMethod = MethodUtils.create(ClassUtils._QQNTWrapperSession())
                .withMethodName("onMsfPush")
                .withParamTypes(String.class, byte[].class, pushExtraInfo)
                .findOne();
        return true;
    }

    @Override
    protected void initCallback() {
        HookUtils.hookIfEnable(this, sOnMsfPushMethod, param -> {
            String cmd = param.args[0].toString();
            byte[] protoBuf = (byte[]) param.args[1];

            if ("trpc.msg.register_proxy.RegisterProxy.InfoSyncPush".equals(cmd)) {
                handleInfoSyncPush(protoBuf, param);
            } else if ("trpc.msg.olpush.OlPushService.MsgPush".equals(cmd)) {
                handleMsgPush(protoBuf, param);
            }
        }, null);

        mAddViewListener = (frameLayout, msgRecord) -> {
            String msgSeq = FieldUtils.create(msgRecord).withName("msgSeq").getValue().toString();
            MsgData msgData = new MsgData(msgRecord);

            if (mRetractMessageMap.get(msgData.peerUid) == null) {
                return;
            }

            List<String> seqList = mRetractMessageMap.get(msgData.peerUid);
            addRevokeView(frameLayout);

            if (seqList.contains(msgSeq)) {
                setRevokeViewText(frameLayout, "已撤回");
            } else {
                setRevokeViewText(frameLayout, "");
            }
        };
    }

    @Override
    public void startHook() {
        super.startHook();
        readData();
        OnAIOViewUpdate.INSTANCE.addListener(mAddViewListener);
    }

    @Override
    public void stopHook() {
        super.stopHook();
        OnAIOViewUpdate.INSTANCE.removeListener(mAddViewListener);
    }

    private void addRevokeView(FrameLayout frameLayout) {
        for (int i = 0; i < frameLayout.getChildCount(); i++) {
            Object tag = frameLayout.getChildAt(i).getTag();
            if (tag != null && "撤回视图".equals(tag.toString())) {
                return;
            }
        }

        TextView revokeView = new TextView(frameLayout.getContext());
        revokeView.setTextColor(Color.BLUE);
        revokeView.setTag("撤回视图");
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER | Gravity.TOP;
        frameLayout.addView(revokeView, params);
    }

    private void setRevokeViewText(FrameLayout frameLayout, String text) {
        for (int i = 0; i < frameLayout.getChildCount(); i++) {
            Object tag = frameLayout.getChildAt(i).getTag();
            if (tag != null && "撤回视图".equals(tag.toString())) {
                ((TextView) frameLayout.getChildAt(i)).setText(text);
            }
        }
    }

    private void readData() {
        Object obj = DataUtils.deserialize("data", "retractMessageMap");
        if (obj == null) {
            mRetractMessageMap = new HashMap<>();
        } else {
            mRetractMessageMap = (Map<String, List<String>>) obj;
        }
    }

    private void writeAndRefresh(String peerUid, int msgSeq) {
        List<String> seqList = mRetractMessageMap.get(peerUid);
        if (seqList == null) {
            seqList = new ArrayList<>();
        }

        seqList.add(String.valueOf(msgSeq));
        mRetractMessageMap.put(peerUid, seqList);
        DataUtils.serialize("data", "retractMessageMap", mRetractMessageMap);
    }

    private void handleInfoSyncPush(byte[] protoBuf, XC_MethodHook.MethodHookParam param) throws Throwable {
        InfoSyncPushOuterClass.InfoSyncPush infoSyncPush = InfoSyncPushOuterClass.InfoSyncPush.parseFrom(protoBuf);
        InfoSyncPushOuterClass.InfoSyncPush.SyncRecallOperateInfo syncRecallContent = infoSyncPush.getSyncRecallContent();
        List<InfoSyncPushOuterClass.InfoSyncPush.SyncRecallOperateInfo.SyncInfoBody> syncInfoBodyList = syncRecallContent.getSyncInfoBodyList();

        for (InfoSyncPushOuterClass.InfoSyncPush.SyncRecallOperateInfo.SyncInfoBody syncInfoBody : syncInfoBodyList) {
            List<QQMessageOuterClass.QQMessage> msgList = syncInfoBody.getMsgList();
            for (QQMessageOuterClass.QQMessage qqMessage : msgList) {
                int msgType = qqMessage.getMessageContentInfo().getMsgType();
                int msgSubType = qqMessage.getMessageContentInfo().getMsgSubType();

                if ((msgType == 732 && msgSubType == 17) || (msgType == 528 && msgSubType == 138)) {
                    InfoSyncPushOuterClass.InfoSyncPush.SyncRecallOperateInfo.Builder newSyncRecallContentBuilder = syncRecallContent.toBuilder();

                    for (int i = 0; i < syncInfoBodyList.size(); i++) {
                        InfoSyncPushOuterClass.InfoSyncPush.SyncRecallOperateInfo.SyncInfoBody.Builder syncInfoBodyBuilder = newSyncRecallContentBuilder.getSyncInfoBodyBuilder(i);
                        syncInfoBodyBuilder.clearMsg();
                        newSyncRecallContentBuilder.setSyncInfoBody(i, syncInfoBodyBuilder.build());
                    }

                    InfoSyncPushOuterClass.InfoSyncPush newInfoSyncPush = infoSyncPush.toBuilder()
                            .setSyncRecallContent(newSyncRecallContentBuilder.build()).build();

                    param.args[1] = newInfoSyncPush.toByteArray();
                    return;
                }
            }
        }
    }

    private void handleMsgPush(byte[] protoBuf, XC_MethodHook.MethodHookParam param) throws Throwable {
        MsgPushOuterClass.MsgPush msgPush = MsgPushOuterClass.MsgPush.parseFrom(protoBuf);
        QQMessageOuterClass.QQMessage qqMessage = msgPush.getQqMessage();
        int msgType = qqMessage.getMessageContentInfo().getMsgType();
        int msgSubType = qqMessage.getMessageContentInfo().getMsgSubType();
        byte[] operationInfoByteArray = qqMessage.getMessageBody().getOperationInfo().toByteArray();

        if (msgType == 732 && msgSubType == 17) {
            onGroupRecallByMsgPush(operationInfoByteArray, param);
        } else if (msgType == 528 && msgSubType == 138) {
            onC2CRecallByMsgPush(operationInfoByteArray, param);
        }
    }

    private void onGroupRecallByMsgPush(byte[] operationInfoByteArray,
                                        XC_MethodHook.MethodHookParam param) throws Throwable {
        byte[] secondPart = Arrays.copyOfRange(operationInfoByteArray, 7, operationInfoByteArray.length);
        QQMessageOuterClass.QQMessage.MessageBody.GroupRecallOperationInfo operationInfo = QQMessageOuterClass.QQMessage.MessageBody.GroupRecallOperationInfo.parseFrom(secondPart);
        long msgTime = operationInfo.getInfo().getMsgInfo().getMsgTime();
        if (System.currentTimeMillis() / 1000 - msgTime > 3600) return;
        int recallMsgSeq = operationInfo.getInfo().getMsgInfo().getMsgSeq();
        String groupPeerId = String.valueOf(operationInfo.getPeerId());
        List<String> msgseqList = mRetractMessageMap.get(groupPeerId);
        if (msgseqList != null && msgseqList.contains(String.valueOf(recallMsgSeq))) return;
        String operatorUid = operationInfo.getInfo().getOperatorUid();
        String senderUid = operationInfo.getInfo().getMsgInfo().getSenderUid();
        String selfUin = QQCurrentEnv.getCurrentUin();
        String selfUid = FriendTool.getUidFromUin(selfUin);

        if (operatorUid.equals(selfUid)) {
            return;
        }

        writeAndRefresh(groupPeerId, recallMsgSeq);
        param.args[1] = new byte[0];

        SyncUtils.runOnNewThread("RevokeMsgHook", () -> {
            NtGrayTipJsonBuilder builder = new NtGrayTipJsonBuilder();

            builder.append(new NtGrayTipJsonBuilder.UserItem(FriendTool.getUinFromUid(operatorUid), operatorUid,
                    TroopTool.getMemberName(groupPeerId, FriendTool.getUinFromUid(operatorUid))));

            builder.appendText("尝试撤回");
            if (!operatorUid.equals(senderUid)) {
                builder.append(new NtGrayTipJsonBuilder.UserItem(FriendTool.getUinFromUid(senderUid), senderUid,
                        TroopTool.getMemberName(groupPeerId, FriendTool.getUinFromUid(senderUid))));

                builder.appendText("的");
            }

            builder.append(new NtGrayTipJsonBuilder.MsgRefItem("一条消息", recallMsgSeq));

            Object contact = ClassUtils.makeDefaultObject(ClassUtils._Contact(), 2, groupPeerId, "");
            NtGrayTipJsonBuilder.addLocalGrayTipMsg(contact, builder.build().toString(),
                    NtGrayTipJsonBuilder.AIO_AV_GROUP_NOTICE);
        });
    }

    private void onC2CRecallByMsgPush(byte[] operationInfoByteArray,
                                      XC_MethodHook.MethodHookParam param) throws Throwable {
        QQMessageOuterClass.QQMessage.MessageBody.C2CRecallOperationInfo operationInfo = QQMessageOuterClass.QQMessage.MessageBody.C2CRecallOperationInfo.parseFrom(operationInfoByteArray);
        long msgTime = operationInfo.getInfo().getMsgTime();
        if (System.currentTimeMillis() / 1000 - msgTime > 3600) return;
        int recallMsgSeq = operationInfo.getInfo().getMsgSeq();
        String operatorUid = operationInfo.getInfo().getOperatorUid();
        List<String> msgseqList = mRetractMessageMap.get(operatorUid);
        if (msgseqList != null && msgseqList.contains(String.valueOf(recallMsgSeq))) return;
        writeAndRefresh(operatorUid, recallMsgSeq);

        param.args[1] = new byte[0];

        String selfUin = QQCurrentEnv.getCurrentUin();
        String selfUid = FriendTool.getUidFromUin(selfUin);
        NtGrayTipJsonBuilder builder = new NtGrayTipJsonBuilder();

        if (selfUid.equals(operatorUid)) {
            return;
        } else {
            builder.appendText("对方");
        }

        builder.appendText("尝试撤回");
        builder.append(new NtGrayTipJsonBuilder.MsgRefItem("一条消息", recallMsgSeq));
        Object contact = ClassUtils.makeDefaultObject(ClassUtils._Contact(), 1, operatorUid, "");
        NtGrayTipJsonBuilder.addLocalGrayTipMsg(contact, builder.build().toString(),
                NtGrayTipJsonBuilder.AIO_AV_C2C_NOTICE);
    }
}
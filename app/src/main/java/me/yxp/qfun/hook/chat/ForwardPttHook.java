package me.yxp.qfun.hook.chat;

import android.app.Activity;
import android.content.Intent;
import android.os.Parcelable;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import me.yxp.qfun.hook.base.BaseSwitchHookItem;
import me.yxp.qfun.hook.base.HookItemAnnotation;
import me.yxp.qfun.utils.dexkit.DexKit;
import me.yxp.qfun.utils.error.ErrorOutput;
import me.yxp.qfun.utils.hook.HookUtils;
import me.yxp.qfun.utils.hook.xpcompat.XposedBridge;
import me.yxp.qfun.utils.qq.HostInfo;
import me.yxp.qfun.utils.qq.MsgTool;
import me.yxp.qfun.utils.qq.QQCurrentEnv;
import me.yxp.qfun.utils.reflect.ClassUtils;
import me.yxp.qfun.utils.reflect.FieldUtils;
import me.yxp.qfun.utils.reflect.MethodUtils;
import me.yxp.qfun.utils.thread.SimpleIntervalExecutor;

@HookItemAnnotation(TAG = "转发语音", desc = "语音长按菜单出现转发按钮（支持私聊，群聊，临时会话）")
public final class ForwardPttHook extends BaseSwitchHookItem {
    private static final int MSG_TYPE_PTT = 6;
    private static final int MSG_TYPE_TEXT = 2;
    private static final int INVALID_TYPE = 114514;

    private static Class<?> sForwardMenuItem;
    private static Method sSetMenuMethod;
    private static Method sSendIntentMethod;
    private static Method sOnActivityResultMethod;
    private static Method sHandleForwardMethod;
    private final SimpleIntervalExecutor executor = new SimpleIntervalExecutor(500);
    private Object mLastAIOMsgItem;

    @Override
    protected boolean initMethod() throws Throwable {
        Class<?> aioPttContentComponent = ClassUtils.load("com.tencent.mobileqq.aio.msglist.holder.component.ptt.AIOPttContentComponent");
        Class<?> ntMsgForwardUtils = ClassUtils._NtMsgForwardUtils();
        sForwardMenuItem = DexKit.getClass(getNAME());
        sSetMenuMethod = MethodUtils.create(aioPttContentComponent)
                .withReturnType(List.class)
                .withParamTypes()
                .findOne();

        if (HostInfo.isTIM() || (HostInfo.isQQ() && HostInfo.getVersionCode() < 11820)) {
            sOnActivityResultMethod = MethodUtils.create(ntMsgForwardUtils)
                    .withReturnType(void.class)
                    .withParamTypes(Activity.class, int.class, int.class, Intent.class, null)
                    .findOne();
            sSendIntentMethod = MethodUtils.create(ntMsgForwardUtils)
                    .withReturnType(boolean.class)
                    .withParamTypes(Activity.class, null, ClassUtils._AIOMsgItem())
                    .findOne();
        } else {
            Class<?> aioForwardHandler = ClassUtils.load("com.tencent.mobileqq.sharepanel.forward.handler.AIOForwardHandler");
            sHandleForwardMethod = MethodUtils.create(aioForwardHandler)
                    .withMethodName("forward")
                    .withParamTypes(Map.class, null, List.class, String.class, null)
                    .findOne();
            sSendIntentMethod = DexKit.getMethod(getNAME());
        }

        return true;
    }

    @Override
    protected void initCallback() {
        HookUtils.hookIfEnable(this, sSetMenuMethod, null, param -> {
            List<Object> menu = (List<Object>) param.getResult();
            Object aioPttContentComponent = param.thisObject;
            Object aIOMsgItem = FieldUtils.create(aioPttContentComponent)
                    .ofType(ClassUtils._AIOMsgItem())
                    .inParent(aioPttContentComponent.getClass().getSuperclass())
                    .getValue();
            menu.add(0, ClassUtils.makeDefaultObject(sForwardMenuItem,
                    QQCurrentEnv.getActivity(), aIOMsgItem, aioPttContentComponent, null));
        });

        HookUtils.hookIfEnable(this, sSendIntentMethod, param -> {
            mLastAIOMsgItem = null;
            Object msgRecord = FieldUtils.create(param.args[2])
                    .ofType(ClassUtils._MsgRecord())
                    .inParent(ClassUtils._AIOMsgItem())
                    .getValue();
            if ((int) FieldUtils.create(msgRecord).withName("msgType").getValue() != MSG_TYPE_PTT) {
                return;
            }
            FieldUtils.create(msgRecord).withName("msgType").setValue(MSG_TYPE_TEXT);
            mLastAIOMsgItem = param.args[2];
        }, null);

        if (HostInfo.isTIM() || (HostInfo.isQQ() && HostInfo.getVersionCode() < 11820)) {
            HookUtils.replaceIfEnable(this, sOnActivityResultMethod, param -> {
                Intent intent = (Intent) param.args[3];

                if (intent == null) {
                    return XposedBridge.invokeOriginalMethod(param.method, param.thisObject, param.args);
                }

                long msgId = intent.getLongExtra("forward_nt_msg_id", 0L);
                if (msgId == 0L || mLastAIOMsgItem == null) {
                    return XposedBridge.invokeOriginalMethod(param.method, param.thisObject, param.args);
                }

                Object msgRecord = FieldUtils.create(mLastAIOMsgItem)
                        .ofType(ClassUtils._MsgRecord())
                        .inParent(ClassUtils._AIOMsgItem())
                        .getValue();
                ArrayList<Object> elements = (ArrayList<Object>) FieldUtils.create(msgRecord)
                        .withName("elements")
                        .getValue();
                String uin1 = intent.getStringExtra("uin");
                int uinType1 = intent.getIntExtra("uintype", INVALID_TYPE);
                boolean flag = sendMsg(uin1, uinType1, elements);

                List<Parcelable> list = intent.getParcelableArrayListExtra("forward_multi_target");
                if (list != null) {
                    for (Parcelable parcelable : list) {
                        String uin2 = (String) FieldUtils.create(parcelable).withName("uin").getValue();
                        int uinType2 = (int) FieldUtils.create(parcelable).withName("uinType").getValue();
                        boolean flag2 = sendMsg(uin2, uinType2, elements);
                        flag = flag || flag2;
                    }
                }

                if (!flag) {
                    return XposedBridge.invokeOriginalMethod(param.method, param.thisObject, param.args);
                }
                executor.startExecute();
                return null;
            });
        } else {
            HookUtils.replaceIfEnable(this, sHandleForwardMethod, param -> {
                Map<String, Object> map = (Map<String, Object>) param.args[0];
                List<Object> list = (List<Object>) param.args[2];

                if (map.size() == 1 && mLastAIOMsgItem != null) {
                    Object msgRecord = FieldUtils.create(mLastAIOMsgItem)
                            .ofType(ClassUtils._MsgRecord())
                            .inParent(ClassUtils._AIOMsgItem())
                            .getValue();
                    ArrayList<Object> elements = (ArrayList<Object>) FieldUtils.create(msgRecord)
                            .withName("elements")
                            .getValue();

                    for (Object contact : list) {
                        String peerUin = (String) FieldUtils.create(contact).withName("peerUin").getValue();
                        int peerType = (int) FieldUtils.create(contact).withName("peerType").getValue();
                        FieldUtils.create(contact).withName("peerType").setValue(INVALID_TYPE);

                        executor.addTask(() -> {
                            try {
                                MsgTool.sendMsg(MsgTool.makeContact(peerUin, peerType), elements);
                            } catch (Throwable th) {
                                ErrorOutput.itemHookError(this, th);
                            }
                        });

                    }

                    executor.startExecute();

                }
                return XposedBridge.invokeOriginalMethod(param.method, param.thisObject, param.args);
            });
        }
    }

    private boolean sendMsg(String uin, int uinType, ArrayList<Object> elements) {
        int chatType = switch (uinType) {
            case 0 -> 1;
            case 1 -> 2;
            case 1000 -> 100;
            default -> INVALID_TYPE;
        };

        if (uin == null || chatType == INVALID_TYPE) {
            return false;
        }

        executor.addTask(() -> {
            try {
                MsgTool.sendMsg(MsgTool.makeContact(uin, chatType), elements);
            } catch (Throwable th) {
                ErrorOutput.itemHookError(this, th);
            }
        });

        return true;
    }
}
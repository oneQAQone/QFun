package me.yxp.qfun.hook;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.yxp.qfun.hook.api.OnAIOViewUpdate;
import me.yxp.qfun.hook.api.OnGetMsgRecord;
import me.yxp.qfun.hook.api.OnMsg;
import me.yxp.qfun.hook.api.OnMsgMenuOpen;
import me.yxp.qfun.hook.api.OnPaiYiPai;
import me.yxp.qfun.hook.api.OnRKey;
import me.yxp.qfun.hook.api.OnSendMsg;
import me.yxp.qfun.hook.api.OnTroopJoin;
import me.yxp.qfun.hook.api.OnTroopQuit;
import me.yxp.qfun.hook.api.OnTroopShutUp;
import me.yxp.qfun.hook.base.ApiHookItem;
import me.yxp.qfun.hook.base.BaseSwitchHookItem;
import me.yxp.qfun.hook.base.BaseWithDataHookItem;
import me.yxp.qfun.hook.browser.RemoveRiskWebpageBlockHook;
import me.yxp.qfun.hook.chat.DefaultBubbleAndFontHook;
import me.yxp.qfun.hook.chat.ForwardPttHook;
import me.yxp.qfun.hook.chat.MsgTimeHook;
import me.yxp.qfun.hook.chat.MultiRecallHook;
import me.yxp.qfun.hook.chat.RemoveReplyAtHook;
import me.yxp.qfun.hook.chat.ShowAtTargetHook;
import me.yxp.qfun.hook.device.CustomDeviceHook;
import me.yxp.qfun.hook.device.TabletModeHook;
import me.yxp.qfun.hook.entry.QQHomeInject;
import me.yxp.qfun.hook.entry.QQSettingInject;
import me.yxp.qfun.hook.file.AutoRemarkApkHook;
import me.yxp.qfun.hook.file.AutoSendOriginalPicHook;
import me.yxp.qfun.hook.file.DownloadTimesHook;
import me.yxp.qfun.hook.msg.EmotionToPicHook;
import me.yxp.qfun.hook.msg.FlashPicHook;
import me.yxp.qfun.hook.msg.MsgContentHook;
import me.yxp.qfun.hook.msg.PicSummaryHook;
import me.yxp.qfun.hook.msg.RandomFaceHook;
import me.yxp.qfun.hook.msg.RecordsReplyMsgHook;
import me.yxp.qfun.hook.msg.RepeatMsgHook;
import me.yxp.qfun.hook.msg.RevokeMsgHook;
import me.yxp.qfun.hook.purify.AntiInteractivePopHook;
import me.yxp.qfun.hook.purify.AntiLinkPreviewHook;
import me.yxp.qfun.hook.purify.RemoveEmoReplyHook;
import me.yxp.qfun.hook.purify.RemoveFilterVideoHook;
import me.yxp.qfun.hook.qrcode.LiftQRCodeLimitHook;
import me.yxp.qfun.hook.qrcode.SkipWaitTimeHook;
import me.yxp.qfun.hook.social.AutoKeepSparkHook;
import me.yxp.qfun.hook.social.OneClickLikeHook;
import me.yxp.qfun.hook.social.PaiYiPaiHook;
import me.yxp.qfun.hook.troop.AntiAtAllHook;
import me.yxp.qfun.hook.troop.SimpleTroopManagement;
import me.yxp.qfun.hook.troop.TroopClockInHook;
import me.yxp.qfun.javaplugin.MainPlugin;
import me.yxp.qfun.utils.data.DataUtils;
import me.yxp.qfun.utils.error.ErrorOutput;
import me.yxp.qfun.utils.hook.HookUtils;
import me.yxp.qfun.utils.qq.QQCurrentEnv;
import me.yxp.qfun.utils.reflect.ClassUtils;
import me.yxp.qfun.utils.reflect.MethodUtils;

public class MainHook {
    public static final String DATA_KEY_ENABLE = "Enable";
    private static final String ACTION_INIT = "init";
    private static final String ACTION_SAVE = "save";

    public static List<BaseSwitchHookItem> HookItems = new ArrayList<>();
    public static Map<String, Boolean> Enable = new HashMap<>();

    public static void hook() throws Throwable {
        initEntry();
        loadApiHook();
        initHookItem();
        MainPlugin.initView();

        Method onCreate = MethodUtils.create(ClassUtils._QQAppInterface())
                .withMethodName("onCreateQQMessageFacade")
                .findOne();

        HookUtils.hookAlways(onCreate, param -> {
            Object qQAppInterface = param.thisObject;
            QQCurrentEnv.setCurrentEnv(qQAppInterface);
            onAccountChange();
        }, null);
    }

    public static void savaData() {
        updateHook();
        DataUtils.serialize("data", DATA_KEY_ENABLE, Enable);
        hookItemData(ACTION_SAVE);
    }

    public static void initData() {
        initEnable();
        hookItemData(ACTION_INIT);
        updateHook();
    }

    private static void initEntry() {
        try {
            QQSettingInject.hook();
        } catch (Throwable th) {
            ErrorOutput.Error("QQSettingInject", th);
        }

        try {
            QQHomeInject.hook();
        } catch (Throwable th) {
            ErrorOutput.Error("QQHomeInject", th);
        }
    }

    public static void initHookItem() {
        Class<?>[] hookClasses = {
            TroopClockInHook.class, AutoKeepSparkHook.class, RevokeMsgHook.class, PaiYiPaiHook.class,
                RepeatMsgHook.class, MsgTimeHook.class, FlashPicHook.class,
                PicSummaryHook.class, RemoveRiskWebpageBlockHook.class, RandomFaceHook.class,
                AntiAtAllHook.class, MsgContentHook.class,
                OneClickLikeHook.class, LiftQRCodeLimitHook.class, SkipWaitTimeHook.class,
                AutoRemarkApkHook.class, RemoveReplyAtHook.class, TabletModeHook.class,
                SimpleTroopManagement.class, DefaultBubbleAndFontHook.class, EmotionToPicHook.class,
                AutoSendOriginalPicHook.class, RecordsReplyMsgHook.class,
                ForwardPttHook.class, DownloadTimesHook.class, CustomDeviceHook.class,
                AntiInteractivePopHook.class, RemoveFilterVideoHook.class, AntiLinkPreviewHook.class,
                MultiRecallHook.class, RemoveEmoReplyHook.class, ShowAtTargetHook.class
        };

        for (Class<?> hookClass : hookClasses) {
            BaseSwitchHookItem hookItem = ClassUtils.getHookItem(hookClass);
            if (hookItem == null) {
                continue;
            }

            try {
                hookItem.init();
            } catch (Exception e) {
                ErrorOutput.itemHookError(hookItem, e);
            }
            HookItems.add(hookItem);
        }
    }

    private static void loadApiHook() {
        Class<?>[] apiClasses = {
                OnSendMsg.class, OnAIOViewUpdate.class, OnRKey.class,
                OnMsg.class, OnTroopJoin.class, OnTroopQuit.class,
                OnTroopShutUp.class, OnMsgMenuOpen.class,
                OnPaiYiPai.class, OnGetMsgRecord.class
        };

        for (Class<?> apiClass : apiClasses) {
            try {
                ApiHookItem apiHookItem = (ApiHookItem) apiClass.getDeclaredField("INSTANCE").get(null);
                apiHookItem.loadHook();
            } catch (Throwable th) {
                ErrorOutput.Error(apiClass.getSimpleName(), th);
            }
        }
    }

    private static void initEnable() {
        Object data = DataUtils.deserialize("data", DATA_KEY_ENABLE);
        if (data == null) {
            for (BaseSwitchHookItem hookItem : HookItems) {
                Enable.put(hookItem.getNAME(), false);
            }
        } else {
            Enable = (HashMap<String, Boolean>) data;
            for (BaseSwitchHookItem hookItem : HookItems) {
                if (!Enable.containsKey(hookItem.getNAME())) {
                    Enable.put(hookItem.getNAME(), false);
                }
            }
        }
    }

    private static void onAccountChange() {
        initData();
        MainPlugin.initPlugins();
    }

    private static void updateHook() {
        for (BaseSwitchHookItem hookItem : HookItems) {
            if (Enable.get(hookItem.getNAME()) && hookItem.isAvailable) {
                hookItem.startHook();
            } else {
                hookItem.stopHook();
            }
        }
    }

    private static void hookItemData(String action) {
        for (BaseSwitchHookItem hookItem : HookItems) {
            if (!(hookItem instanceof BaseWithDataHookItem) || !hookItem.isAvailable) {
                continue;
            }

            BaseWithDataHookItem dataHookItem = (BaseWithDataHookItem) hookItem;
            switch (action) {
                case ACTION_INIT:
                    dataHookItem.initData();
                    break;
                case ACTION_SAVE:
                    dataHookItem.savaData();
                    break;
                default:
                    break;
            }
        }
    }
}

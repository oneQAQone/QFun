package me.yxp.qfun.hook.msg;

import android.app.AlertDialog;

import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Random;

import me.yxp.qfun.hook.base.BaseSwitchHookItem;
import me.yxp.qfun.hook.base.HookItemAnnotation;
import me.yxp.qfun.utils.error.ErrorOutput;
import me.yxp.qfun.utils.hook.HookUtils;
import me.yxp.qfun.utils.hook.xpcompat.XposedBridge;
import me.yxp.qfun.utils.json.ProtoData;
import me.yxp.qfun.utils.qq.QQCurrentEnv;
import me.yxp.qfun.utils.qq.ToastUtils;
import me.yxp.qfun.utils.reflect.ClassUtils;
import me.yxp.qfun.utils.reflect.FieldUtils;
import me.yxp.qfun.utils.reflect.MethodUtils;
import me.yxp.qfun.utils.thread.SyncUtils;

@HookItemAnnotation(TAG = "自定义随机表情", desc = "使用猜拳/骰子可自定义结果")
public final class RandomFaceHook extends BaseSwitchHookItem {
    private static final String BASE_JSON_TEMPLATE = """
              {
            "1": {
            	"type1": {
            		"type2": peerUid
            	}
            },
            "3": {
            	"1": {
            		"2": {
            			"53": {
            				"1": 37,
            				"2": {
            					"1": "1",
            					"2": "v1",
            					"3": v2,
            					"4": 1,
            					"5": 2,
            					"6": "结果",
            					"7": "faceText",
            					"9": 1
            				},
            				"3": 20
            			}
            		}
            	}
            },
            "4": random1,
            "5": random2
              }
            """;

    private static Method sAddAttributeMethod;
    private static Method sSendToServiceMethod;
    private static Method sSendMsgMethod;

    private static byte[] makeBytes(String type, int value, String peer, int chatType) {
        String id = chatType == 1 ? "\"" + peer + "\"" : peer;
        String v1 = type.equals("/骰子") ? "33" : "34";
        String v2 = type.equals("/骰子") ? "358" : "359";
        String result = String.valueOf(value);
        String random1 = String.valueOf(Math.abs(new Random().nextInt()));
        String random2 = String.valueOf(Math.abs(new Random().nextInt()));

        String jsonStr = BASE_JSON_TEMPLATE
                .replace("type1", String.valueOf(chatType))
                .replace("type2", String.valueOf(3 - chatType))
                .replace("peerUid", id)
                .replace("v1", v1)
                .replace("v2", v2)
                .replace("结果", result)
                .replace("faceText", type)
                .replace("random1", random1)
                .replace("random2", random2);

        try {
            JSONObject json = new JSONObject(jsonStr);
            ProtoData data = new ProtoData();
            data.fromJSON(json);
            return data.toBytes();
        } catch (Exception e) {
            return new byte[0];
        }
    }

    @Override
    protected boolean initMethod() throws Throwable {
        Class<?> appInterface = ClassUtils.load("com.tencent.common.app.AppInterface");
        Class<?> msgService = ClassUtils.load("com.tencent.qqnt.kernel.api.impl.MsgService");

        sAddAttributeMethod = MethodUtils.create(ClassUtils._ToServiceMsg())
                .withMethodName("addAttribute")
                .findOne();

        sSendToServiceMethod = MethodUtils.create(appInterface)
                .withMethodName("sendToService")
                .findOne();

        sSendMsgMethod = MethodUtils.create(msgService)
                .withMethodName("sendMsg")
                .findOne();

        return true;
    }

    @Override
    protected void initCallback() {
        HookUtils.replaceIfEnable(this, sSendMsgMethod, param -> {
            ArrayList<?> msgElement = (ArrayList<?>) param.args[2];
            if (Integer.parseInt(FieldUtils.create(param.args[1]).withName("chatType").getValue().toString()) == 100) {
                return XposedBridge.invokeOriginalMethod(param.method, param.thisObject, param.args);
            }

            Object faceElement = FieldUtils.create(msgElement.get(0)).withName("faceElement").getValue();
            if (faceElement == null) {
                return XposedBridge.invokeOriginalMethod(param.method, param.thisObject, param.args);
            }

            Object obj = FieldUtils.create(faceElement).withName("faceText").getValue();
            if (obj == null) {
                return XposedBridge.invokeOriginalMethod(param.method, param.thisObject, param.args);
            }

            String faceText = obj.toString();
            final String[] values;
            final String title;

            switch (faceText) {
                case "/骰子":
                    values = new String[]{"1点", "2点", "3点", "4点", "5点", "6点"};
                    title = "自定义骰子";
                    break;
                case "/包剪锤":
                    values = new String[]{"布", "剪刀", "石头"};
                    title = "自定义猜拳";
                    break;
                default:
                    return XposedBridge.invokeOriginalMethod(param.method, param.thisObject, param.args);
            }

            SyncUtils.postDelayed(() ->
                    new AlertDialog.Builder(QQCurrentEnv.getActivity(), AlertDialog.THEME_DEVICE_DEFAULT_LIGHT)
                            .setTitle(title)
                            .setSingleChoiceItems(values, -1, (dialog, which) -> {
                                ToastUtils.QQToast(2, values[which]);
                                String peerUid = FieldUtils.create(param.args[1]).withName("peerUid").getValue().toString();
                                int chatType = (int) FieldUtils.create(param.args[1]).withName("chatType").getValue();
                                try {
                                    sendBuffer(makeBytes(faceText, which + 1, peerUid, chatType));
                                } catch (Exception e) {
                                    ErrorOutput.itemHookError(RandomFaceHook.this, e);
                                }
                                dialog.dismiss();
                            })
                            .create()
                            .show(), 100);
            return null;
        });
    }

    private void sendBuffer(byte[] bytes) {
        try {
            Object toServiceMsg = ClassUtils.makeDefaultObject(ClassUtils._ToServiceMsg(),
                    "mobileqq.service", QQCurrentEnv.getCurrentUin(), "MessageSvc.PbSendMsg");
            FieldUtils.create(toServiceMsg).withName("wupBuffer").setValue(bytes);
            sAddAttributeMethod.invoke(toServiceMsg, "req_pb_protocol_flag", true);
            sSendToServiceMethod.invoke(QQCurrentEnv.getQQAppInterface(), toServiceMsg);
        } catch (Exception e) {
            ErrorOutput.itemHookError(this, e);
        }
    }
}
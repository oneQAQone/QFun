package me.yxp.qfun.hook.troop;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.lang.reflect.Method;

import me.yxp.qfun.R;
import me.yxp.qfun.hook.base.BaseSwitchHookItem;
import me.yxp.qfun.hook.base.HookItemAnnotation;
import me.yxp.qfun.javaplugin.api.MsgData;
import me.yxp.qfun.utils.dexkit.DexKit;
import me.yxp.qfun.utils.hook.HookUtils;
import me.yxp.qfun.utils.hook.xpcompat.XC_MethodHook;
import me.yxp.qfun.utils.hook.xpcompat.XposedBridge;
import me.yxp.qfun.utils.qq.MsgTool;
import me.yxp.qfun.utils.qq.QQCurrentEnv;
import me.yxp.qfun.utils.qq.ToastUtils;
import me.yxp.qfun.utils.qq.TroopTool;
import me.yxp.qfun.utils.reflect.ClassUtils;
import me.yxp.qfun.utils.reflect.FieldUtils;
import me.yxp.qfun.utils.reflect.MethodUtils;

@HookItemAnnotation(TAG = "简洁群管", desc = "点击群聊头像开启菜单，省去进入主页管理群员")
public final class SimpleTroopManagement extends BaseSwitchHookItem {
    private static Method sOnClickAvatar;

    @Override
    protected boolean initMethod() throws Throwable {
        Class<?> listener = DexKit.getClass(getNAME());
        sOnClickAvatar = MethodUtils.create(listener).withMethodName("onClick").findOne();
        return true;
    }

    @Override
    public void initCallback() {
        HookUtils.replaceIfEnable(this, sOnClickAvatar, param -> {
            Object aioAvatarContentComponent = FieldUtils.create(param.thisObject)
                    .ofType(ClassUtils._AIOAvatarContentComponent()).getValue();
            Object aioMsgItem = FieldUtils.create(aioAvatarContentComponent).ofType(ClassUtils._AIOMsgItem())
                    .getValue();
            Object msgRecord = FieldUtils.create(aioMsgItem).inParent(ClassUtils._AIOMsgItem())
                    .ofType(ClassUtils._MsgRecord()).getValue();
            View view = (View) param.args[0];
            final MsgData msgData = new MsgData(msgRecord);

            Object memberInfo = TroopTool.getMemberInfo(msgData.peerUin, QQCurrentEnv.getCurrentUin());

            if (msgData.type != 2 || memberInfo == null) {
                return XposedBridge.invokeOriginalMethod(param.method, param.thisObject, param.args);
            }

            String role = FieldUtils.create(memberInfo).withName("role").getValue().toString();
            if (role.equals("MEMBER")) {
                return XposedBridge.invokeOriginalMethod(param.method, param.thisObject, param.args);
            }

            Context context = view.getContext();
            LinearLayout managementLayout = (LinearLayout) LayoutInflater.from(context)
                    .inflate(R.layout.troopmanagement, null);
            View anchorView = ((Activity) context).getWindow().getDecorView();
            PopupWindow popupWindow = new PopupWindow(managementLayout,
                    ViewGroup.LayoutParams.MATCH_PARENT, anchorView.getHeight() / 2, true);

            setOnClickListener(managementLayout, popupWindow, param, msgData);
            popupWindow.showAtLocation(anchorView, Gravity.BOTTOM, 0, 0);

            return null;
        });
    }

    private int getInputValue(EditText editText) {
        try {
            String input = editText.getText().toString().trim();
            if (input.isEmpty()) {
                return 0;
            }
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private void setOnClickListener(LinearLayout parent, PopupWindow popupWindow,
                                    XC_MethodHook.MethodHookParam param, MsgData msgData) {
        final Context context = parent.getContext();
        String troopNick = TroopTool.getMemberName(msgData.peerUin, msgData.userUin);

        ((TextView) parent.findViewById(R.id.troopmanagementUinText)).setText(msgData.userUin);
        ((TextView) parent.findViewById(R.id.troopmanagementNickText)).setText(troopNick);

        parent.findViewById(R.id.troopmanagementButton1).setOnClickListener(v -> {
            try {
                XposedBridge.invokeOriginalMethod(param.method, param.thisObject, param.args);
            } catch (Throwable th) {
                // 忽略异常
            } finally {
                popupWindow.dismiss();
            }
        });

        parent.findViewById(R.id.troopmanagementButton2).setOnClickListener(v -> {
            try {
                MsgTool.recallMsg(msgData.contact, msgData.msgId);
            } catch (Throwable th) {
                // 忽略异常
            } finally {
                popupWindow.dismiss();
            }
        });

        parent.findViewById(R.id.troopmanagementButton3).setOnClickListener(v -> {
            try {
                TroopTool.setGroupAdmin(msgData.peerUin, msgData.userUin, true);
            } catch (Throwable th) {
                // 忽略异常
            } finally {
                popupWindow.dismiss();
            }
        });

        parent.findViewById(R.id.troopmanagementButton4).setOnClickListener(v -> {
            try {
                TroopTool.setGroupAdmin(msgData.peerUin, msgData.userUin, false);
            } catch (Throwable th) {
                // 忽略异常
            } finally {
                popupWindow.dismiss();
            }
        });

        parent.findViewById(R.id.troopmanagementButton5).setOnClickListener(v -> {
            LinearLayout shutUpLayout = (LinearLayout) LayoutInflater.from(context)
                    .inflate(R.layout.shutuplayout, null);

            final EditText etDays = shutUpLayout.findViewById(R.id.etDays);
            final EditText etHours = shutUpLayout.findViewById(R.id.etHours);
            final EditText etMinutes = shutUpLayout.findViewById(R.id.etMinutes);
            final EditText etSeconds = shutUpLayout.findViewById(R.id.etSeconds);

            new AlertDialog.Builder(context, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT)
                    .setView(shutUpLayout)
                    .setTitle("设置禁言时间")
                    .setPositiveButton("确定", (dialog, which) -> {
                        int days = getInputValue(etDays);
                        int hours = getInputValue(etHours);
                        int minutes = getInputValue(etMinutes);
                        int seconds = getInputValue(etSeconds);

                        if (days < 0 || hours < 0 || minutes < 0 || seconds < 0) {
                            ToastUtils.QQToast(1, "请输入非负数");
                            return;
                        }

                        long totalSeconds = seconds + minutes * 60L + hours * 3600L + days * 86400L;
                        try {
                            TroopTool.shutUp(msgData.peerUin, msgData.userUin, totalSeconds);
                        } catch (Exception e) {
                            // 忽略异常
                        }
                    }).show();
            popupWindow.dismiss();
        });

        parent.findViewById(R.id.troopmanagementButton6).setOnClickListener(v -> {
            try {
                TroopTool.shutUp(msgData.peerUin, msgData.userUin, 0);
            } catch (Throwable th) {
                // 忽略异常
            } finally {
                popupWindow.dismiss();
            }
        });

        parent.findViewById(R.id.troopmanagementButton7).setOnClickListener(v -> {
            final EditText editText = new EditText(context);
            new AlertDialog.Builder(context, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT)
                    .setTitle("设置头衔")
                    .setView(editText)
                    .setPositiveButton("确定", (dialog, which) -> {
                        try {
                            TroopTool.setGroupMemberTitle(msgData.peerUin, msgData.userUin,
                                    editText.getText().toString());
                        } catch (Exception e) {
                            // 忽略异常
                        }
                    }).show();
            popupWindow.dismiss();
        });

        parent.findViewById(R.id.troopmanagementButton8).setOnClickListener(v -> {
            final EditText editText = new EditText(context);
            editText.setText(troopNick);
            new AlertDialog.Builder(context, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT)
                    .setTitle("设置群昵称")
                    .setView(editText)
                    .setNegativeButton("取消", null)
                    .setPositiveButton("确定", (dialog, which) -> {
                        try {
                            TroopTool.changeMemberName(msgData.peerUin, msgData.userUin,
                                    editText.getText().toString());
                        } catch (Exception e) {
                            // 忽略异常
                        }
                    }).show();
            popupWindow.dismiss();
        });

        parent.findViewById(R.id.troopmanagementButton9).setOnClickListener(v -> {
            try {
                TroopTool.kickGroup(msgData.peerUid, msgData.userUin, false);
            } catch (Throwable th) {
                // 忽略异常
            } finally {
                popupWindow.dismiss();
            }
        });

        parent.findViewById(R.id.troopmanagementButton10).setOnClickListener(v -> {
            try {
                TroopTool.kickGroup(msgData.peerUin, msgData.userUin, true);
            } catch (Throwable th) {
                // 忽略异常
            } finally {
                popupWindow.dismiss();
            }
        });

        parent.findViewById(R.id.troopmanagementButton11).setOnClickListener(v -> {
            try {
                TroopTool.shutUpAll(msgData.peerUin, true);
            } catch (Throwable th) {
                // 忽略异常
            } finally {
                popupWindow.dismiss();
            }
        });

        parent.findViewById(R.id.troopmanagementButton12).setOnClickListener(v -> {
            try {
                TroopTool.shutUpAll(msgData.peerUin, false);
            } catch (Throwable th) {
                // 忽略异常
            } finally {
                popupWindow.dismiss();
            }
        });
    }
}
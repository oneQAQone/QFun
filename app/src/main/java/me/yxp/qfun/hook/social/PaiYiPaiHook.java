package me.yxp.qfun.hook.social;

import android.app.Activity;
import android.app.AlertDialog;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.widget.EditText;

import java.lang.reflect.Method;

import me.yxp.qfun.hook.base.BaseSwitchHookItem;
import me.yxp.qfun.hook.base.HookItemAnnotation;
import me.yxp.qfun.utils.hook.HookUtils;
import me.yxp.qfun.utils.hook.xpcompat.XC_MethodHook;
import me.yxp.qfun.utils.hook.xpcompat.XposedBridge;
import me.yxp.qfun.utils.qq.QQCurrentEnv;
import me.yxp.qfun.utils.reflect.ClassUtils;
import me.yxp.qfun.utils.reflect.MethodUtils;

@HookItemAnnotation(TAG = "拍一拍连拍", desc = "双击头像后可输入次数(单日上限200)")
public final class PaiYiPaiHook extends BaseSwitchHookItem {
    private static Method sSendPaiYiPaiMethod;
    private static Method sLiftTimeMethod;

    @Override
    protected boolean initMethod() throws Throwable {
        sSendPaiYiPaiMethod = MethodUtils.create(ClassUtils._PaiYiPaiHandler())
                .withReturnType(void.class)
                .withParamTypes(String.class, String.class, int.class, int.class)
                .findOne();

        sLiftTimeMethod = MethodUtils.create(ClassUtils._PaiYiPaiHandler())
                .withReturnType(boolean.class)
                .withParamTypes(String.class)
                .findOne();

        return true;
    }

    @Override
    protected void initCallback() {
        HookUtils.hookIfEnable(this, sLiftTimeMethod, null,
                param -> param.setResult(true));

        HookUtils.hookIfEnable(this, sSendPaiYiPaiMethod, param -> {
            param.setResult(null);
            Activity activity = QQCurrentEnv.getActivity();
            showInputDialog(activity, param);
        }, null);
    }

    private void showInputDialog(Activity activity, XC_MethodHook.MethodHookParam param) {
        EditText editText = new EditText(activity);

        editText.setText("1");
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        editText.addTextChangedListener(createTextWatcher(editText));

        new AlertDialog.Builder(activity, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT)
                .setTitle("请输入次数")
                .setView(editText)
                .setPositiveButton("确定", (dialogInterface, which) -> {
                    String input = editText.getText().toString();
                    int num = input.isEmpty() ? 1 : Integer.parseInt(input);
                    sendMultiplePaiYiPai(param, num);
                })
                .create()
                .show();
    }

    private TextWatcher createTextWatcher(EditText editText) {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = s.toString();
                if (!text.isEmpty()) {
                    try {
                        int value = Integer.parseInt(text);
                        if (value < 1 || value > 200) {
                            editText.removeTextChangedListener(this);
                            editText.setText(String.valueOf(1));
                            editText.setSelection(editText.getText().length());
                            editText.addTextChangedListener(this);
                        }
                    } catch (NumberFormatException e) {
                        editText.removeTextChangedListener(this);
                        editText.setText("");
                        editText.addTextChangedListener(this);
                    }
                }
            }
        };
    }

    private void sendMultiplePaiYiPai(XC_MethodHook.MethodHookParam param, int count) {
        try {
            for (int i = 0; i < count; i++) {
                XposedBridge.invokeOriginalMethod(param.method, param.thisObject, param.args);
            }
        } catch (Exception e) {
            // 忽略异常
        }
    }
}
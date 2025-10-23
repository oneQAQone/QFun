package me.yxp.qfun.hook.file;

import android.view.View;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.yxp.qfun.hook.base.BaseSwitchHookItem;
import me.yxp.qfun.hook.base.HookItemAnnotation;
import me.yxp.qfun.utils.hook.HookUtils;
import me.yxp.qfun.utils.reflect.ClassUtils;
import me.yxp.qfun.utils.reflect.MethodUtils;

@HookItemAnnotation(TAG = "显示文件下载次数", desc = "群文件显示具体下载次数（显示无效可尝试进入群文件页面触发热插件下载后重启应用）")
public final class DownloadTimesHook extends BaseSwitchHookItem {
    private static Method sGetView;
    private static Method sGetItem;

    @Override
    protected boolean initMethod() throws Throwable {
        if (!isInTargetProcess()) {
            return false;
        }

        Class<?> troopFileShowAdapter;
        try {
            troopFileShowAdapter = ClassUtils.load("com.tencent.mobileqq.troop.file.data.TroopFileShowAdapter");
        } catch (Exception e) {
            troopFileShowAdapter = ClassUtils.loadFromPlugin("troop_plugin.apk",
                    "com.tencent.mobileqq.troop.data.TroopFileShowAdapter");
        }

        sGetView = MethodUtils.create(troopFileShowAdapter)
                .withMethodName("getView")
                .findOne();

        sGetItem = MethodUtils.create(troopFileShowAdapter)
                .withMethodName("getItem")
                .findOne();

        return true;
    }

    @Override
    protected void initCallback() {
        HookUtils.hookIfEnable(this, sGetView, null, param -> {
            String fileItemString = sGetItem.invoke(param.thisObject, param.args[0]).toString();
            String fileName = getFileName(fileItemString);
            String downloadTimes = getDownloadTimes(fileItemString);

            if (fileName == null || downloadTimes == null) {
                return;
            }

            Object tag = ((View) param.getResult()).getTag();
            for (Field field : tag.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                Object value = field.get(tag);
                if (value instanceof TextView) {
                    TextView textView = (TextView) value;
                    if (fileName.contentEquals(textView.getText())) {
                        textView.setText(fileName + "(下载次数：" + downloadTimes + ")");
                    }
                }
            }
        });
    }

    private String getDownloadTimes(String troopFileInfo) {
        Pattern pattern = Pattern.compile("uint32_download_times=(\\d+)");
        Matcher matcher = pattern.matcher(troopFileInfo);
        return matcher.find() ? matcher.group(1) : null;
    }

    private String getFileName(String troopFileInfo) {
        Pattern pattern = Pattern.compile("str_file_name='([^']*)'");
        Matcher matcher = pattern.matcher(troopFileInfo);
        return matcher.find() ? matcher.group(1) : null;
    }
}
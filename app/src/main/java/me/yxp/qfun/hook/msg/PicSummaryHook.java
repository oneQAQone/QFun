package me.yxp.qfun.hook.msg;

import android.app.AlertDialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import me.yxp.qfun.hook.api.OnSendMsg;
import me.yxp.qfun.hook.base.BaseWithDataHookItem;
import me.yxp.qfun.hook.base.HookItemAnnotation;
import me.yxp.qfun.utils.data.DataUtils;
import me.yxp.qfun.utils.json.JsonUtil;
import me.yxp.qfun.utils.reflect.FieldUtils;
import me.yxp.qfun.utils.thread.SyncUtils;

@HookItemAnnotation(TAG = "修改图片外显", desc = "返回值为Json格式时支持读取嵌套的key，为纯文本长度需小于30")
public final class PicSummaryHook extends BaseWithDataHookItem {
    private Map<String, String> dataMap = new HashMap<>();
    private String mPicSummary;
    private OnSendMsg.SendMsgListener mChangeSummaryListener;

    @Override
    protected void initCallback() {

        mChangeSummaryListener = msgElements -> {

            if (mPicSummary.isEmpty()) {
                return;
            }
            boolean hasPic = false;

            for (Object msgElement : msgElements) {
                Object picElement = FieldUtils.create(msgElement).withName("picElement").getValue();
                Object marketFaceElement = FieldUtils.create(msgElement).withName("marketFaceElement").getValue();

                if (picElement != null) {
                    FieldUtils.create(picElement).withName("summary").setValue(mPicSummary);
                    hasPic = true;
                }
                if (marketFaceElement != null) {
                    FieldUtils.create(marketFaceElement).withName("faceName").setValue(mPicSummary);
                    hasPic = true;
                }
                if (hasPic) setNextPicSummary();
            }
        };
    }

    @Override
    public void startHook() {
        OnSendMsg.INSTANCE.addListener(mChangeSummaryListener);
    }

    @Override
    public void stopHook() {
        OnSendMsg.INSTANCE.removeListener(mChangeSummaryListener);
    }

    @Override
    public void initData() {
        Object obj = DataUtils.deserialize("data", "SummaryDataMap");
        if (obj != null) {
            dataMap = (Map<String, String>) obj;
        } else {
            dataMap.put("key", "");
            dataMap.put("summaryOrUrl", "");
        }
        setNextPicSummary();
    }

    @Override
    public void savaData() {
        DataUtils.serialize("data", "SummaryDataMap", dataMap);
    }

    @Override
    public void onClick(View v) {
        Context context = v.getContext();
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        EditText summaryText = new EditText(context);
        EditText keyText = new EditText(context);

        linearLayout.setGravity(Gravity.CENTER);
        summaryText.setHint("普通外显或api链接");
        summaryText.setText(dataMap.get("summaryOrUrl"));
        summaryText.setPadding(40, 40, 40, 40);
        keyText.setHint("需要显示的key");
        keyText.setText(dataMap.get("key"));
        keyText.setPadding(40, 40, 40, 40);

        linearLayout.addView(keyText);
        linearLayout.addView(summaryText);

        new AlertDialog.Builder(context, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT)
                .setTitle("设置图片外显文本")
                .setView(linearLayout)
                .setPositiveButton("保存", (dialog, which) -> {
                    dataMap.put("key", keyText.getText().toString());
                    dataMap.put("summaryOrUrl", summaryText.getText().toString());
                    setNextPicSummary();
                })
                .create()
                .show();
    }

    private void setNextPicSummary() {
        SyncUtils.runOnNewThread(getNAME(), () -> {
            String summaryOrUrl = dataMap.get("summaryOrUrl");
            String picSummaryFromUrl = get(summaryOrUrl);
            try {
                JSONObject jsonObject = new JSONObject(picSummaryFromUrl);
                Object value = JsonUtil.findValueByKey(jsonObject, dataMap.get("key"));
                if (value instanceof String) {
                    mPicSummary = (String) value;
                } else {
                    mPicSummary = "";
                }
            } catch (Exception e) {
                if (picSummaryFromUrl.length() > 30) {
                    mPicSummary = summaryOrUrl;
                } else {
                    mPicSummary = picSummaryFromUrl;
                }
            }
        });
    }

    private String get(String urlString) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(15000);

            if (conn.getResponseCode() == 200) {
                InputStream inputStream = conn.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder response = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

                reader.close();
                conn.disconnect();
                return response.toString();
            }
        } catch (Exception ignored) {
        }
        return urlString;
    }
}
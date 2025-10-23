package me.yxp.qfun.javaplugin.loader;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.Switch;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import me.yxp.qfun.R;

public class PluginInfo implements OnClickListener {
    private final String mDesc;
    private final String mPluginVersion;
    private final String mAuthorName;
    public String pluginPath;
    public String pluginName;
    public String pluginId;
    public boolean isRunning = false;
    public PluginCompiler pluginCompiler;

    public PluginInfo(String pluginName, String pluginVersion, String authorName,
                      String pluginPath, String pluginId) {
        mAuthorName = authorName;
        this.pluginName = pluginName;
        mPluginVersion = pluginVersion;
        this.pluginPath = pluginPath + "/";
        this.pluginId = pluginId;
        this.pluginCompiler = new PluginCompiler(this);
        mDesc = getDesc(this.pluginPath);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof PluginInfo)) {
            return false;
        }
        PluginInfo pluginInfo = (PluginInfo) obj;
        return pluginInfo.pluginId.equals(pluginId);
    }

    private String getDesc(String pluginPath) {
        File file = new File(pluginPath + "desc.txt");

        try (FileInputStream fis = new FileInputStream(file);
             DataInputStream dis = new DataInputStream(fis);
             BufferedReader br = new BufferedReader(new InputStreamReader(dis))) {

            String line;
            StringBuilder sb = new StringBuilder();
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
            return sb.toString();
        } catch (Exception e) {
            return "";
        }
    }

    @Override
    public void onClick(View v) {
        Activity activity = (Activity) v.getContext();
        View dialog = LayoutInflater.from(activity).inflate(R.layout.plugininfodialog, null);

        TextView authorTextView = dialog.findViewById(R.id.authorTextView);
        TextView versionTextView = dialog.findViewById(R.id.versionTextView);
        TextView descTextView = dialog.findViewById(R.id.descTextView);
        Switch autoSwitch = dialog.findViewById(R.id.autoSwitch);
        Button deleteButton = dialog.findViewById(R.id.deleteButton);
        Button uploadButton = dialog.findViewById(R.id.uploadButton);

        authorTextView.setText("作者：" + mAuthorName);
        versionTextView.setText("版本：" + mPluginVersion);
        descTextView.setText(mDesc);

        if (PluginManager.autoLoadList.contains(pluginId)) {
            autoSwitch.setChecked(true);
        }

        autoSwitch.setOnCheckedChangeListener((view, isChecked) -> {
            if (isChecked) {
                PluginManager.autoLoadList.add(pluginId);
            } else {
                PluginManager.autoLoadList.remove(pluginId);
            }
        });

        View root = activity.findViewById(android.R.id.content);
        PopupWindow popupWindow = new PopupWindow(dialog,
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.setWidth(root.getWidth() * 4 / 5);
        popupWindow.setHeight(root.getHeight() * 4 / 7);
        popupWindow.showAtLocation(root, Gravity.CENTER, 0, 0);

        deleteButton.setOnClickListener(view -> {
            popupWindow.dismiss();
            new AlertDialog.Builder(activity, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT)
                    .setNegativeButton("取消", null)
                    .setTitle("提示")
                    .setMessage("确认删除该脚本？")
                    .setPositiveButton("确定", (d, w) ->
                            PluginManager.deletePlugin(PluginInfo.this, activity))
                    .create()
                    .show();
        });
    }
}
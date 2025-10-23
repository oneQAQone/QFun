package me.yxp.qfun.activity;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;

import me.yxp.qfun.R;
import me.yxp.qfun.javaplugin.loader.PluginInfo;
import me.yxp.qfun.javaplugin.loader.PluginManager;
import me.yxp.qfun.utils.data.DataUtils;
import me.yxp.qfun.utils.error.PluginError;
import me.yxp.qfun.utils.qq.HostInfo;
import me.yxp.qfun.utils.qq.QQCurrentEnv;
import me.yxp.qfun.utils.qq.QQUtils;
import me.yxp.qfun.utils.thread.SyncUtils;

public class JavaPlugin extends Activity {
    private static final String PLUGIN_PATH = HostInfo.getMODULE_DATA_PATH() + "[当前QQ号]/plugin/";
    private static final int REQUEST_CODE_PICK_DIR = 1;

    private ItemAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.javapluginsettings);

        setTranslucentStatus(this);
        initView();
    }

    @Override
    protected void onDestroy() {
        DataUtils.serialize("data", "AutoLoadList", PluginManager.autoLoadList);
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_PICK_DIR && resultCode == RESULT_OK && data != null) {
            Uri sourceDirUri = data.getData();
            if (sourceDirUri != null) {
                PluginManager.importPlugin(sourceDirUri, this);
            }
        }
    }

    private void initView() {
        PluginManager.getAllPlugin();

        ListView pluginListView = findViewById(R.id.pluginListView);
        mAdapter = new ItemAdapter(R.layout.pluginitem, PluginManager.pluginInfos);
        pluginListView.setAdapter(mAdapter);

        TextView pluginPathTextView = findViewById(R.id.pluginPathTextView);
        String currentPath = PLUGIN_PATH.replace("[当前QQ号]", QQCurrentEnv.getCurrentUin());
        pluginPathTextView.setText("脚本存放目录（点击可复制）\n" + currentPath);
        pluginPathTextView.setOnClickListener(this::copyToClipBoard);
    }

    private void setTranslucentStatus(Activity activity) {
        if (activity == null) {
            return;
        }

        Window window = activity.getWindow();
        if (window == null) {
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    public void gotoDocumentation(View v) {
        String url = "https://docs.qq.com/doc/DWmNYaVRpTWRSVGpV";
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }

    public void copyToClipBoard(View v) {
        ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        String currentPath = PLUGIN_PATH.replace("[当前QQ号]", QQCurrentEnv.getCurrentUin());
        ClipData clipData = ClipData.newPlainText("Copied Text", currentPath);
        clipboardManager.setPrimaryClip(clipData);
        QQUtils.QQToast(2, "复制成功");
    }

    public void importPlugin(View v) {
        openDirectoryPicker();
    }

    private void openDirectoryPicker() {
        try {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
            startActivityForResult(intent, REQUEST_CODE_PICK_DIR);
        } catch (Exception e) {
            QQUtils.QQToast(1, "无法打开文件管理器");
        }
    }

    public void notifyDataSetChanged() {
        mAdapter.notifyDataSetChanged();
    }

    private class ItemAdapter extends ArrayAdapter<PluginInfo> {
        private final int mLayoutId;

        public ItemAdapter(int layoutId, List<PluginInfo> list) {
            super(JavaPlugin.this, layoutId, list);
            mLayoutId = layoutId;
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            PluginInfo pluginInfo = getItem(position);
            if (pluginInfo == null) {
                return new View(getContext());
            }

            View view = LayoutInflater.from(getContext()).inflate(mLayoutId, parent, false);
            LinearLayout pluginitemLinearLayout = view.findViewById(R.id.pluginItemLinearLayout);
            TextView pluginitemTextView = view.findViewById(R.id.pluginItemTextView);
            Switch pluginitemSwitch = view.findViewById(R.id.pluginItemSwitch);

            pluginitemLinearLayout.setOnClickListener(pluginInfo);
            pluginitemTextView.setText(pluginInfo.pluginName);
            pluginitemSwitch.setChecked(pluginInfo.isRunning);

            pluginitemSwitch.setOnClickListener(v -> {
                if (pluginitemSwitch.isChecked()) {
                    new Thread(() -> {
                        try {
                            pluginInfo.pluginCompiler.startPlugin();
                        } catch (Exception e) {
                            PluginError.evalError(e, pluginInfo);
                            QQUtils.QQToast(1, "加载失败");
                            SyncUtils.postDelayed(() -> pluginitemSwitch.setChecked(false), 200);
                        }
                    }).start();
                } else {
                    pluginInfo.pluginCompiler.stopPlugin();
                }
            });

            return view;
        }
    }
}

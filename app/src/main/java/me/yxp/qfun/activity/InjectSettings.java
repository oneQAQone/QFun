package me.yxp.qfun.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
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
import androidx.documentfile.provider.DocumentFile;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import me.yxp.qfun.R;
import me.yxp.qfun.hook.MainHook;
import me.yxp.qfun.hook.base.BaseSwitchHookItem;
import me.yxp.qfun.hook.base.BaseWithDataHookItem;
import me.yxp.qfun.hook.msg.RepeatMsgHook;
import me.yxp.qfun.utils.data.FileUtils;
import me.yxp.qfun.utils.qq.HostInfo;
import me.yxp.qfun.utils.qq.QQUtils;

public class InjectSettings extends Activity {
    public static final int PICK_IMAGE_REQUEST = 1000;
    private static final int IMPORT_REQUEST_CODE = 1001;

    private File mConfigFolder;
    private File mExportDir;
    private ItemAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.injectsettings);

        setTranslucentStatus(this);
        initData();
        initView();
    }

    @Override
    protected void onDestroy() {
        MainHook.savaData();
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK) {
            handleImagePick(data);
        }

        if (resultCode == RESULT_OK && requestCode == IMPORT_REQUEST_CODE && data != null) {
            handleConfigImport(data);
        }
    }

    private void initData() {
        mConfigFolder = new File(HostInfo.getMODULE_DATA_PATH());
        mExportDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
    }

    private void initView() {
        ListView hookListView = findViewById(R.id.hookListView);
        mAdapter = new ItemAdapter(R.layout.hookitem, MainHook.HookItems);
        hookListView.setAdapter(mAdapter);
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
        } else {
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    private void handleImagePick(Intent data) {
        try {
            RepeatMsgHook.sBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
            QQUtils.QQToast(2, "加一图标导入成功！");
        } catch (Exception ignored) {
            QQUtils.QQToast(1, "加一图标导入失败！");
        }
    }

    private void handleConfigImport(Intent data) {
        Uri uri = data.getData();
        if (uri == null) {
            return;
        }

        try {
            File tempZip = File.createTempFile("config_import", ".zip", getCacheDir());
            DocumentFile documentFile = DocumentFile.fromSingleUri(this, uri);

            if (!FileUtils.copySingleFile(this, documentFile, tempZip)) {
                QQUtils.QQToast(2, "读取导入文件失败");
                return;
            }

            FileUtils.deleteDirectory(mConfigFolder.getAbsolutePath());
            mConfigFolder.mkdirs();

            if (FileUtils.unzip(tempZip, mConfigFolder.getParentFile())) {
                QQUtils.QQToast(2, "配置导入成功");
            } else {
                QQUtils.QQToast(1, "解压配置文件失败");
            }

            tempZip.delete();
            MainHook.initData();
            mAdapter.notifyDataSetChanged();

        } catch (Exception e) {
            QQUtils.QQToast(1, "导入处理失败: " + e.getMessage());
        }
    }

    public void startImportConfig(View v) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/zip");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, IMPORT_REQUEST_CODE);
    }

    public void startExportConfig(View v) {
        MainHook.savaData();

        File tempZip;
        try {
            tempZip = File.createTempFile("config_export", ".zip", getCacheDir());
        } catch (IOException e) {
            QQUtils.QQToast(1, "创建临时文件失败");
            return;
        }

        if (!FileUtils.zipFolder(mConfigFolder, tempZip)) {
            QQUtils.QQToast(1, "压缩配置文件失败");
            return;
        }

        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String exportFileName = "QFun_config_backup_" + timestamp + ".zip";
        File exportFile = new File(mExportDir, exportFileName);

        if (FileUtils.copy(tempZip.getAbsolutePath(), exportFile.getAbsolutePath())) {
            QQUtils.QQToast(2, "成功导出配置到Download文件夹");
        } else {
            QQUtils.QQToast(1, "导出文件失败");
        }
    }

    private class ItemAdapter extends ArrayAdapter<BaseSwitchHookItem> {
        private final int mLayoutId;

        public ItemAdapter(int layoutId, List<BaseSwitchHookItem> list) {
            super(InjectSettings.this, layoutId, list);
            mLayoutId = layoutId;
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            BaseSwitchHookItem hookItem = getItem(position);
            if (hookItem == null) {
                return new View(getContext());
            }

            View view = LayoutInflater.from(getContext()).inflate(mLayoutId, parent, false);
            LinearLayout hookitemLinearLayout = view.findViewById(R.id.hookitemLinearLayout);
            TextView desc = view.findViewById(R.id.hookItemDesc);
            TextView hookitemTextView = view.findViewById(R.id.hookItemTextView);
            Switch hookitemSwitch = view.findViewById(R.id.hookItemSwitch);

            if (hookItem instanceof BaseWithDataHookItem && hookItem.isAvailable) {
                hookitemLinearLayout.setOnClickListener((BaseWithDataHookItem) hookItem);
            }

            if (!hookItem.isAvailable) {
                view.setBackgroundColor(Color.argb(255, 192, 192, 192));
            }

            hookitemTextView.setText(hookItem.getTAG());
            desc.setText(hookItem.getDESC());

            if (!hookItem.isAvailable) {
                hookitemSwitch.setChecked(false);
                hookitemSwitch.setEnabled(false);
            } else {
                hookitemSwitch.setChecked(MainHook.Enable.get(hookItem.getNAME()));
                hookitemSwitch.setOnClickListener(v ->
                        MainHook.Enable.replace(hookItem.getNAME(), hookitemSwitch.isChecked()));
            }

            return view;
        }
    }
}

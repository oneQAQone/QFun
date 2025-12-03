package me.yxp.qfun;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashSet;
import java.util.Set;

import io.github.libxposed.service.XposedService;
import me.yxp.qfun.utils.hook.hookstatus.HookStatus;
import me.yxp.qfun.utils.qq.HostInfo;

public class MainActivity extends Activity implements View.OnClickListener {
    private TextView customText;
    private Button btnUsage;
    private Button btnDiscussion;
    private Button btnChangelog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        HookStatus.init(this);
        initView();
        updateActivationStatus();
    }

    private void initView() {
        this.customText = findViewById(R.id.custom_text);
        this.btnUsage = findViewById(R.id.btn_usage);
        this.btnDiscussion = findViewById(R.id.btn_discussion);
        this.btnChangelog = findViewById(R.id.btn_changelog);

        btnUsage.setOnClickListener(this);
        btnDiscussion.setOnClickListener(this);
        btnChangelog.setOnClickListener(this);
    }

    public void gotoTelegram() {
        jump("https://t.me/QFunChannel");
    }

    public void gotoGithub() {
        jump("https://github.com/oneQAQone/QFun");
    }

    public void gotoQQGroup() {
        jump("mqqapi://card/show_pslcard?src_type=internal&version=1&uin=1067198087&card_type=group&source=qrcode");
    }

    public void gotoTelegramChat() {
        jump("https://t.me/QFunChatGroup");
    }

    private void jump(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }

    private void updateActivationStatus() {
        boolean isHookEnabled = checkHookStatus();
        customText.setText(isHookEnabled ? "状态：已激活" : "状态：未激活");
    }

    private boolean checkHookStatus() {
        boolean isEnabledByLegacyApi = HookStatus.isModuleEnabled() || HostInfo.isInHostProcess();
        boolean isEnabledByLibXposedApi = checkLibXposedStatus();

        return isEnabledByLegacyApi || isEnabledByLibXposedApi;
    }

    private boolean checkLibXposedStatus() {
        XposedService xposedService = HookStatus.getXposedService().getValue();
        if (xposedService == null) {
            return false;
        }

        Set<String> scope = new HashSet<>(xposedService.getScope());
        scope.retainAll(Set.of(HostInfo.PACKAGE_NAME_QQ, HostInfo.PACKAGE_NAME_TIM));
        return !scope.isEmpty();
    }

    @Override
    public void onClick(View v) {
        // 有点懵了这里，原本想用switch
        // 结果R.id的final被砍了，我还不记得恢复的配置是哪个了
        if (v.getId() == R.id.btn_usage)
            gotoGithub();
        else if (v.getId() == R.id.btn_discussion)
            startDiscussionDialog();
        else if (v.getId() == R.id.btn_changelog)
            Toast.makeText(this, "暂时没有更多哦！", Toast.LENGTH_SHORT).show();
    }


    public void startDiscussionDialog() {
        var builder = new AlertDialog.Builder(this, android.R.style.Theme_DeviceDefault_Light_Dialog_Alert);
        var items = new CharSequence[]{
                "Telegram Channel",
                "Telegram Chat Group",
        };
        builder.setTitle("交流讨论")
        .setItems(items, (dialog, which) -> {
            if (which == 0) gotoTelegram();
            if (which == 1) gotoTelegramChat();
        }).show();
    }
}
